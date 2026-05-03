package com.ftp.ui;

import com.ftp.logging.LogLevel;

public interface LogListener {
    void onLog(LogLevel level, String message, Throwable throwable);
}
