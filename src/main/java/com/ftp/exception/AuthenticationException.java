package com.ftp.exception;

public class AuthenticationException extends FtpException {
    public AuthenticationException(String message) {
        super(530, message);
    }

    public static AuthenticationException notLoggedIn() {
        return new AuthenticationException("Not logged in");
    }

    public static AuthenticationException invalidPassword() {
        return new AuthenticationException("Invalid password");
    }

    public static AuthenticationException userNotFound(String username) {
        return new AuthenticationException("User not found: " + username);
    }

    public static AuthenticationException homeDirectoryDenied() {
        return new AuthenticationException("Home directory access denied");
    }

    public static AuthenticationException accountRequired() {
        return new AuthenticationException("Account required for login");
    }
}
