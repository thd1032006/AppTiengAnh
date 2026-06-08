package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import helper.DatabaseConnection;
import model.User;

public class UserDAO {

    /** Đăng nhập: trả về User nếu đúng, null nếu sai */
    public User login(String username, String password) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return null;
        try (c; PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM Users WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("Lỗi UserDAO.login: " + e.getMessage());
        }
        return null;
    }

    /** Đăng ký tài khoản mới. Trả về null nếu thành công, hoặc thông báo lỗi */
    public String register(String username, String password, String email, String phone) {
        // Kiểm tra trùng username
        if (existsByField("username", username)) return "Tên đăng nhập đã được sử dụng!";
        // Kiểm tra trùng email
        if (email != null && !email.trim().isEmpty() && existsByField("email", email))
            return "Email đã được sử dụng!";
        // Kiểm tra trùng phone
        if (phone != null && !phone.trim().isEmpty() && existsByField("phone", phone))
            return "Số điện thoại đã được sử dụng!";

        Connection c = DatabaseConnection.getConnection();
        if (c == null) return "Không thể kết nối cơ sở dữ liệu!";
        try (c; PreparedStatement ps = c.prepareStatement(
                "INSERT INTO Users (username, password, email, phone) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, email != null && !email.trim().isEmpty() ? email.trim() : null);
            ps.setString(4, phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            ps.executeUpdate();
            return null; // thành công
        } catch (Exception e) {
            System.err.println("Lỗi UserDAO.register: " + e.getMessage());
            return "Đăng ký thất bại: " + e.getMessage();
        }
    }

    /** Tìm user theo email hoặc số điện thoại (dùng cho quên mật khẩu) */
    public User findByEmailOrPhone(String contact) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return null;
        try (c; PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM Users WHERE email = ? OR phone = ?")) {
            ps.setString(1, contact.trim());
            ps.setString(2, contact.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("Lỗi UserDAO.findByEmailOrPhone: " + e.getMessage());
        }
        return null;
    }

    /** Đặt lại mật khẩu mới cho user theo id */
    public boolean resetPassword(int userId, String newPassword) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        try (c; PreparedStatement ps = c.prepareStatement(
                "UPDATE Users SET password = ? WHERE id = ?")) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi UserDAO.resetPassword: " + e.getMessage());
            return false;
        }
    }

    private boolean existsByField(String field, String value) {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return false;
        try (c; PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM Users WHERE " + field + " = ?")) {
            ps.setString(1, value.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("Lỗi UserDAO.existsByField: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}
