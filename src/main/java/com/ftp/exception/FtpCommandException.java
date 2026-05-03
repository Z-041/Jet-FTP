package com.ftp.exception;

public class FtpCommandException extends FtpException {
    public FtpCommandException(int replyCode, String message) {
        super(replyCode, message);
    }

    public FtpCommandException(int replyCode, String message, Throwable cause) {
        super(replyCode, message, cause);
    }
}
