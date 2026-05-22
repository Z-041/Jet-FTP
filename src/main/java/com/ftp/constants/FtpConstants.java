package com.ftp.constants;

public final class FtpConstants {

    private FtpConstants() {
    }

    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
    public static final String ENCODING_DEFAULT = ENCODING_UTF_8;

    public static final int DEFAULT_PORT = 21;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_TIMEOUT_SECONDS = 300;
    public static final String DEFAULT_ROOT_DIRECTORY = "ftp-root";
    public static final String DEFAULT_LOG_LEVEL = "INFO";
    public static final String DEFAULT_LOG_FILE_PATH = "logs/ftp-server.log";
    public static final int DEFAULT_BCRYPT_ROUNDS = 12;

    public static final int LIMIT_MIN_PORT = 1;
    public static final int LIMIT_MAX_PORT = 65535;
    public static final int LIMIT_MIN_MAX_CONNECTIONS = 1;
    public static final int LIMIT_MAX_MAX_CONNECTIONS = 1000;
    public static final int LIMIT_MIN_TIMEOUT = 10;
    public static final int LIMIT_MAX_TIMEOUT = 3600;
    public static final int LIMIT_MIN_BCRYPT_ROUNDS = 4;
    public static final int LIMIT_MAX_BCRYPT_ROUNDS = 31;

    public static final int BUFFER_DEFAULT = 8192;
    public static final int BUFFER_SMALL = 1024;
    public static final int BUFFER_LARGE = 16384;

    public static final String CONFIG_KEY_SERVER_PORT = "server.port";
    public static final String CONFIG_KEY_SERVER_ROOT_DIRECTORY = "server.rootDirectory";
    public static final String CONFIG_KEY_SERVER_MAX_CONNECTIONS = "server.maxConnections";
    public static final String CONFIG_KEY_SERVER_TIMEOUT_SECONDS = "server.timeoutSeconds";
    public static final String CONFIG_KEY_SERVER_THREADPOOL_CORE_SIZE = "server.threadPool.coreSize";
    public static final String CONFIG_KEY_SERVER_THREADPOOL_KEEP_ALIVE_SECONDS = "server.threadPool.keepAliveSeconds";
    public static final String CONFIG_KEY_SERVER_THREADPOOL_QUEUE_CAPACITY = "server.threadPool.queueCapacity";
    public static final String CONFIG_KEY_SERVER_BIND_ADDRESS = "server.bindAddress";
    public static final String CONFIG_KEY_SERVER_DUAL_STACK_ENABLED = "server.dualStackEnabled";
    public static final String CONFIG_KEY_SERVER_LISTEN_INTERFACE = "server.listenInterface";
    public static final String CONFIG_KEY_SERVER_PREFER_IPV6 = "server.preferIPv6";
    public static final String CONFIG_KEY_LOG_LEVEL = "log.level";
    public static final String CONFIG_KEY_LOG_FILE_PATH = "log.filePath";
    public static final String CONFIG_KEY_PASSIVE_MODE_EXTERNAL_IP = "passiveMode.externalIp";
    public static final String CONFIG_KEY_PASSIVE_MODE_PORT_MIN = "passiveMode.portMin";
    public static final String CONFIG_KEY_PASSIVE_MODE_PORT_MAX = "passiveMode.portMax";
    public static final String CONFIG_KEY_PASSIVE_MODE_CONNECTION_TIMEOUT = "passiveMode.connectionTimeout";
    public static final String CONFIG_KEY_PASSIVE_MODE_IPV4_EXTERNAL_IP = "passiveMode.ipv4ExternalIp";
    public static final String CONFIG_KEY_PASSIVE_MODE_IPV6_EXTERNAL_IP = "passiveMode.ipv6ExternalIp";
    public static final String CONFIG_KEY_SECURITY_BCRYPT_ROUNDS = "security.bcryptRounds";
    public static final String CONFIG_KEY_LOG_DETAILED_TRANSFER = "log.detailedTransfer";
    public static final String CONFIG_KEY_LOG_QUEUE_SIZE = "log.queueSize";

    public static final String CONFIG_FILE_DEFAULT = "ftp-server.properties";
    public static final String CONFIG_FILE_DESCRIPTION = "FTP Server Configuration File";

    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARN = "WARN";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_FATAL = "FATAL";
    public static final String[] LOG_LEVELS_VALID = {LOG_LEVEL_DEBUG, LOG_LEVEL_INFO, LOG_LEVEL_WARN, LOG_LEVEL_ERROR, LOG_LEVEL_FATAL};

    public static final String FTP_WELCOME_MESSAGE = "Java FTP Server";
    public static final int FTP_BCRYPT_ROUNDS = 12;
    public static final String FTP_ANONYMOUS_USER = "anonymous";

    public static final int THREADPOOL_CORE_SIZE_FACTOR = 10;
    public static final long THREADPOOL_KEEP_ALIVE_TIME_SECONDS = 60;
    public static final int THREADPOOL_WORK_QUEUE_CAPACITY = 100;

    public static final long LOGGING_MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final int LOGGING_MAX_BACKUP_INDEX = 5;
    public static final String LOGGING_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String RESPONSE_CODE_150 = "150 File status okay; about to open data connection.";
    public static final String RESPONSE_CODE_200 = "200 Command okay.";
    public static final String RESPONSE_CODE_226 = "226 Closing data connection. Requested file action successful.";
    public static final String RESPONSE_CODE_331 = "331 User name okay, need password.";
    public static final String RESPONSE_CODE_426 = "426 Connection closed; transfer aborted.";
    public static final String RESPONSE_CODE_451 = "451 Requested action aborted. Local error in processing.";
    public static final String RESPONSE_CODE_500 = "500 Syntax error, command unrecognized.";
    public static final String RESPONSE_CODE_501 = "501 Syntax error in parameters or arguments.";
    public static final String RESPONSE_CODE_502 = "502 Command not implemented.";
    public static final String RESPONSE_CODE_530 = "530 Not logged in.";
    public static final String RESPONSE_CODE_550 = "550 Requested action not taken. File unavailable.";
}
