package com.ftp.config;

import java.util.Objects;

public class Config {
    private final int port;
    private final String rootDirectory;
    private final int maxConnections;
    private final int timeoutSeconds;
    private final String logLevel;
    private final String logFilePath;
    private final int threadPoolCoreSize;
    private final int threadPoolKeepAliveSeconds;
    private final int threadPoolQueueCapacity;
    private final String passiveModeExternalIp;
    private final int passiveModePortMin;
    private final int passiveModePortMax;
    private final int passiveModeConnectionTimeout;
    private final String bindAddress;
    private final boolean dualStackEnabled;
    private final String listenInterface;
    private final String ipv4ExternalIp;
    private final String ipv6ExternalIp;
    private final boolean preferIPv6;
    private final int bcryptRounds;
    private final boolean detailedTransferLog;
    private final int logQueueSize;

    private Config(Builder builder) {
        this.port = builder.port;
        this.rootDirectory = Objects.requireNonNull(builder.rootDirectory, "Root directory cannot be null");
        this.maxConnections = builder.maxConnections;
        this.timeoutSeconds = builder.timeoutSeconds;
        this.logLevel = Objects.requireNonNull(builder.logLevel, "Log level cannot be null");
        this.logFilePath = Objects.requireNonNull(builder.logFilePath, "Log file path cannot be null");
        this.threadPoolCoreSize = builder.threadPoolCoreSize;
        this.threadPoolKeepAliveSeconds = builder.threadPoolKeepAliveSeconds;
        this.threadPoolQueueCapacity = builder.threadPoolQueueCapacity;
        this.passiveModeExternalIp = builder.passiveModeExternalIp;
        this.passiveModePortMin = builder.passiveModePortMin;
        this.passiveModePortMax = builder.passiveModePortMax;
        this.passiveModeConnectionTimeout = builder.passiveModeConnectionTimeout;
        this.bindAddress = Objects.requireNonNull(builder.bindAddress, "Bind address cannot be null");
        this.dualStackEnabled = builder.dualStackEnabled;
        this.listenInterface = Objects.requireNonNull(builder.listenInterface, "Listen interface cannot be null");
        this.ipv4ExternalIp = Objects.requireNonNull(builder.ipv4ExternalIp, "IPv4 external IP cannot be null");
        this.ipv6ExternalIp = Objects.requireNonNull(builder.ipv6ExternalIp, "IPv6 external IP cannot be null");
        this.preferIPv6 = builder.preferIPv6;
        this.bcryptRounds = builder.bcryptRounds;
        this.detailedTransferLog = builder.detailedTransferLog;
        this.logQueueSize = builder.logQueueSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int port = 21;
        private String rootDirectory = "ftp-root";
        private int maxConnections = 10;
        private int timeoutSeconds = 300;
        private String logLevel = "INFO";
        private String logFilePath = "logs/ftp-server.log";
        private int threadPoolCoreSize = 10;
        private int threadPoolKeepAliveSeconds = 60;
        private int threadPoolQueueCapacity = 100;
        private String passiveModeExternalIp = null;
        private int passiveModePortMin = 0;
        private int passiveModePortMax = 0;
        private int passiveModeConnectionTimeout = 30;
        private String bindAddress = "::";
        private boolean dualStackEnabled = true;
        private String listenInterface = "auto";
        private String ipv4ExternalIp = "auto";
        private String ipv6ExternalIp = "auto";
        private boolean preferIPv6 = false;
        private int bcryptRounds = 12;
        private boolean detailedTransferLog = false;
        private int logQueueSize = 512;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder rootDirectory(String rootDirectory) {
            this.rootDirectory = rootDirectory;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder logLevel(String logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder logFilePath(String logFilePath) {
            this.logFilePath = logFilePath;
            return this;
        }

        public Builder threadPoolCoreSize(int threadPoolCoreSize) {
            this.threadPoolCoreSize = threadPoolCoreSize;
            return this;
        }

        public Builder threadPoolKeepAliveSeconds(int threadPoolKeepAliveSeconds) {
            this.threadPoolKeepAliveSeconds = threadPoolKeepAliveSeconds;
            return this;
        }

        public Builder threadPoolQueueCapacity(int threadPoolQueueCapacity) {
            this.threadPoolQueueCapacity = threadPoolQueueCapacity;
            return this;
        }

        public Builder passiveModeExternalIp(String passiveModeExternalIp) {
            this.passiveModeExternalIp = passiveModeExternalIp;
            return this;
        }

        public Builder passiveModePortMin(int passiveModePortMin) {
            this.passiveModePortMin = passiveModePortMin;
            return this;
        }

        public Builder passiveModePortMax(int passiveModePortMax) {
            this.passiveModePortMax = passiveModePortMax;
            return this;
        }

        public Builder passiveModeConnectionTimeout(int passiveModeConnectionTimeout) {
            this.passiveModeConnectionTimeout = passiveModeConnectionTimeout;
            return this;
        }

        public Builder bindAddress(String bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }

        public Builder dualStackEnabled(boolean dualStackEnabled) {
            this.dualStackEnabled = dualStackEnabled;
            return this;
        }

        public Builder listenInterface(String listenInterface) {
            this.listenInterface = listenInterface;
            return this;
        }

        public Builder ipv4ExternalIp(String ipv4ExternalIp) {
            this.ipv4ExternalIp = ipv4ExternalIp;
            return this;
        }

        public Builder ipv6ExternalIp(String ipv6ExternalIp) {
            this.ipv6ExternalIp = ipv6ExternalIp;
            return this;
        }

        public Builder preferIPv6(boolean preferIPv6) {
            this.preferIPv6 = preferIPv6;
            return this;
        }

        public Builder bcryptRounds(int bcryptRounds) {
            this.bcryptRounds = bcryptRounds;
            return this;
        }

        public Builder detailedTransferLog(boolean detailedTransferLog) {
            this.detailedTransferLog = detailedTransferLog;
            return this;
        }

        public Builder logQueueSize(int logQueueSize) {
            this.logQueueSize = logQueueSize;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    public int getPort() { return port; }
    public String getRootDirectory() { return rootDirectory; }
    public int getMaxConnections() { return maxConnections; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public String getLogLevel() { return logLevel; }
    public String getLogFilePath() { return logFilePath; }
    public int getThreadPoolCoreSize() { return threadPoolCoreSize; }
    public int getThreadPoolKeepAliveSeconds() { return threadPoolKeepAliveSeconds; }
    public int getThreadPoolQueueCapacity() { return threadPoolQueueCapacity; }
    public String getPassiveModeExternalIp() { return passiveModeExternalIp; }
    public int getPassiveModePortMin() { return passiveModePortMin; }
    public int getPassiveModePortMax() { return passiveModePortMax; }
    public int getPassiveModeConnectionTimeout() { return passiveModeConnectionTimeout; }
    public String getBindAddress() { return bindAddress; }
    public boolean isDualStackEnabled() { return dualStackEnabled; }
    public String getListenInterface() { return listenInterface; }
    public String getIpv4ExternalIp() { return ipv4ExternalIp; }
    public String getIpv6ExternalIp() { return ipv6ExternalIp; }
    public boolean isPreferIPv6() { return preferIPv6; }
    public int getBcryptRounds() { return bcryptRounds; }
    public boolean isDetailedTransferLog() { return detailedTransferLog; }
    public int getLogQueueSize() { return logQueueSize; }

    @Override
    public String toString() {
        return "Config{" +
                "port=" + port +
                ", rootDirectory='" + rootDirectory + '\'' +
                ", maxConnections=" + maxConnections +
                ", timeoutSeconds=" + timeoutSeconds +
                ", logLevel='" + logLevel + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", threadPoolCoreSize=" + threadPoolCoreSize +
                ", threadPoolKeepAliveSeconds=" + threadPoolKeepAliveSeconds +
                ", threadPoolQueueCapacity=" + threadPoolQueueCapacity +
                ", passiveModeExternalIp='" + passiveModeExternalIp + '\'' +
                ", passiveModePortRange=" + passiveModePortMin + "-" + passiveModePortMax +
                ", passiveModeConnectionTimeout=" + passiveModeConnectionTimeout +
                ", bindAddress='" + bindAddress + '\'' +
                ", dualStackEnabled=" + dualStackEnabled +
                ", listenInterface='" + listenInterface + '\'' +
                ", ipv4ExternalIp='" + ipv4ExternalIp + '\'' +
                ", ipv6ExternalIp='" + ipv6ExternalIp + '\'' +
                ", preferIPv6=" + preferIPv6 +
                '}';
    }
}
