package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import helper.DatabaseConnection;

public class ProgressDAO {

    public void saveLearningProgress(int userId, int wordId) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return;
        try (c) {
            try (PreparedStatement psCheck = c.prepareStatement(
                    "SELECT learn_count FROM UserProgress WHERE user_id = ? AND vocabulary_id = ?")) {
                psCheck.setInt(1, userId);
                psCheck.setInt(2, wordId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        try (PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO UserProgress (user_id, vocabulary_id, is_mastered, is_favorite, learn_count) VALUES (?, ?, 0, 0, 1)")) {
                            ps.setInt(1, userId);
                            ps.setInt(2, wordId);
                            ps.executeUpdate();
                        }
                    } else {
                        int newCount = rs.getInt("learn_count") + 1;
                        try (PreparedStatement ps = c.prepareStatement(
                                "UPDATE UserProgress SET learn_count = ?, is_mastered = ? WHERE user_id = ? AND vocabulary_id = ?")) {
                            ps.setInt(1, newCount);
                            ps.setInt(2, newCount >= 3 ? 1 : 0);
                            ps.setInt(3, userId);
                            ps.setInt(4, wordId);
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Loi ProgressDAO.saveLearningProgress: " + e.getMessage());
        }
    }

    public void markAsMastered(int userId, int wordId) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return;
        try (c; PreparedStatement ps = c.prepareStatement(
                "UPDATE UserProgress SET is_mastered = 1 WHERE user_id = ? AND vocabulary_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, wordId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Loi ProgressDAO.markAsMastered: " + e.getMessage());
        }
    }

    public void saveQuizScore(int userId, int score) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return;
        try (c) {
            int nextNum = 1;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COALESCE(MAX(quiz_number), 0) FROM QuizHistory WHERE user_id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) nextNum = rs.getInt(1) + 1;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO QuizHistory (user_id, quiz_number, score, date_taken) VALUES (?, ?, ?, date('now'))")) {
                ps.setInt(1, userId);
                ps.setInt(2, nextNum);
                ps.setInt(3, score);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Loi ProgressDAO.saveQuizScore: " + e.getMessage());
        }
    }

    public void resetAllProgress(int userId) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return;
        try (c) {
            try (PreparedStatement ps1 = c.prepareStatement(
                    "DELETE FROM UserProgress WHERE user_id = ?")) {
                ps1.setInt(1, userId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(
                    "DELETE FROM QuizHistory WHERE user_id = ?")) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Loi ProgressDAO.resetAllProgress: " + e.getMessage());
        }
    }
}
