package com.swim.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 从 jdbc.properties 读取配置，提供数据库连接
 */
public class DBUtil {

    private static String url;
    private static String username;
    private static String password;
    private static String driverClassName;

    static {
        try (InputStream is = DBUtil.class.getClassLoader()
                .getResourceAsStream("jdbc.properties")) {
            Properties props = new Properties();
            props.load(is);

            url = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
            driverClassName = props.getProperty("jdbc.driverClassName");

            Class.forName(driverClassName);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库配置加载失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}