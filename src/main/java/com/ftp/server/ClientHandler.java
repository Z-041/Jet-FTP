package com.ftp.server;

import com.ftp.command.CommandFactory;
import com.ftp.command.CommandHandler;
import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.protocol.CommandParser;
import com.ftp.protocol.ResponseGenerator;
import com.ftp.session.Session;
import com.ftp.ui.ConnectionInfo;
import com.ftp.ui.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Logger logger;
    private final Session session;
    private volatile boolean running;
    private final String handlerId;
    private final ConnectionInfo connectionInfo;
    private final List<ConnectionListener> connectionListeners;
    private final CommandFactory commandFactory;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.logger = LoggerFactory.getLogger(ClientHandler.class);
        this.handlerId = UUID.randomUUID().toString();
        this.connectionListeners = new ArrayList<>();
        this.commandFactory = CommandFactory.getInstance();
        
        Config config = ConfigManager.getInstance().getConfig();
        File rootDir = new File(config.getRootDirectory());
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        this.session = new Session(rootDir.getCanonicalFile());
        this.session.getSessionOptions().setClientAddress(clientSocket.getInetAddress());
        this.running = true;
        this.connectionInfo = new ConnectionInfo(handlerId, clientSocket.getInetAddress());
    }

    public String getHandlerId() {
        return handlerId;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
             OutputStream out = clientSocket.getOutputStream()) {
            
            logger.info("Client connected: " + clientSocket.getInetAddress());
            sendResponse(out, ResponseGenerator.welcome("Java FTP Server"));

            String line;
            while (running && (line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                logger.info("Received command: " + line);
                
                CommandParser parser = new CommandParser(line);
                String commandName = parser.getCommand();
                String argument = parser.getArgument();

                CommandHandler handler = commandFactory.getCommand(commandName);

                if (handler == null) {
                    String upperCmd = commandName.toUpperCase();
                    if (isCommonExploratoryCommand(upperCmd)) {
                        logger.debug("Exploratory command, returning 200: " + commandName);
                        sendResponse(out, ResponseGenerator.CODE_200);
                    } else {
                        logger.warn("Command not implemented: " + commandName);
                        sendResponse(out, ResponseGenerator.CODE_502);
                    }
                    continue;
                }

                if (handler.requiresAuthentication() && !session.getAuthContext().isAuthenticated()) {
                    sendResponse(out, ResponseGenerator.CODE_530);
                    continue;
                }

                if (handler.isExploratory()) {
                    logger.debug("Exploratory command, returning 200: " + commandName);
                    sendResponse(out, ResponseGenerator.CODE_200);
                    continue;
                }

                try {
                    handler.handle(argument, session, out);
                    
                    if ("QUIT".equals(commandName)) {
                        running = false;
                    }
                } catch (Exception e) {
                    logger.error("Error handling command: " + commandName, e);
                    sendResponse(out, ResponseGenerator.CODE_451);
                }
            }
        } catch (IOException e) {
            if (running && !clientSocket.isClosed()) {
                logger.error("Error in client handler", e);
            } else {
                logger.info("Client disconnected normally: " + clientSocket.getInetAddress());
            }
        } finally {
            cleanup();
        }
    }

    private void sendResponse(OutputStream out, String response) throws IOException {
        out.write((response + "\r\n").getBytes("UTF-8"));
        out.flush();
        logger.info("Sent response: " + response);
    }

    private void cleanup() {
        session.getDataConnectionManager().closePassiveServerSocket();
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            logger.warn("Error closing client socket", e);
        }
        logger.info("Client disconnected: " + clientSocket.getInetAddress());
    }

    public void stop() {
        running = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.warn("Error stopping client handler", e);
        }
    }

    private static final Set<String> EXPLORATORY_COMMANDS = Set.of(
        "XCRC", "XCWD", "XPWD", "XSEN", "XSEM", "XSHR", "XDEL",
        "XCUP", "XRS", "XRM", "XMKD", "XRMD", "XCDUP",
        "CLNT", "MFMT", "MFCT", "AVBL", "EPRT",
        "LANG", "TVFS", "PROT", "PBSZ",
        "CCC", "AUTH", "ADAT", "MIC", "CONF", "ENC",
        "CPSV", "ALGS", "SPSV", "LPRT", "HOST", "RANG",
        "ESTA", "STOU", "ABOR"
    );

    private boolean isCommonExploratoryCommand(String command) {
        return EXPLORATORY_COMMANDS.contains(command);
    }
}
