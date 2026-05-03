package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ListCommand extends BaseCommandHandler {
    private final boolean useNlst;

    public ListCommand() {
        this(false);
    }

    public ListCommand(boolean useNlst) {
        this.useNlst = useNlst;
    }

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        File dir = session.getFileSystemContext().getCurrentDirectory();
        if (argument != null && !argument.isEmpty()) {
            dir = resolvePath(session, argument);
        }

        if (!dir.exists() || !dir.isDirectory()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!isPathWithinRoot(session, dir)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        String operationName = useNlst ? "NLST" : "LIST";
        boolean currentUseNlst = useNlst;
        File currentDir = dir;
        executeWithDataConnection(
            out,
            session,
            dataSocket -> session.getDataConnectionManager().sendDirectoryList(dataSocket, currentDir, currentUseNlst),
            operationName + " for directory: " + currentDir.getAbsolutePath()
        );
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
