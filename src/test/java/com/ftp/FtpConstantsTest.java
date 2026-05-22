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
        assertEquals("UTF-8", FtpConstants.ENCODING_UTF_8);
        assertEquals("ISO-8859-1", FtpConstants.ENCODING_ISO_8859_1);
        assertEquals(FtpConstants.ENCODING_UTF_8, FtpConstants.ENCODING_DEFAULT);
    }

    @Test
    @DisplayName("Defaults 常量测试")
    public void testDefaultsConstants() {
        assertEquals(21, FtpConstants.DEFAULT_PORT);
        assertEquals(10, FtpConstants.DEFAULT_MAX_CONNECTIONS);
        assertEquals(300, FtpConstants.DEFAULT_TIMEOUT_SECONDS);
        assertEquals("ftp-root", FtpConstants.DEFAULT_ROOT_DIRECTORY);
        assertEquals("INFO", FtpConstants.DEFAULT_LOG_LEVEL);
        assertEquals("logs/ftp-server.log", FtpConstants.DEFAULT_LOG_FILE_PATH);
        assertEquals(12, FtpConstants.DEFAULT_BCRYPT_ROUNDS);
    }

    @Test
    @DisplayName("Limits 常量测试")
    public void testLimitsConstants() {
        assertEquals(1, FtpConstants.LIMIT_MIN_PORT);
        assertEquals(65535, FtpConstants.LIMIT_MAX_PORT);
        assertEquals(1, FtpConstants.LIMIT_MIN_MAX_CONNECTIONS);
        assertEquals(1000, FtpConstants.LIMIT_MAX_MAX_CONNECTIONS);
        assertEquals(10, FtpConstants.LIMIT_MIN_TIMEOUT);
        assertEquals(3600, FtpConstants.LIMIT_MAX_TIMEOUT);
        assertEquals(4, FtpConstants.LIMIT_MIN_BCRYPT_ROUNDS);
        assertEquals(31, FtpConstants.LIMIT_MAX_BCRYPT_ROUNDS);
    }

    @Test
    @DisplayName("BufferSizes 常量测试")
    public void testBufferSizesConstants() {
        assertEquals(8192, FtpConstants.BUFFER_DEFAULT);
        assertEquals(1024, FtpConstants.BUFFER_SMALL);
        assertEquals(16384, FtpConstants.BUFFER_LARGE);
    }

    @Test
    @DisplayName("ConfigKeys 常量测试")
    public void testConfigKeysConstants() {
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_PORT);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_ROOT_DIRECTORY);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_MAX_CONNECTIONS);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_TIMEOUT_SECONDS);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_THREADPOOL_CORE_SIZE);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_THREADPOOL_KEEP_ALIVE_SECONDS);
        assertNotNull(FtpConstants.CONFIG_KEY_SERVER_THREADPOOL_QUEUE_CAPACITY);
    }

    @Test
    @DisplayName("ConfigFile 常量测试")
    public void testConfigFileConstants() {
        assertNotNull(FtpConstants.CONFIG_FILE_DEFAULT);
        assertNotNull(FtpConstants.CONFIG_FILE_DESCRIPTION);
    }

    @Test
    @DisplayName("LogLevels 常量测试")
    public void testLogLevelsConstants() {
        assertEquals("DEBUG", FtpConstants.LOG_LEVEL_DEBUG);
        assertEquals("INFO", FtpConstants.LOG_LEVEL_INFO);
        assertEquals("WARN", FtpConstants.LOG_LEVEL_WARN);
        assertEquals("ERROR", FtpConstants.LOG_LEVEL_ERROR);
        assertEquals("FATAL", FtpConstants.LOG_LEVEL_FATAL);
        assertNotNull(FtpConstants.LOG_LEVELS_VALID);
        assertEquals(5, FtpConstants.LOG_LEVELS_VALID.length);
    }

    @Test
    @DisplayName("FTP 常量测试")
    public void testFtpConstants() {
        assertEquals("Java FTP Server", FtpConstants.FTP_WELCOME_MESSAGE);
        assertEquals(12, FtpConstants.FTP_BCRYPT_ROUNDS);
        assertEquals("anonymous", FtpConstants.FTP_ANONYMOUS_USER);
    }

    @Test
    @DisplayName("ThreadPool 常量测试")
    public void testThreadPoolConstants() {
        assertEquals(10, FtpConstants.THREADPOOL_CORE_SIZE_FACTOR);
        assertEquals(60L, FtpConstants.THREADPOOL_KEEP_ALIVE_TIME_SECONDS);
        assertEquals(100, FtpConstants.THREADPOOL_WORK_QUEUE_CAPACITY);
    }

    @Test
    @DisplayName("ResponseCodes 常量测试")
    public void testResponseCodesConstants() {
        assertNotNull(FtpConstants.RESPONSE_CODE_150);
        assertNotNull(FtpConstants.RESPONSE_CODE_200);
        assertNotNull(FtpConstants.RESPONSE_CODE_226);
        assertNotNull(FtpConstants.RESPONSE_CODE_331);
        assertNotNull(FtpConstants.RESPONSE_CODE_426);
        assertNotNull(FtpConstants.RESPONSE_CODE_451);
        assertNotNull(FtpConstants.RESPONSE_CODE_500);
        assertNotNull(FtpConstants.RESPONSE_CODE_501);
        assertNotNull(FtpConstants.RESPONSE_CODE_502);
        assertNotNull(FtpConstants.RESPONSE_CODE_530);
        assertNotNull(FtpConstants.RESPONSE_CODE_550);
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