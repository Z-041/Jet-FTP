package com.ftp.ui;

import com.ftp.config.Config;
import com.ftp.config.ConfigChangeListener;
import com.ftp.config.ConfigManager;
import com.ftp.server.FtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeftControlPanel extends JPanel implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(LeftControlPanel.class);
    private final FtpServer server;
    private JLabel statusIndicator;
    private JLabel statusText;
    private JLabel portValueLabel;
    private JLabel rootDirValueLabel;
    private JLabel maxConnValueLabel;
    private JLabel timeoutValueLabel;
    private JLabel runTimeLabel;
    private JLabel currentConnValueLabel;
    private Timer runtimeTimer;
    private long startTime;

    public LeftControlPanel(FtpServer server) {
        this.server = server;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 15));
        setPreferredSize(new Dimension(240, 0));
        setBackground(UIStyles.BG_COLOR);

        add(createStatusSection());
        add(Box.createVerticalStrut(10));
        add(createInfoSection());
        add(Box.createVerticalStrut(10));
        add(createConfigSection());
        add(Box.createVerticalGlue());

        loadConfig();
        addConfigChangeListener();
    }

    private void addConfigChangeListener() {
        ConfigManager.getInstance().addConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void onConfigChanged(Config oldConfig, Config newConfig) {
                if (newConfig != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (portValueLabel != null) portValueLabel.setText(String.valueOf(newConfig.getPort()));
                        if (rootDirValueLabel != null) rootDirValueLabel.setText(newConfig.getRootDirectory());
                        if (maxConnValueLabel != null) maxConnValueLabel.setText(String.valueOf(newConfig.getMaxConnections()));
                        if (timeoutValueLabel != null) timeoutValueLabel.setText(newConfig.getTimeoutSeconds() + "秒");
                    });
                }
            }
        });
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIStyles.BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        return panel;
    }

    private JPanel createStatusSection() {
        JPanel panel = createCardPanel();

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 16));
        indicator.setBackground(UIStyles.SUCCESS_COLOR);

        JLabel titleLabel = new JLabel("服务状态");
        titleLabel.setFont(UIStyles.FONT_SUBTITLE);
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);

        headerPanel.add(indicator);
        headerPanel.add(titleLabel);
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(LEFT_ALIGNMENT);
        statusPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        statusIndicator = new JLabel("●");
        statusIndicator.setFont(UIStyles.FONT_NORMAL);
        statusIndicator.setForeground(UIStyles.DANGER_LIGHT);

        statusText = new JLabel("已停止");
        statusText.setFont(UIStyles.FONT_SMALL);
        statusText.setForeground(UIStyles.TEXT_SECONDARY);

        statusPanel.add(statusIndicator);
        statusPanel.add(statusText);

        runTimeLabel = new JLabel("运行时间：--");
        runTimeLabel.setFont(UIStyles.FONT_TINY);
        runTimeLabel.setForeground(UIStyles.TEXT_MUTED);
        runTimeLabel.setAlignmentX(LEFT_ALIGNMENT);
        runTimeLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        panel.add(headerPanel);
        panel.add(statusPanel);
        panel.add(runTimeLabel);

        return panel;
    }

    private JPanel createInfoSection() {
        JPanel panel = createCardPanel();

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 16));
        indicator.setBackground(UIStyles.INFO_COLOR);

        JLabel titleLabel = new JLabel("服务器信息");
        titleLabel.setFont(UIStyles.FONT_SUBTITLE);
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);

        headerPanel.add(indicator);
        headerPanel.add(titleLabel);
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel infoGrid = new JPanel();
        infoGrid.setLayout(new BoxLayout(infoGrid, BoxLayout.Y_AXIS));
        infoGrid.setOpaque(false);
        infoGrid.setAlignmentX(LEFT_ALIGNMENT);
        infoGrid.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel currentConnRow = createInfoRow("当前连接", "0");
        currentConnValueLabel = findValueLabel(currentConnRow);

        JPanel portRow = createInfoRow("监听端口", "-");
        portValueLabel = findValueLabel(portRow);

        infoGrid.add(currentConnRow);
        infoGrid.add(portRow);

        panel.add(headerPanel);
        panel.add(infoGrid);

        return panel;
    }

    private JPanel createConfigSection() {
        JPanel panel = createCardPanel();

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 16));
        indicator.setBackground(UIStyles.WARNING_COLOR);

        JLabel titleLabel = new JLabel("快速配置");
        titleLabel.setFont(UIStyles.FONT_SUBTITLE);
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);

        headerPanel.add(indicator);
        headerPanel.add(titleLabel);
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel configGrid = new JPanel();
        configGrid.setLayout(new BoxLayout(configGrid, BoxLayout.Y_AXIS));
        configGrid.setOpaque(false);
        configGrid.setAlignmentX(LEFT_ALIGNMENT);
        configGrid.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel rootDirRow = createInfoRow("根目录", "-");
        rootDirValueLabel = findValueLabel(rootDirRow);

        JPanel maxConnRow = createInfoRow("最大连接", "-");
        maxConnValueLabel = findValueLabel(maxConnRow);

        JPanel timeoutRow = createInfoRow("超时时间", "-");
        timeoutValueLabel = findValueLabel(timeoutRow);

        configGrid.add(rootDirRow);
        configGrid.add(Box.createVerticalStrut(2));
        configGrid.add(maxConnRow);
        configGrid.add(Box.createVerticalStrut(2));
        configGrid.add(timeoutRow);

        JButton editConfigBtn = new JButton("编辑配置");
        editConfigBtn.setFont(UIStyles.FONT_SMALL);
        editConfigBtn.setForeground(UIStyles.PRIMARY_COLOR);
        editConfigBtn.setBackground(UIStyles.BG_COLOR);
        editConfigBtn.setBorderPainted(false);
        editConfigBtn.setFocusPainted(false);
        editConfigBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editConfigBtn.setAlignmentX(LEFT_ALIGNMENT);
        editConfigBtn.setMaximumSize(new Dimension(200, 30));
        editConfigBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        editConfigBtn.addActionListener(e -> {
            try {
                showConfigDialog();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "打开配置编辑器失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(6, 0, 0, 0));
        buttonPanel.add(editConfigBtn);

        panel.add(headerPanel);
        panel.add(configGrid);
        panel.add(buttonPanel);

        return panel;
    }

    private JLabel findValueLabel(JPanel row) {
        for (Component c : row.getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getHorizontalAlignment() == SwingConstants.RIGHT) {
                return (JLabel) c;
            }
        }
        return null;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row.setPreferredSize(new Dimension(200, 20));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(UIStyles.FONT_TINY);
        labelComp.setForeground(UIStyles.TEXT_SECONDARY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(UIStyles.FONT_TINY);
        valueComp.setForeground(UIStyles.TEXT_PRIMARY);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);

        return row;
    }

    private void loadConfig() {
        try {
            Config config = ConfigManager.getInstance().getConfig();
            if (portValueLabel != null) portValueLabel.setText(String.valueOf(config.getPort()));
            if (rootDirValueLabel != null) rootDirValueLabel.setText(config.getRootDirectory());
            if (maxConnValueLabel != null) maxConnValueLabel.setText(String.valueOf(config.getMaxConnections()));
            if (timeoutValueLabel != null) timeoutValueLabel.setText(config.getTimeoutSeconds() + "秒");
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
        }
    }

    public void updateServerStatus(boolean running) {
        SwingUtilities.invokeLater(() -> {
            if (running) {
                statusIndicator.setForeground(UIStyles.SUCCESS_LIGHT);
                statusText.setText("运行中");
                statusText.setForeground(UIStyles.SUCCESS_COLOR);

                startTime = System.currentTimeMillis();
                if (runtimeTimer != null) runtimeTimer.stop();
                runtimeTimer = new Timer(1000, e -> updateRuntime());
                runtimeTimer.start();
            } else {
                statusIndicator.setForeground(UIStyles.DANGER_LIGHT);
                statusText.setText("已停止");
                statusText.setForeground(UIStyles.DANGER_COLOR);

                if (runtimeTimer != null) {
                    runtimeTimer.stop();
                    runtimeTimer = null;
                }
                runTimeLabel.setText("运行时间: --");
            }
        });
    }

    public void updateConnectionCount() {
        SwingUtilities.invokeLater(() -> {
            if (currentConnValueLabel != null) {
                currentConnValueLabel.setText(server.getActiveConnections() + " / " + server.getMaxConnections());
            }
        });
    }

    @Override
    public void onConnectionAdded(ConnectionInfo info) {
        updateConnectionCount();
    }

    @Override
    public void onConnectionRemoved(String id) {
        updateConnectionCount();
    }

    @Override
    public void onConnectionUpdated(ConnectionInfo info) {
    }

    private void updateRuntime() {
        long elapsed = System.currentTimeMillis() - startTime;
        long seconds = elapsed / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String timeStr;
        if (hours > 0) {
            timeStr = String.format("%d时%d分%d秒", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            timeStr = String.format("%d分%d秒", minutes, seconds % 60);
        } else {
            timeStr = String.format("%d秒", seconds);
        }

        runTimeLabel.setText("运行时间: " + timeStr);
    }

    private void showConfigDialog() {
        // 使用新的现代化配置对话框
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        ConfigDialog dialog = new ConfigDialog(parent);
        dialog.setVisible(true);
    }
}
