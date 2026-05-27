package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:english_app.db";

    public static Connection getConnection() {
        try {
            File db = new File("english_app.db");
            if (!db.exists()) System.out.println("⚠️ CHƯA CÓ FILE english_app.db TẠI: " + db.getAbsolutePath());
            
            Connection conn = DriverManager.getConnection(URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS QuizHistory (id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER, date_taken TEXT)");
            }
            return conn;
        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối DB: " + e.getMessage());
            return null;
        }
    }
}