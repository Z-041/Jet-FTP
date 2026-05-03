package com.ftp.config;

public interface ConfigChangeListener {
    void onConfigChanged(Config oldConfig, Config newConfig);
}
