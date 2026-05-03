package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MkdCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        
        File newDir = resolvePath(session, argument);
        
        if (!isPathWithinRoot(session, newDir)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        if (newDir.exists()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        if (newDir.mkdirs()) {
            String relativePath = computeRelativePath(session.getFileSystemContext().getRootDirectory(), newDir);
            logger.info("Created directory: " + newDir.getAbsolutePath());
            sendResponse(out, String.format(ResponseGenerator.CODE_257, relativePath));
        } else {
            sendResponse(out, ResponseGenerator.CODE_550);
        }
    }

    private String computeRelativePath(File rootDir, File targetFile) throws IOException {
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
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
