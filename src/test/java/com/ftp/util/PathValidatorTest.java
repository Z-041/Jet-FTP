package com.ftp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PathValidator 测试")
class PathValidatorTest {

    @TempDir
    Path tempDir;

    private File rootDir;

    @BeforeEach
    void setUp() {
        rootDir = tempDir.toFile();
    }

    @Nested
    @DisplayName("路径遍历攻击防护测试")
    class PathTraversalTests {

        @Test
        @DisplayName("尝试遍历到父目录应被阻止")
        void parentTraversalShouldBeBlocked() {
            File targetDir = new File(rootDir, "subdir");
            targetDir.mkdirs();

            File maliciousFile = new File(targetDir, "../secret.txt");
            assertFalse(PathValidator.isPathWithinRoot(rootDir, maliciousFile));
        }

        @Test
        @DisplayName("多层父目录遍历应被阻止")
        void multiLevelParentTraversalShouldBeBlocked() {
            File subDir = new File(rootDir, "a/b/c");
            subDir.mkdirs();

            File maliciousPath = new File(subDir, "../../../etc/passwd");
            assertFalse(PathValidator.isPathWithinRoot(rootDir, maliciousPath));
        }

        @Test
        @DisplayName("绝对路径遍历应被阻止")
        void absolutePathTraversalShouldBeBlocked() {
            File maliciousPath = new File("/etc/passwd");
            assertFalse(PathValidator.isPathWithinRoot(rootDir, maliciousPath));
        }
    }

    @Nested
    @DisplayName("有效路径测试")
    class ValidPathTests {

        @Test
        @DisplayName("根目录自身应有效")
        void rootDirectoryItselfShouldBeValid() {
            assertTrue(PathValidator.isPathWithinRoot(rootDir, rootDir));
        }

        @Test
        @DisplayName("子目录文件应有效")
        void fileInSubdirectoryShouldBeValid() throws IOException {
            File subDir = new File(rootDir, "subdir");
            subDir.mkdirs();
            File validFile = new File(subDir, "file.txt");
            validFile.createNewFile();

            assertTrue(PathValidator.isPathWithinRoot(rootDir, validFile));
        }

        @Test
        @DisplayName("根目录下的文件应有效")
        void fileInRootShouldBeValid() throws IOException {
            File validFile = new File(rootDir, "file.txt");
            validFile.createNewFile();

            assertTrue(PathValidator.isPathWithinRoot(rootDir, validFile));
        }
    }

    @Nested
    @DisplayName("安全路径解析测试")
    class SecurePathResolutionTests {

        @Test
        @DisplayName("空路径应返回根目录")
        void emptyPathShouldReturnRoot() {
            File result = PathValidator.resolvePath(rootDir, "");
            assertEquals(rootDir.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("单斜杠应返回根目录")
        void singleSlashShouldReturnRoot() {
            File result = PathValidator.resolvePath(rootDir, "/");
            assertEquals(rootDir.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("相对路径应正确解析")
        void relativePathShouldBeResolved() throws IOException {
            File subDir = new File(rootDir, "subdir");
            subDir.mkdirs();

            File result = PathValidator.resolvePath(rootDir, "subdir");
            assertEquals(subDir.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("子目录文件路径应正确解析")
        void subdirectoryFilePathShouldBeResolved() throws IOException {
            File subDir = new File(rootDir, "subdir");
            subDir.mkdirs();
            File file = new File(subDir, "file.txt");
            file.createNewFile();

            File result = PathValidator.resolvePath(rootDir, "subdir/file.txt");
            assertEquals(file.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("路径遍历攻击应抛出异常")
        void pathTraversalShouldThrowException() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(rootDir, "../etc/passwd"));
        }

        @Test
        @DisplayName("双点号遍历应抛出异常")
        void doubleDotTraversalShouldThrowException() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(rootDir, "path/to/../etc/passwd"));
        }

        @Test
        @DisplayName("null字节注入应被阻止")
        void nullByteInjectionShouldBeBlocked() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(rootDir, "path\0/to/file"));
        }

        @Test
        @DisplayName("绝对路径应被拒绝")
        void absolutePathShouldBeRejected() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(rootDir, "/etc/passwd"));
        }

        @Test
        @DisplayName("波浪号路径应被拒绝")
        void tildePathShouldBeRejected() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(rootDir, "~/path"));
        }

        @Test
        @DisplayName("双斜杠应被规范化")
        void doubleSlashShouldBeNormalized() throws IOException {
            File result = PathValidator.resolvePath(rootDir, "path//to//file");
            assertFalse(result.getPath().contains("//"));
        }

        @Test
        @DisplayName("尾部斜杠应被移除")
        void trailingSlashShouldBeRemoved() throws IOException {
            File result = PathValidator.resolvePath(rootDir, "path/to/file/");
            assertFalse(result.getPath().endsWith("/"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("null路径应返回根目录")
        void nullPathShouldReturnRoot() {
            File result = PathValidator.resolvePath(rootDir, null);
            assertEquals(rootDir.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("空白路径应返回根目录")
        void whitespacePathShouldReturnRoot() {
            File result = PathValidator.resolvePath(rootDir, "   ");
            assertEquals(rootDir.getAbsolutePath(), result.getAbsolutePath());
        }

        @Test
        @DisplayName("无效根目录应抛出异常")
        void invalidRootDirectoryShouldThrowException() {
            File invalidRoot = new File("/nonexistent/path/that/does/not/exist");
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(invalidRoot, "path"));
        }

        @Test
        @DisplayName("null根目录应抛出异常")
        void nullRootDirectoryShouldThrowException() {
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(null, "path"));
        }

        @Test
        @DisplayName("文件类型的根目录应抛出异常")
        void fileAsRootDirectoryShouldThrowException() throws IOException {
            File fileAsRoot = new File(tempDir.toFile(), "file.txt");
            fileAsRoot.createNewFile();
            assertThrows(SecurityException.class,
                () -> PathValidator.resolvePath(fileAsRoot, "path"));
        }
    }
}
