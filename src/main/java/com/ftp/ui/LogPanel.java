package com.ftp.ui;

import com.ftp.config.Config;
import com.ftp.config.ConfigChangeListener;
import com.ftp.config.ConfigManager;
import com.ftp.logging.LogLevel;
import com.ftp.ui.LogListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPanel extends JPanel implements LogListener {

    public enum DisplayLogLevel {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3);

        private final int level;

        DisplayLogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public static DisplayLogLevel fromString(String levelStr) {
            if (levelStr == null) {
                return INFO;
            }
            try {
                return valueOf(levelStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return INFO;
            }
        }

        public boolean isEnabledFor(DisplayLogLevel minLevel) {
            return this.level >= minLevel.level;
        }
    }

    private final JTextPane logTextPane;
    private final StyledDocument logDocument;
    private final JComboBox<DisplayLogLevel> levelFilterComboBox;
    private final JButton clearButton;
    private final JCheckBox autoScrollCheckBox;
    private final SimpleAttributeSet debugStyle;
    private final SimpleAttributeSet infoStyle;
    private final SimpleAttributeSet warnStyle;
    private final SimpleAttributeSet errorStyle;
    private final SimpleDateFormat dateFormat;
    private DisplayLogLevel currentFilterLevel;

    public LogPanel() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyles.BG_WHITE);

        dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        Config config = ConfigManager.getInstance().getConfig();
        currentFilterLevel = DisplayLogLevel.fromString(config.getLogLevel());

        debugStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(debugStyle, UIStyles.TEXT_MUTED);
        StyleConstants.setFontFamily(debugStyle, UIStyles.FONT_MONO);
        StyleConstants.setFontSize(debugStyle, 12);

        infoStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(infoStyle, UIStyles.TEXT_PRIMARY);
        StyleConstants.setFontFamily(infoStyle, UIStyles.FONT_MONO);
        StyleConstants.setFontSize(infoStyle, 12);

        warnStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(warnStyle, UIStyles.WARNING_COLOR);
        StyleConstants.setFontFamily(warnStyle, UIStyles.FONT_MONO);
        StyleConstants.setFontSize(warnStyle, 12);

        errorStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(errorStyle, UIStyles.DANGER_COLOR);
        StyleConstants.setFontFamily(errorStyle, UIStyles.FONT_MONO);
        StyleConstants.setFontSize(errorStyle, 12);

        logTextPane = new JTextPane();
        logTextPane.setEditable(false);
        logTextPane.setFont(UIStyles.FONT_MONO_NORMAL);
        logTextPane.setBackground(new Color(253, 254, 254));
        logTextPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        logDocument = logTextPane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(logTextPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(253, 254, 254));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel filterLabel = new JLabel("日志级别");
        filterLabel.setFont(UIStyles.FONT_SMALL);
        filterLabel.setForeground(UIStyles.TEXT_SECONDARY);

        levelFilterComboBox = new JComboBox<>(DisplayLogLevel.values());
        levelFilterComboBox.setSelectedItem(currentFilterLevel);
        levelFilterComboBox.setFont(UIStyles.FONT_SMALL);
        levelFilterComboBox.setBackground(UIStyles.BG_WHITE);
        levelFilterComboBox.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));

        clearButton = createButton("清空日志", UIStyles.TEXT_SECONDARY);

        autoScrollCheckBox = new JCheckBox("自动滚动", true);
        autoScrollCheckBox.setFont(UIStyles.FONT_SMALL);
        autoScrollCheckBox.setForeground(UIStyles.TEXT_SECONDARY);
        autoScrollCheckBox.setBackground(UIStyles.BG_WHITE);
        autoScrollCheckBox.setBorderPainted(false);
        autoScrollCheckBox.setFocusPainted(false);

        controlPanel.add(filterLabel);
        controlPanel.add(levelFilterComboBox);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(clearButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(autoScrollCheckBox);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setupActionListeners();
        addConfigChangeListener();
    }

    private void addConfigChangeListener() {
        ConfigManager.getInstance().addConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void onConfigChanged(Config oldConfig, Config newConfig) {
                if (newConfig != null) {
                    DisplayLogLevel newLevel = DisplayLogLevel.fromString(newConfig.getLogLevel());
                    currentFilterLevel = newLevel;
                    SwingUtilities.invokeLater(() -> {
                        levelFilterComboBox.setSelectedItem(newLevel);
                    });
                }
            }
        });
    }
    
    @Override
    public void onLog(com.ftp.logging.LogLevel level, String message, Throwable throwable) {
        DisplayLogLevel displayLevel = convertLogLevel(level);
        appendLog(displayLevel, message, throwable);
    }
    
    private DisplayLogLevel convertLogLevel(com.ftp.logging.LogLevel level) {
        return switch (level) {
            case DEBUG -> DisplayLogLevel.DEBUG;
            case INFO -> DisplayLogLevel.INFO;
            case WARN -> DisplayLogLevel.WARN;
            case ERROR -> DisplayLogLevel.ERROR;
        };
    }

    private JButton createButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(UIStyles.FONT_SMALL);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 28));
        button.setBorder(new EmptyBorder(0, 12, 0, 12));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.lighten(baseColor, 0.15f));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }
        });

        return button;
    }

    private void setupActionListeners() {
        levelFilterComboBox.addActionListener(e -> {
            currentFilterLevel = (DisplayLogLevel) levelFilterComboBox.getSelectedItem();
        });

        clearButton.addActionListener(e -> clearLogs());
    }

    private void clearLogs() {
        try {
            logDocument.remove(0, logDocument.getLength());
        } catch (BadLocationException ex) {
            System.err.println("Failed to clear logs: " + ex.getMessage());
        }
    }

    public void appendLog(DisplayLogLevel level, String message, Throwable throwable) {
        if (!level.isEnabledFor(currentFilterLevel)) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                String timestamp = dateFormat.format(new Date());
                String levelStr = String.format("%-5s", level.toString());
                String logMessage = String.format("[%s] [%s] %s%n", timestamp, levelStr, message);

                SimpleAttributeSet style;
                switch (level) {
                    case DEBUG:
                        style = debugStyle;
                        break;
                    case INFO:
                        style = infoStyle;
                        break;
                    case WARN:
                        style = warnStyle;
                        break;
                    case ERROR:
                        style = errorStyle;
                        break;
                    default:
                        style = infoStyle;
                }

                logDocument.insertString(logDocument.getLength(), logMessage, style);

                if (throwable != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    logDocument.insertString(logDocument.getLength(), sw.toString() + "\n", style);
                }

                if (autoScrollCheckBox.isSelected()) {
                    logTextPane.setCaretPosition(logDocument.getLength());
                }
            } catch (BadLocationException ex) {
                System.err.println("Failed to insert log message: " + ex.getMessage());
            }
        });
    }
}
