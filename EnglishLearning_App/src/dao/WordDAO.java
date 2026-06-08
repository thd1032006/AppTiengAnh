package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Word;
import helper.DatabaseConnection;

public class WordDAO {

    public int countWordsByLesson(int lessonId) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return 0;
        try (c; PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM Vocabularies WHERE lesson_id = ?")) {
            ps.setInt(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Loi WordDAO.countWordsByLesson: " + e.getMessage());
        }
        return 0;
    }

    public List<Word> getWordsByLesson(int lessonId) {
        List<Word> list = new ArrayList<>();
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return list;
        try (c; PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM Vocabularies WHERE lesson_id = ?")) {
            ps.setInt(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("Loi WordDAO.getWordsByLesson: " + e.getMessage());
        }
        return list;
    }

    public List<Word> getAllWords() {
        List<Word> list = new ArrayList<>();
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return list;
        try (c; PreparedStatement ps = c.prepareStatement("SELECT * FROM Vocabularies");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            System.err.println("Loi WordDAO.getAllWords: " + e.getMessage());
        }
        return list;
    }

    public boolean addWord(Word w) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        String sql = "INSERT INTO Vocabularies (word, type, meaning, pronunciation, example, lesson_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (c; PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, w.getWord());
            ps.setString(2, w.getType());
            ps.setString(3, w.getMeaning());
            ps.setString(4, w.getPronunciation());
            ps.setString(5, w.getExample());
            ps.setInt(6, w.getLessonId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Loi WordDAO.addWord: " + e.getMessage());
            return false;
        }
    }

    public boolean updateWord(Word w) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        String sql = "UPDATE Vocabularies SET word=?, type=?, meaning=?, pronunciation=?, example=?, lesson_id=? WHERE id=?";
        try (c; PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, w.getWord());
            ps.setString(2, w.getType());
            ps.setString(3, w.getMeaning());
            ps.setString(4, w.getPronunciation());
            ps.setString(5, w.getExample());
            ps.setInt(6, w.getLessonId());
            ps.setInt(7, w.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Loi WordDAO.updateWord: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWord(int id) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        try (c; PreparedStatement ps = c.prepareStatement("DELETE FROM Vocabularies WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Loi WordDAO.deleteWord: " + e.getMessage());
            return false;
        }
    }

    private Word mapRow(ResultSet rs) throws Exception {
        Word w = new Word();
        w.setId(rs.getInt("id"));
        w.setWord(rs.getString("word"));
        w.setType(rs.getString("type"));
        w.setMeaning(rs.getString("meaning"));
        w.setPronunciation(rs.getString("pronunciation"));
        w.setExample(rs.getString("example"));
        w.setLessonId(rs.getInt("lesson_id"));
        return w;
    }
}
