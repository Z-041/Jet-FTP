package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class DeleCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        File fileToDelete = resolvePath(session, argument);

        if (!isPathWithinRoot(session, fileToDelete)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!fileToDelete.exists() || !fileToDelete.isFile()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (fileToDelete.delete()) {
            logger.info("Deleted file: " + fileToDelete.getAbsolutePath());
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
