package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.session.TransferContext;

import java.io.IOException;
import java.io.OutputStream;

public class TypeCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        String typeCode = argument.toUpperCase();
        if (typeCode.startsWith("A")) {
            session.getTransferContext().setTransferType(TransferContext.TransferType.ASCII);
            sendResponse(out, ResponseGenerator.CODE_200);
        } else if (typeCode.startsWith("I")) {
            session.getTransferContext().setTransferType(TransferContext.TransferType.BINARY);
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
