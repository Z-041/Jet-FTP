package com.ftp.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FtpException 层次测试")
class FtpExceptionTest {

    @Nested
    @DisplayName("FtpException 基本测试")
    class BasicFtpExceptionTests {

        @Test
        @DisplayName("应正确设置回复码和消息")
        void shouldSetReplyCodeAndMessage() {
            FtpException exception = new FtpException(550, "File not found");
            assertEquals(550, exception.getReplyCode());
            assertEquals("File not found", exception.getMessage());
        }

        @Test
        @DisplayName("应支持异常链")
        void shouldSupportExceptionChaining() {
            RuntimeException cause = new RuntimeException("Original error");
            FtpException exception = new FtpException(550, "File not found", cause);
            assertEquals(cause, exception.getCause());
            assertEquals("File not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("FtpCommandException 测试")
    class FtpCommandExceptionTests {

        @Test
        @DisplayName("应继承FtpException")
        void shouldExtendFtpException() {
            FtpCommandException exception = new FtpCommandException(501, "Invalid syntax");
            assertTrue(exception instanceof FtpException);
        }

        @Test
        @DisplayName("应正确传递回复码")
        void shouldPassReplyCode() {
            FtpCommandException exception = new FtpCommandException(553, "Permission denied");
            assertEquals(553, exception.getReplyCode());
        }
    }

    @Nested
    @DisplayName("PathTraversalException 测试")
    class PathTraversalExceptionTests {

        @Test
        @DisplayName("默认回复码应为550")
        void defaultReplyCodeShouldBe550() {
            PathTraversalException exception = new PathTraversalException("../etc/passwd");
            assertEquals(550, exception.getReplyCode());
        }

        @Test
        @DisplayName("应记录请求的路径")
        void shouldRecordRequestedPath() {
            PathTraversalException exception = new PathTraversalException("/etc/passwd");
            assertEquals("/etc/passwd", exception.getRequestedPath());
        }

        @Test
        @DisplayName("自定义消息测试")
        void customMessageTest() {
            PathTraversalException exception = new PathTraversalException("/etc/passwd", "Access denied");
            assertEquals("Access denied", exception.getMessage());
            assertEquals("/etc/passwd", exception.getRequestedPath());
        }
    }

    @Nested
    @DisplayName("FtpDataConnectionException 测试")
    class FtpDataConnectionExceptionTests {

        @Test
        @DisplayName("静态工厂方法connectionFailed")
        void connectionFailedFactory() {
            FtpDataConnectionException exception = FtpDataConnectionException.connectionFailed("timeout");
            assertEquals(425, exception.getReplyCode());
            assertTrue(exception.getMessage().contains("timeout"));
        }

        @Test
        @DisplayName("静态工厂方法connectionTimeout")
        void connectionTimeoutFactory() {
            FtpDataConnectionException exception = FtpDataConnectionException.connectionTimeout();
            assertEquals(426, exception.getReplyCode());
            assertTrue(exception.getMessage().contains("timed out"));
        }

        @Test
        @DisplayName("静态工厂方法transferAborted")
        void transferAbortedFactory() {
            FtpDataConnectionException exception = FtpDataConnectionException.transferAborted("disk full");
            assertEquals(426, exception.getReplyCode());
            assertTrue(exception.getMessage().contains("disk full"));
        }
    }

    @Nested
    @DisplayName("AuthenticationException 测试")
    class AuthenticationExceptionTests {

        @Test
        @DisplayName("默认回复码应为530")
        void defaultReplyCodeShouldBe530() {
            AuthenticationException exception = new AuthenticationException("Not logged in");
            assertEquals(530, exception.getReplyCode());
        }

        @Test
        @DisplayName("静态工厂方法notLoggedIn")
        void notLoggedInFactory() {
            AuthenticationException exception = AuthenticationException.notLoggedIn();
            assertEquals("Not logged in", exception.getMessage());
        }

        @Test
        @DisplayName("静态工厂方法invalidPassword")
        void invalidPasswordFactory() {
            AuthenticationException exception = AuthenticationException.invalidPassword();
            assertEquals("Invalid password", exception.getMessage());
        }

        @Test
        @DisplayName("静态工厂方法userNotFound")
        void userNotFoundFactory() {
            AuthenticationException exception = AuthenticationException.userNotFound("john");
            assertTrue(exception.getMessage().contains("john"));
        }

        @Test
        @DisplayName("静态工厂方法homeDirectoryDenied")
        void homeDirectoryDeniedFactory() {
            AuthenticationException exception = AuthenticationException.homeDirectoryDenied();
            assertEquals("Home directory access denied", exception.getMessage());
        }

        @Test
        @DisplayName("静态工厂方法accountRequired")
        void accountRequiredFactory() {
            AuthenticationException exception = AuthenticationException.accountRequired();
            assertEquals("Account required for login", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("异常层次结构验证")
    class ExceptionHierarchyTests {

        @Test
        @DisplayName("所有异常应继承FtpException")
        void allExceptionsShouldExtendFtpException() {
            assertTrue(new FtpCommandException(500, "test") instanceof FtpException);
            assertTrue(new PathTraversalException("test") instanceof FtpException);
            assertTrue(new FtpDataConnectionException(425, "test") instanceof FtpException);
            assertTrue(new AuthenticationException("test") instanceof FtpException);
        }

        @Test
        @DisplayName("FtpCommandException应继承FtpException")
        void commandExceptionShouldExtendFtpException() {
            assertTrue(new FtpCommandException(500, "test") instanceof FtpException);
        }

        @Test
        @DisplayName("PathTraversalException应继承FtpCommandException")
        void pathTraversalExceptionShouldExtendCommandException() {
            assertTrue(new PathTraversalException("test") instanceof FtpCommandException);
        }
    }
}
