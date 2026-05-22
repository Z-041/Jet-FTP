package com.ftp.logging;

import com.ftp.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TransferLog {
    private static final Logger logger = LoggerFactory.getLogger("TransferLog");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String username;
    private final String fileName;
    private final long fileSize;
    private final TransferType transferType;
    private final TransferStatus status;
    private final long durationMs;
    private final String clientAddress;
    private final Instant timestamp;

    public enum TransferType {
        DOWNLOAD,
        UPLOAD
    }

    public enum TransferStatus {
        SUCCESS,
        FAILED,
        CANCELLED
    }

    public TransferLog(String username, String fileName, long fileSize, TransferType transferType,
                       TransferStatus status, long durationMs, String clientAddress) {
        this.username = username;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.transferType = transferType;
        this.status = status;
        this.durationMs = durationMs;
        this.clientAddress = clientAddress;
        this.timestamp = Instant.now();
    }

    public String getUsername() {
        return username;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getTransferSpeedBytesPerSecond() {
        if (durationMs <= 0) {
            return 0;
        }
        return (double) fileSize / durationMs * 1000;
    }

    public String getFormattedTimestamp() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        return dateTime.format(FORMATTER);
    }

    public void log() {
        boolean detailed = false;
        try {
            detailed = ConfigManager.getInstance().getConfig().isDetailedTransferLog();
        } catch (Exception e) {
            // Ignore configuration issue
        }
        
        // Always log failed and cancelled transfers
        if (status != TransferStatus.SUCCESS) {
            String logMessage = formatLogMessage();
            switch (status) {
                case FAILED:
                    logger.error(logMessage);
                    break;
                case CANCELLED:
                    logger.warn(logMessage);
                    break;
            }
            return;
        }
        
        // For successful transfers, check if detailed logging is enabled
        if (detailed) {
            String logMessage = formatLogMessage();
            logger.info(logMessage);
        } else {
            // Simple log format for success
            String simpleMsg = String.format("%s %s completed: %s", 
                transferType == TransferType.DOWNLOAD ? "Download" : "Upload",
                username,
                fileName);
            logger.info(simpleMsg);
        }
    }

    public String formatLogMessage() {
        String speed = formatSpeed(getTransferSpeedBytesPerSecond());
        String size = formatFileSize(fileSize);
        String duration = formatDuration(durationMs);

        return String.format("%s [%s] %s %s (%s) from/to %s - %s in %s (%s/s)",
                getFormattedTimestamp(),
                username,
                transferType == TransferType.DOWNLOAD ? "DOWNLOAD" : "UPLOAD",
                fileName,
                size,
                clientAddress,
                status,
                duration,
                speed);
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    private String formatSpeed(double bytesPerSecond) {
        if (bytesPerSecond < 1024) {
            return String.format("%.2f B/s", bytesPerSecond);
        } else if (bytesPerSecond < 1024 * 1024) {
            return String.format("%.2f KB/s", bytesPerSecond / 1024);
        } else {
            return String.format("%.2f MB/s", bytesPerSecond / (1024 * 1024));
        }
    }

    private String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + " ms";
        } else if (milliseconds < 60 * 1000) {
            return String.format("%.2f s", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / (60 * 1000);
            long seconds = (milliseconds % (60 * 1000)) / 1000;
            return String.format("%d min %d s", minutes, seconds);
        }
    }

    public static class Builder {
        private String username;
        private String fileName;
        private long fileSize;
        private TransferType transferType;
        private TransferStatus status;
        private long durationMs;
        private String clientAddress;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder file(File file) {
            this.fileName = file.getName();
            this.fileSize = file.length();
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder transferType(TransferType transferType) {
            this.transferType = transferType;
            return this;
        }

        public Builder status(TransferStatus status) {
            this.status = status;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder clientAddress(String clientAddress) {
            this.clientAddress = clientAddress;
            return this;
        }

        public TransferLog build() {
            return new TransferLog(username, fileName, fileSize, transferType, status, durationMs, clientAddress);
        }
    }
}