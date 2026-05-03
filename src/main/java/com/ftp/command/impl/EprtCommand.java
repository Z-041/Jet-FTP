package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.session.TransferContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Inet6Address;

public class EprtCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        // EPRT格式: |protocol|address|port|
        // protocol: 1=IPv4, 2=IPv6
        
        if (argument == null || !argument.startsWith("|")) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        
        String[] parts = argument.split("\\|");
        if (parts.length != 4) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }
        
        try {
            int protocol = Integer.parseInt(parts[1]);
            String address = parts[2];
            int port = Integer.parseInt(parts[3]);
            
            if (protocol != 1 && protocol != 2) {
                sendResponse(out, "522 Network protocol not supported, use 1 (IPv4) or 2 (IPv6)");
                return;
            }
            
            if (port < 1 || port > 65535) {
                sendResponse(out, ResponseGenerator.CODE_501);
                return;
            }
            
            InetAddress addr = InetAddress.getByName(address);
            boolean isIPv6 = addr instanceof Inet6Address;
            
            if ((protocol == 2 && !isIPv6) || (protocol == 1 && isIPv6)) {
                sendResponse(out, "522 Protocol/address mismatch");
                return;
            }
            
            session.getTransferContext().setPassiveMode(false);
            session.getTransferContext().setActiveAddress(address);
            session.getTransferContext().setActivePort(port);
            session.getTransferContext().setActiveProtocol(TransferContext.Protocol.fromValue(protocol));
            session.getDataConnectionManager().closePassiveServerSocket();
            
            logger.info("EPRT active mode: " + address + ":" + port + " (proto=" + 
                       protocol + ", IPv" + (isIPv6 ? "6" : "4") + ")");
            sendResponse(out, ResponseGenerator.CODE_200);
            
        } catch (NumberFormatException e) {
            logger.error("Invalid EPRT format", e);
            sendResponse(out, ResponseGenerator.CODE_501);
        } catch (Exception e) {
            logger.error("Error processing EPRT command", e);
            sendResponse(out, "501 Invalid parameter");
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
