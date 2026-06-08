package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import helper.DatabaseConnection;
import view.LoginPanel;

public class App {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            Class<?> flatLaf = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            UIManager.setLookAndFeel(
                (javax.swing.LookAndFeel) flatLaf.getDeclaredConstructor().newInstance()
            );
        } catch (ClassNotFoundException ignored) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored2) {}
        } catch (Exception e) {
            System.err.println("Loi L&F: " + e.getMessage());
        }

        DatabaseConnection.getConnection();

        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}
