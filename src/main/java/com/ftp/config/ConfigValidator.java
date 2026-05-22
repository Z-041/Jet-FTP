package com.ftp.config;

import com.ftp.constants.FtpConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigValidator {

    public static ValidationResult validateConfig(Config config) {
        ValidationResult result = new ValidationResult();

        if (config == null) {
            result.addError("配置对象不能为空");
            return result;
        }

        validatePort(config.getPort(), result);
        validateRootDirectory(config.getRootDirectory(), result);
        validateMaxConnections(config.getMaxConnections(), result);
        validateTimeout(config.getTimeoutSeconds(), result);
        validateThreadPoolConfig(config, result);
        validateBindAddress(config.getBindAddress(), result);
        validateLogLevel(config.getLogLevel(), result);
        validateLogFilePath(config.getLogFilePath(), result);
        validatePassiveModeConfig(config, result);
        validateBcryptRounds(config.getBcryptRounds(), result);

        return result;
    }
    
    private static void validateThreadPoolConfig(Config config, ValidationResult result) {
        int coreSize = config.getThreadPoolCoreSize();
        int keepAliveSeconds = config.getThreadPoolKeepAliveSeconds();
        int queueCapacity = config.getThreadPoolQueueCapacity();
        
        if (coreSize < 1) {
            result.addError(String.format("线程池核心大小必须大于0，当前值：%d", coreSize));
        }
        
        if (keepAliveSeconds < 1) {
            result.addError(String.format("线程池保持时间必须大于0，当前值：%d", keepAliveSeconds));
        }
        
        if (queueCapacity < 1) {
            result.addError(String.format("线程池队列容量必须大于0，当前值：%d", queueCapacity));
        }
    }
    
    private static void validateBindAddress(String bindAddress, ValidationResult result) {
        if (bindAddress == null || bindAddress.trim().isEmpty()) {
            result.addError("绑定地址不能为空");
            return;
        }
        
        if (bindAddress.contains("\0")) {
            result.addError("绑定地址包含非法字符");
        }
    }
    
    private static void validateBcryptRounds(int rounds, ValidationResult result) {
        if (rounds < FtpConstants.Limits.MIN_BCRYPT_ROUNDS || rounds > FtpConstants.Limits.MAX_BCRYPT_ROUNDS) {
            result.addError(String.format("BCrypt rounds 必须在 %d-%d 之间，当前值：%d",
                FtpConstants.Limits.MIN_BCRYPT_ROUNDS,
                FtpConstants.Limits.MAX_BCRYPT_ROUNDS,
                rounds));
        }
    }

    private static void validatePort(int port, ValidationResult result) {
        if (port < FtpConstants.Limits.MIN_PORT || port > FtpConstants.Limits.MAX_PORT) {
            result.addError(String.format("端口号必须在 %d-%d 之间，当前值：%d",
                FtpConstants.Limits.MIN_PORT,
                FtpConstants.Limits.MAX_PORT,
                port));
            return; // 如果端口号无效，不要尝试创建ServerSocket
        }

        try {
            java.net.ServerSocket socket = new java.net.ServerSocket(port);
            socket.close();
        } catch (java.io.IOException e) {
            result.addWarning(String.format("端口 %d 可能已被占用", port));
        }
    }

    private static void validateRootDirectory(String rootDirectory, ValidationResult result) {
        if (rootDirectory == null || rootDirectory.trim().isEmpty()) {
            result.addError("根目录不能为空");
            return;
        }

        File dir = new File(rootDirectory);

        if (rootDirectory.contains("\0")) {
            result.addError("根目录路径包含非法字符");
            return;
        }

        if (!dir.exists()) {
            try {
                if (!dir.mkdirs()) {
                    result.addError(String.format("无法创建根目录：%s", rootDirectory));
                }
            } catch (SecurityException e) {
                result.addError(String.format("没有权限创建根目录：%s", rootDirectory));
            }
        }

        if (dir.exists() && !dir.isDirectory()) {
            result.addError(String.format("根目录路径指向一个文件而非目录：%s", rootDirectory));
        }
    }

    private static void validateMaxConnections(int maxConnections, ValidationResult result) {
        if (maxConnections < FtpConstants.Limits.MIN_MAX_CONNECTIONS || maxConnections > FtpConstants.Limits.MAX_MAX_CONNECTIONS) {
            result.addError(String.format("最大连接数必须在 %d-%d 之间，当前值：%d",
                FtpConstants.Limits.MIN_MAX_CONNECTIONS,
                FtpConstants.Limits.MAX_MAX_CONNECTIONS,
                maxConnections));
        }
    }

    private static void validateTimeout(int timeout, ValidationResult result) {
        if (timeout < FtpConstants.Limits.MIN_TIMEOUT || timeout > FtpConstants.Limits.MAX_TIMEOUT) {
            result.addError(String.format("超时时间必须在 %d-%d 秒之间，当前值：%d",
                FtpConstants.Limits.MIN_TIMEOUT,
                FtpConstants.Limits.MAX_TIMEOUT,
                timeout));
        }
    }

    private static void validateLogLevel(String logLevel, ValidationResult result) {
        if (logLevel == null || logLevel.trim().isEmpty()) {
            result.addError("日志级别不能为空");
            return;
        }

        boolean valid = false;
        for (String level : FtpConstants.LogLevels.VALID_LEVELS) {
            if (level.equalsIgnoreCase(logLevel.trim())) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            result.addError(String.format("无效的日志级别：%s，有效值为：%s",
                logLevel, String.join(", ", FtpConstants.LogLevels.VALID_LEVELS)));
        }
    }

    private static void validateLogFilePath(String logFilePath, ValidationResult result) {
        if (logFilePath == null || logFilePath.trim().isEmpty()) {
            result.addWarning("日志文件路径未设置，将使用默认路径");
            return;
        }

        File logFile = new File(logFilePath);
        File parentDir = logFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            result.addWarning(String.format("日志文件目录不存在，将尝试创建：%s", parentDir.getAbsolutePath()));
        }
    }

    private static void validatePassiveModeConfig(Config config, ValidationResult result) {
        int minPort = config.getPassiveModePortMin();
        int maxPort = config.getPassiveModePortMax();

        if (minPort < 0 || minPort > 65535) {
            result.addError(String.format("被动模式最小端口号无效：%d", minPort));
        }

        if (maxPort < 0 || maxPort > 65535) {
            result.addError(String.format("被动模式最大端口号无效：%d", maxPort));
        }

        if (minPort > 0 && maxPort > 0 && minPort > maxPort) {
            result.addError(String.format("被动模式端口范围无效：最小端口 %d 大于最大端口 %d", minPort, maxPort));
        }
    }

    public static class ValidationResult {
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult() {
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }
    }
}
