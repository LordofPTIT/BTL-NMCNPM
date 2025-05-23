package com.example.dao;

import com.example.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO {

    public UserDAO() {
        super();
    }

    public User checkLogin(String username, String password) {
        User user = null;
        String sql = "SELECT id, username, fullName, role FROM tblUser WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // Trong thực tế, password nên được hash và so sánh hash
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fullName"),
                        rs.getString("role")
                );
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi cụ thể hơn nếu cần
        }
        return user;
    }
}