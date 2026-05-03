package com.ftp.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginAttemptManager 测试")
class LoginAttemptManagerTest {

    private LoginAttemptManager loginAttemptManager;
    private InetAddress testAddress;

    @BeforeEach
    void setUp() throws UnknownHostException {
        loginAttemptManager = new LoginAttemptManager(3, 60000);
        testAddress = InetAddress.getByName("192.168.1.100");
    }

    @Nested
    @DisplayName("基础功能测试")
    class BasicFunctionTests {

        @Test
        @DisplayName("新 IP 不应被阻止")
        void newIpShouldNotBeBlocked() {
            assertFalse(loginAttemptManager.isBlocked(testAddress));
        }

        @Test
        @DisplayName("记录失败后应增加计数")
        void recordFailedAttemptShouldIncrementCount() {
            loginAttemptManager.recordFailedAttempt(testAddress);
            assertEquals(1, loginAttemptManager.getFailedAttempts(testAddress));
        }

        @Test
        @DisplayName("清除记录后计数应为0")
        void clearAttemptsShouldResetCount() {
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.clearAttempts(testAddress);
            assertEquals(0, loginAttemptManager.getFailedAttempts(testAddress));
        }
    }

    @Nested
    @DisplayName("IP 阻止功能测试")
    class IpBlockingTests {

        @Test
        @DisplayName("达到最大失败次数应被阻止")
        void ipShouldBeBlockedAfterMaxFailedAttempts() {
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);
            assertFalse(loginAttemptManager.isBlocked(testAddress));
            loginAttemptManager.recordFailedAttempt(testAddress);
            assertTrue(loginAttemptManager.isBlocked(testAddress));
        }

        @Test
        @DisplayName("成功登录后应清除阻止状态")
        void successfulLoginShouldClearBlockStatus() {
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);
            assertTrue(loginAttemptManager.isBlocked(testAddress));

            loginAttemptManager.clearAttempts(testAddress);
            assertFalse(loginAttemptManager.isBlocked(testAddress));
        }

        @Test
        @DisplayName("获取剩余锁定时间")
        void getRemainingLockoutTimeShouldReturnPositiveValue() {
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);
            loginAttemptManager.recordFailedAttempt(testAddress);

            long remainingTime = loginAttemptManager.getRemainingLockoutTimeMs(testAddress);
            assertTrue(remainingTime > 0);
        }

        @Test
        @DisplayName("未锁定的 IP 剩余时间应为0")
        void unblockedIpShouldHaveZeroRemainingTime() {
            assertEquals(0, loginAttemptManager.getRemainingLockoutTimeMs(testAddress));
        }
    }

    @Nested
    @DisplayName("单例模式测试")
    class SingletonTests {

        @Test
        @DisplayName("getInstance 应返回相同实例")
        void getInstanceShouldReturnSameInstance() {
            LoginAttemptManager instance1 = LoginAttemptManager.getInstance();
            LoginAttemptManager instance2 = LoginAttemptManager.getInstance();
            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("自定义配置测试")
    class CustomConfigTests {

        @Test
        @DisplayName("自定义最大尝试次数应生效")
        void customMaxAttemptsShouldWork() {
            LoginAttemptManager customManager = new LoginAttemptManager(5, 60000);
            for (int i = 0; i < 4; i++) {
                customManager.recordFailedAttempt(testAddress);
            }
            assertFalse(customManager.isBlocked(testAddress));
            customManager.recordFailedAttempt(testAddress);
            assertTrue(customManager.isBlocked(testAddress));
        }
    }
}