package com.ftp.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PathUtil {

    public static final String FTP_SEPARATOR = "/";

    private PathUtil() {
    }

    private static String normalizeSeparators(String path) {
        if (path == null) {
            return null;
        }
        return path.replace('\\', '/').replaceAll("/+", "/");
    }

    public static String toFtpPath(String localPath) {
        if (localPath == null) {
            return FTP_SEPARATOR;
        }
        return localPath.replace(File.separatorChar, '/');
    }

    public static String normalizeFtpPath(String path) {
        if (path == null || path.isEmpty()) {
            return FTP_SEPARATOR;
        }
        String normalized = normalizeSeparators(path);
        if (!normalized.startsWith(FTP_SEPARATOR)) {
            normalized = FTP_SEPARATOR + normalized;
        }
        return normalized;
    }

    public static String toLocalPath(String ftpPath) {
        if (ftpPath == null) {
            return "";
        }
        return ftpPath.replace('/', File.separatorChar);
    }
    
    public static boolean isPathWithinRoot(File rootDir, File targetDir) {
        Objects.requireNonNull(rootDir, "Root directory cannot be null");
        Objects.requireNonNull(targetDir, "Target directory cannot be null");
        
        try {
            File canonicalRoot = rootDir.getCanonicalFile();
            File canonicalTarget = targetDir.getCanonicalFile();
            
            File parent = canonicalTarget;
            while (parent != null) {
                if (parent.equals(canonicalRoot)) {
                    return true;
                }
                parent = parent.getParentFile();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 计算相对于根目录的 FTP 路径
     * @param rootDir 根目录
     * @param targetFile 目标文件/目录
     * @return 以 / 开头的 FTP 相对路径
     */
    public static String computeFtpRelativePath(File rootDir, File targetFile) {
        try {
            String rootPath = rootDir.getCanonicalPath();
            String targetPath = targetFile.getCanonicalPath();
            
            if (targetPath.equals(rootPath)) {
                return "/";
            }
            
            String relativePath;
            if (targetPath.startsWith(rootPath)) {
                relativePath = targetPath.substring(rootPath.length());
            } else {
                relativePath = targetFile.getName();
            }
            
            relativePath = relativePath.replace(File.separatorChar, '/');
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            
            return relativePath;
        } catch (IOException e) {
            return "/" + targetFile.getName();
        }
    }
}
