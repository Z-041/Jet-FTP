package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PwdCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        File currentDir = session.getFileSystemContext().getCurrentDirectory();
        File rootDir = session.getFileSystemContext().getRootDirectory();
        
        String relativePath = PathUtil.computeFtpRelativePath(rootDir, currentDir);
        sendResponse(out, ResponseGenerator.pwd(relativePath));
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
