package com.ftp.ui;

import com.ftp.config.Config;
import com.ftp.config.ConfigManager;
import com.ftp.config.ConfigValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfigDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(ConfigDialog.class);
    
    private JTextField portField;
    private JTextField rootDirField;
    private JTextField maxConnectionsField;
    private JTextField timeoutField;
    private JComboBox<String> logLevelCombo;
    private JTextField logFileField;
    private JTextField passiveExternalIpField;
    private JTextField passivePortMinField;
    private JTextField passivePortMaxField;
    private JTextField passiveTimeoutField;
    
    private boolean configSaved = false;

    public ConfigDialog(Frame owner) {
        super(owner, "服务器配置", true);
        initUI();
        loadConfig();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyles.BG_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIStyles.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 标题
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 配置表单（带滚动）
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 按钮
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.BG_WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        // 图标指示器
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(6, 24));
        indicator.setBackground(UIStyles.PRIMARY_COLOR);
        indicator.setBorder(new EmptyBorder(0, 0, 0, 0));

        // 标题和说明
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("服务器配置");
        titleLabel.setFont(UIStyles.FONT_TITLE);
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("修改配置后需要重启服务器才能生效");
        subtitleLabel.setFont(UIStyles.FONT_SMALL);
        subtitleLabel.setForeground(UIStyles.TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        leftPanel.add(indicator);
        leftPanel.add(textPanel);

        panel.add(leftPanel, BorderLayout.WEST);
        return panel;
    }

    private JTextField createPassiveExternalIpField() {
        passiveExternalIpField = new StyledTextField();
        passiveExternalIpField.setFont(UIStyles.FONT_NORMAL);
        passiveExternalIpField.setForeground(UIStyles.TEXT_PRIMARY);
        passiveExternalIpField.setPreferredSize(new Dimension(300, 36));
        passiveExternalIpField.setToolTipText("输入IP地址或留空使用auto自动检测");
        return passiveExternalIpField;
    }

    private JPanel createPassivePortRangeField() {
        passivePortMinField = new StyledTextField();
        passivePortMinField.setFont(UIStyles.FONT_NORMAL);
        passivePortMinField.setForeground(UIStyles.TEXT_PRIMARY);
        passivePortMinField.setPreferredSize(new Dimension(100, 36));
        passivePortMinField.setText("0");

        JLabel separatorLabel = new JLabel(" - ");
        separatorLabel.setFont(UIStyles.FONT_NORMAL);
        separatorLabel.setForeground(UIStyles.TEXT_SECONDARY);

        passivePortMaxField = new StyledTextField();
        passivePortMaxField.setFont(UIStyles.FONT_NORMAL);
        passivePortMaxField.setForeground(UIStyles.TEXT_PRIMARY);
        passivePortMaxField.setPreferredSize(new Dimension(100, 36));
        passivePortMaxField.setText("0");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(UIStyles.BG_WHITE);
        panel.add(passivePortMinField);
        panel.add(separatorLabel);
        panel.add(passivePortMaxField);

        return panel;
    }

    private JTextField createPassiveTimeoutField() {
        passiveTimeoutField = new StyledTextField();
        passiveTimeoutField.setFont(UIStyles.FONT_NORMAL);
        passiveTimeoutField.setForeground(UIStyles.TEXT_PRIMARY);
        passiveTimeoutField.setPreferredSize(new Dimension(300, 36));
        return passiveTimeoutField;
    }

    private JPanel createSectionLabel(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(UIStyles.BG_COLOR);

        JLabel label = new JLabel(title);
        label.setFont(UIStyles.FONT_TITLE);
        label.setForeground(UIStyles.PRIMARY_COLOR);

        panel.add(label);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyles.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // 端口号
        gbc.gridy = row++;
        panel.add(createFormRow("端口号:", createPortField(), 
            "FTP 服务器监听端口（1-65535）"), gbc);

        // 根目录
        gbc.gridy = row++;
        panel.add(createFormRow("根目录:", createRootDirField(), 
            "FTP 文件根目录路径"), gbc);

        // 最大连接数
        gbc.gridy = row++;
        panel.add(createFormRow("最大连接数:", createMaxConnectionsField(), 
            "允许的最大并发连接数（1-1000）"), gbc);

        // 超时时间
        gbc.gridy = row++;
        panel.add(createFormRow("超时时间 (秒):", createTimeoutField(), 
            "连接超时时间（秒）"), gbc);

        // 日志级别
        gbc.gridy = row++;
        panel.add(createFormRow("日志级别:", createLogLevelCombo(), 
            "日志记录级别"), gbc);

        // 日志文件路径
        gbc.gridy = row++;
        panel.add(createFormRow("日志文件:", createLogFileField(), 
            "日志文件存储路径"), gbc);

        // 分隔线 - 被动模式配置
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 8, 0);
        panel.add(createSectionLabel("被动模式配置 (Passive Mode)"), gbc);
        gbc.insets = new Insets(8, 0, 8, 0);

        // 被动模式外部IP
        gbc.gridy = row++;
        panel.add(createFormRow("外部IP地址:", createPassiveExternalIpField(), 
            "被动模式下返回给客户端的IP（auto=自动检测）"), gbc);

        // 被动模式端口范围
        gbc.gridy = row++;
        panel.add(createFormRow("端口范围:", createPassivePortRangeField(), 
            "数据连接端口范围（0=随机，如50000-50100）"), gbc);

        // 被动模式超时
        gbc.gridy = row++;
        panel.add(createFormRow("连接超时 (秒):", createPassiveTimeoutField(), 
            "等待数据连接的超时时间"), gbc);

        return panel;
    }

    private JPanel createFormRow(String label, JComponent field, String tooltip) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyles.BG_WHITE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));
        panel.setToolTipText(tooltip);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 15);

        // 标签
        JLabel labelField = new JLabel(label);
        labelField.setFont(UIStyles.FONT_NORMAL);
        labelField.setForeground(UIStyles.TEXT_SECONDARY);
        labelField.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(labelField, gbc);

        // 输入字段
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);

        return panel;
    }

    private JTextField createPortField() {
        portField = new StyledTextField();
        portField.setFont(UIStyles.FONT_NORMAL);
        portField.setForeground(UIStyles.TEXT_PRIMARY);
        portField.setPreferredSize(new Dimension(300, 36));
        return portField;
    }

    private JPanel createRootDirField() {
        rootDirField = new StyledTextField();
        rootDirField.setFont(UIStyles.FONT_NORMAL);
        rootDirField.setForeground(UIStyles.TEXT_PRIMARY);
        rootDirField.setPreferredSize(new Dimension(300, 36));
        
        JButton browseButton = new JButton("浏览...");
        browseButton.setFont(UIStyles.FONT_SMALL);
        browseButton.setPreferredSize(new Dimension(80, 36));
        browseButton.setBackground(UIStyles.BG_HOVER);
        browseButton.setForeground(UIStyles.TEXT_PRIMARY);
        browseButton.setFocusPainted(false);
        browseButton.setBorderPainted(false);
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        browseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(UIStyles.PRIMARY_LIGHT);
                browseButton.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(UIStyles.BG_HOVER);
                browseButton.setForeground(UIStyles.TEXT_PRIMARY);
            }
        });
        
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("选择根目录");
            
            if (fileChooser.showOpenDialog(ConfigDialog.this) == JFileChooser.APPROVE_OPTION) {
                rootDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(UIStyles.BG_WHITE);
        panel.add(rootDirField);
        panel.add(browseButton);
        
        return panel;
    }

    private JTextField createMaxConnectionsField() {
        maxConnectionsField = new StyledTextField();
        maxConnectionsField.setFont(UIStyles.FONT_NORMAL);
        maxConnectionsField.setForeground(UIStyles.TEXT_PRIMARY);
        maxConnectionsField.setPreferredSize(new Dimension(300, 36));
        return maxConnectionsField;
    }

    private JTextField createTimeoutField() {
        timeoutField = new StyledTextField();
        timeoutField.setFont(UIStyles.FONT_NORMAL);
        timeoutField.setForeground(UIStyles.TEXT_PRIMARY);
        timeoutField.setPreferredSize(new Dimension(300, 36));
        return timeoutField;
    }

    private JComboBox<String> createLogLevelCombo() {
        logLevelCombo = new JComboBox<>(new String[]{"DEBUG", "INFO", "WARN", "ERROR"});
        logLevelCombo.setFont(UIStyles.FONT_NORMAL);
        logLevelCombo.setForeground(UIStyles.TEXT_PRIMARY);
        logLevelCombo.setBackground(UIStyles.BG_WHITE);
        logLevelCombo.setPreferredSize(new Dimension(300, 36));
        logLevelCombo.setFocusable(true);
        logLevelCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 自定义渲染器
        logLevelCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                label.setFont(UIStyles.FONT_NORMAL);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));
                return label;
            }
        });
        
        return logLevelCombo;
    }

    private JPanel createLogFileField() {
        logFileField = new StyledTextField();
        logFileField.setFont(UIStyles.FONT_NORMAL);
        logFileField.setForeground(UIStyles.TEXT_PRIMARY);
        logFileField.setPreferredSize(new Dimension(300, 36));
        
        JButton browseButton = new JButton("浏览...");
        browseButton.setFont(UIStyles.FONT_SMALL);
        browseButton.setPreferredSize(new Dimension(80, 36));
        browseButton.setBackground(UIStyles.BG_HOVER);
        browseButton.setForeground(UIStyles.TEXT_PRIMARY);
        browseButton.setFocusPainted(false);
        browseButton.setBorderPainted(false);
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        browseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(UIStyles.PRIMARY_LIGHT);
                browseButton.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                browseButton.setBackground(UIStyles.BG_HOVER);
                browseButton.setForeground(UIStyles.TEXT_PRIMARY);
            }
        });
        
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择日志文件");
            fileChooser.setSelectedFile(new java.io.File("ftp.log"));
            
            if (fileChooser.showSaveDialog(ConfigDialog.this) == JFileChooser.APPROVE_OPTION) {
                logFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(UIStyles.BG_WHITE);
        panel.add(logFileField);
        panel.add(browseButton);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(UIStyles.BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton cancelButton = createButton("取消", UIStyles.BG_HOVER, UIStyles.TEXT_SECONDARY);
        JButton saveButton = createButton("保存", UIStyles.PRIMARY_COLOR, Color.WHITE);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveConfig());

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(UIStyles.FONT_NORMAL);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setPreferredSize(new Dimension(100, 38));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(0, 20, 0, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.lighten(bg, 0.1f));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bg);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.darken(bg, 0.1f));
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.lighten(bg, 0.1f));
                }
            }
        });

        return button;
    }

    private void loadConfig() {
        Config config = ConfigManager.getInstance().getConfig();
        
        portField.setText(String.valueOf(config.getPort()));
        rootDirField.setText(config.getRootDirectory());
        maxConnectionsField.setText(String.valueOf(config.getMaxConnections()));
        timeoutField.setText(String.valueOf(config.getTimeoutSeconds()));
        logLevelCombo.setSelectedItem(config.getLogLevel());
        logFileField.setText(config.getLogFilePath());
        passiveExternalIpField.setText(config.getPassiveModeExternalIp() != null ? 
            config.getPassiveModeExternalIp() : "auto");
        passivePortMinField.setText(String.valueOf(config.getPassiveModePortMin()));
        passivePortMaxField.setText(String.valueOf(config.getPassiveModePortMax()));
        passiveTimeoutField.setText(String.valueOf(config.getPassiveModeConnectionTimeout()));
    }

    private void saveConfig() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            String rootDir = rootDirField.getText().trim();
            int maxConnections = Integer.parseInt(maxConnectionsField.getText().trim());
            int timeout = Integer.parseInt(timeoutField.getText().trim());
            String logLevel = (String) logLevelCombo.getSelectedItem();
            String logFile = logFileField.getText().trim();
            
            String passiveExternalIp = passiveExternalIpField.getText().trim();
            if ("auto".equalsIgnoreCase(passiveExternalIp) || passiveExternalIp.isEmpty()) {
                passiveExternalIp = null;
            }
            int passivePortMin = parsePortValue(passivePortMinField.getText().trim(), 0);
            int passivePortMax = parsePortValue(passivePortMaxField.getText().trim(), 0);
            int passiveTimeout = parseIntValue(passiveTimeoutField.getText().trim(), 30);

            Config newConfig = Config.builder()
                .port(port)
                .rootDirectory(rootDir)
                .maxConnections(maxConnections)
                .timeoutSeconds(timeout)
                .logLevel(logLevel)
                .logFilePath(logFile)
                .threadPoolCoreSize(10)
                .threadPoolKeepAliveSeconds(60)
                .threadPoolQueueCapacity(100)
                .passiveModeExternalIp(passiveExternalIp)
                .passiveModePortMin(passivePortMin)
                .passiveModePortMax(passivePortMax)
                .passiveModeConnectionTimeout(passiveTimeout)
                .build();

            ConfigValidator.ValidationResult result = ConfigValidator.validateConfig(newConfig);
            
            if (!result.isValid()) {
                StringBuilder errorMsg = new StringBuilder("配置验证失败：\n");
                for (String error : result.getErrors()) {
                    errorMsg.append("• ").append(error).append("\n");
                }
                JOptionPane.showMessageDialog(this, 
                    errorMsg.toString(), 
                    "配置错误", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!result.getWarnings().isEmpty()) {
                StringBuilder warnMsg = new StringBuilder("配置警告：\n");
                for (String warning : result.getWarnings()) {
                    warnMsg.append("• ").append(warning).append("\n");
                }
                int response = JOptionPane.showConfirmDialog(this,
                    warnMsg.toString() + "\n是否继续保存？",
                    "配置警告",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            ConfigManager.getInstance().saveConfig(newConfig);

            configSaved = true;
            logger.info("配置已保存");
            JOptionPane.showMessageDialog(this,
                "配置保存成功！\n重启服务器后生效。",
                "成功",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "请输入有效的数字",
                "输入错误",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.error("保存配置失败", e);
            JOptionPane.showMessageDialog(this,
                "保存失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parsePortValue(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            int port = Integer.parseInt(value.trim());
            return (port >= 0 && port <= 65535) ? port : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseIntValue(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean isConfigSaved() {
        return configSaved;
    }

    /**
     * 样式化文本字段（带圆角边框）
     */
    private static class StyledTextField extends JTextField {
        public StyledTextField() {
            super();
            setBorder(new EmptyBorder(0, 12, 0, 12));
            setBackground(UIStyles.BG_WHITE);
            setCaretColor(UIStyles.TEXT_PRIMARY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制背景
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制边框
            g2.setColor(UIStyles.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            
            g2.dispose();
        }
    }
}
