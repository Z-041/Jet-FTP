package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MdtmCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        File file = resolvePath(session, argument);

        if (!isPathWithinRoot(session, file)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!file.exists()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date(file.lastModified()));
        sendResponse(out, ResponseGenerator.mdtm(timestamp));
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
