package com.ftp.exception;

public class FtpDataConnectionException extends FtpException {
    public FtpDataConnectionException(int replyCode, String message) {
        super(replyCode, message);
    }

    public FtpDataConnectionException(int replyCode, String message, Throwable cause) {
        super(replyCode, message, cause);
    }

    public static FtpDataConnectionException connectionFailed(String details) {
        return new FtpDataConnectionException(425, "Can't open data connection: " + details);
    }

    public static FtpDataConnectionException connectionTimeout() {
        return new FtpDataConnectionException(426, "Connection timed out");
    }

    public static FtpDataConnectionException transferAborted(String details) {
        return new FtpDataConnectionException(426, "Transfer aborted: " + details);
    }
}
