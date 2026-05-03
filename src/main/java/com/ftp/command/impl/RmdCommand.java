package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class RmdCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        
        File dirToDelete = resolvePath(session, argument);
        
        if (!isPathWithinRoot(session, dirToDelete)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        if (!dirToDelete.exists() || !dirToDelete.isDirectory()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        String[] files = dirToDelete.list();
        if (files != null && files.length > 0) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        if (dirToDelete.delete()) {
            logger.info("Deleted directory: " + dirToDelete.getAbsolutePath());
            sendResponse(out, ResponseGenerator.CODE_250);
        } else {
            sendResponse(out, ResponseGenerator.CODE_550);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
