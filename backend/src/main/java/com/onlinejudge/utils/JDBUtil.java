package com.onlinejudge.utils;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JDBUtil {
    // DataSource 这个东西一般一个程序里有一个实例就够了
    // 单例模式
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/OJDB?characterEncoding=utf8&useSSL=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "code";

    private static DataSource dataSource = null;

    // 创建出一个 DataSource 实例
    private static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new MysqlDataSource();
            ((MysqlDataSource)dataSource).setUrl(URL);
            ((MysqlDataSource)dataSource).setUser(USERNAME);
            ((MysqlDataSource)dataSource).setPassword(PASSWORD);
        }
        return dataSource;
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 回收资源
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
