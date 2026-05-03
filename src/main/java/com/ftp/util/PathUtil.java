package com.ftp.util;

import java.io.File;
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
}
