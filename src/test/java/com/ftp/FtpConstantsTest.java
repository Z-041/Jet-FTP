package com.ftp;

import com.ftp.constants.FtpConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FtpConstants 常量类测试")
public class FtpConstantsTest {

    @Test
    @DisplayName("Encoding 常量测试")
    public void testEncodingConstants() {
        assertEquals("UTF-8", FtpConstants.Encoding.UTF_8);
        assertEquals("ISO-8859-1", FtpConstants.Encoding.ISO_8859_1);
        assertEquals(FtpConstants.Encoding.UTF_8, FtpConstants.Encoding.DEFAULT_ENCODING);
    }

    @Test
    @DisplayName("Defaults 常量测试")
    public void testDefaultsConstants() {
        assertEquals(21, FtpConstants.Defaults.DEFAULT_PORT);
        assertEquals(10, FtpConstants.Defaults.DEFAULT_MAX_CONNECTIONS);
        assertEquals(300, FtpConstants.Defaults.DEFAULT_TIMEOUT_SECONDS);
        assertEquals("ftp-root", FtpConstants.Defaults.DEFAULT_ROOT_DIRECTORY);
        assertEquals("INFO", FtpConstants.Defaults.DEFAULT_LOG_LEVEL);
        assertEquals("logs/ftp-server.log", FtpConstants.Defaults.DEFAULT_LOG_FILE_PATH);
        assertEquals(12, FtpConstants.Defaults.DEFAULT_BCRYPT_ROUNDS);
    }

    @Test
    @DisplayName("Limits 常量测试")
    public void testLimitsConstants() {
        assertEquals(1, FtpConstants.Limits.MIN_PORT);
        assertEquals(65535, FtpConstants.Limits.MAX_PORT);
        assertEquals(1, FtpConstants.Limits.MIN_MAX_CONNECTIONS);
        assertEquals(1000, FtpConstants.Limits.MAX_MAX_CONNECTIONS);
        assertEquals(10, FtpConstants.Limits.MIN_TIMEOUT);
        assertEquals(3600, FtpConstants.Limits.MAX_TIMEOUT);
        assertEquals(4, FtpConstants.Limits.MIN_BCRYPT_ROUNDS);
        assertEquals(31, FtpConstants.Limits.MAX_BCRYPT_ROUNDS);
    }

    @Test
    @DisplayName("BufferSizes 常量测试")
    public void testBufferSizesConstants() {
        assertEquals(8192, FtpConstants.BufferSizes.DEFAULT_BUFFER_SIZE);
        assertEquals(1024, FtpConstants.BufferSizes.SMALL_BUFFER_SIZE);
        assertEquals(16384, FtpConstants.BufferSizes.LARGE_BUFFER_SIZE);
    }

    @Test
    @DisplayName("ConfigKeys 常量测试")
    public void testConfigKeysConstants() {
        assertNotNull(FtpConstants.ConfigKeys.SERVER_PORT);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_ROOT_DIRECTORY);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_MAX_CONNECTIONS);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_TIMEOUT_SECONDS);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_THREADPOOL_CORE_SIZE);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_THREADPOOL_KEEP_ALIVE_SECONDS);
        assertNotNull(FtpConstants.ConfigKeys.SERVER_THREADPOOL_QUEUE_CAPACITY);
    }

    @Test
    @DisplayName("ConfigFile 常量测试")
    public void testConfigFileConstants() {
        assertNotNull(FtpConstants.ConfigFile.DEFAULT_CONFIG_FILE);
        assertNotNull(FtpConstants.ConfigFile.CONFIG_DESCRIPTION);
    }

    @Test
    @DisplayName("LogLevels 常量测试")
    public void testLogLevelsConstants() {
        assertEquals("DEBUG", FtpConstants.LogLevels.DEBUG);
        assertEquals("INFO", FtpConstants.LogLevels.INFO);
        assertEquals("WARN", FtpConstants.LogLevels.WARN);
        assertEquals("ERROR", FtpConstants.LogLevels.ERROR);
        assertEquals("FATAL", FtpConstants.LogLevels.FATAL);
        assertNotNull(FtpConstants.LogLevels.VALID_LEVELS);
        assertEquals(5, FtpConstants.LogLevels.VALID_LEVELS.length);
    }

    @Test
    @DisplayName("FTP 常量测试")
    public void testFtpConstants() {
        assertEquals("Java FTP Server", FtpConstants.FTP.WELCOME_MESSAGE);
        assertEquals(12, FtpConstants.FTP.BCRYPT_ROUNDS);
        assertEquals("anonymous", FtpConstants.FTP.ANONYMOUS_USER);
    }

    @Test
    @DisplayName("ThreadPool 常量测试")
    public void testThreadPoolConstants() {
        assertEquals(10, FtpConstants.ThreadPool.CORE_POOL_SIZE_FACTOR);
        assertEquals(60L, FtpConstants.ThreadPool.KEEP_ALIVE_TIME_SECONDS);
        assertEquals(100, FtpConstants.ThreadPool.WORK_QUEUE_CAPACITY);
    }

    @Test
    @DisplayName("ResponseCodes 常量测试")
    public void testResponseCodesConstants() {
        assertNotNull(FtpConstants.ResponseCodes.CODE_150);
        assertNotNull(FtpConstants.ResponseCodes.CODE_200);
        assertNotNull(FtpConstants.ResponseCodes.CODE_226);
        assertNotNull(FtpConstants.ResponseCodes.CODE_331);
        assertNotNull(FtpConstants.ResponseCodes.CODE_426);
        assertNotNull(FtpConstants.ResponseCodes.CODE_451);
        assertNotNull(FtpConstants.ResponseCodes.CODE_500);
        assertNotNull(FtpConstants.ResponseCodes.CODE_501);
        assertNotNull(FtpConstants.ResponseCodes.CODE_502);
        assertNotNull(FtpConstants.ResponseCodes.CODE_530);
        assertNotNull(FtpConstants.ResponseCodes.CODE_550);
    }

    @Test
    @DisplayName("常量类有私有构造函数测试")
    public void testConstantsClassHasPrivateConstructor() {
        try {
            java.lang.reflect.Constructor<?> constructor = 
                FtpConstants.class.getDeclaredConstructor();
            assertFalse(constructor.isAccessible(), "构造函数应该是私有的");
        } catch (NoSuchMethodException e) {
            fail("应该有一个构造函数");
        }
    }
}
