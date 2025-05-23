package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected Connection con;

    // Thay đổi các thông tin này cho phù hợp với cấu hình MySQL của bạn
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chess_tournament_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "286116";

    public DAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Trong ứng dụng thực tế, nên có cơ chế xử lý lỗi tốt hơn
            // Ví dụ: throw new RuntimeException("Không thể kết nối đến cơ sở dữ liệu", e);
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        // Kiểm tra nếu kết nối bị đóng hoặc null thì tạo mới
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy lại kết nối CSDL: " + e.getMessage());
        }
        return con;
    }

    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}