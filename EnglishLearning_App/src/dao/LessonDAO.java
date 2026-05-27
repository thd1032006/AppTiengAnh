package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import helper.DatabaseConnection;

public class LessonDAO {
    public List<Lesson> getAllLessons() {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM Lessons";
        try (Connection c = DatabaseConnection.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Lesson l = new Lesson();
                l.setId(rs.getInt("id"));
                l.setLessonName(rs.getString("lesson_name"));
                list.add(l);
            }
        } catch (Exception e) { System.err.println("Lỗi LessonDAO: " + e.getMessage()); }
        return list;
    }
}