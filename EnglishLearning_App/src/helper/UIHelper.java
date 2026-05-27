package helper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class UIHelper {
    public static ImageIcon loadIcon(Object context, String path, int width, int height) {
        try {
            java.io.File file = new java.io.File("src" + path);
            if (file.exists()) return new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            URL imgURL = context.getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    public static JButton createButton(Object context, String text, String iconPath, Color bgColor) {
        JButton btn = new JButton(text);
        if (iconPath != null) {
            ImageIcon icon = loadIcon(context, iconPath, 20, 20);
            if (icon != null) { btn.setIcon(icon); btn.setIconTextGap(8); }
        }
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.BLACK); 
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 5, 10, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void enforceBlackText(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                Color c = comp.getForeground();
                if (c != Color.RED && c != new Color(16, 185, 129) && c != new Color(37, 99, 235)) comp.setForeground(Color.BLACK);
            } else if (comp instanceof JRadioButton || comp instanceof JComboBox || comp instanceof JTextField) {
                comp.setForeground(Color.BLACK); comp.setBackground(Color.WHITE);
            }
            if (comp instanceof Container) enforceBlackText((Container) comp);
        }
    }
}