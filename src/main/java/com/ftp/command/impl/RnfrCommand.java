package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class RnfrCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        File file = resolvePath(session, argument);

        if (!isPathWithinRoot(session, file)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!file.exists()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        session.getFileSystemContext().setRenameFrom(file.getAbsolutePath());
        sendResponse(out, ResponseGenerator.CODE_350);
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
