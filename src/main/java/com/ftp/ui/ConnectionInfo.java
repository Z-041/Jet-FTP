package com.ftp.ui;

import java.net.InetAddress;
import java.util.Date;

public class ConnectionInfo {
    private final String id;
    private final InetAddress address;
    private String username;
    private final Date connectTime;
    private boolean authenticated;

    public ConnectionInfo(String id, InetAddress address) {
        this.id = id;
        this.address = address;
        this.connectTime = new Date();
        this.authenticated = false;
    }

    public String getId() {
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
