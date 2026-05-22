package com.ftp.constants;

public final class FtpConstants {

    private FtpConstants() {
    }

    public static final class Encoding {
        public static final String UTF_8 = "UTF-8";
        public static final String ISO_8859_1 = "ISO-8859-1";
        public static final String DEFAULT_ENCODING = UTF_8;
    }

    public static final class Defaults {
        public static final int DEFAULT_PORT = 21;
        public static final int DEFAULT_MAX_CONNECTIONS = 10;
        public static final int DEFAULT_TIMEOUT_SECONDS = 300;
        public static final String DEFAULT_ROOT_DIRECTORY = "ftp-root";
        public static final String DEFAULT_LOG_LEVEL = "INFO";
        public static final String DEFAULT_LOG_FILE_PATH = "logs/ftp-server.log";
        public static final int DEFAULT_BCRYPT_ROUNDS = 12;
    }

    public static final class Limits {
        public static final int MIN_PORT = 1;
        public static final int MAX_PORT = 65535;
        public static final int MIN_MAX_CONNECTIONS = 1;
        public static final int MAX_MAX_CONNECTIONS = 1000;
        public static final int MIN_TIMEOUT = 10;
        public static final int MAX_TIMEOUT = 3600;
        public static final int MIN_BCRYPT_ROUNDS = 4;
        public static final int MAX_BCRYPT_ROUNDS = 31;
    }

    public static final class BufferSizes {
        public static final int DEFAULT_BUFFER_SIZE = 8192;
        public static final int SMALL_BUFFER_SIZE = 1024;
        public static final int LARGE_BUFFER_SIZE = 16384;
    }

    public static final class ConfigKeys {
        public static final String SERVER_PORT = "server.port";
        public static final String SERVER_ROOT_DIRECTORY = "server.rootDirectory";
        public static final String SERVER_MAX_CONNECTIONS = "server.maxConnections";
        public static final String SERVER_TIMEOUT_SECONDS = "server.timeoutSeconds";
        public static final String SERVER_THREADPOOL_CORE_SIZE = "server.threadPool.coreSize";
        public static final String SERVER_THREADPOOL_KEEP_ALIVE_SECONDS = "server.threadPool.keepAliveSeconds";
        public static final String SERVER_THREADPOOL_QUEUE_CAPACITY = "server.threadPool.queueCapacity";
        public static final String SERVER_BIND_ADDRESS = "server.bindAddress";
        public static final String SERVER_DUAL_STACK_ENABLED = "server.dualStackEnabled";
        public static final String SERVER_LISTEN_INTERFACE = "server.listenInterface";
        public static final String SERVER_PREFER_IPV6 = "server.preferIPv6";
        public static final String LOG_LEVEL = "log.level";
        public static final String LOG_FILE_PATH = "log.filePath";
        public static final String PASSIVE_MODE_EXTERNAL_IP = "passiveMode.externalIp";
        public static final String PASSIVE_MODE_PORT_MIN = "passiveMode.portMin";
        public static final String PASSIVE_MODE_PORT_MAX = "passiveMode.portMax";
        public static final String PASSIVE_MODE_CONNECTION_TIMEOUT = "passiveMode.connectionTimeout";
        public static final String PASSIVE_MODE_IPV4_EXTERNAL_IP = "passiveMode.ipv4ExternalIp";
        public static final String PASSIVE_MODE_IPV6_EXTERNAL_IP = "passiveMode.ipv6ExternalIp";
        public static final String SECURITY_BCRYPT_ROUNDS = "security.bcryptRounds";
    }

    public static final class ConfigFile {
        public static final String DEFAULT_CONFIG_FILE = "ftp-server.properties";
        public static final String CONFIG_DESCRIPTION = "FTP Server Configuration File";
    }

    public static final class LogLevels {
        public static final String DEBUG = "DEBUG";
        public static final String INFO = "INFO";
        public static final String WARN = "WARN";
        public static final String ERROR = "ERROR";
        public static final String FATAL = "FATAL";
        
        public static final String[] VALID_LEVELS = {DEBUG, INFO, WARN, ERROR, FATAL};
    }

    public static final class FTP {
        public static final String WELCOME_MESSAGE = "Java FTP Server";
        public static final int BCRYPT_ROUNDS = 12;
        public static final String ANONYMOUS_USER = "anonymous";
    }

    public static final class ThreadPool {
        public static final int CORE_POOL_SIZE_FACTOR = 10;
        public static final long KEEP_ALIVE_TIME_SECONDS = 60;
        public static final int WORK_QUEUE_CAPACITY = 100;
    }

    public static final class Logging {
        public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024;
        public static final int DEFAULT_MAX_BACKUP_INDEX = 5;
        public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    }

    public static final class ResponseCodes {
        public static final String CODE_150 = "150 File status okay; about to open data connection.";
        public static final String CODE_200 = "200 Command okay.";
        public static final String CODE_226 = "226 Closing data connection. Requested file action successful.";
        public static final String CODE_331 = "331 User name okay, need password.";
        public static final String CODE_426 = "426 Connection closed; transfer aborted.";
        public static final String CODE_451 = "451 Requested action aborted. Local error in processing.";
        public static final String CODE_500 = "500 Syntax error, command unrecognized.";
        public static final String CODE_501 = "501 Syntax error in parameters or arguments.";
        public static final String CODE_502 = "502 Command not implemented.";
        public static final String CODE_530 = "530 Not logged in.";
        public static final String CODE_550 = "550 Requested action not taken. File unavailable.";
    }
}
