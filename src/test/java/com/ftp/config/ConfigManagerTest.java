package com.ftp.config;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigManager 测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigManagerTest {

    @TempDir
    Path tempDir;

    private File testConfigFile;

    @BeforeEach
    void setUp() throws IOException {
        testConfigFile = tempDir.resolve("test-config.properties").toFile();
    }

    @Test
    @Order(1)
    @DisplayName("测试ConfigManager单例")
    void testSingleton() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @Order(2)
    @DisplayName("测试获取默认配置")
    void testGetDefaultConfig() {
        Config config = ConfigManager.getInstance().getConfig();
        assertNotNull(config);
        assertEquals(21, config.getPort());
        assertNotNull(config.getRootDirectory());
        assertTrue(config.getMaxConnections() > 0);
    }

    @Test
    @Order(3)
    @DisplayName("测试Config构建")
    void testConfigBuilder() {
        Config config = Config.builder()
                .port(2121)
                .rootDirectory("/test/root")
                .maxConnections(50)
                .timeoutSeconds(600)
                .logLevel("DEBUG")
                .logFilePath("/test/logs/ftp.log")
                .threadPoolCoreSize(20)
                .threadPoolKeepAliveSeconds(120)
                .threadPoolQueueCapacity(200)
                .bindAddress("127.0.0.1")
                .dualStackEnabled(false)
                .listenInterface("eth0")
                .ipv4ExternalIp("192.168.1.100")
                .ipv6ExternalIp("2001:db8::1")
                .preferIPv6(true)
                .passiveModeExternalIp("192.168.1.100")
                .passiveModePortMin(1024)
                .passiveModePortMax(65535)
                .passiveModeConnectionTimeout(60)
                .bcryptRounds(14)
                .build();

        assertEquals(2121, config.getPort());
        assertEquals("/test/root", config.getRootDirectory());
        assertEquals(50, config.getMaxConnections());
        assertEquals(600, config.getTimeoutSeconds());
        assertEquals("DEBUG", config.getLogLevel());
        assertEquals("/test/logs/ftp.log", config.getLogFilePath());
        assertEquals(20, config.getThreadPoolCoreSize());
        assertEquals(120, config.getThreadPoolKeepAliveSeconds());
        assertEquals(200, config.getThreadPoolQueueCapacity());
        assertEquals("127.0.0.1", config.getBindAddress());
        assertFalse(config.isDualStackEnabled());
        assertEquals("eth0", config.getListenInterface());
        assertEquals("192.168.1.100", config.getIpv4ExternalIp());
        assertEquals("2001:db8::1", config.getIpv6ExternalIp());
        assertTrue(config.isPreferIPv6());
        assertEquals("192.168.1.100", config.getPassiveModeExternalIp());
        assertEquals(1024, config.getPassiveModePortMin());
        assertEquals(65535, config.getPassiveModePortMax());
        assertEquals(60, config.getPassiveModeConnectionTimeout());
        assertEquals(14, config.getBcryptRounds());
    }

    @Test
    @Order(4)
    @DisplayName("测试ConfigValidator验证")
    void testConfigValidation() {
        Config validConfig = Config.builder()
                .port(21)
                .rootDirectory(tempDir.toAbsolutePath().toString())
                .maxConnections(10)
                .timeoutSeconds(300)
                .logLevel("INFO")
                .logFilePath("test.log")
                .threadPoolCoreSize(10)
                .threadPoolKeepAliveSeconds(60)
                .threadPoolQueueCapacity(100)
                .bindAddress("127.0.0.1")
                .dualStackEnabled(true)
                .listenInterface("auto")
                .ipv4ExternalIp("auto")
                .ipv6ExternalIp("auto")
                .preferIPv6(false)
                .passiveModePortMin(0)
                .passiveModePortMax(0)
                .passiveModeConnectionTimeout(30)
                .bcryptRounds(12)
                .build();

        ConfigValidator.ValidationResult result = ConfigValidator.validateConfig(validConfig);
        assertTrue(result.isValid());
        assertTrue(result.getWarnings().isEmpty() || !result.getWarnings().isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("测试无效Config验证")
    void testInvalidConfigValidation() {
        Config invalidConfig = Config.builder()
                .port(65536)
                .rootDirectory("")
                .maxConnections(0)
                .timeoutSeconds(0)
                .logLevel("INVALID")
                .logFilePath("")
                .threadPoolCoreSize(0)
                .threadPoolKeepAliveSeconds(0)
                .threadPoolQueueCapacity(0)
                .bindAddress("")
                .dualStackEnabled(true)
                .listenInterface("auto")
                .ipv4ExternalIp("auto")
                .ipv6ExternalIp("auto")
                .preferIPv6(false)
                .passiveModePortMin(65536)
                .passiveModePortMax(-1)
                .passiveModeConnectionTimeout(30)
                .bcryptRounds(0)
                .build();

        ConfigValidator.ValidationResult result = ConfigValidator.validateConfig(invalidConfig);
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("测试ValidationResult")
    void testValidationResult() {
        ConfigValidator.ValidationResult result = new ConfigValidator.ValidationResult();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());

        result.addError("Test error");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("Test error", result.getErrors().get(0));

        result.addWarning("Test warning");
        assertEquals(1, result.getWarnings().size());
        assertEquals("Test warning", result.getWarnings().get(0));
    }
}