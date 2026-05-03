package com.ftp.exception;

public class PathTraversalException extends FtpCommandException {
    private final String requestedPath;

    public PathTraversalException(String requestedPath) {
        super(550, "Permission denied");
        this.requestedPath = requestedPath;
    }

    public PathTraversalException(String requestedPath, String message) {
        super(550, message);
        this.requestedPath = requestedPath;
    }

    public String getRequestedPath() {
        return requestedPath;
    }
}
