package com.ftp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginAttemptManager {
    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptManager.class);

    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final long DEFAULT_LOCKOUT_DURATION_MS = 15 * 60 * 1000;

    private final int maxAttempts;
    private final long lockoutDurationMs;
    private final ConcurrentHashMap<String, LoginAttempt> attempts;

    private static class LoginAttemptManagerHolder {
        private static final LoginAttemptManager INSTANCE = new LoginAttemptManager();
    }

    private LoginAttemptManager() {
        this(DEFAULT_MAX_ATTEMPTS, DEFAULT_LOCKOUT_DURATION_MS);
    }

    public LoginAttemptManager(int maxAttempts, long lockoutDurationMs) {
        this.maxAttempts = maxAttempts;
        this.lockoutDurationMs = lockoutDurationMs;
        this.attempts = new ConcurrentHashMap<>();
    }

    public static LoginAttemptManager getInstance() {
        return LoginAttemptManagerHolder.INSTANCE;
    }

    public boolean isBlocked(InetAddress address) {
        String ip = address.getHostAddress();
        LoginAttempt attempt = attempts.get(ip);
        if (attempt == null) {
            return false;
        }
        if (attempt.isExpired(lockoutDurationMs)) {
            attempts.remove(ip);
            return false;
        }
        return attempt.getFailedAttempts() >= maxAttempts;
    }

    public void recordFailedAttempt(InetAddress address) {
        String ip = address.getHostAddress();
        LoginAttempt result = attempts.compute(ip, (key, existing) -> {
            if (existing == null) {
                return new LoginAttempt(1, System.currentTimeMillis());
            }
            existing.increment();
            existing.updateTimestamp();
            return existing;
        });

        if (result != null && result.getFailedAttempts() >= maxAttempts) {
            logger.warn("IP blocked due to multiple failed login attempts: " + ip);
        }
    }

    public void clearAttempts(InetAddress address) {
        String ip = address.getHostAddress();
        attempts.remove(ip);
        logger.info("Login attempts cleared for IP: " + ip);
    }

    public int getFailedAttempts(InetAddress address) {
        String ip = address.getHostAddress();
        LoginAttempt attempt = attempts.get(ip);
        if (attempt == null || attempt.isExpired(lockoutDurationMs)) {
            return 0;
        }
        return attempt.getFailedAttempts();
    }

    public long getRemainingLockoutTimeMs(InetAddress address) {
        String ip = address.getHostAddress();
        LoginAttempt attempt = attempts.get(ip);
        if (attempt == null) {
            return 0;
        }
        return Math.max(0, lockoutDurationMs - (System.currentTimeMillis() - attempt.getTimestamp()));
    }

    private static class LoginAttempt {
        private final AtomicInteger failedAttempts;
        private volatile long timestamp;

        public LoginAttempt(int initial, long timestamp) {
            this.failedAttempts = new AtomicInteger(initial);
            this.timestamp = timestamp;
        }

        public void increment() {
            failedAttempts.incrementAndGet();
        }

        public int getFailedAttempts() {
            return failedAttempts.get();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void updateTimestamp() {
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired(long durationMs) {
            return System.currentTimeMillis() - timestamp > durationMs;
        }
    }
}