package com.ftp.server;

import com.ftp.config.Config;
import com.ftp.config.ConfigChangeListener;
import com.ftp.config.ConfigManager;
import com.ftp.ui.ConnectionListener;
import com.ftp.util.IPFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FtpServer {
    private static final Logger logger = LoggerFactory.getLogger(FtpServer.class);
    
    private final int port;
    private final int maxConnections;
    private final int timeoutSeconds;
    private final int threadPoolCoreSize;
    private final int threadPoolKeepAliveSeconds;
    private final int threadPoolQueueCapacity;
    
    private volatile boolean running;
    private final List<ServerSocket> serverSockets = new ArrayList<>();
    private ExecutorService threadPool;
    private final Set<ClientHandler> activeHandlers;
    private final List<ConnectionListener> connectionListeners;
    private final AtomicInteger activeConnectionCount = new AtomicInteger(0);
    private final ServerSocketFactory socketFactory;

    public FtpServer() {
        this(ConfigManager.getInstance().getConfig());
    }

    public FtpServer(Config config) {
        this.port = config.getPort();
        this.maxConnections = config.getMaxConnections();
        this.timeoutSeconds = config.getTimeoutSeconds();
        this.threadPoolCoreSize = config.getThreadPoolCoreSize();
        this.threadPoolKeepAliveSeconds = config.getThreadPoolKeepAliveSeconds();
        this.threadPoolQueueCapacity = config.getThreadPoolQueueCapacity();
        this.activeHandlers = ConcurrentHashMap.newKeySet();
        this.connectionListeners = new ArrayList<>();
        this.socketFactory = new ServerSocketFactory();
        
        ConfigManager.getInstance().addConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void onConfigChanged(Config oldConfig, Config newConfig) {
                if (newConfig != null) {
                    logger.info("Configuration changed:");
                    logger.info("- Port: " + oldConfig.getPort() + " -> " + newConfig.getPort());
                    logger.info("- Max Connections: " + oldConfig.getMaxConnections() + " -> " + newConfig.getMaxConnections());
                    logger.info("- Timeout: " + oldConfig.getTimeoutSeconds() + "s -> " + newConfig.getTimeoutSeconds() + "s");
                    logger.info("- Dual Stack: " + oldConfig.isDualStackEnabled() + " -> " + newConfig.isDualStackEnabled());
                    logger.info("Restart server to apply changes");
                }
            }
        });
    }

    public void reloadConfig() {
        logger.info("Configuration reloaded, restart required for changes to take effect");
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public Set<ClientHandler> getActiveHandlers() {
        return activeHandlers;
    }

    public int getPort() { return port; }
    public int getMaxConnections() { return maxConnections; }

    private void notifyConnectionAdded(ClientHandler handler) {
        for (ConnectionListener listener : connectionListeners) {
            try {
                listener.onConnectionAdded(handler.getConnectionInfo());
            } catch (Exception e) {
                logger.error("Error notifying connection listener", e);
            }
        }
    }

    private void notifyConnectionRemoved(String id) {
        for (ConnectionListener listener : connectionListeners) {
            try {
                listener.onConnectionRemoved(id);
            } catch (Exception e) {
                logger.error("Error notifying connection listener", e);
            }
        }
    }

    public void start() throws IOException {
        if (running) {
            logger.warn("Server is already running");
            return;
        }

        running = true;
        
        // 初始化线程池
        int corePoolSize = Math.min(threadPoolCoreSize, maxConnections);
        threadPool = new ThreadPoolExecutor(
            corePoolSize,
            maxConnections,
            threadPoolKeepAliveSeconds,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(threadPoolQueueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        Config config = ConfigManager.getInstance().getConfig();
        
        // 根据配置启动服务器
        if (config.isDualStackEnabled()) {
            startDualStackServer(config);
        } else {
            startSingleStackServer(config);
        }
        
        logger.info("FTP Server started successfully");
        logger.info("Listening on port: " + port);
        logger.info("Max connections: " + maxConnections);
        logger.info("Timeout: " + timeoutSeconds + " seconds");
        logger.info("Thread pool: core=" + corePoolSize + ", max=" + maxConnections + 
                   ", keepAlive=" + threadPoolKeepAliveSeconds + "s, queue=" + threadPoolQueueCapacity);
        logger.info("Dual stack mode: " + config.isDualStackEnabled());
    }

    private void startSingleStackServer(Config config) throws IOException {
        String bindAddressStr = config.getBindAddress();
        
        try {
            InetAddress bindAddr = InetAddress.getByName(bindAddressStr);
            ServerSocket ss = socketFactory.createServerSocket(port, bindAddr);
            
            synchronized (serverSockets) {
                serverSockets.add(ss);
            }
            
            String protocol = bindAddr instanceof Inet6Address ? "IPv6" : "IPv4";
            logger.info("Starting single-stack server (" + protocol + ") on " + 
                       bindAddr.getHostAddress() + ":" + port);
            
            new Thread(() -> acceptConnections(ss), "FTP-Acceptor-" + protocol).start();
            
        } catch (Exception e) {
            throw new IOException("Failed to start single-stack server on " + bindAddressStr + ":" + port, e);
        }
    }

    private void startDualStackServer(Config config) throws IOException {
        List<InetAddress> listenAddresses = socketFactory.getListenAddresses(config);
        
        if (listenAddresses.isEmpty()) {
            throw new IOException("No suitable network addresses found for listening");
        }
        
        for (InetAddress addr : listenAddresses) {
            try {
                ServerSocket ss = socketFactory.createServerSocket(port, addr);
                
                synchronized (serverSockets) {
                    serverSockets.add(ss);
                }
                
                String protocol = addr instanceof Inet6Address ? "IPv6" : "IPv4";
                String ifaceName = socketFactory.getInterfaceName(addr);
                
                logger.info("Starting dual-stack listener (" + protocol + ") on " + 
                           addr.getHostAddress() + ":" + port + 
                           (ifaceName != null ? " [" + ifaceName + "]" : ""));
                
                final ServerSocket socketRef = ss;
                new Thread(() -> acceptConnections(socketRef), 
                         "FTP-Acceptor-" + protocol + "-" + (ifaceName != null ? ifaceName : "default")).start();
                
            } catch (IOException e) {
                logger.warn("Failed to bind to " + addr.getHostAddress() + ": " + e.getMessage());
            }
        }
        
        if (serverSockets.isEmpty()) {
            throw new IOException("Failed to bind to any address on port " + port);
        }
    }

    private void acceptConnections(ServerSocket serverSocket) {
        while (running && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleNewConnection(clientSocket);
                
            } catch (SocketTimeoutException e) {
                // 正常超时，继续循环检查running状态
                continue;
            } catch (IOException e) {
                if (running && !serverSocket.isClosed()) {
                    logger.error("Error accepting connection on " + 
                               serverSocket.getInetAddress(), e);
                }
            }
        }
        
        logger.info("Accept thread stopped for " + serverSocket.getInetAddress());
    }

    private void handleNewConnection(Socket clientSocket) {
        try {
            InetAddress clientAddress = clientSocket.getInetAddress();
            
            // IP过滤
            if (!IPFilter.getInstance().isAllowed(clientAddress)) {
                logger.warn("Connection rejected from blocked IP: " + clientAddress.getHostAddress());
                clientSocket.close();
                return;
            }
            
            // 连接数限制
            if (activeConnectionCount.get() >= maxConnections) {
                logger.warn("Max connections reached (" + maxConnections + 
                          "), rejecting from " + clientAddress);
                clientSocket.close();
                return;
            }
            
            // 增加连接计数
            activeConnectionCount.incrementAndGet();
            
            ClientHandler handler = new ClientHandler(clientSocket);
            for (ConnectionListener listener : connectionListeners) {
                handler.addConnectionListener(listener);
            }
            activeHandlers.add(handler);
            notifyConnectionAdded(handler);
            
            // 提交到线程池处理
            threadPool.submit(() -> {
                try {
                    handler.run();
                } finally {
                    activeHandlers.remove(handler);
                    activeConnectionCount.decrementAndGet();
                    notifyConnectionRemoved(handler.getHandlerId());
                }
            });
            
        } catch (IOException e) {
            logger.error("Error handling new connection", e);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                logger.warn("Error closing rejected socket", ex);
            }
        }
    }

    public void stop() {
        if (!running) {
            logger.warn("Server is not running");
            return;
        }

        logger.info("Stopping FTP server...");
        running = false;

        // 停止所有客户端处理器
        for (ClientHandler handler : activeHandlers) {
            handler.stop();
        }
        activeHandlers.clear();

        // 关闭所有ServerSocket
        synchronized (serverSockets) {
            for (ServerSocket ss : serverSockets) {
                try {
                    if (ss != null && !ss.isClosed()) {
                        ss.close();
                        logger.debug("Closed server socket: " + ss.getInetAddress());
                    }
                } catch (IOException e) {
                    logger.warn("Error closing server socket", e);
                }
            }
            serverSockets.clear();
        }

        // 关闭线程池
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.warn("Thread pool did not terminate gracefully, forcing shutdown");
                    threadPool.shutdownNow();
                    
                    if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                        logger.error("Thread pool did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logger.info("FTP server stopped completely");
    }

    public boolean isRunning() { return running; }

    public int getActiveConnections() { return activeConnectionCount.get(); }

    public List<InetAddress> getListenAddresses() {
        List<InetAddress> result = new ArrayList<>();
        synchronized (serverSockets) {
            for (ServerSocket ss : serverSockets) {
                if (ss != null && !ss.isClosed()) {
                    result.add(ss.getInetAddress());
                }
            }
        }
        return result;
    }
}
