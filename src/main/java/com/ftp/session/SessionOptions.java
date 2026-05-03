package com.ftp.session;

import java.net.InetAddress;

public class SessionOptions {
    private boolean utf8Enabled;
    private InetAddress clientAddress;
    private InetAddress passiveBindAddress;

    public SessionOptions() {
        this.utf8Enabled = false;
    }

    public boolean isUtf8Enabled() {
        return utf8Enabled;
    }

    public void setUtf8Enabled(boolean utf8Enabled) {
        this.utf8Enabled = utf8Enabled;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public InetAddress getPassiveBindAddress() {
        return passiveBindAddress;
    }

    public void setPassiveBindAddress(InetAddress passiveBindAddress) {
        this.passiveBindAddress = passiveBindAddress;
    }

    public boolean isClientIPv6() {
        return clientAddress instanceof java.net.Inet6Address;
    }

    public boolean isClientIPv4() {
        return clientAddress instanceof java.net.Inet4Address;
    }
}
