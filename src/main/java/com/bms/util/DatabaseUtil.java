package com.bms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * H2 数据库连接与初始化工具。
 */
public class DatabaseUtil {

    private static final String JDBC_URL = "jdbc:h2:file:./data/bookdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("无法加载 H2 数据库驱动", e);
        }
    }

    private DatabaseUtil() {
    }

    /**
     * 获取数据库连接。
     *
     * @return 数据库连接
     * @throws SQLException 连接异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * 初始化数据库表结构。
     */
    public static void initializeDatabase() {
        String booksSql = "CREATE TABLE IF NOT EXISTS books (" +
                "isbn VARCHAR(20) PRIMARY KEY, " +
                "title VARCHAR(100) NOT NULL, " +
                "publisher VARCHAR(100), " +
                "author VARCHAR(100), " +
                "stock INT NOT NULL CHECK (stock >= 0), " +
                "price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)" +
                ")";

        String salesSql = "CREATE TABLE IF NOT EXISTS sales (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "isbn VARCHAR(20) NOT NULL, " +
                "title VARCHAR(100) NOT NULL, " +
                "quantity INT NOT NULL CHECK (quantity > 0), " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "sale_time TIMESTAMP NOT NULL" +
                ")";

        String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) PRIMARY KEY, " +
                "password VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(booksSql);
            stmt.execute(salesSql);
            stmt.execute(usersSql);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库失败", e);
        }
    }

    /**
     * 关闭数据库资源。
     *
     * @param conn      连接
     * @param statement 语句
     */
    public static void closeQuietly(Connection conn, Statement statement) {
        try {
            if (statement != null) statement.close();
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }
}
