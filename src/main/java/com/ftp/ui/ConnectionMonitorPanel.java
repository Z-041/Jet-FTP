package com.ftp.ui;

import com.ftp.server.ClientHandler;
import com.ftp.server.FtpServer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionMonitorPanel extends JPanel implements ConnectionListener {
    private final FtpServer server;
    private final JTable connectionTable;
    private final DefaultTableModel tableModel;
    private final JButton disconnectButton;
    private final Map<String, ConnectionInfo> connectionInfoMap;
    private final SimpleDateFormat dateFormat;

    public ConnectionMonitorPanel(FtpServer server) {
        this.server = server;
        this.connectionInfoMap = new ConcurrentHashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyles.BG_WHITE);

        String[] columnNames = {"ID", "IP地址", "用户名", "连接时间", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        connectionTable = new JTable(tableModel);
        connectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionTable.setRowHeight(36);
        connectionTable.setIntercellSpacing(new Dimension(0, 0));
        connectionTable.setShowGrid(false);
        connectionTable.setBackground(UIStyles.BG_WHITE);
        connectionTable.setSelectionBackground(UIStyles.TABLE_SELECTION);
        connectionTable.setSelectionForeground(UIStyles.TEXT_PRIMARY);
        connectionTable.setFont(UIStyles.FONT_SMALL);
        connectionTable.setBorder(new EmptyBorder(0, 0, 0, 0));

        JTableHeader header = connectionTable.getTableHeader();
        header.setDefaultRenderer(new TableHeaderRenderer());
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        connectionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        connectionTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        connectionTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        connectionTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        connectionTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        connectionTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(connectionTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyles.BG_WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        disconnectButton = createButton("断开连接", UIStyles.DANGER_COLOR);
        disconnectButton.setEnabled(false);
        buttonPanel.add(disconnectButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setupActionListeners();
        loadExistingConnections();
    }

    private JButton createButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(UIStyles.FONT_SMALL);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 30));
        button.setBorder(new EmptyBorder(0, 15, 0, 15));

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
        disconnectButton.addActionListener(e -> disconnectSelectedConnection());

        connectionTable.getSelectionModel().addListSelectionListener(e -> {
            disconnectButton.setEnabled(connectionTable.getSelectedRow() != -1);
        });
    }

    private void loadExistingConnections() {
        for (ClientHandler handler : server.getActiveHandlers()) {
            ConnectionInfo info = handler.getConnectionInfo();
            connectionInfoMap.put(info.getId(), info);
            addConnectionToTable(info);
        }
    }

    private void addConnectionToTable(ConnectionInfo info) {
        SwingUtilities.invokeLater(() -> {
            Object[] row = {
                info.getId().substring(0, 8) + "...",
                info.getAddress().getHostAddress(),
                info.getUsername() != null ? info.getUsername() : "-",
                dateFormat.format(info.getConnectTime()),
                info.isAuthenticated() ? "已认证" : "未认证"
            };
            tableModel.addRow(row);
        });
    }

    private void updateConnectionInTable(ConnectionInfo info) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String id = (String) tableModel.getValueAt(i, 0);
                if (id != null && info.getId().startsWith(id.replace("...", ""))) {
                    tableModel.setValueAt(info.getUsername() != null ? info.getUsername() : "-", i, 2);
                    tableModel.setValueAt(info.isAuthenticated() ? "已认证" : "未认证", i, 4);
                    break;
                }
            }
        });
    }

    private void removeConnectionFromTable(String id) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String tableId = (String) tableModel.getValueAt(i, 0);
                if (tableId != null && id.startsWith(tableId.replace("...", ""))) {
                    tableModel.removeRow(i);
                    break;
                }
            }
        });
    }

    private void disconnectSelectedConnection() {
        int selectedRow = connectionTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        String idPrefix = (String) tableModel.getValueAt(selectedRow, 0);
        if (idPrefix == null) {
            return;
        }

        String shortId = idPrefix.replace("...", "");
        for (ClientHandler handler : server.getActiveHandlers()) {
            if (handler.getHandlerId().startsWith(shortId)) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要断开该连接吗？",
                    "确认断开",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    handler.stop();
                }
                break;
            }
        }
    }

    @Override
    public void onConnectionAdded(ConnectionInfo info) {
        connectionInfoMap.put(info.getId(), info);
        addConnectionToTable(info);
    }

    @Override
    public void onConnectionRemoved(String id) {
        connectionInfoMap.remove(id);
        removeConnectionFromTable(id);
    }

    @Override
    public void onConnectionUpdated(ConnectionInfo info) {
        connectionInfoMap.put(info.getId(), info);
        updateConnectionInTable(info);
    }

    private static class TableHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setFont(UIStyles.FONT_SMALL);
            label.setForeground(UIStyles.TEXT_SECONDARY);
            label.setBackground(UIStyles.TABLE_HEADER_BG);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            return label;
        }
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setFont(UIStyles.FONT_SMALL);
            label.setBorder(new EmptyBorder(0, 10, 0, 10));

            if ("已认证".equals(value)) {
                label.setForeground(UIStyles.SUCCESS_COLOR);
            } else {
                label.setForeground(UIStyles.TEXT_MUTED);
            }

            if (isSelected) {
                label.setBackground(UIStyles.TABLE_SELECTION);
            } else {
                int modelRow = table.convertRowIndexToModel(row);
                boolean isEven = modelRow % 2 == 0;
                label.setBackground(isEven ? UIStyles.TABLE_ROW_EVEN : UIStyles.TABLE_ROW_ODD);
            }

            label.setOpaque(true);
            return label;
        }
    }
}
