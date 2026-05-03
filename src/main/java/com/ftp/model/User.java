package com.ftp.model;

import java.util.Objects;

public final class User {
    private final String username;
    private final String passwordHash;
    private final String homeDirectory;
    private final boolean anonymous;

    public User(String username, String passwordHash, String homeDirectory) {
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.homeDirectory = Objects.requireNonNull(homeDirectory, "Home directory cannot be null");
        this.anonymous = false;
    }

    private User(String username, String passwordHash, String homeDirectory, boolean anonymous) {
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.homeDirectory = Objects.requireNonNull(homeDirectory, "Home directory cannot be null");
        this.anonymous = anonymous;
    }

    public User withAnonymous(boolean anonymous) {
        return new User(username, passwordHash, homeDirectory, anonymous);
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return anonymous == user.anonymous &&
               Objects.equals(username, user.username) &&
               Objects.equals(passwordHash, user.passwordHash) &&
               Objects.equals(homeDirectory, user.homeDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, homeDirectory, anonymous);
    }

    @Override
    public String toString() {
        return "User{" +
               "username='" + username + '\'' +
               ", homeDirectory='" + homeDirectory + '\'' +
               ", anonymous=" + anonymous +
               '}';
    }
}
