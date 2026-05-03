package com.ftp.session;

public class TransferContext {
    private TransferType transferType;
    private boolean passiveMode;
    private int passivePort;
    private String passiveAddress;
    private String activeAddress;
    private int activePort;
    private int activeProtocol;
    private long restartPosition;

    public enum TransferType {
        ASCII, BINARY
    }

    public TransferContext() {
        this.transferType = TransferType.ASCII;
        this.passiveMode = false;
        this.activeProtocol = 1;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public int getPassivePort() {
        return passivePort;
    }

    public void setPassivePort(int passivePort) {
        this.passivePort = passivePort;
    }

    public String getPassiveAddress() {
        return passiveAddress;
    }

    public void setPassiveAddress(String passiveAddress) {
        this.passiveAddress = passiveAddress;
    }

    public String getActiveAddress() {
        return activeAddress;
    }

    public void setActiveAddress(String activeAddress) {
        this.activeAddress = activeAddress;
    }

    public int getActivePort() {
        return activePort;
    }

    public void setActivePort(int activePort) {
        this.activePort = activePort;
    }

    public int getActiveProtocol() {
        return activeProtocol;
    }

    public void setActiveProtocol(int activeProtocol) {
        if (activeProtocol == 1 || activeProtocol == 2) {
            this.activeProtocol = activeProtocol;
        } else {
            throw new IllegalArgumentException("Protocol must be 1 (IPv4) or 2 (IPv6)");
        }
    }

    public long getRestartPosition() {
        return restartPosition;
    }

    public void setRestartPosition(long restartPosition) {
        this.restartPosition = restartPosition;
    }

    public void reset() {
        this.passiveMode = false;
        this.passivePort = 0;
        this.passiveAddress = null;
        this.activeAddress = null;
        this.activePort = 0;
        this.activeProtocol = 1;
        this.restartPosition = 0;
    }
}
