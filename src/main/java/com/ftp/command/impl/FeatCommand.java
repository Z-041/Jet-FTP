package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class FeatCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        sendResponse(out, ResponseGenerator.feat());
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
