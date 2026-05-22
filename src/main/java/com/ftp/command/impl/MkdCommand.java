package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.util.PathUtil;

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
            String relativePath = PathUtil.computeFtpRelativePath(session.getFileSystemContext().getRootDirectory(), newDir);
            logger.info("Created directory: " + newDir.getAbsolutePath());
            sendResponse(out, ResponseGenerator.created(relativePath));
        } else {
            sendResponse(out, ResponseGenerator.CODE_550);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
