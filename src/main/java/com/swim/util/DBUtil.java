package com.swim.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {

    private static String url;
    private static String username;
    private static String password;

    // 静态代码块：类加载时自动读取 jdbc.properties 文件
    static {
        try {
            // 读取 resources 目录下的 jdbc.properties
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties props = new Properties();
            props.load(is);
            url = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库配置加载失败！请检查 jdbc.properties");
        }
    }

    // 获取连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // 关闭资源（防止内存泄漏）
    public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}