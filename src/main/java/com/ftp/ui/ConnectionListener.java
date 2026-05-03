package com.ftp.ui;

public interface ConnectionListener {
    void onConnectionAdded(ConnectionInfo info);
    void onConnectionRemoved(String id);
    void onConnectionUpdated(ConnectionInfo info);
}
