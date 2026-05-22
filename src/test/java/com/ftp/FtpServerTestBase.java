package com.ftp;

import com.ftp.auth.UserManager;
import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.model.User;
import com.ftp.server.FtpServer;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FtpServerTestBase {
    
    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "testpass";
    
    protected FtpServer server;
    protected FTPClient ftpClient;
    protected int testPort;
    
    @TempDir
    protected Path tempRootDir;
    
    @BeforeEach
    protected void setUp() throws Exception {
        testPort = 2121;
        
        Config testConfig = Config.builder()
                .port(testPort)
                .rootDirectory(tempRootDir.toAbsolutePath().toString())
                .maxConnections(5)
                .timeoutSeconds(60)
                .logLevel("OFF")
                .logFilePath("")
                .dualStackEnabled(true)
                .bindAddress("127.0.0.1")
                .bcryptRounds(4)
                .build();
        
        ConfigManager.getInstance().setConfig(testConfig);
        
        // 添加测试用户
        String hashedPassword = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt(4));
        User testUser = new User(TEST_USERNAME, hashedPassword, tempRootDir.toAbsolutePath().toString());
        UserManager.getInstance().addUser(testUser);
        
        // 添加匿名用户
        User anonymousUser = new User("anonymous", "", tempRootDir.toAbsolutePath().toString())
                .withAnonymous(true);
        UserManager.getInstance().addUser(anonymousUser);
        
        server = new FtpServer(testConfig);
        server.start();
        
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(5000);
        ftpClient.connect("127.0.0.1", testPort);
    }
    
    @AfterEach
    protected void tearDown() throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
            } catch (IOException e) {
                // Ignore
            }
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // Ignore
            }
        }
        
        if (server != null) {
            server.stop();
        }
    }
    
    protected Path createTestFile(String filename, String content) throws IOException {
        Path file = tempRootDir.resolve(filename);
        Files.write(file, content.getBytes());
        return file;
    }
    
    protected Path createTestDirectory(String dirName) throws IOException {
        Path dir = tempRootDir.resolve(dirName);
        Files.createDirectories(dir);
        return dir;
    }
}
