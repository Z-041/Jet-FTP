package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MlsdCommand extends BaseCommandHandler {

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

        sendResponse(out, ResponseGenerator.CODE_150);

        try (Socket dataSocket = session.getDataConnectionManager().openDataConnection(session)) {
            sendMlsdList(dataSocket, dir);
            sendResponse(out, ResponseGenerator.CODE_226);
        } catch (IOException e) {
            logger.error("Error sending MLSD directory list", e);
            sendResponse(out, ResponseGenerator.CODE_426);
        } finally {
            session.getDataConnectionManager().closePassiveServerSocket();
            session.resetDataConnection();
        }
    }

    private void sendMlsdList(Socket socket, File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            files = new File[0];
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(socket.getOutputStream()))) {
            for (File file : files) {
                StringBuilder sb = new StringBuilder();
                sb.append("type=").append(file.isDirectory() ? "dir" : "file").append(";");
                sb.append("size=").append(file.length()).append(";");
                sb.append("modify=").append(sdf.format(new Date(file.lastModified()))).append(";");
                sb.append("perm=").append(file.canRead() ? "r" : "").append(file.canWrite() ? "w" : "").append(";");
                sb.append(" ").append(file.getName()).append("\r\n");
                writer.write(sb.toString());
            }
            writer.flush();
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
