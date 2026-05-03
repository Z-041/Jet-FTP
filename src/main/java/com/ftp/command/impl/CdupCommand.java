package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CdupCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        File currentDir = session.getFileSystemContext().getCurrentDirectory();
        File rootDir = session.getFileSystemContext().getRootDirectory();

        if (currentDir.equals(rootDir)) {
            sendResponse(out, ResponseGenerator.CODE_250);
            return;
        }

        File parentDir = currentDir.getParentFile();
        if (parentDir == null || !isPathWithinRoot(session, parentDir)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        session.getFileSystemContext().setCurrentDirectory(parentDir);
        logger.info("Changed directory up to: " + parentDir.getAbsolutePath());
        sendResponse(out, ResponseGenerator.CODE_250);
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
