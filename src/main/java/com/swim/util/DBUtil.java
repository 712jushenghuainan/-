package com.swim.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBUtil {

    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            // 1. 读取 jdbc.properties 文件
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties props = new Properties();
            props.load(is);

            url = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");

            // 2. 加载驱动
            Class.forName(props.getProperty("jdbc.driver"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库初始化失败，请检查 jdbc.properties 配置！");
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // 关闭资源（增删改用）
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    // 关闭资源（查询用）
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
