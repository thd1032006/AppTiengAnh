package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import helper.DatabaseConnection;

public class LessonDAO {

    public List<Lesson> getAllLessons() {
        List<Lesson> list = new ArrayList<>();
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return list;
        try (c; Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM Lessons ORDER BY id")) {
            while (rs.next()) {
                Lesson l = new Lesson();
                l.setId(rs.getInt("id"));
                l.setLessonName(rs.getString("lesson_name"));
                try { l.setDescription(rs.getString("description")); } catch (Exception ignored) {}
                list.add(l);
            }
        } catch (Exception e) {
            System.err.println("Loi LessonDAO.getAllLessons: " + e.getMessage());
        }
        return list;
    }

    public int addLesson(Lesson lesson) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return -1;
        try (c) {
            c.setAutoCommit(false);
            int newId = 1;
            try (ResultSet rs = c.createStatement().executeQuery(
                    "SELECT id FROM Lessons ORDER BY id ASC")) {
                while (rs.next()) {
                    int existId = rs.getInt(1);
                    if (existId == newId) newId++;
                    else break;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Lessons (id, lesson_name, description) VALUES (?, ?, ?)")) {
                ps.setInt(1, newId);
                ps.setString(2, lesson.getLessonName());
                ps.setString(3, lesson.getDescription() != null ? lesson.getDescription() : "");
                ps.executeUpdate();
            }
            c.commit();
            return newId;
        } catch (Exception e) {
            System.err.println("Loi LessonDAO.addLesson: " + e.getMessage());
            try { c.rollback(); } catch (Exception ignored) {}
            return -1;
        }
    }

    public boolean updateLesson(Lesson lesson) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        String sql = "UPDATE Lessons SET lesson_name=?, description=? WHERE id=?";
        try (c; PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lesson.getLessonName());
            ps.setString(2, lesson.getDescription() != null ? lesson.getDescription() : "");
            ps.setInt(3, lesson.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Loi LessonDAO.updateLesson: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteLesson(int lessonId) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        try (c) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(
                    "DELETE FROM Vocabularies WHERE lesson_id=?")) {
                ps1.setInt(1, lessonId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(
                    "DELETE FROM Lessons WHERE id=?")) {
                ps2.setInt(1, lessonId);
                ps2.executeUpdate();
            }
            c.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Loi LessonDAO.deleteLesson: " + e.getMessage());
            try { c.rollback(); } catch (Exception ignored) {}
            return false;
        }
    }
}
