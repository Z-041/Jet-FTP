package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class RestCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        try {
            long restartPosition = Long.parseLong(argument);
            session.getTransferContext().setRestartPosition(restartPosition);
            sendResponse(out, ResponseGenerator.CODE_350);
        } catch (NumberFormatException e) {
            sendResponse(out, ResponseGenerator.CODE_501);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
