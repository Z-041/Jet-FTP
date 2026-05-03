package com.ftp.config;

import com.ftp.constants.FtpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigManager {

    private Config config;
    private final List<ConfigChangeListener> listeners;
    private final ConfigParser configParser;
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static class ConfigManagerHolder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    private ConfigManager() {
        this.configParser = new ConfigParser();
        this.config = loadConfig(FtpConstants.ConfigFile.DEFAULT_CONFIG_FILE);
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        listeners.add(listener);
        logger.info("Config change listener added: " + listener.getClass().getSimpleName());
    }

    public void removeConfigChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
        logger.info("Config change listener removed: " + listener.getClass().getSimpleName());
    }

    private void fireConfigChanged(Config oldConfig, Config newConfig) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChanged(oldConfig, newConfig);
            } catch (Exception e) {
                logger.error("Error notifying config change listener", e);
            }
        }
    }

    /**
     * 获取 ConfigManager 的单例实例
     * @return ConfigManager 实例
     */
    public static ConfigManager getInstance() {
        return ConfigManagerHolder.INSTANCE;
    }

    /**
     * 获取当前配置对象
     * @return Config配置对象
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 重新加载默认配置文件
     */
    public void reloadConfig() {
        this.config = loadConfig(FtpConstants.ConfigFile.DEFAULT_CONFIG_FILE);
    }

    /**
     * 重新加载指定的配置文件
     * @param configFilePath 配置文件路径
     */
    public void reloadConfig(String configFilePath) {
        this.config = loadConfig(configFilePath);
    }

    /**
     * 从指定路径加载配置
     * @param configFilePath 配置文件路径
     * @return Config 配置对象
     */
    private Config loadConfig(String configFilePath) {
        Properties properties = new Properties();
        File configFile = new File(configFilePath);

        try {
            if (configFile.exists()) {
                try (InputStream inputStream = new FileInputStream(configFile)) {
                    properties.load(inputStream);
                }
            } else {
                try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
                    if (inputStream != null) {
                        properties.load(inputStream);
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to load configuration file, using defaults: " + e.getMessage());
        }

        return buildConfig(properties);
    }

    /**
     * 从 Properties 对象构建 Config 对象
     * @param properties Properties 对象
     * @return Config 配置对象
     */
    private Config buildConfig(Properties properties) {
        int port = configParser.parsePort(properties.getProperty("server.port"));
        String rootDirectory = configParser.parseRootDirectory(properties.getProperty("server.rootDirectory"));
        int maxConnections = configParser.parseMaxConnections(properties.getProperty("server.maxConnections"));
        int timeoutSeconds = configParser.parseTimeout(properties.getProperty("server.timeoutSeconds"));
        String logLevel = configParser.parseLogLevel(properties.getProperty("log.level"));
        String logFilePath = configParser.parseLogFilePath(properties.getProperty("log.filePath"));
        int threadPoolCoreSize = configParser.parseThreadPoolCoreSize(properties.getProperty("server.threadPool.coreSize"));
        int threadPoolKeepAliveSeconds = configParser.parseThreadPoolKeepAliveSeconds(properties.getProperty("server.threadPool.keepAliveSeconds"));
        int threadPoolQueueCapacity = configParser.parseThreadPoolQueueCapacity(properties.getProperty("server.threadPool.queueCapacity"));
        String passiveModeExternalIp = configParser.parsePassiveModeExternalIp(properties.getProperty("passiveMode.externalIp"));
        int passiveModePortMin = configParser.parsePassiveModePortMin(properties.getProperty("passiveMode.portMin"));
        int passiveModePortMax = configParser.parsePassiveModePortMax(properties.getProperty("passiveMode.portMax"));
        int passiveModeConnectionTimeout = configParser.parsePassiveModeConnectionTimeout(properties.getProperty("passiveMode.connectionTimeout"));
        int bcryptRounds = configParser.parseBcryptRounds(properties.getProperty("security.bcryptRounds"));

        return Config.builder()
            .port(port)
            .rootDirectory(rootDirectory)
            .maxConnections(maxConnections)
            .timeoutSeconds(timeoutSeconds)
            .logLevel(logLevel)
            .logFilePath(logFilePath)
            .threadPoolCoreSize(threadPoolCoreSize)
            .threadPoolKeepAliveSeconds(threadPoolKeepAliveSeconds)
            .threadPoolQueueCapacity(threadPoolQueueCapacity)
            .passiveModeExternalIp(passiveModeExternalIp)
            .passiveModePortMin(passiveModePortMin)
            .passiveModePortMax(passiveModePortMax)
            .passiveModeConnectionTimeout(passiveModeConnectionTimeout)
            .bcryptRounds(bcryptRounds)
            .build();
    }

    /**
     * 保存配置到文件
     * @param config 要保存的配置对象
     */
    public void saveConfig(Config config) {
        saveConfig(config, FtpConstants.ConfigFile.DEFAULT_CONFIG_FILE);
    }

    /**
     * 保存配置到指定文件（带验证）
     * @param config 要保存的配置对象
     * @param configFilePath 配置文件路径
     * @return 验证结果
     */
    public ConfigValidator.ValidationResult saveConfig(Config config, String configFilePath) {
        ConfigValidator.ValidationResult validationResult = ConfigValidator.validateConfig(config);
        
        if (!validationResult.isValid()) {
            logger.error("配置验证失败，拒绝保存：" + validationResult);
            return validationResult;
        }

        if (!validationResult.getWarnings().isEmpty()) {
            logger.warn("配置验证存在警告，但仍将保存：" + validationResult.getWarnings());
        }

        Properties properties = new Properties();
        properties.setProperty(FtpConstants.ConfigKeys.SERVER_PORT, String.valueOf(config.getPort()));
        properties.setProperty(FtpConstants.ConfigKeys.SERVER_ROOT_DIRECTORY, config.getRootDirectory());
        properties.setProperty(FtpConstants.ConfigKeys.SERVER_MAX_CONNECTIONS, String.valueOf(config.getMaxConnections()));
        properties.setProperty(FtpConstants.ConfigKeys.SERVER_TIMEOUT_SECONDS, String.valueOf(config.getTimeoutSeconds()));
        properties.setProperty("server.threadPool.coreSize", String.valueOf(config.getThreadPoolCoreSize()));
        properties.setProperty("server.threadPool.keepAliveSeconds", String.valueOf(config.getThreadPoolKeepAliveSeconds()));
        properties.setProperty("server.threadPool.queueCapacity", String.valueOf(config.getThreadPoolQueueCapacity()));
        properties.setProperty(FtpConstants.ConfigKeys.LOG_LEVEL, config.getLogLevel());
        properties.setProperty(FtpConstants.ConfigKeys.LOG_FILE_PATH, config.getLogFilePath());
        properties.setProperty(FtpConstants.ConfigKeys.PASSIVE_MODE_EXTERNAL_IP, 
            config.getPassiveModeExternalIp() != null ? config.getPassiveModeExternalIp() : "auto");
        properties.setProperty(FtpConstants.ConfigKeys.PASSIVE_MODE_PORT_MIN, 
            String.valueOf(config.getPassiveModePortMin()));
        properties.setProperty(FtpConstants.ConfigKeys.PASSIVE_MODE_PORT_MAX, 
            String.valueOf(config.getPassiveModePortMax()));
        properties.setProperty(FtpConstants.ConfigKeys.PASSIVE_MODE_CONNECTION_TIMEOUT, 
            String.valueOf(config.getPassiveModeConnectionTimeout()));

        try (OutputStream outputStream = new FileOutputStream(configFilePath)) {
            properties.store(outputStream, FtpConstants.ConfigFile.CONFIG_DESCRIPTION);
            Config oldConfig = this.config;
            this.config = config;
            fireConfigChanged(oldConfig, config);
            logger.info("Configuration saved successfully");
        } catch (IOException e) {
            logger.error("Error saving configuration file: " + e.getMessage());
            validationResult.addError("保存配置文件失败：" + e.getMessage());
        }

        return validationResult;
    }
}
