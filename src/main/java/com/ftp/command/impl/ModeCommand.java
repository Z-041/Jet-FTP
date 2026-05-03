package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class ModeCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument != null && (argument.equals("S") || argument.equals("s"))) {
            sendResponse(out, ResponseGenerator.CODE_200);
        } else {
            sendResponse(out, ResponseGenerator.CODE_504);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
