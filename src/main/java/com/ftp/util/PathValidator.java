package com.ftp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


public class PathValidator {
    private static final Logger logger = LoggerFactory.getLogger(PathValidator.class);

    public static File resolvePath(File rootDir, String userPath) throws SecurityException {
        return resolvePath(rootDir, rootDir, userPath);
    }
    
    public static File resolvePath(File rootDir, File baseDir, String userPath) throws SecurityException {
        validateRootDirectory(rootDir);

        if (userPath == null || userPath.trim().isEmpty() || "/".equals(userPath) || "\\".equals(userPath)) {
            return rootDir;
        }

        validateUserPath(userPath);

        // 确定实际用于解析的基准目录
        File actualBaseDir;
        String actualPath;
        
        // 如果路径以 / 开头，则相对于根目录解析
        if (userPath.startsWith("/")) {
            actualBaseDir = rootDir;
            actualPath = userPath.substring(1); // 去除开头的 /
        } else {
            // 否则，相对于指定的基准目录解析
            actualBaseDir = baseDir;
            actualPath = userPath;
        }

        String sanitized = sanitizeAndValidate(actualPath);
        if (sanitized == null || sanitized.isEmpty() || ".".equals(sanitized)) {
            return actualBaseDir;
        }

        Path rootPath = rootDir.toPath().toAbsolutePath().normalize();
        Path basePath = actualBaseDir.toPath().toAbsolutePath().normalize();
        Path resolvedPath = basePath.resolve(sanitized).toAbsolutePath().normalize();

        if (!isSubpath(rootPath, resolvedPath)) {
            logger.warn("Path traversal attempt blocked: {} (root: {}, resolved: {})", userPath, rootPath, resolvedPath);
            throw new SecurityException("Access denied: path outside root directory");
        }

        return resolvedPath.toFile();
    }

    public static boolean isPathWithinRoot(File rootDir, File targetFile) {
        if (rootDir == null || targetFile == null) {
            return false;
        }

        try {
            // 首先检查原始路径中是否有路径遍历的迹象
            String originalPath = targetFile.getPath();
            if (originalPath.contains("..") || originalPath.contains("../") || originalPath.contains("..\\")) {
                logger.warn("Potential path traversal detected in original path: {}", originalPath);
                return false;
            }

            Path rootPath = rootDir.toPath().toAbsolutePath().normalize();
            Path targetPath = targetFile.toPath().toAbsolutePath().normalize();

            // 同时检查规范化后的路径是否在根目录内
            return isSubpath(rootPath, targetPath);
        } catch (Exception e) {
            logger.error("Error checking path validity", e);
            return false;
        }
    }

    private static boolean isSubpath(Path root, Path target) {
        try {
            if (Files.exists(root) && Files.exists(target)) {
                if (Files.isSameFile(root, target)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Files.isSameFile check failed, using path startsWith instead", e);
        }
        
        return target.startsWith(root);
    }

    private static void validateRootDirectory(File rootDir) throws SecurityException {
        if (rootDir == null) {
            throw new SecurityException("Root directory cannot be null");
        }
        if (!rootDir.exists()) {
            throw new SecurityException("Root directory does not exist");
        }
        if (!rootDir.isDirectory()) {
            throw new SecurityException("Root directory is not a directory");
        }
    }

    private static void validateUserPath(String userPath) throws SecurityException {
        if (userPath.contains("\0")) {
            throw new SecurityException("Invalid path: contains null byte");
        }

        String normalized = userPath.replace('\\', '/');

        // 检测真正的系统绝对路径
        if (isSystemAbsolutePath(userPath)) {
            throw new SecurityException("Access denied: absolute paths not allowed");
        }

        if (normalized.contains("../") || normalized.startsWith("..")) {
            throw new SecurityException("Access denied: path traversal attempt");
        }

        if (normalized.contains("/../") || normalized.endsWith("/..")) {
            throw new SecurityException("Access denied: path traversal attempt");
        }

        if (normalized.contains("~")) {
            throw new SecurityException("Access denied: tilde expansion not allowed");
        }
    }

    private static boolean isSystemAbsolutePath(String path) {
        // 检测 Windows 绝对路径 (例如 C:\ 或 D:/)
        if (path.length() >= 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') {
            return true;
        }

        // 检测 Windows UNC 路径 (例如 \\server\share)
        if (path.startsWith("\\\\")) {
            return true;
        }

        // 注意：FTP协议中以 / 开头的路径不是系统绝对路径，而是相对于服务器根目录
        return false;
    }

    private static String sanitizeAndValidate(String path) {
        if (path == null) {
            return "";
        }

        String sanitized = path.trim();
        
        // 统一路径分隔符
        sanitized = sanitized.replace('\\', '/');
        
        // 合并连续的斜杠
        sanitized = sanitized.replaceAll("//+", "/");
        
        // 移除 ./ 前缀
        if (sanitized.startsWith("./")) {
            sanitized = sanitized.substring(2);
        }
        
        // 移除尾部斜杠（除了根目录，但此时我们已经处理过了）
        if (sanitized.endsWith("/") && sanitized.length() > 1) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }
        
        // 检查是否包含非法字符或路径遍历
        // 注意：validateUserPath 已经检查过了，这里只是再次确保
        if (sanitized.contains("..")) {
            return "";
        }
        
        return sanitized;
    }
}
