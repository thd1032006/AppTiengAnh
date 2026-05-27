package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import helper.DatabaseConnection;

public class ProgressDAO {
    public void saveLearningProgress(int wordId) {
        String check = "SELECT 1 FROM UserProgress WHERE user_id = 1 AND vocabulary_id = ?";
        String insert = "INSERT INTO UserProgress (user_id, vocabulary_id, is_mastered, is_favorite) VALUES (1, ?, 0, 0)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement psCheck = c.prepareStatement(check)) {
            psCheck.setInt(1, wordId);
            if (!psCheck.executeQuery().next()) {
                try (PreparedStatement psIns = c.prepareStatement(insert)) { psIns.setInt(1, wordId); psIns.executeUpdate(); }
            }
        } catch (Exception e) { }
    }

    public void markAsMastered(int wordId) {
        String update = "UPDATE UserProgress SET is_mastered = 1 WHERE user_id = 1 AND vocabulary_id = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(update)) {
            ps.setInt(1, wordId); ps.executeUpdate();
        } catch (Exception e) {}
    }

    public void saveQuizScore(int score) {
        String insert = "INSERT INTO QuizHistory (score, date_taken) VALUES (?, date('now'))";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setInt(1, score); ps.executeUpdate();
        } catch (Exception e) {}
    }
}