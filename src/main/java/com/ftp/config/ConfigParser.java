package com.ftp.config;

import com.ftp.constants.FtpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfigParser {
    private static final Logger logger = LoggerFactory.getLogger(ConfigParser.class);

    int parsePort(String value) {
        int defaultValue = FtpConstants.Defaults.DEFAULT_PORT;
        return parseIntWithBounds(value, defaultValue, 
            FtpConstants.Limits.MIN_PORT, FtpConstants.Limits.MAX_PORT, "port");
    }

    int parseMaxConnections(String value) {
        int defaultValue = FtpConstants.Defaults.DEFAULT_MAX_CONNECTIONS;
        return parseIntWithBounds(value, defaultValue,
            FtpConstants.Limits.MIN_MAX_CONNECTIONS, FtpConstants.Limits.MAX_MAX_CONNECTIONS, "max connections");
    }

    int parseTimeout(String value) {
        int defaultValue = FtpConstants.Defaults.DEFAULT_TIMEOUT_SECONDS;
        return parseIntWithBounds(value, defaultValue,
            FtpConstants.Limits.MIN_TIMEOUT, FtpConstants.Limits.MAX_TIMEOUT, "timeout");
    }

    String parseLogLevel(String value) {
        String defaultValue = FtpConstants.Defaults.DEFAULT_LOG_LEVEL;
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String level = value.trim().toUpperCase();
        for (String validLevel : FtpConstants.LogLevels.VALID_LEVELS) {
            if (validLevel.equals(level)) {
                return level;
            }
        }
        logger.warn("Invalid log level " + value + ", using default " + defaultValue);
        return defaultValue;
    }

    String parseLogFilePath(String value) {
        String defaultValue = FtpConstants.Defaults.DEFAULT_LOG_FILE_PATH;
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    String parseRootDirectory(String value) {
        String defaultValue = FtpConstants.Defaults.DEFAULT_ROOT_DIRECTORY;
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    int parseThreadPoolCoreSize(String value) {
        return parseIntPositive(value, 10, "thread pool core size");
    }

    int parseThreadPoolKeepAliveSeconds(String value) {
        return parseIntPositive(value, 60, "thread pool keep alive time");
    }

    int parseThreadPoolQueueCapacity(String value) {
        return parseIntPositive(value, 100, "thread pool queue capacity");
    }

    String parseBindAddress(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "::";
        }
        return value.trim();
    }

    boolean parseDualStackEnabled(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return Boolean.parseBoolean(value.trim());
    }

    String parseListenInterface(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "auto";
        }
        return value.trim();
    }

    String parseIpv4ExternalIp(String value) {
        if (value == null || value.trim().isEmpty() || "auto".equalsIgnoreCase(value.trim())) {
            return "auto";
        }
        return value.trim();
    }

    String parseIpv6ExternalIp(String value) {
        if (value == null || value.trim().isEmpty() || "auto".equalsIgnoreCase(value.trim())) {
            return "auto";
        }
        return value.trim();
    }

    boolean parsePreferIPv6(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return Boolean.parseBoolean(value.trim());
    }

    String parsePassiveModeExternalIp(String value) {
        if (value == null || value.trim().isEmpty() || "auto".equalsIgnoreCase(value.trim())) {
            return null;
        }
        String ip = value.trim();
        return "0.0.0.0".equals(ip) ? null : ip;
    }

    int parsePassiveModePortMin(String value) {
        return parsePortRange(value, "min");
    }

    int parsePassiveModePortMax(String value) {
        return parsePortRange(value, "max");
    }

    int parsePassiveModeConnectionTimeout(String value) {
        return parseIntPositive(value, 30, "passive mode connection timeout");
    }

    private int parseIntWithBounds(String value, int defaultValue, int min, int max, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min || parsed > max) {
                logger.warn("Invalid " + fieldName + " " + parsed + ", using default " + defaultValue);
                return defaultValue;
            }
            return parsed;
        } catch (NumberFormatException e) {
            logger.warn("Invalid " + fieldName + " format, using default " + defaultValue);
            return defaultValue;
        }
    }

    private int parseIntPositive(String value, int defaultValue, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < 1) {
                logger.warn("Invalid " + fieldName + " " + parsed + ", using default " + defaultValue);
                return defaultValue;
            }
            return parsed;
        } catch (NumberFormatException e) {
            logger.warn("Invalid " + fieldName + " format, using default " + defaultValue);
            return defaultValue;
        }
    }

    private int parsePortRange(String value, String type) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            int port = Integer.parseInt(value.trim());
            if (port < 0 || port > 65535) {
                logger.warn("Invalid passive mode port " + type + " " + port + ", using default (random)");
                return 0;
            }
            return port;
        } catch (NumberFormatException e) {
            logger.warn("Invalid passive mode port " + type + " format, using default (random)");
            return 0;
        }
    }

    int parseBcryptRounds(String value) {
        int defaultValue = FtpConstants.Defaults.DEFAULT_BCRYPT_ROUNDS;
        return parseIntWithBounds(value, defaultValue,
            FtpConstants.Limits.MIN_BCRYPT_ROUNDS, FtpConstants.Limits.MAX_BCRYPT_ROUNDS, "bcrypt rounds");
    }
}
