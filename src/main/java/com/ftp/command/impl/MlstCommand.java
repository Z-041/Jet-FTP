package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MlstCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        File file = session.getFileSystemContext().getCurrentDirectory();
        if (argument != null && !argument.isEmpty()) {
            file = resolvePath(session, argument);
        }

        if (!file.exists()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!isPathWithinRoot(session, file)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("250-Listing ");
        if (argument != null && !argument.isEmpty()) {
            sb.append(argument);
        } else {
            sb.append(".");
        }
        sb.append("\r\n");
        sb.append(" type=").append(file.isDirectory() ? "dir" : "file").append(";");
        sb.append("size=").append(file.length()).append(";");
        sb.append("modify=").append(DateUtil.formatMlsxTimestamp(file.lastModified())).append(";");
        sb.append("perm=").append(file.canRead() ? "r" : "").append(file.canWrite() ? "w" : "").append(";");
        sb.append(" ").append(file.getName()).append("\r\n");
        sb.append("250 End");
        
        sendResponse(out, sb.toString());
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
