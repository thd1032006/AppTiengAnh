package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Word;
import helper.DatabaseConnection;

public class WordDAO {
    private String safeStr(ResultSet rs, String col) {
        try { String v = rs.getString(col); return v != null ? v : ""; } catch (Exception e) { return ""; }
    }

    public List<Word> getWordsByLesson(int lessonId) {
        List<Word> list = new ArrayList<>();
        String sql = "SELECT * FROM Vocabularies WHERE lesson_id = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Word w = new Word();
                w.setId(rs.getInt("id"));
                w.setWord(safeStr(rs, "word"));
                w.setPronunciation(safeStr(rs, "pronunciation"));
                w.setMeaning(safeStr(rs, "meaning"));
                w.setExample(safeStr(rs, "example"));
                w.setLessonId(lessonId);
                list.add(w);
            }
        } catch (Exception e) {}
        return list;
    }

    public List<Word> getAllWords() {
        List<Word> list = new ArrayList<>();
        String sql = "SELECT * FROM Vocabularies";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Word w = new Word();
                w.setId(rs.getInt("id"));
                w.setWord(safeStr(rs, "word"));
                w.setPronunciation(safeStr(rs, "pronunciation"));
                w.setMeaning(safeStr(rs, "meaning"));
                w.setExample(safeStr(rs, "example"));
                w.setLessonId(rs.getInt("lesson_id"));
                list.add(w);
            }
        } catch (Exception e) {}
        return list;
    }
}