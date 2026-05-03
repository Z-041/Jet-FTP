package com.ftp.server;

import com.ftp.config.Config;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class ServerSocketFactory {

    ServerSocket createServerSocket(int port, InetAddress bindAddr) throws IOException {
        ServerSocket ss = new ServerSocket(port, 50, bindAddr);
        ss.setReuseAddress(true);
        ss.setSoTimeout(1000);
        return ss;
    }

    List<InetAddress> getListenAddresses(Config config) throws IOException {
        List<InetAddress> addresses = new ArrayList<>();
        String listenInterface = config.getListenInterface();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();

            if (!ni.isUp()) {
                continue;
            }

            if (shouldSkipInterface(ni, listenInterface)) {
                continue;
            }

            Enumeration<InetAddress> enumAddresses = ni.getInetAddresses();
            while (enumAddresses.hasMoreElements()) {
                InetAddress addr = enumAddresses.nextElement();

                if (isExcludedAddress(addr)) {
                    continue;
                }

                addresses.add(addr);
            }
        }

        if (addresses.isEmpty()) {
            throw new IOException("No network interfaces available for listening");
        }

        return addresses;
    }

    private boolean shouldSkipInterface(NetworkInterface ni, String listenInterface) {
        if (listenInterface == null || "auto".equalsIgnoreCase(listenInterface)) {
            return false;
        }
        return !ni.getName().equalsIgnoreCase(listenInterface);
    }

    private boolean isExcludedAddress(InetAddress addr) {
        if (addr instanceof Inet6Address && addr.isLinkLocalAddress()) {
            return true;
        }

        return false;
    }

    String getInterfaceName(InetAddress addr) {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
            return ni != null ? ni.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
