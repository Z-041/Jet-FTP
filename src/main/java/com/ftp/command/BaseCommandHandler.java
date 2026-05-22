package com.ftp.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.util.PathValidator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class BaseCommandHandler implements CommandHandler {
    protected final Logger logger = LoggerFactory.getLogger(BaseCommandHandler.class);

    protected void sendResponse(OutputStream out, String response) throws IOException {
        out.write((response + "\r\n").getBytes("UTF-8"));
        out.flush();
    }

    protected File resolvePath(Session session, String path) {
        try {
            File rootDir = session.getFileSystemContext().getRootDirectory();
            File currentDir = session.getFileSystemContext().getCurrentDirectory();
            return PathValidator.resolvePath(rootDir, currentDir, path);
        } catch (SecurityException e) {
            logger.error("Path resolution failed: " + path + " - " + e.getMessage());
            return session.getFileSystemContext().getRootDirectory();
        }
    }

    protected boolean isPathWithinRoot(Session session, File file) {
        return PathValidator.isPathWithinRoot(session.getFileSystemContext().getRootDirectory(), file);
    }

    protected void handleCommandSafely(String argument, Session session, OutputStream out, CommandExecutor executor) {
        try {
            executor.execute(argument, session, out);
        } catch (SecurityException e) {
            logger.error("Security violation: " + e.getMessage(), e);
            try {
                sendResponse(out, "550 Permission denied.");
            } catch (IOException ex) {
                logger.error("Error sending response", ex);
            }
        } catch (IOException e) {
            logger.error("IO error during command execution", e);
            try {
                sendResponse(out, "451 Local error in processing.");
            } catch (IOException ex) {
                logger.error("Error sending response", ex);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during command execution", e);
            try {
                sendResponse(out, "451 Requested action aborted: local error in processing.");
            } catch (IOException ex) {
                logger.error("Error sending response", ex);
            }
        }
    }

    @FunctionalInterface
    protected interface DataTransferOperation {
        void execute(Socket dataSocket) throws IOException;
    }

    @FunctionalInterface
    protected interface ErrorHandler {
        void handleError(IOException e);
    }

    protected void executeWithDataConnection(
            OutputStream out,
            Session session,
            DataTransferOperation operation,
            String operationName,
            ErrorHandler errorHandler) throws IOException {

        sendResponse(out, ResponseGenerator.CODE_150);

        try (Socket dataSocket = session.getDataConnectionManager().openDataConnection(session)) {
            operation.execute(dataSocket);
            sendResponse(out, ResponseGenerator.CODE_226);
            logger.info(operationName + " completed successfully");
        } catch (IOException e) {
            logger.error("Error during " + operationName, e);
            sendResponse(out, ResponseGenerator.CODE_426);

            if (errorHandler != null) {
                errorHandler.handleError(e);
            }
        }
        // 注意：不在 finally 中调用 resetDataConnection()
        // 因为客户端可能复用 PASV 连接进行多次传输
        // 资源清理由 ClientHandler 在客户端断开时统一处理
    }

    protected void executeWithDataConnection(
            OutputStream out,
            Session session,
            DataTransferOperation operation,
            String operationName) throws IOException {
        executeWithDataConnection(out, session, operation, operationName, null);
    }

    @FunctionalInterface
    protected interface CommandExecutor {
        void execute(String argument, Session session, OutputStream out) throws Exception;
    }
}
