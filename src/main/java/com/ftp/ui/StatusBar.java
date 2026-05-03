package com.ftp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StatusBar extends JPanel {
    private final JLabel statusLabel;
    private final JLabel hostLabel;
    private final JLabel versionLabel;
    private final JLabel timeLabel;
    private Timer timeTimer;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.BG_WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyles.BORDER_COLOR),
            new EmptyBorder(8, 20, 8, 20)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        leftPanel.setOpaque(false);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusPanel.setOpaque(false);

        JLabel statusIcon = new JLabel("●");
        statusIcon.setFont(new Font(UIStyles.FONT_FAMILY, Font.BOLD, 10));
        statusIcon.setForeground(UIStyles.TEXT_MUTED);

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(UIStyles.FONT_SMALL);
        statusLabel.setForeground(UIStyles.TEXT_SECONDARY);

        statusPanel.add(statusIcon);
        statusPanel.add(statusLabel);

        hostLabel = createInfoLabel("主机: " + getHostName());

        leftPanel.add(statusPanel);
        leftPanel.add(createSeparator());
        leftPanel.add(hostLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 0));
        rightPanel.setOpaque(false);

        timeLabel = createInfoLabel("");
        versionLabel = createInfoLabel("v1.0.0");

        rightPanel.add(timeLabel);
        rightPanel.add(createSeparator());
        rightPanel.add(versionLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        startTimeTimer();
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_SMALL);
        label.setForeground(UIStyles.TEXT_SECONDARY);
        return label;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 14));
        separator.setForeground(UIStyles.BORDER_COLOR);
        return separator;
    }

    private String getHostName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            if (hostname != null && hostname.length() > 15) {
                hostname = hostname.substring(0, 15) + "...";
            }
            return hostname != null ? hostname : "localhost";
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    private void startTimeTimer() {
        updateTime();
        timeTimer = new Timer(1000, e -> updateTime());
        timeTimer.start();
    }

    private void updateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText(sdf.format(new java.util.Date()));
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void stopTimer() {
        if (timeTimer != null) {
            timeTimer.stop();
        }
    }
}
