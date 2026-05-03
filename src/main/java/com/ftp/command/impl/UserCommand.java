package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class UserCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        session.getAuthContext().setUsernamePending(argument);
        logger.info("USER command received: " + argument);
        sendResponse(out, ResponseGenerator.CODE_331);
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
