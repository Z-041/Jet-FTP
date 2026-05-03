package com.ftp.protocol;

public class ResponseGenerator {
    
    private static final String OS_TYPE;
    
    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            OS_TYPE = "215 Windows_NT";
        } else if (os.contains("mac")) {
            OS_TYPE = "215 MACOS Type: L8";
        } else {
            OS_TYPE = "215 UNIX Type: L8";
        }
    }
    
    // 1xx - Information
    public static final String CODE_125 = "125 Data connection already open; transfer starting.";
    public static final String CODE_150 = "150 File status okay; about to open data connection.";
    
    // 2xx - Success
    public static final String CODE_200 = "200 Command okay.";
    public static final String CODE_213 = "213 %s";
    public static final String CODE_214 = "214 Help message.";
    public static final String CODE_215 = "215 UNIX Type: L8";
    public static final String CODE_220 = "220 Service ready for new user.";
    public static final String CODE_221 = "221 Service closing control connection.";
    public static final String CODE_226 = "226 Closing data connection.";
    public static final String CODE_227 = "227 Entering Passive Mode (%s).";
    public static final String CODE_230 = "230 User logged in, proceed.";
    public static final String CODE_250 = "250 Requested file action okay, completed.";
    public static final String CODE_257 = "257 \"%s\" created.";
    
    // 3xx - Need more information
    public static final String CODE_331 = "331 User name okay, need password.";
    public static final String CODE_332 = "332 Need account for login.";
    public static final String CODE_350 = "350 Requested file action pending further information.";
    
    // 4xx - Client error
    public static final String CODE_425 = "425 Can't open data connection.";
    public static final String CODE_426 = "426 Connection closed; transfer aborted.";
    public static final String CODE_450 = "450 Requested file action not taken.";
    public static final String CODE_451 = "451 Requested action aborted: local error in processing.";
    public static final String CODE_452 = "452 Requested action not taken. Insufficient storage space in system.";
    
    // 5xx - Server error
    public static final String CODE_500 = "500 Syntax error, command unrecognized.";
    public static final String CODE_501 = "501 Syntax error in parameters or arguments.";
    public static final String CODE_502 = "502 Command not implemented.";
    public static final String CODE_503 = "503 Bad sequence of commands.";
    public static final String CODE_504 = "504 Command not implemented for that parameter.";
    public static final String CODE_530 = "530 Not logged in.";
    public static final String CODE_532 = "532 Need account for storing files.";
    public static final String CODE_550 = "550 Requested action not taken. File unavailable.";
    public static final String CODE_551 = "551 Page type unknown.";
    public static final String CODE_552 = "552 Requested file action aborted. Exceeded storage allocation.";
    public static final String CODE_553 = "553 Requested action not taken. File name not allowed.";

    public static String welcome(String serverName) {
        return "220 " + serverName + " FTP server ready.";
    }

    public static String pwd(String path) {
        return String.format("257 \"%s\" is current directory.", path);
    }

    public static String syst() {
        return OS_TYPE;
    }

    public static String size(long size) {
        return "213 " + size;
    }

    public static String mdtm(String timestamp) {
        return "213 " + timestamp;
    }

    public static String passMode(String address) {
        return "227 Entering Passive Mode (" + address + ")";
    }

    public static String feat() {
        StringBuilder sb = new StringBuilder();
        sb.append("211-Features:\r\n");
        sb.append(" SIZE\r\n");
        sb.append(" MDTM\r\n");
        sb.append(" PASV\r\n");
        sb.append(" UTF8\r\n");
        sb.append(" MLSD\r\n");
        sb.append(" MLST\r\n");
        sb.append(" REST STREAM\r\n");
        sb.append("211 End");
        return sb.toString();
    }
}
