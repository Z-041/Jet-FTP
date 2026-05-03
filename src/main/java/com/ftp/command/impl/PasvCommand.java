package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.data.DataConnectionManager;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public class PasvCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            InetAddress clientAddress = session.getSessionOptions().getClientAddress();

            if (clientAddress != null && clientAddress instanceof java.net.Inet6Address) {
                sendResponse(out, "521 PASV command not supported for IPv6 connections. Use EPSV instead.");
                logger.warn("IPv6 client attempted to use PASV, redirecting to EPSV");
                return;
            }

            DataConnectionManager.PassiveResult result = session.getDataConnectionManager()
                .enterPassiveMode(serverAddress, clientAddress);

            if (result.isIPv6()) {
                sendResponse(out, "521 Cannot use PASV for IPv6 connections. Use EPSV instead.");
                return;
            }

            session.getTransferContext().setPassiveMode(true);
            session.getSessionOptions().setPassiveBindAddress(result.getResponseAddress());
            session.getTransferContext().setPassivePort(result.getPort());

            String addressFormat = result.toPASVFormat();
            sendResponse(out, ResponseGenerator.passMode(addressFormat));

            logger.info("PASV mode entered on " + result.getResponseAddress().getHostAddress() +
                       ":" + result.getPort());

        } catch (UnsupportedOperationException e) {
            logger.error("PASV format error (non-IPv4 address)", e);
            sendResponse(out, "521 Cannot generate PASV response for this address type. Use EPSV instead.");
        } catch (IOException e) {
            logger.error("Error entering PASV mode", e);
            sendResponse(out, ResponseGenerator.CODE_425);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
