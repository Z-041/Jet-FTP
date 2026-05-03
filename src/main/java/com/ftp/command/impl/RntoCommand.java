package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class RntoCommand extends BaseCommandHandler {
    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        String renameFrom = session.getFileSystemContext().getRenameFrom();
        if (renameFrom == null) {
            sendResponse(out, ResponseGenerator.CODE_503);
            return;
        }

        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        File fromFile = new File(renameFrom);

        if (!isPathWithinRoot(session, fromFile)) {
            logger.warn("RNTO: Source file outside root directory: " + renameFrom);
            session.getFileSystemContext().clearRenameFrom();
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!fromFile.exists()) {
            logger.warn("RNTO: Source file no longer exists: " + renameFrom);
            session.getFileSystemContext().clearRenameFrom();
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        File toFile = resolvePath(session, argument);

        if (!isPathWithinRoot(session, toFile)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (toFile.exists()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        try {
            String fromCanonical = fromFile.getCanonicalPath();
            String toCanonical = toFile.getCanonicalPath();

            if (fromCanonical.equals(toCanonical)) {
                sendResponse(out, ResponseGenerator.CODE_550);
                return;
            }
        } catch (IOException e) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (fromFile.renameTo(toFile)) {
            logger.info("Renamed file from " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
            session.getFileSystemContext().clearRenameFrom();
            sendResponse(out, ResponseGenerator.CODE_250);
        } else {
            sendResponse(out, ResponseGenerator.CODE_550);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
