package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import view.MainDashboard;

public class App {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 15));
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}