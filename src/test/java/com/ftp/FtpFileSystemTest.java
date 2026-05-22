package com.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FtpFileSystemTest extends FtpServerTestBase {
    
    @Test
    public void testPwd() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        String pwd = ftpClient.printWorkingDirectory();
        assertNotNull(pwd);
        assertEquals("/", pwd);
    }
    
    @Test
    public void testCwdAndCdup() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        Path testDir = createTestDirectory("testdir");
        assertTrue(ftpClient.changeWorkingDirectory("testdir"));
        assertEquals("/testdir", ftpClient.printWorkingDirectory());
        
        assertTrue(ftpClient.changeToParentDirectory());
        assertEquals("/", ftpClient.printWorkingDirectory());
    }
    
    @Test
    public void testMkdAndRmd() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        assertTrue(ftpClient.makeDirectory("newdir"));
        assertTrue(Files.exists(tempRootDir.resolve("newdir")));
        
        assertTrue(ftpClient.removeDirectory("newdir"));
        assertFalse(Files.exists(tempRootDir.resolve("newdir")));
    }
    
    @Test
    public void testListFiles() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        createTestFile("test1.txt", "content1");
        createTestFile("test2.txt", "content2");
        createTestDirectory("subdir");
        
        FTPFile[] files = ftpClient.listFiles();
        assertNotNull(files);
        assertEquals(3, files.length);
    }
    
    @Test
    public void testNlst() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        createTestFile("test.txt", "content");
        
        String[] fileNames = ftpClient.listNames();
        assertNotNull(fileNames);
        assertTrue(fileNames.length >= 1);
    }
    
    @Test
    public void testDele() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        Path testFile = createTestFile("todelete.txt", "content");
        assertTrue(Files.exists(testFile));
        
        assertTrue(ftpClient.deleteFile("todelete.txt"));
        assertFalse(Files.exists(testFile));
    }
    
    @Test
    public void testRename() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        Path oldFile = createTestFile("oldname.txt", "content");
        assertTrue(Files.exists(oldFile));
        
        assertTrue(ftpClient.rename("oldname.txt", "newname.txt"));
        assertFalse(Files.exists(oldFile));
        assertTrue(Files.exists(tempRootDir.resolve("newname.txt")));
    }
    
    @Test
    public void testSize() throws Exception {
        assertTrue(ftpClient.login(TEST_USERNAME, TEST_PASSWORD));
        
        String content = "Hello, FTP!";
        createTestFile("sizecheck.txt", content);
        
        long size = ftpClient.mlistFile("sizecheck.txt").getSize();
        assertEquals(content.length(), size);
    }
}
