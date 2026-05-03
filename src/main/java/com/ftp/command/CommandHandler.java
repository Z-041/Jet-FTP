package com.ftp.command;

import com.ftp.session.Session;

import java.io.IOException;
import java.io.OutputStream;

public interface CommandHandler {
    void handle(String argument, Session session, OutputStream out) throws IOException;
    boolean requiresAuthentication();
    default boolean isExploratory() {
        return false;
    }
}
