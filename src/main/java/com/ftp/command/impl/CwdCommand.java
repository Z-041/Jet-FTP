package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CwdCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        
        File newDir = resolvePath(session, argument);
        
        if (!newDir.exists() || !newDir.isDirectory()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        if (!isPathWithinRoot(session, newDir)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        session.getFileSystemContext().setCurrentDirectory(newDir);
        logger.info("Changed directory to: " + newDir.getAbsolutePath());
        sendResponse(out, ResponseGenerator.CODE_250);
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
