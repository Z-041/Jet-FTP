package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class OptsCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument != null && argument.equalsIgnoreCase("UTF8 ON")) {
            session.getSessionOptions().setUtf8Enabled(true);
            sendResponse(out, "200 UTF8 set to on");
        } else {
            sendResponse(out, ResponseGenerator.CODE_200);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
