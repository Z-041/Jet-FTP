package com.ftp.config;

public class ConfigChangeEvent {
    private final Config oldConfig;
    private final Config newConfig;
    private final long timestamp;

    public ConfigChangeEvent(Config oldConfig, Config newConfig) {
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
        this.timestamp = System.currentTimeMillis();
    }

    public Config getOldConfig() {
        return oldConfig;
    }

    public Config getNewConfig() {
        return newConfig;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
