package com.ftp.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

public class UILogbackAppender extends AppenderBase<ILoggingEvent> {

    private final Logger customLogger;

    public UILogbackAppender() {
        this.customLogger = Logger.getInstance();
    }

    @Override
    protected void append(ILoggingEvent event) {
        try {
            LogLevel level = convertLevel(event.getLevel());
            String message = event.getFormattedMessage();
            Throwable throwable = null;
            
            if (event.getThrowableProxy() != null && event.getThrowableProxy() instanceof ThrowableProxy) {
                ThrowableProxy proxy = (ThrowableProxy) event.getThrowableProxy();
                throwable = proxy.getThrowable();
            }
            
            // 转发到我们的自定义Logger
            switch (level) {
                case DEBUG:
                    customLogger.debug(message, throwable);
                    break;
                case INFO:
                    customLogger.info(message, throwable);
                    break;
                case WARN:
                    customLogger.warn(message, throwable);
                    break;
                case ERROR:
                    customLogger.error(message, throwable);
                    break;
            }
        } catch (Exception e) {
            addError("Error in UILogbackAppender", e);
        }
    }
    
    private LogLevel convertLevel(Level level) {
        if (level == Level.DEBUG || level == Level.TRACE) {
            return LogLevel.DEBUG;
        } else if (level == Level.INFO) {
            return LogLevel.INFO;
        } else if (level == Level.WARN) {
            return LogLevel.WARN;
        } else if (level == Level.ERROR) {
            return LogLevel.ERROR;
        }
        return LogLevel.INFO;
    }
}
