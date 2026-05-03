package com.ftp.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransferLog 测试")
class TransferLogTest {

    @Nested
    @DisplayName("构建器测试")
    class BuilderTests {

        @Test
        @DisplayName("使用 Builder 应能正确创建 TransferLog")
        void builderShouldCreateTransferLog() {
            TransferLog log = new TransferLog.Builder()
                    .username("testuser")
                    .fileName("test.txt")
                    .fileSize(1024)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(100)
                    .clientAddress("192.168.1.1")
                    .build();

            assertEquals("testuser", log.getUsername());
            assertEquals("test.txt", log.getFileName());
            assertEquals(1024, log.getFileSize());
            assertEquals(TransferLog.TransferType.DOWNLOAD, log.getTransferType());
            assertEquals(TransferLog.TransferStatus.SUCCESS, log.getStatus());
            assertEquals(100, log.getDurationMs());
            assertEquals("192.168.1.1", log.getClientAddress());
        }

        @Test
        @DisplayName("传输速度计算应正确")
        void transferSpeedCalculationShouldBeCorrect() {
            TransferLog log = new TransferLog.Builder()
                    .username("testuser")
                    .fileName("test.txt")
                    .fileSize(10000)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(1000)
                    .clientAddress("192.168.1.1")
                    .build();

            assertEquals(10000.0, log.getTransferSpeedBytesPerSecond(), 0.01);
        }

        @Test
        @DisplayName("零时长传输速度应为0")
        void zeroDurationShouldResultInZeroSpeed() {
            TransferLog log = new TransferLog.Builder()
                    .username("testuser")
                    .fileName("test.txt")
                    .fileSize(10000)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(0)
                    .clientAddress("192.168.1.1")
                    .build();

            assertEquals(0.0, log.getTransferSpeedBytesPerSecond(), 0.01);
        }
    }

    @Nested
    @DisplayName("格式化测试")
    class FormattingTests {

        @Test
        @DisplayName("格式化时间戳不应为空")
        void formattedTimestampShouldNotBeEmpty() {
            TransferLog log = new TransferLog.Builder()
                    .username("testuser")
                    .fileName("test.txt")
                    .fileSize(1024)
                    .transferType(TransferLog.TransferType.UPLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(500)
                    .clientAddress("192.168.1.1")
                    .build();

            assertNotNull(log.getFormattedTimestamp());
            assertFalse(log.getFormattedTimestamp().isEmpty());
        }

        @Test
        @DisplayName("日志消息格式化应包含所有信息")
        void logMessageShouldContainAllInfo() {
            TransferLog log = new TransferLog.Builder()
                    .username("ftpuser")
                    .fileName("document.pdf")
                    .fileSize(2048)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(200)
                    .clientAddress("10.0.0.1")
                    .build();

            String message = log.formatLogMessage();
            assertNotNull(message);
            assertTrue(message.contains("ftpuser"));
            assertTrue(message.contains("document.pdf"));
            assertTrue(message.contains("DOWNLOAD"));
            assertTrue(message.contains("SUCCESS"));
        }
    }

    @Nested
    @DisplayName("传输类型和状态枚举测试")
    class EnumTests {

        @Test
        @DisplayName("DOWNLOAD 类型应正确")
        void downloadTypeShouldBeCorrect() {
            TransferLog log = new TransferLog.Builder()
                    .username("user")
                    .fileName("file")
                    .fileSize(100)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(100)
                    .clientAddress("127.0.0.1")
                    .build();

            assertEquals(TransferLog.TransferType.DOWNLOAD, log.getTransferType());
        }

        @Test
        @DisplayName("UPLOAD 类型应正确")
        void uploadTypeShouldBeCorrect() {
            TransferLog log = new TransferLog.Builder()
                    .username("user")
                    .fileName("file")
                    .fileSize(100)
                    .transferType(TransferLog.TransferType.UPLOAD)
                    .status(TransferLog.TransferStatus.SUCCESS)
                    .durationMs(100)
                    .clientAddress("127.0.0.1")
                    .build();

            assertEquals(TransferLog.TransferType.UPLOAD, log.getTransferType());
        }

        @Test
        @DisplayName("FAILED 状态应正确")
        void failedStatusShouldBeCorrect() {
            TransferLog log = new TransferLog.Builder()
                    .username("user")
                    .fileName("file")
                    .fileSize(100)
                    .transferType(TransferLog.TransferType.DOWNLOAD)
                    .status(TransferLog.TransferStatus.FAILED)
                    .durationMs(100)
                    .clientAddress("127.0.0.1")
                    .build();

            assertEquals(TransferLog.TransferStatus.FAILED, log.getStatus());
        }
    }
}