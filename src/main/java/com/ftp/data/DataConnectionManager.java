package com.ftp.data;

import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ftp.session.Session;
import com.ftp.session.TransferContext;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class DataConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(DataConnectionManager.class);

    private ServerSocket passiveServerSocket;
    private final AddressSelector addressSelector;
    private final ReentrantLock passiveSocketLock;

    public static class PassiveResult {
        private final InetAddress responseAddress;
        private final int port;
        private final boolean isIPv6;

        public PassiveResult(InetAddress responseAddress, int port, boolean isIPv6) {
            this.responseAddress = responseAddress;
            this.port = port;
            this.isIPv6 = isIPv6;
        }

        public InetAddress getResponseAddress() { return responseAddress; }
        public int getPort() { return port; }
        public boolean isIPv6() { return isIPv6; }

        public String toPASVFormat() {
            if (isIPv6 || !(responseAddress instanceof Inet4Address)) {
                throw new UnsupportedOperationException("PASV format only supports IPv4 addresses");
            }
            byte[] addr = responseAddress.getAddress();
            int p1 = port / 256;
            int p2 = port % 256;
            return String.format("%d,%d,%d,%d,%d,%d",
                addr[0] & 0xff, addr[1] & 0xff, addr[2] & 0xff, addr[3] & 0xff,
                p1, p2);
        }

        public String toEPSVFormat() {
            return String.format("|||%d|", port);
        }
    }

    public DataConnectionManager() {
        this.addressSelector = new AddressSelector();
        this.passiveSocketLock = new ReentrantLock();
    }

    public Socket openDataConnection(Session session) throws IOException {
        if (session.getTransferContext().isPassiveMode()) {
            return openPassiveConnection(session);
        } else {
            return openActiveConnection(session);
        }
    }

    private Socket openPassiveConnection(Session session) throws IOException {
        passiveSocketLock.lock();
        try {
            if (passiveServerSocket == null) {
                throw new IOException("Passive server socket not initialized");
            }

            Config config = ConfigManager.getInstance().getConfig();
            int timeoutMs = config.getPassiveModeConnectionTimeout() * 1000;
            passiveServerSocket.setSoTimeout(timeoutMs);

            logger.debug("Waiting for passive connection on port " + passiveServerSocket.getLocalPort() +
                         " (timeout: " + timeoutMs + "ms)");

            try {
                Socket socket = passiveServerSocket.accept();
                closePassiveServerSocketInternal();

                logger.info("Passive connection accepted from " + socket.getInetAddress() +
                            ":" + socket.getPort());
                return socket;
            } catch (SocketTimeoutException e) {
                logger.error("Passive mode connection timeout after " + timeoutMs + "ms on port " +
                            passiveServerSocket.getLocalPort());
                closePassiveServerSocketInternal();
                throw new IOException("Passive mode connection timeout. Client failed to connect to data port.", e);
            }
        } finally {
            passiveSocketLock.unlock();
        }
    }

    private Socket openActiveConnection(Session session) throws IOException {
        String address = session.getTransferContext().getActiveAddress();
        int port = session.getTransferContext().getActivePort();
        if (address == null || port == 0) {
            throw new IOException("Active mode address/port not set");
        }
        logger.info("Opening active connection to " + address + ":" + port);
        Socket socket = new Socket(address, port);
        socket.setSoTimeout(ConfigManager.getInstance().getConfig().getTimeoutSeconds() * 1000);
        return socket;
    }

    public PassiveResult enterPassiveMode(InetAddress serverAddress, InetAddress clientAddress) throws IOException {
        passiveSocketLock.lock();
        try {
            Config config = ConfigManager.getInstance().getConfig();
            boolean clientIsIPv6 = clientAddress instanceof Inet6Address;

            int port = findAvailablePort(config.getPassiveModePortMin(), config.getPassiveModePortMax());

            InetAddress bindAddr = addressSelector.selectAddressForClient(clientAddress, config);

            passiveServerSocket = new ServerSocket(port, 50, bindAddr);
            passiveServerSocket.setReuseAddress(true);

            PassiveResult result = new PassiveResult(bindAddr, port, clientIsIPv6);

            if (clientIsIPv6) {
                logger.info("EPSV mode entered (IPv6) on " + bindAddr.getHostAddress() + ":" + port);
            } else {
                logger.info("PASV mode entered (IPv4) on " + bindAddr.getHostAddress() + ":" + port);
            }

            return result;
        } finally {
            passiveSocketLock.unlock();
        }
    }

    private int findAvailablePort(int portMin, int portMax) throws IOException {
        if (portMin > 0 && portMax > 0 && portMax >= portMin) {
            int range = portMax - portMin + 1;
            int maxAttempts = Math.min(range, 100);

            for (int i = 0; i < maxAttempts; i++) {
                int port = portMin + ThreadLocalRandom.current().nextInt(range);
                try (ServerSocket testSocket = new ServerSocket(port)) {
                    testSocket.setReuseAddress(true);
                    return port;
                } catch (IOException e) {
                    continue;
                }
            }
            logger.warn("Could not find available port in range " + portMin + "-" + portMax + ", using system-assigned port");
        }

        ServerSocket socket = new ServerSocket(0);
        socket.setReuseAddress(true);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    public void closePassiveServerSocket() {
        passiveSocketLock.lock();
        try {
            closePassiveServerSocketInternal();
        } finally {
            passiveSocketLock.unlock();
        }
    }

    private void closePassiveServerSocketInternal() {
        if (passiveServerSocket != null) {
            try {
                passiveServerSocket.close();
                logger.debug("Passive server socket closed on port " + passiveServerSocket.getLocalPort());
            } catch (IOException e) {
                logger.warn("Error closing passive server socket", e);
            } finally {
                passiveServerSocket = null;
            }
        }
    }

    public void sendFile(Socket socket, File file, TransferContext.TransferType transferType, long restartPosition, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        try (OutputStream out = socket.getOutputStream()) {
            if (transferType == TransferContext.TransferType.ASCII) {
                try (InputStream in = new BufferedInputStream(new FileInputStream(file));
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8))) {
                    // 对于ASCII模式，我们需要跳过指定数量的字节
                    if (restartPosition > 0) {
                        in.skip(restartPosition);
                    }
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String lineData = line + "\r\n";
                        writer.write(lineData);
                        if (limiter != null && limiter.isDownloadLimited()) {
                            limiter.limitDownload(null, lineData.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
                        }
                    }
                }
            } else {
                try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                    // 对于二进制模式，跳过指定数量的字节
                    if (restartPosition > 0) {
                        in.skip(restartPosition);
                    }
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        if (limiter != null && limiter.isDownloadLimited()) {
                            limiter.limitDownload(null, bytesRead);
                        }
                    }
                }
            }
            out.flush();
        }
    }

    public void sendFile(Socket socket, File file, TransferContext.TransferType transferType, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        sendFile(socket, file, transferType, 0, limiter);
    }

    public void sendFile(Socket socket, File file, TransferContext.TransferType transferType) throws IOException {
        sendFile(socket, file, transferType, null);
    }

    public void receiveFile(Socket socket, File file, TransferContext.TransferType transferType, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        receiveFile(socket, file, transferType, false, limiter);
    }

    public void receiveFile(Socket socket, File file, TransferContext.TransferType transferType) throws IOException {
        receiveFile(socket, file, transferType, false, null);
    }

    public void receiveFile(Socket socket, File file, TransferContext.TransferType transferType, boolean append, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        if (transferType == TransferContext.TransferType.ASCII) {
            receiveFileAscii(socket, file, append, limiter);
        } else {
            receiveFileBinary(socket, file, append, limiter);
        }
    }

    private void receiveFileAscii(Socket socket, File file, boolean append, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(file, append), java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String lineData = line + "\n";
                writer.write(lineData);
                if (limiter != null && limiter.isUploadLimited()) {
                    limiter.limitUpload(null, lineData.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
                }
            }
            writer.flush();
        }
    }

    private void receiveFileBinary(Socket socket, File file, boolean append, com.ftp.util.BandwidthLimiter limiter) throws IOException {
        try (InputStream in = socket.getInputStream();
             OutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                if (limiter != null && limiter.isUploadLimited()) {
                    limiter.limitUpload(null, bytesRead);
                }
            }
            out.flush();
        }
    }

    public void sendDirectoryList(Socket socket, File directory, boolean useNlst) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            files = new File[0];
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            for (File file : files) {
                if (useNlst) {
                    writer.write(file.getName());
                } else {
                    writer.write(formatListing(file));
                }
                writer.write("\r\n");
            }
            writer.flush();
        }
    }

    private String formatListing(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm", Locale.US);

        StringBuilder sb = new StringBuilder();
        sb.append(file.isDirectory() ? "d" : "-");
        sb.append("rwxr-xr-x 1 ftp ftp ");
        sb.append(String.format("%12d ", file.length()));
        sb.append(sdf.format(new Date(file.lastModified()))).append(" ");
        sb.append(file.getName());
        return sb.toString();
    }
}
