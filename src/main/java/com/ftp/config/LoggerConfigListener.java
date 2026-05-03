package com.ftp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerConfigListener implements ConfigChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggerConfigListener.class);

    @Override
    public void onConfigChanged(Config oldConfig, Config newConfig) {
        if (oldConfig == null || newConfig == null) {
            return;
        }

        String oldLogLevel = oldConfig.getLogLevel();
        String newLogLevel = newConfig.getLogLevel();

        if (newLogLevel != null && !newLogLevel.equals(oldLogLevel)) {
            logger.info("Log level changed from " + oldLogLevel + " to " + newLogLevel);
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", newLogLevel);
        }

        String oldLogPath = oldConfig.getLogFilePath();
        String newLogPath = newConfig.getLogFilePath();

        if (newLogPath != null && !newLogPath.equals(oldLogPath)) {
            logger.info("Log file path changed - please restart for changes to take effect");
        }
    }
}
