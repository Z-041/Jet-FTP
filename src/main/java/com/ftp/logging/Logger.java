package com.ftp.logging;

import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.ui.LogListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logger {

    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int DEFAULT_MAX_BACKUP_INDEX = 5;

    private volatile LogLevel currentLevel;
    private volatile String logFilePath;
    private final ThreadLocal<SimpleDateFormat> dateFormat;
    private final List<LogListener> listeners;
    private final long maxFileSize;
    private final int maxBackupIndex;
    private final Object fileLock = new Object();
    private PrintWriter cachedWriter;
    private File currentLogFile;

    private static class LoggerHolder {
        private static final Logger INSTANCE = new Logger();
    }

    private Logger() {
        Config config = ConfigManager.getInstance().getConfig();
        this.currentLevel = LogLevel.fromString(config.getLogLevel());
        this.logFilePath = config.getLogFilePath();
        this.dateFormat = ThreadLocal.withInitial(() -> 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        this.listeners = new CopyOnWriteArrayList<>();
        this.maxFileSize = DEFAULT_MAX_FILE_SIZE;
        this.maxBackupIndex = DEFAULT_MAX_BACKUP_INDEX;
        initializeLogFile();
    }

    private void initializeLogFile() {
        String currentPath = this.logFilePath;
        if (currentPath == null || currentPath.trim().isEmpty()) {
            return;
        }
        
        File logFile = new File(currentPath);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        this.currentLogFile = logFile;
    }

    public void addLogListener(LogListener listener) {
        listeners.add(listener);
    }

    public void removeLogListener(LogListener listener) {
        listeners.remove(listener);
    }

    public static Logger getInstance() {
        return LoggerHolder.INSTANCE;
    }

    public void setLogLevel(LogLevel level) {
        this.currentLevel = level;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, message, throwable);
    }

    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public void info(String message, Throwable throwable) {
        log(LogLevel.INFO, message, throwable);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        if (!level.isEnabledFor(currentLevel)) {
            return;
        }

        String logMessage = formatLogMessage(level, message);

        outputToConsole(level, logMessage, throwable);

        String currentPath = this.logFilePath;
        if (currentPath != null && !currentPath.trim().isEmpty()) {
            outputToFile(logMessage, throwable);
        }

        notifyListeners(level, message, throwable);
    }

    private void notifyListeners(LogLevel level, String message, Throwable throwable) {
        for (LogListener listener : listeners) {
            try {
                listener.onLog(level, message, throwable);
            } catch (Exception e) {
                System.err.println("Error notifying log listener: " + e.getMessage());
            }
        }
    }

    private String formatLogMessage(LogLevel level, String message) {
        String timestamp = dateFormat.get().format(new Date());
        return String.format("[%s] [%s] %s", timestamp, level, message);
    }

    private void outputToConsole(LogLevel level, String message, Throwable throwable) {
        switch (level) {
            case ERROR:
            case WARN:
                System.err.println(message);
                if (throwable != null) {
                    throwable.printStackTrace(System.err);
                }
                break;
            default:
                System.out.println(message);
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
                break;
        }
    }

    private void outputToFile(String message, Throwable throwable) {
        synchronized (fileLock) {
            if (currentLogFile == null) {
                return;
            }

            if (currentLogFile.exists() && currentLogFile.length() >= maxFileSize) {
                rotateLogFiles(currentLogFile);
                closeCachedWriter();
                initializeLogFile();
            }

            PrintWriter writer = getCachedWriter();
            if (writer != null) {
                writer.println(message);
                if (throwable != null) {
                    throwable.printStackTrace(writer);
                }
                writer.flush();
            }
        }
    }

    private PrintWriter getCachedWriter() {
        if (cachedWriter == null && currentLogFile != null) {
            try {
                cachedWriter = new PrintWriter(new BufferedWriter(new FileWriter(currentLogFile, true)));
            } catch (IOException e) {
                System.err.println("Failed to create log writer: " + e.getMessage());
                return null;
            }
        }
        return cachedWriter;
    }

    private void closeCachedWriter() {
        if (cachedWriter != null) {
            cachedWriter.close();
            cachedWriter = null;
        }
    }

    private void rotateLogFiles(File logFile) {
        try {
            for (int i = maxBackupIndex - 1; i >= 1; i--) {
                File oldFile = new File(logFile.getParentFile(), logFile.getName() + "." + i);
                if (oldFile.exists()) {
                    File newFile = new File(logFile.getParentFile(), logFile.getName() + "." + (i + 1));
                    Files.move(oldFile.toPath(), newFile.toPath());
                }
            }
            
            File backupFile = new File(logFile.getParentFile(), logFile.getName() + ".1");
            Files.move(logFile.toPath(), backupFile.toPath());
            
            System.out.println("Log file rotated: " + logFile.getName());
        } catch (IOException e) {
            System.err.println("Failed to rotate log file: " + e.getMessage());
        }
    }
}
