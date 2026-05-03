package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class PortCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        try {
            String[] parts = argument.split(",");
            if (parts.length != 6) {
                sendResponse(out, ResponseGenerator.CODE_501);
                return;
            }

            String address = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
            int p1 = Integer.parseInt(parts[4]);
            int p2 = Integer.parseInt(parts[5]);
            int port = p1 * 256 + p2;

            session.getTransferContext().setPassiveMode(false);
            session.getTransferContext().setActiveAddress(address);
            session.getTransferContext().setActivePort(port);
            session.getDataConnectionManager().closePassiveServerSocket();

            logger.info("Active mode set: " + address + ":" + port);
            sendResponse(out, ResponseGenerator.CODE_200);
        } catch (NumberFormatException e) {
            sendResponse(out, ResponseGenerator.CODE_501);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
