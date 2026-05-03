package com.ftp.session;

import com.ftp.model.User;

public class AuthenticationContext {
    private User user;
    private boolean authenticated;
    private String usernamePending;

    public AuthenticationContext() {
        this.authenticated = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUsernamePending() {
        return usernamePending;
    }

    public void setUsernamePending(String usernamePending) {
        this.usernamePending = usernamePending;
    }

    public void clear() {
        this.user = null;
        this.authenticated = false;
        this.usernamePending = null;
    }

    public void clearPendingUsername() {
        this.usernamePending = null;
    }
}
