package com.ftp.auth;

import com.ftp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

class UserFileHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserFileHandler.class);
    private final String usersFile;

    UserFileHandler(String usersFile) {
        this.usersFile = usersFile;
    }

    void loadUsers(Map<String, User> users) {
        Path usersPath = Paths.get(usersFile);
        if (!Files.exists(usersPath)) {
            logger.debug("Users file does not exist: " + usersFile);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(usersPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|", 4);
                if (parts.length >= 3) {
                    parseUserLine(parts, users);
                }
            }
            logger.info("Loaded " + users.size() + " users from " + usersFile);
        } catch (IOException e) {
            logger.error("Failed to load users file", e);
        }
    }

    private void parseUserLine(String[] parts, Map<String, User> users) {
        String username = parts[0];
        String passwordHash = parts[1];
        String rootDir = parts[2];
        boolean anonymous = parts.length > 3 && Boolean.parseBoolean(parts[3]);

        User user = new User(username, passwordHash, rootDir).withAnonymous(anonymous);
        users.put(username.toLowerCase(), user);
    }

    void saveUsers(Map<String, User> users) {
        Path usersPath = Paths.get(usersFile);
        try {
            ensureDirectoryExists(usersPath);
            writeUsersFile(users, usersPath);
            setSecurePermissions(usersPath);
            logger.info("Saved " + users.size() + " users to " + usersFile);
        } catch (IOException e) {
            logger.error("Failed to save users file", e);
        }
    }

    private void ensureDirectoryExists(Path path) throws IOException {
        Path parentPath = path.getParent();
        if (parentPath != null) {
            File parentDir = parentPath.toFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
        }
    }

    private void writeUsersFile(Map<String, User> users, Path path) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8)))) {
            writer.println("# FTP Server Users Database");
            writer.println("# Format: username|passwordHash|rootDirectory|anonymous");
            writer.println("# Password hashes are BCrypt encoded");
            writer.println("# anonymous users have empty password and anonymous=true");
            writer.println();

            for (User user : users.values()) {
                if (user.isAnonymous()) {
                    writer.println(String.format("%s||%s|true",
                        user.getUsername(),
                        user.getHomeDirectory()));
                } else {
                    writer.println(String.format("%s|%s|%s|false",
                        user.getUsername(),
                        user.getPasswordHash(),
                        user.getHomeDirectory()));
                }
            }
        }
    }

    private void setSecurePermissions(Path path) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return;
        }

        try {
            Files.setPosixFilePermissions(path, Set.of(
                java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
            ));
        } catch (UnsupportedOperationException e) {
            logger.debug("POSIX file permissions not supported on this operating system");
        } catch (IOException e) {
            logger.warn("Failed to set file permissions", e);
        }
    }
}
