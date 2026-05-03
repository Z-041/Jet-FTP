package com.ftp.service;

import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.server.FtpServer;
import com.ftp.ui.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FtpServerServiceImpl implements FtpServerService {

    private static final Logger logger = LoggerFactory.getLogger(FtpServerServiceImpl.class);
    private final FtpServer ftpServer;
    
    public FtpServerServiceImpl() {
        this(new FtpServer());
    }
    
    public FtpServerServiceImpl(FtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }
    
    @Override
    public void start() throws IOException {
        logger.info("Starting FTP server service...");
        ftpServer.start();
        logger.info("FTP server service started");
    }
    
    @Override
    public void stop() {
        logger.info("Stopping FTP server service...");
        ftpServer.stop();
        logger.info("FTP server service stopped");
    }
    
    @Override
    public boolean isRunning() {
        return ftpServer.isRunning();
    }
    
    @Override
    public int getActiveConnections() {
        return ftpServer.getActiveConnections();
    }
    
    @Override
    public int getPort() {
        return ftpServer.getPort();
    }
    
    @Override
    public int getMaxConnections() {
        return ftpServer.getMaxConnections();
    }
    
    @Override
    public void reloadConfig() {
        logger.info("Reloading FTP server configuration...");
        ftpServer.reloadConfig();
        logger.info("FTP server configuration reloaded");
    }
    
    @Override
    public void addConnectionListener(ConnectionListener listener) {
        ftpServer.addConnectionListener(listener);
    }
    
    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        ftpServer.removeConnectionListener(listener);
    }
    
    @Override
    public Config getConfig() {
        return ConfigManager.getInstance().getConfig();
    }
}
