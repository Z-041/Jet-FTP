package com.ftp.session;

import com.ftp.data.DataConnectionManager;
import com.ftp.util.BandwidthLimiter;

import java.io.File;

public class Session {
    private final FileSystemContext fileSystemContext;
    private final AuthenticationContext authContext;
    private final TransferContext transferContext;
    private final SessionOptions sessionOptions;
    private final DataConnectionManager dataConnectionManager;
    private final BandwidthLimiter bandwidthLimiter;

    public Session(File rootDirectory) {
        this.fileSystemContext = new FileSystemContext(rootDirectory);
        this.authContext = new AuthenticationContext();
        this.transferContext = new TransferContext();
        this.sessionOptions = new SessionOptions();
        this.dataConnectionManager = new DataConnectionManager();
        this.bandwidthLimiter = new BandwidthLimiter();
    }

    public FileSystemContext getFileSystemContext() {
        return fileSystemContext;
    }

    public AuthenticationContext getAuthContext() {
        return authContext;
    }

    public TransferContext getTransferContext() {
        return transferContext;
    }

    public SessionOptions getSessionOptions() {
        return sessionOptions;
    }

    public DataConnectionManager getDataConnectionManager() {
        return dataConnectionManager;
    }

    public BandwidthLimiter getBandwidthLimiter() {
        return bandwidthLimiter;
    }

    public void resetDataConnection() {
        transferContext.reset();
    }

    public void reset() {
        fileSystemContext.resetToRoot();
        authContext.clear();
        transferContext.reset();
    }
}
