package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class StruCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument != null && (argument.equals("F") || argument.equals("f"))) {
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
