package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class StatCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("211-Status:\r\n");
            sb.append(" Connected to Java FTP Server\r\n");
            if (session.getAuthContext().isAuthenticated()) {
                sb.append(" Logged in as: ").append(session.getAuthContext().getUser().getUsername()).append("\r\n");
            }
            sb.append(" Type: ").append(session.getTransferContext().getTransferType()).append("\r\n");
            sb.append(" Mode: ").append(session.getTransferContext().isPassiveMode() ? "Passive" : "Active").append("\r\n");
            sb.append("211 End of status");
            sendResponse(out, sb.toString());
        } else {
            sendResponse(out, ResponseGenerator.CODE_200);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
