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

            String addrStr = formatAddress(result.getResponseAddress());
            logger.info("EPSV mode entered on " + addrStr + ":" + result.getPort() +
                       " (" + (result.isIPv6() ? "IPv6" : "IPv4") + ")");

        } catch (IOException e) {
            logger.error("Error entering EPSV mode", e);
            sendResponse(out, ResponseGenerator.CODE_425);
        }
    }

    private String formatAddress(InetAddress addr) {
        if (addr instanceof java.net.Inet6Address) {
            return compressIPv6(addr.getHostAddress());
        }
        return addr.getHostAddress();
    }

    private String compressIPv6(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        try {
            if (address.equals("0:0:0:0:0:0:0:1")) {
                return "::1";
            }

            if (address.equals("0:0:0:0:0:0:0:0")) {
                return "::";
            }

            String[] parts = address.split(":");
            int maxZeroLength = 0;
            int maxZeroStart = -1;
            int currentZeroLength = 0;
            int currentZeroStart = -1;

            for (int i = 0; i < parts.length; i++) {
                if ("0".equals(parts[i]) || parts[i].isEmpty()) {
                    if (currentZeroLength == 0) {
                        currentZeroStart = i;
                    }
                    currentZeroLength++;
                    if (currentZeroLength > maxZeroLength) {
                        maxZeroLength = currentZeroLength;
                        maxZeroStart = currentZeroStart;
                    }
                } else {
                    currentZeroLength = 0;
                    currentZeroStart = -1;
                }
            }

            if (maxZeroLength >= 2 && maxZeroStart != -1) {
                StringBuilder sb = new StringBuilder();
                boolean needDoubleColon = true;

                for (int i = 0; i < parts.length; i++) {
                    if (i >= maxZeroStart && i < maxZeroStart + maxZeroLength) {
                        if (needDoubleColon) {
                            sb.append("::");
                            needDoubleColon = false;
                        }
                        continue;
                    }

                    if (sb.length() > 0 && !sb.toString().endsWith("::") && !sb.toString().endsWith(":")) {
                        sb.append(":");
                    }

                    if (!parts[i].isEmpty()) {
                        String part = parts[i].toLowerCase().replaceFirst("^0+", "");
                        sb.append(part.isEmpty() ? "0" : part);
                    }
                }

                String result = sb.toString();
                if (result.endsWith(":") && !result.endsWith("::")) {
                    result = result.substring(0, result.length() - 1);
                }

                return result;
            }

            StringBuilder simple = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    if (simple.length() > 0) {
                        simple.append(":");
                    }
                    String part = parts[i].toLowerCase().replaceFirst("^0+", "");
                    simple.append(part.isEmpty() ? "0" : part);
                }
            }
            return simple.toString();

        } catch (Exception e) {
            return address;
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
