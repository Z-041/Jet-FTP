package com.ftp;

import org.apache.commons.net.ftp.FTP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FtpOtherCommandsTest extends FtpServerTestBase {
    
    @Test
    public void testNoop() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        int reply = ftpClient.noop();
        assertTrue(reply > 0);
    }
    
    @Test
    public void testSyst() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        String system = ftpClient.getSystemName();
        assertNotNull(system);
    }
    
    @Test
    public void testFeat() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        assertTrue(ftpClient.hasFeature("UTF8"));
    }
    
    @Test
    public void testType() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        assertTrue(ftpClient.setFileType(FTP.BINARY_FILE_TYPE));
        assertTrue(ftpClient.setFileType(FTP.ASCII_FILE_TYPE));
    }
    
    @Test
    public void testHelp() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        int reply = ftpClient.help();
        assertTrue(reply > 0);
    }
    
    @Test
    public void testStat() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        ftpClient.enterLocalPassiveMode();
        String status = ftpClient.getStatus("/");
        assertNotNull(status);
    }
}
