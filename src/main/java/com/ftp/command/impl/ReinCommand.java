package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class ReinCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        session.getAuthContext().clear();
        session.reset();
        sendResponse(out, ResponseGenerator.CODE_220);
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
