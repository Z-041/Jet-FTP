package com.ftp.exception;

public class FtpException extends Exception {
    private final int replyCode;

    public FtpException(int replyCode, String message) {
        super(message);
        this.replyCode = replyCode;
    }

    public FtpException(int replyCode, String message, Throwable cause) {
        super(message, cause);
        this.replyCode = replyCode;
    }

    public int getReplyCode() {
        return replyCode;
    }
}
