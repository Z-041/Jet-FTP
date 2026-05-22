package com.ftp.ui;

import com.ftp.server.FtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FtpServerUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(FtpServerUI.class);
    private final FtpServer server;
    private LeftControlPanel leftControlPanel;
    private ConnectionMonitorPanel connectionMonitorPanel;
    private LogPanel logPanel;
    private StatusBar statusBar;
    private JLabel statusIndicator;
    private JLabel titleLabel;
    private JButton startButton;
    private JButton stopButton;

    public FtpServerUI(FtpServer server) {
        this.server = server;
        
        // 设置全局异常处理器
        setupGlobalExceptionHandler();
        
        setTitle("FTP 服务器管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyles.BG_COLOR);

        connectionMonitorPanel = new ConnectionMonitorPanel(server);
        logPanel = new LogPanel();
        leftControlPanel = new LeftControlPanel(server);

        // 注册日志监听器
        com.ftp.logging.Logger.getInstance().addLogListener(logPanel);
        
        server.addConnectionListener(connectionMonitorPanel);
        server.addConnectionListener(leftControlPanel);

        createMenuBar();
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server.isRunning()) {
                    server.stop();
                }
                statusBar.stopTimer();
            }
        });
        
        // 根据服务器实际状态初始化 UI
        updateServerStatus(server.isRunning());
    }

    /**
     * 设置全局异常处理器，捕获所有未处理的异常
     */
    private void setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("未处理的异常 in thread: " + thread.getName(), throwable);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    this,
                    "发生未知错误：" + throwable.getMessage(),
                    "系统错误",
                    JOptionPane.ERROR_MESSAGE
                );
            });
        });
    }

    private JPanel createTopPanel() {
        // 创建渐变背景面板
        JPanel topPanel = new GradientPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // 状态指示器 - 带发光效果
        statusIndicator = new JLabel("●");
        statusIndicator.setFont(UIStyles.FONT_SUBTITLE_LARGE);
        statusIndicator.setForeground(UIStyles.DANGER_LIGHT);
        statusIndicator.setHorizontalAlignment(SwingConstants.CENTER);

        // 标题 - 加大加粗
        titleLabel = new JLabel("FTP 服务器管理系统");
        titleLabel.setFont(UIStyles.FONT_TITLE_LARGE);
        titleLabel.setForeground(Color.WHITE);

        // 按钮 - 现代化样式
        startButton = createModernButton("启动服务", UIStyles.SUCCESS_COLOR);
        stopButton = createModernButton("停止服务", UIStyles.DANGER_COLOR);
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> {
            try {
                if (!server.isRunning()) {
                    server.start();
                    updateServerStatus(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "启动失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        stopButton.addActionListener(e -> {
            if (server.isRunning()) {
                server.stop();
                updateServerStatus(false);
            }
        });

        // 状态指示器和标题
        leftPanel.add(statusIndicator);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(titleLabel);

        // 右侧按钮面板
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(startButton);
        rightPanel.add(Box.createHorizontalStrut(8));
        rightPanel.add(stopButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    /**
     * 创建现代化按钮（带圆角和阴影）
     */
    private JButton createModernButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(UIStyles.FONT_NORMAL);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 38));
        button.setBorder(new EmptyBorder(0, 20, 0, 20));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.lighten(baseColor, 0.1f));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.darken(baseColor, 0.1f));
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIStyles.lighten(baseColor, 0.1f));
                }
            }
        });

        return button;
    }

    /**
     * 渐变背景面板
     */
    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制渐变背景
            GradientPaint gradient = new GradientPaint(
                0, 0, UIStyles.PRIMARY_GRADIENT_START,
                0, getHeight(), UIStyles.PRIMARY_GRADIENT_END
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.dispose();
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UIStyles.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);

        // 连接监控卡片 - 带阴影和圆角
        JPanel connectionWrapper = createModernCard();
        JPanel connectionHeader = createModernSectionHeader("连接监控", UIStyles.PRIMARY_COLOR);
        connectionWrapper.add(connectionHeader, BorderLayout.NORTH);
        connectionWrapper.add(connectionMonitorPanel, BorderLayout.CENTER);

        // 系统日志卡片 - 带阴影和圆角
        JPanel logWrapper = createModernCard();
        JPanel logHeader = createModernSectionHeader("系统日志", UIStyles.INFO_COLOR);
        logWrapper.add(logHeader, BorderLayout.NORTH);
        logWrapper.add(logPanel, BorderLayout.CENTER);

        centerPanel.add(connectionWrapper);
        centerPanel.add(logWrapper);

        mainPanel.add(leftControlPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * 创建现代化卡片（带圆角和阴影）
     */
    private JPanel createModernCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.BG_CARD);
        panel.setBorder(new UIStyles.RoundedBorder(UIStyles.BORDER_RADIUS, UIStyles.BORDER_COLOR));
        return panel;
    }

    /**
     * 创建现代化章节标题
     */
    private JPanel createModernSectionHeader(String title, Color accentColor) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.BG_WHITE);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        // 彩色指示条
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(5, 22));
        indicator.setBackground(accentColor);
        indicator.setBorder(new EmptyBorder(0, 0, 0, 0));

        // 标题
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_SUBTITLE_LARGE);
        titleLabel.setForeground(UIStyles.TEXT_PRIMARY);

        leftPanel.add(indicator);
        leftPanel.add(titleLabel);

        header.add(leftPanel, BorderLayout.WEST);

        JPanel bottomLine = new JPanel();
        bottomLine.setPreferredSize(new Dimension(0, 1));
        bottomLine.setBackground(UIStyles.BORDER_COLOR);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(bottomLine, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createStatusBar() {
        statusBar = new StatusBar();
        return statusBar;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UIStyles.BG_WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER_COLOR));

        JMenu fileMenu = createMenu("文件");
        JMenuItem exitMenuItem = createMenuItem("退出");
        exitMenuItem.addActionListener(e -> {
            if (server.isRunning()) {
                server.stop();
            }
            System.exit(0);
        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = createMenu("帮助");
        JMenuItem aboutMenuItem = createMenuItem("关于");
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "FTP 服务器管理系统\n版本: v1.0.0\n\n基于 Java 17+ Swing 开发\n完全兼容 RFC 959 协议标准",
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(UIStyles.FONT_NORMAL);
        menu.setForeground(UIStyles.TEXT_PRIMARY);
        return menu;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(UIStyles.FONT_NORMAL);
        item.setForeground(UIStyles.TEXT_PRIMARY);
        return item;
    }

    private void updateServerStatus(boolean running) {
        SwingUtilities.invokeLater(() -> {
            if (running) {
                statusIndicator.setForeground(UIStyles.SUCCESS_LIGHT);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                statusBar.setStatus("服务器运行中");
            } else {
                statusIndicator.setForeground(UIStyles.DANGER_LIGHT);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                statusBar.setStatus("服务器已停止");
            }
            leftControlPanel.updateServerStatus(running);
        });
    }

    public static void createAndShowUI(FtpServer server) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("MenuBar.background", UIStyles.BG_WHITE);
                UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.BORDER_COLOR));
            } catch (Exception e) {
                logger.error("Failed to set look and feel", e);
            }

            FtpServerUI ui = new FtpServerUI(server);
            ui.setVisible(true);
        });
    }
}
