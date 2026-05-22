package com.ftp;

import org.apache.commons.net.ftp.FTP;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FtpFileTransferTest extends FtpServerTestBase {
    
    @Test
    public void testStorAndRetr_PassiveMode() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        String testContent = "This is a test file for FTP transfer!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testContent.getBytes());
        
        boolean stored = ftpClient.storeFile("testfile.txt", inputStream);
        assertTrue(stored, "File should be stored successfully");
        
        inputStream.close();
        
        Path storedFile = tempRootDir.resolve("testfile.txt");
        assertTrue(Files.exists(storedFile), "File should exist on server");
        assertEquals(testContent, Files.readString(storedFile));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean retrieved = ftpClient.retrieveFile("testfile.txt", outputStream);
        assertTrue(retrieved, "File should be retrieved successfully");
        
        outputStream.close();
        assertEquals(testContent, outputStream.toString());
    }
    
    @Test
    public void testAppe() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        createTestFile("append.txt", "Hello ");
        
        String appendContent = "World!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appendContent.getBytes());
        
        boolean appended = ftpClient.appendFile("append.txt", inputStream);
        assertTrue(appended);
        
        inputStream.close();
        
        String finalContent = Files.readString(tempRootDir.resolve("append.txt"));
        assertEquals("Hello World!", finalContent);
    }
    
    @Test
    public void testRest() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        String testContent = "0123456789ABCDEF";
        createTestFile("restart.txt", testContent);
        
        ftpClient.setRestartOffset(10);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean retrieved = ftpClient.retrieveFile("restart.txt", outputStream);
        assertTrue(retrieved);
        
        outputStream.close();
        assertEquals("ABCDEF", outputStream.toString());
    }
}
