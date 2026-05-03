package com.ftp.ui;

import java.awt.*;
import javax.swing.border.*;

public final class UIStyles {
    private UIStyles() {}

    // 主色调 - 现代蓝色
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    public static final Color PRIMARY_DARK = new Color(37, 99, 235);
    public static final Color PRIMARY_LIGHT = new Color(96, 165, 250);
    public static final Color PRIMARY_GRADIENT_START = new Color(59, 130, 246);
    public static final Color PRIMARY_GRADIENT_END = new Color(37, 99, 235);

    // 成功色 - 翡翠绿
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    public static final Color SUCCESS_LIGHT = new Color(52, 211, 153);
    public static final Color SUCCESS_DARK = new Color(5, 150, 105);

    // 危险色 - 珊瑚红
    public static final Color DANGER_COLOR = new Color(239, 68, 68);
    public static final Color DANGER_LIGHT = new Color(248, 113, 113);
    public static final Color DANGER_DARK = new Color(220, 38, 38);

    // 警告色 - 琥珀色
    public static final Color WARNING_COLOR = new Color(245, 158, 11);
    public static final Color WARNING_LIGHT = new Color(251, 191, 36);

    // 信息色
    public static final Color INFO_COLOR = new Color(59, 130, 246);
    public static final Color INFO_LIGHT = new Color(147, 197, 253);

    // 背景色 - 现代灰度
    public static final Color BG_COLOR = new Color(243, 244, 246);
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_CARD = new Color(255, 255, 255);
    public static final Color BG_SIDEBAR = new Color(249, 250, 251);
    public static final Color BG_HOVER = new Color(229, 231, 235);
    public static final Color BG_SELECTED = new Color(224, 231, 255);

    // 边框和分割线
    public static final Color BORDER_COLOR = new Color(229, 231, 235);
    public static final Color BORDER_LIGHT = new Color(243, 244, 246);
    public static final Color DIVIDER_COLOR = new Color(229, 231, 235);

    // 文本颜色
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);
    public static final Color TEXT_PLACEHOLDER = new Color(209, 213, 219);

    // 表格样式
    public static final Color TABLE_HEADER_BG = new Color(249, 250, 251);
    public static final Color TABLE_HEADER_TEXT = new Color(107, 114, 128);
    public static final Color TABLE_ROW_ODD = new Color(255, 255, 255);
    public static final Color TABLE_ROW_EVEN = new Color(249, 250, 251);
    public static final Color TABLE_SELECTION = new Color(224, 231, 255);
    public static final Color TABLE_GRID = new Color(243, 244, 246);
    public static final Color TABLE_ROW_HOVER = new Color(249, 250, 251);

    // 阴影颜色
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 20);
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 10);

    // 字体
    public static final String FONT_FAMILY = "Microsoft YaHei";
    public static final String FONT_MONO = "Consolas";

    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_SUBTITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_TINY = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_MONO_NORMAL = new Font(FONT_MONO, Font.PLAIN, 13);
    public static final Font FONT_MONO_SMALL = new Font(FONT_MONO, Font.PLAIN, 12);

    // 间距和圆角
    public static final int BORDER_RADIUS = 8;
    public static final int BORDER_RADIUS_LARGE = 12;
    public static final int BORDER_RADIUS_SMALL = 4;
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_NORMAL = 12;
    public static final int PADDING_LARGE = 16;
    public static final int PADDING_XLARGE = 24;
    public static final int GAP_SMALL = 8;
    public static final int GAP_NORMAL = 12;
    public static final int GAP_LARGE = 16;

    /**
     * 创建带阴影的边框
     */
    public static Border createShadowBorder() {
        return new CompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new ShadowBorder()
        );
    }

    /**
     * 创建圆角边框
     */
    public static Border createRoundedBorder() {
        return new RoundedBorder(BORDER_RADIUS, BORDER_COLOR);
    }

    /**
     * 创建卡片边框（带阴影）
     */
    public static Border createCardBorder() {
        return new CompoundBorder(
            new RoundedBorder(BORDER_RADIUS, BORDER_COLOR),
            new ShadowBorder()
        );
    }

    /**
     * 创建渐变效果
     */
    public static Paint createGradientPaint(int x, int y, int width, int height) {
        return new GradientPaint(
            x, y, PRIMARY_GRADIENT_START,
            x, y + height, PRIMARY_GRADIENT_END
        );
    }

    /**
     * 颜色变亮
     */
    public static Color lighten(Color color, float amount) {
        int r = (int) Math.min(255, color.getRed() + (255 - color.getRed()) * amount);
        int g = (int) Math.min(255, color.getGreen() + (255 - color.getGreen()) * amount);
        int b = (int) Math.min(255, color.getBlue() + (255 - color.getBlue()) * amount);
        return new Color(r, g, b);
    }

    /**
     * 颜色变暗
     */
    public static Color darken(Color color, float amount) {
        int r = (int) Math.max(0, color.getRed() * (1 - amount));
        int g = (int) Math.max(0, color.getGreen() * (1 - amount));
        int b = (int) Math.max(0, color.getBlue() * (1 - amount));
        return new Color(r, g, b);
    }

    /**
     * 创建带透明度的颜色
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * 圆角边框类
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * 阴影边框类
     */
    public static class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制阴影
            for (int i = 4; i > 0; i--) {
                int alpha = (int) (5 * (4 - i));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 8, 8);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 8, 8);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
