package com.ftp;

import com.ftp.auth.UserManager;
import com.ftp.config.ConfigManager;
import com.ftp.config.LoggerConfigListener;
import com.ftp.server.FtpServer;
import com.ftp.ui.FtpServerUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Initializing FTP Server...");

            UserManager.getInstance();

            FtpServer server = new FtpServer();

            ConfigManager.getInstance().addConfigChangeListener(new LoggerConfigListener());
            logger.info("Configuration change listener registered");

            FtpServerUI.createAndShowUI(server);
        } catch (RuntimeException e) {
            logger.error("Failed to initialize application", e);
            System.exit(1);
        } catch (Error e) {
            logger.error("Failed to initialize application due to critical error", e);
            System.exit(1);
        }
    }
}
