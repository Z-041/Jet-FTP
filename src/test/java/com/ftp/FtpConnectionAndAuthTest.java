package com.ftp;

import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FtpConnectionAndAuthTest extends FtpServerTestBase {
    
    @Test
    public void testConnectionSuccess() {
        assertTrue(ftpClient.isConnected());
        int reply = ftpClient.getReplyCode();
        assertTrue(FTPReply.isPositiveCompletion(reply));
    }
    
    @Test
    public void testLoginSuccess() throws Exception {
        boolean success = ftpClient.login(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(success);
        assertTrue(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()));
    }
    
    @Test
    public void testLoginFailure_WrongPassword() throws Exception {
        boolean success = ftpClient.login(TEST_USERNAME, "wrongpassword");
        assertFalse(success);
        assertFalse(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()));
    }
    
    @Test
    public void testLoginFailure_UserNotFound() throws Exception {
        boolean success = ftpClient.login("nonexistent", "anypassword");
        assertFalse(success);
    }
    
    @Test
    public void testAnonymousLogin() throws Exception {
        boolean success = ftpClient.login("anonymous", "test@example.com");
        assertTrue(success);
        assertTrue(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()));
    }
    
    @Test
    public void testQuit() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        boolean loggedOut = ftpClient.logout();
        assertTrue(loggedOut);
    }
}
