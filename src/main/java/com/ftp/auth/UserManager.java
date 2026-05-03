package com.ftp.auth;

import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UserManager {
    private static final String USERS_FILE = "users.dat";
    private final Map<String, User> users;
    private final Logger logger;
    private final UserFileHandler userFileHandler;
    private final int bcryptRounds;

    private static class UserManagerHolder {
        private static final UserManager INSTANCE = new UserManager();
    }

    private UserManager() {
        this.users = new HashMap<>();
        this.logger = LoggerFactory.getLogger(UserManager.class);
        this.userFileHandler = new UserFileHandler(USERS_FILE);
        this.bcryptRounds = ConfigManager.getInstance().getConfig().getBcryptRounds();
        loadUsers();
    }

    public static UserManager getInstance() {
        return UserManagerHolder.INSTANCE;
    }

    private void loadUsers() {
        userFileHandler.loadUsers(users);

        if (users.isEmpty()) {
            initializeDefaultUsers();
            saveUsers();
        }
    }

    private void saveUsers() {
        userFileHandler.saveUsers(users);
    }

    private void initializeDefaultUsers() {
        Config config = ConfigManager.getInstance().getConfig();
        String rootDirectory = config.getRootDirectory();

        String hashedPassword = BCrypt.hashpw("ftp", BCrypt.gensalt(bcryptRounds));
        User defaultUser = new User("ftp", hashedPassword, rootDirectory);
        users.put(defaultUser.getUsername().toLowerCase(), defaultUser);
        logger.info("Default user created: ftp/ftp");

        User anonymousUser = new User("anonymous", "", rootDirectory).withAnonymous(true);
        users.put(anonymousUser.getUsername().toLowerCase(), anonymousUser);
        logger.info("Anonymous user created: anonymous/<any password>");
    }

    public void addUser(User user) {
        users.put(user.getUsername().toLowerCase(), user);
        saveUsers();
        logger.info("User added: " + user.getUsername());
    }

    public void removeUser(String username) {
        users.remove(username.toLowerCase());
        saveUsers();
        logger.info("User removed: " + username);
    }

    public User getUser(String username) {
        return users.get(username.toLowerCase());
    }

    public boolean authenticate(String username, String password) {
        User user = getUser(username);
        if (user == null) {
            logger.warn("Authentication failed: user not found - " + username);
            return false;
        }

        if (user.isAnonymous()) {
            logger.info("Anonymous authentication successful");
            return true;
        }

        boolean authenticated = BCrypt.checkpw(password, user.getPasswordHash());
        if (authenticated) {
            logger.info("Authentication successful: " + username);
        } else {
            logger.warn("Authentication failed: invalid password for user - " + username);
        }
        return authenticated;
    }

    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public void updateUserPassword(String username, String newPassword) {
        User user = getUser(username);
        if (user != null && !user.isAnonymous()) {
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(bcryptRounds));
            User updatedUser = new User(username, newHash, user.getHomeDirectory()).withAnonymous(false);
            users.put(username.toLowerCase(), updatedUser);
            saveUsers();
            logger.info("Password updated for user: " + username);
        }
    }
}
