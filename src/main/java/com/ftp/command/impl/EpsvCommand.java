package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.data.DataConnectionManager;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public class EpsvCommand extends BaseCommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(EpsvCommand.class);

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        DataConnectionManager dataManager = session.getDataConnectionManager();

        if (argument != null && !argument.isEmpty() && !"ALL".equals(argument.toUpperCase())) {
            try {
                int protocol = Integer.parseInt(argument.trim());
                if (protocol != 1 && protocol != 2) {
                    sendResponse(out, "522 Network protocol not supported, use 1 (IPv4), 2 (IPv6), or ALL");
                    return;
                }
            } catch (NumberFormatException e) {
                sendResponse(out, ResponseGenerator.CODE_501);
                return;
            }
        }

        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            InetAddress clientAddress = session.getSessionOptions().getClientAddress();

            DataConnectionManager.PassiveResult result = dataManager.enterPassiveMode(serverAddress, clientAddress);

            session.getTransferContext().setPassiveMode(true);
            session.getSessionOptions().setPassiveBindAddress(result.getResponseAddress());
            session.getTransferContext().setPassivePort(result.getPort());

            String response = String.format("229 Entering Extended Passive Mode (%s)",
                                          result.toEPSVFormat());
            sendResponse(out, response);

            logger.info("EPSV mode entered on port " + result.getPort() +
                       " (" + (result.isIPv6() ? "IPv6" : "IPv4") + ")");

        } catch (IOException e) {
            logger.error("Error entering EPSV mode", e);
            sendResponse(out, ResponseGenerator.CODE_425);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
