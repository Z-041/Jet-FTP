package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public class HelpCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("214-The following commands are recognized:\r\n");
        sb.append(" USER PASS QUIT NOOP SYST FEAT PWD CWD CDUP MKD RMD LIST NLST\r\n");
        sb.append(" RETR STOR DELE RNFR RNTO SIZE MDTM TYPE PORT PASV REST APPE\r\n");
        sb.append(" ALLO STAT OPTS SITE ACCT REIN SMNT MODE STRU HELP\r\n");
        sb.append("214 Help message.");
        sendResponse(out, sb.toString());
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
