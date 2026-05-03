package com.ftp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class PathValidator {
    private static final Logger logger = LoggerFactory.getLogger(PathValidator.class);

    public static File resolvePath(File rootDir, String userPath) throws SecurityException {
        validateRootDirectory(rootDir);

        if (userPath == null || userPath.trim().isEmpty() || "/".equals(userPath) || "\\".equals(userPath)) {
            return rootDir;
        }

        validateUserPath(userPath);

        String sanitized = sanitizeAndValidate(userPath);
        if (sanitized == null || sanitized.isEmpty() || ".".equals(sanitized)) {
            return rootDir;
        }

        Path rootPath = rootDir.toPath().toAbsolutePath().normalize();
        Path resolvedPath = rootPath.resolve(sanitized).toAbsolutePath().normalize();

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

        String originalPath = targetFile.getPath();
        if (originalPath.contains("..")) {
            return false;
        }

        try {
            Path rootPath = rootDir.toPath().toAbsolutePath().normalize();
            Path targetPath = targetFile.toPath().toAbsolutePath().normalize();

            return isSubpath(rootPath, targetPath);
        } catch (Exception e) {
            logger.error("Error checking path validity", e);
            return false;
        }
    }

    private static boolean isSubpath(Path root, Path target) {
        String rootStr = root.toString().replace('\\', '/');
        String targetStr = target.toString().replace('\\', '/');

        if (targetStr.equals(rootStr)) {
            return true;
        }

        if (!targetStr.startsWith(rootStr + "/")) {
            return false;
        }

        return true;
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

        if (normalized.startsWith("/")) {
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

    private static String sanitizeAndValidate(String path) {
        if (path == null) {
            return "";
        }

        String sanitized = path.trim();
        sanitized = sanitized.replace('\\', '/');

        while (sanitized.contains("//")) {
            sanitized = sanitized.replace("//", "/");
        }

        if (sanitized.startsWith("./")) {
            sanitized = sanitized.substring(2);
        }

        if (sanitized.equals("..") || sanitized.startsWith("..")) {
            return "";
        }

        sanitized = sanitized.replaceAll("/\\.\\./", "/");
        if (sanitized.endsWith("/..")) {
            sanitized = sanitized.substring(0, sanitized.length() - 3);
        }

        if (sanitized.contains("..")) {
            return "";
        }

        if (sanitized.contains("~")) {
            sanitized = sanitized.replace("~", "");
        }

        if (sanitized.endsWith("/") && sanitized.length() > 1) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }

        if (sanitized.isEmpty()) {
            return "";
        }

        return sanitized;
    }
}
