package com.ftp.config;

import com.ftp.constants.FtpConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigParser 测试")
class ConfigParserTest {

    private ConfigParser configParser;

    @BeforeEach
    void setUp() {
        configParser = new ConfigParser();
    }

    @Test
    @DisplayName("测试端口解析")
    void testParsePort() {
        assertEquals(21, configParser.parsePort("21"));
        assertEquals(2121, configParser.parsePort("2121"));
        assertEquals(FtpConstants.Defaults.DEFAULT_PORT, configParser.parsePort(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_PORT, configParser.parsePort(""));
        assertEquals(FtpConstants.Defaults.DEFAULT_PORT, configParser.parsePort("invalid"));
    }

    @Test
    @DisplayName("测试最大连接数解析")
    void testParseMaxConnections() {
        assertEquals(10, configParser.parseMaxConnections("10"));
        assertEquals(100, configParser.parseMaxConnections("100"));
        assertEquals(FtpConstants.Defaults.DEFAULT_MAX_CONNECTIONS, configParser.parseMaxConnections(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_MAX_CONNECTIONS, configParser.parseMaxConnections(""));
        assertEquals(FtpConstants.Defaults.DEFAULT_MAX_CONNECTIONS, configParser.parseMaxConnections("invalid"));
    }

    @Test
    @DisplayName("测试超时时间解析")
    void testParseTimeout() {
        assertEquals(300, configParser.parseTimeout("300"));
        assertEquals(600, configParser.parseTimeout("600"));
        assertEquals(FtpConstants.Defaults.DEFAULT_TIMEOUT_SECONDS, configParser.parseTimeout(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_TIMEOUT_SECONDS, configParser.parseTimeout(""));
        assertEquals(FtpConstants.Defaults.DEFAULT_TIMEOUT_SECONDS, configParser.parseTimeout("invalid"));
    }

    @Test
    @DisplayName("测试日志级别解析")
    void testParseLogLevel() {
        assertEquals("DEBUG", configParser.parseLogLevel("debug"));
        assertEquals("INFO", configParser.parseLogLevel("INFO"));
        assertEquals("WARN", configParser.parseLogLevel("warn"));
        assertEquals("ERROR", configParser.parseLogLevel("ERROR"));
        assertEquals(FtpConstants.Defaults.DEFAULT_LOG_LEVEL, configParser.parseLogLevel(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_LOG_LEVEL, configParser.parseLogLevel(""));
        assertEquals(FtpConstants.Defaults.DEFAULT_LOG_LEVEL, configParser.parseLogLevel("invalid"));
    }

    @Test
    @DisplayName("测试日志文件路径解析")
    void testParseLogFilePath() {
        assertEquals("logs/ftp-server.log", configParser.parseLogFilePath("logs/ftp-server.log"));
        assertEquals(FtpConstants.Defaults.DEFAULT_LOG_FILE_PATH, configParser.parseLogFilePath(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_LOG_FILE_PATH, configParser.parseLogFilePath(""));
    }

    @Test
    @DisplayName("测试根目录解析")
    void testParseRootDirectory() {
        assertEquals("ftp-root", configParser.parseRootDirectory("ftp-root"));
        assertEquals(FtpConstants.Defaults.DEFAULT_ROOT_DIRECTORY, configParser.parseRootDirectory(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_ROOT_DIRECTORY, configParser.parseRootDirectory(""));
    }

    @Test
    @DisplayName("测试线程池核心大小解析")
    void testParseThreadPoolCoreSize() {
        assertEquals(10, configParser.parseThreadPoolCoreSize("10"));
        assertEquals(50, configParser.parseThreadPoolCoreSize("50"));
        assertEquals(10, configParser.parseThreadPoolCoreSize(null));
        assertEquals(10, configParser.parseThreadPoolCoreSize(""));
        assertEquals(10, configParser.parseThreadPoolCoreSize("invalid"));
    }

    @Test
    @DisplayName("测试线程池保持时间解析")
    void testParseThreadPoolKeepAliveSeconds() {
        assertEquals(60, configParser.parseThreadPoolKeepAliveSeconds("60"));
        assertEquals(120, configParser.parseThreadPoolKeepAliveSeconds("120"));
        assertEquals(60, configParser.parseThreadPoolKeepAliveSeconds(null));
        assertEquals(60, configParser.parseThreadPoolKeepAliveSeconds(""));
        assertEquals(60, configParser.parseThreadPoolKeepAliveSeconds("invalid"));
    }

    @Test
    @DisplayName("测试线程池队列容量解析")
    void testParseThreadPoolQueueCapacity() {
        assertEquals(100, configParser.parseThreadPoolQueueCapacity("100"));
        assertEquals(500, configParser.parseThreadPoolQueueCapacity("500"));
        assertEquals(100, configParser.parseThreadPoolQueueCapacity(null));
        assertEquals(100, configParser.parseThreadPoolQueueCapacity(""));
        assertEquals(100, configParser.parseThreadPoolQueueCapacity("invalid"));
    }

    @Test
    @DisplayName("测试绑定地址解析")
    void testParseBindAddress() {
        assertEquals("127.0.0.1", configParser.parseBindAddress("127.0.0.1"));
        assertEquals("::", configParser.parseBindAddress("::"));
        assertEquals("::", configParser.parseBindAddress(null));
        assertEquals("::", configParser.parseBindAddress(""));
    }

    @Test
    @DisplayName("测试双栈启用解析")
    void testParseDualStackEnabled() {
        assertTrue(configParser.parseDualStackEnabled("true"));
        assertFalse(configParser.parseDualStackEnabled("false"));
        assertTrue(configParser.parseDualStackEnabled(null));
        assertTrue(configParser.parseDualStackEnabled(""));
    }

    @Test
    @DisplayName("测试监听接口解析")
    void testParseListenInterface() {
        assertEquals("eth0", configParser.parseListenInterface("eth0"));
        assertEquals("auto", configParser.parseListenInterface("auto"));
        assertEquals("auto", configParser.parseListenInterface(null));
        assertEquals("auto", configParser.parseListenInterface(""));
    }

    @Test
    @DisplayName("测试IPv4外部IP解析")
    void testParseIpv4ExternalIp() {
        assertEquals("192.168.1.100", configParser.parseIpv4ExternalIp("192.168.1.100"));
        assertEquals("auto", configParser.parseIpv4ExternalIp("auto"));
        assertEquals("auto", configParser.parseIpv4ExternalIp(null));
        assertEquals("auto", configParser.parseIpv4ExternalIp(""));
    }

    @Test
    @DisplayName("测试IPv6外部IP解析")
    void testParseIpv6ExternalIp() {
        assertEquals("2001:db8::1", configParser.parseIpv6ExternalIp("2001:db8::1"));
        assertEquals("auto", configParser.parseIpv6ExternalIp("auto"));
        assertEquals("auto", configParser.parseIpv6ExternalIp(null));
        assertEquals("auto", configParser.parseIpv6ExternalIp(""));
    }

    @Test
    @DisplayName("测试优先IPv6解析")
    void testParsePreferIPv6() {
        assertTrue(configParser.parsePreferIPv6("true"));
        assertFalse(configParser.parsePreferIPv6("false"));
        assertFalse(configParser.parsePreferIPv6(null));
        assertFalse(configParser.parsePreferIPv6(""));
    }

    @Test
    @DisplayName("测试被动模式外部IP解析")
    void testParsePassiveModeExternalIp() {
        assertEquals("192.168.1.100", configParser.parsePassiveModeExternalIp("192.168.1.100"));
        assertNull(configParser.parsePassiveModeExternalIp("auto"));
        assertNull(configParser.parsePassiveModeExternalIp(null));
        assertNull(configParser.parsePassiveModeExternalIp(""));
    }

    @Test
    @DisplayName("测试被动模式端口范围解析")
    void testParsePassiveModePortRange() {
        assertEquals(1024, configParser.parsePassiveModePortMin("1024"));
        assertEquals(65535, configParser.parsePassiveModePortMax("65535"));
        assertEquals(0, configParser.parsePassiveModePortMin(null));
        assertEquals(0, configParser.parsePassiveModePortMax(null));
        assertEquals(0, configParser.parsePassiveModePortMin(""));
        assertEquals(0, configParser.parsePassiveModePortMax(""));
        assertEquals(0, configParser.parsePassiveModePortMin("invalid"));
        assertEquals(0, configParser.parsePassiveModePortMax("invalid"));
    }

    @Test
    @DisplayName("测试被动模式连接超时解析")
    void testParsePassiveModeConnectionTimeout() {
        assertEquals(30, configParser.parsePassiveModeConnectionTimeout("30"));
        assertEquals(60, configParser.parsePassiveModeConnectionTimeout("60"));
        assertEquals(30, configParser.parsePassiveModeConnectionTimeout(null));
        assertEquals(30, configParser.parsePassiveModeConnectionTimeout(""));
        assertEquals(30, configParser.parsePassiveModeConnectionTimeout("invalid"));
    }

    @Test
    @DisplayName("测试BCrypt rounds解析")
    void testParseBcryptRounds() {
        assertEquals(12, configParser.parseBcryptRounds("12"));
        assertEquals(10, configParser.parseBcryptRounds("10"));
        assertEquals(FtpConstants.Defaults.DEFAULT_BCRYPT_ROUNDS, configParser.parseBcryptRounds(null));
        assertEquals(FtpConstants.Defaults.DEFAULT_BCRYPT_ROUNDS, configParser.parseBcryptRounds(""));
        assertEquals(FtpConstants.Defaults.DEFAULT_BCRYPT_ROUNDS, configParser.parseBcryptRounds("invalid"));
    }
}