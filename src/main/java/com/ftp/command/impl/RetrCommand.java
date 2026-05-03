package com.ftp.command.impl;

import com.ftp.command.BaseCommandHandler;
import com.ftp.logging.TransferLog;
import com.ftp.model.User;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class RetrCommand extends BaseCommandHandler {

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        if (argument == null || argument.isEmpty()) {
            sendResponse(out, ResponseGenerator.CODE_501);
            return;
        }

        File file = resolvePath(session, argument);

        if (!file.exists() || !file.isFile()) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        if (!isPathWithinRoot(session, file)) {
            sendResponse(out, ResponseGenerator.CODE_550);
            return;
        }

        long startTime = System.currentTimeMillis();
        TransferLog.Builder logBuilder = new TransferLog.Builder()
                .file(file)
                .transferType(TransferLog.TransferType.DOWNLOAD)
                .clientAddress(session.getSessionOptions().getClientAddress().getHostAddress());

        User user = session.getAuthContext().getUser();
        if (user != null) {
            logBuilder.username(user.getUsername());
        } else {
            logBuilder.username("anonymous");
        }

        executeWithDataConnection(
            out,
            session,
            dataSocket -> {
                long txStart = System.currentTimeMillis();
                session.getDataConnectionManager().sendFile(
                    dataSocket, file, session.getTransferContext().getTransferType(), session.getBandwidthLimiter());
                long txDuration = System.currentTimeMillis() - txStart;

                logBuilder.status(TransferLog.TransferStatus.SUCCESS)
                        .durationMs(txDuration)
                        .build()
                        .log();
            },
            "File send: " + file.getAbsolutePath(),
            e -> {
                long txDuration = System.currentTimeMillis() - startTime;
                logBuilder.status(TransferLog.TransferStatus.FAILED)
                        .durationMs(txDuration)
                        .build()
                        .log();
            }
        );
    }

    @Override
    public boolean requiresAuthentication() {
        return true;
    }
}
