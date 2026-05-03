package com.ftp.command.impl;

import com.ftp.auth.LoginAttemptManager;
import com.ftp.auth.UserManager;
import com.ftp.command.BaseCommandHandler;
import com.ftp.model.User;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PassCommand extends BaseCommandHandler {
    private final UserManager userManager;
    private final LoginAttemptManager loginAttemptManager;

    public PassCommand() {
        this.userManager = UserManager.getInstance();
        this.loginAttemptManager = LoginAttemptManager.getInstance();
    }

    @Override
    public void handle(String argument, Session session, OutputStream out) throws IOException {
        String username = session.getAuthContext().getUsernamePending();
        if (username == null) {
            sendResponse(out, ResponseGenerator.CODE_503);
            return;
        }

        if (loginAttemptManager.isBlocked(session.getSessionOptions().getClientAddress())) {
            long remainingTime = loginAttemptManager.getRemainingLockoutTimeMs(session.getSessionOptions().getClientAddress());
            logger.warn("Blocked login attempt from blocked IP: " + session.getSessionOptions().getClientAddress());
            sendResponse(out, "530 Login blocked due to too many failed attempts. Try again in " + (remainingTime / 1000) + " seconds.");
            return;
        }

        User user = userManager.getUser(username);
        if (user == null) {
            session.getAuthContext().clearPendingUsername();
            loginAttemptManager.recordFailedAttempt(session.getSessionOptions().getClientAddress());
            sendResponse(out, ResponseGenerator.CODE_530);
            return;
        }

        boolean authenticated;
        if (user.isAnonymous()) {
            authenticated = true;
            logger.info("Anonymous login accepted for user: " + username);
        } else {
            authenticated = userManager.authenticate(username, argument);
        }

        if (authenticated) {
            loginAttemptManager.clearAttempts(session.getSessionOptions().getClientAddress());
            session.getAuthContext().setUser(user);
            session.getAuthContext().setAuthenticated(true);

            File homeDir = new File(user.getHomeDirectory());
            File rootDir = session.getFileSystemContext().getRootDirectory();

            try {
                File canonicalHome = homeDir.getCanonicalFile();
                if (!PathUtil.isPathWithinRoot(rootDir, canonicalHome)) {
                    logger.warn("User home directory outside root: " + canonicalHome + " (root: " + rootDir.getCanonicalPath() + ")");
                    sendResponse(out, "530 Home directory access denied.");
                    return;
                }

                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }

                session.getFileSystemContext().setCurrentDirectory(canonicalHome);
            } catch (IOException e) {
                logger.error("Error setting home directory for user: " + username, e);
                session.getFileSystemContext().setCurrentDirectory(rootDir);
            }

            sendResponse(out, ResponseGenerator.CODE_230);
        } else {
            loginAttemptManager.recordFailedAttempt(session.getSessionOptions().getClientAddress());
            session.getAuthContext().clearPendingUsername();
            sendResponse(out, ResponseGenerator.CODE_530);
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }
}
