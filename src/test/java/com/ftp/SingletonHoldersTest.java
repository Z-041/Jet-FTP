package com.ftp;

import com.ftp.auth.LoginAttemptManager;
import com.ftp.auth.UserManager;
import com.ftp.command.CommandFactory;
import com.ftp.config.ConfigManager;
import com.ftp.logging.Logger;
import com.ftp.util.IPFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Singleton Holder类测试")
public class SingletonHoldersTest {

    @Test
    @DisplayName("ConfigManager单例测试")
    public void testConfigManagerSingleton() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertSame(instance1, instance2, "ConfigManager应该返回相同的实例");
        assertNotNull(instance1.getConfig(), "Config不应该为null");
    }

    @Test
    @DisplayName("UserManager单例测试")
    public void testUserManagerSingleton() {
        UserManager instance1 = UserManager.getInstance();
        UserManager instance2 = UserManager.getInstance();
        assertSame(instance1, instance2, "UserManager应该返回相同的实例");
        assertNotNull(instance1.getAllUsers(), "用户列表不应该为null");
    }

    @Test
    @DisplayName("LoginAttemptManager单例测试")
    public void testLoginAttemptManagerSingleton() {
        LoginAttemptManager instance1 = LoginAttemptManager.getInstance();
        LoginAttemptManager instance2 = LoginAttemptManager.getInstance();
        assertSame(instance1, instance2, "LoginAttemptManager应该返回相同的实例");
    }

    @Test
    @DisplayName("CommandFactory单例测试")
    public void testCommandFactorySingleton() {
        CommandFactory instance1 = CommandFactory.getInstance();
        CommandFactory instance2 = CommandFactory.getInstance();
        assertSame(instance1, instance2, "CommandFactory应该返回相同的实例");
        assertNotNull(instance1.getCommand("HELP"), "应该能找到HELP命令");
    }

    @Test
    @DisplayName("Logger单例测试")
    public void testLoggerSingleton() {
        Logger instance1 = Logger.getInstance();
        Logger instance2 = Logger.getInstance();
        assertSame(instance1, instance2, "Logger应该返回相同的实例");
    }

    @Test
    @DisplayName("IPFilter单例测试")
    public void testIPFilterSingleton() {
        IPFilter instance1 = IPFilter.getInstance();
        IPFilter instance2 = IPFilter.getInstance();
        assertSame(instance1, instance2, "IPFilter应该返回相同的实例");
    }

    @Test
    @DisplayName("多个单例的集成测试")
    public void testMultipleSingletonsIntegration() {
        ConfigManager configManager = ConfigManager.getInstance();
        UserManager userManager = UserManager.getInstance();
        CommandFactory commandFactory = CommandFactory.getInstance();
        IPFilter ipFilter = IPFilter.getInstance();
        Logger logger = Logger.getInstance();
        LoginAttemptManager loginManager = LoginAttemptManager.getInstance();

        assertNotNull(configManager);
        assertNotNull(userManager);
        assertNotNull(commandFactory);
        assertNotNull(ipFilter);
        assertNotNull(logger);
        assertNotNull(loginManager);
    }
}
