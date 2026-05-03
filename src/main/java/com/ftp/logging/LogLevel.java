package com.ftp.logging;

public enum LogLevel {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3);

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static LogLevel fromString(String levelStr) {
        if (levelStr == null) {
            return INFO;
        }
        try {
            return valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return INFO;
        }
    }

    public boolean isEnabledFor(LogLevel minLevel) {
        return this.level >= minLevel.level;
    }
}
