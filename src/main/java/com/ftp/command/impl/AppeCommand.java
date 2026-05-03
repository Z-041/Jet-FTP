package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class AppeCommand extends BaseCommandHandler {

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

        if (file.exists() && file.isDirectory()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        executeWithDataConnection(
            out,
            session,
            dataSocket -> session.getDataConnectionManager().receiveFile(
                dataSocket, file, session.getTransferContext().getTransferType(), true, session.getBandwidthLimiter()),
            "File append: " + file.getAbsolutePath()
        );
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
