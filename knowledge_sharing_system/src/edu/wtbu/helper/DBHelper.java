package edu.wtbu.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库工具类
 * 作用：管理数据库连接，避免重复代码
 */
public class DBHelper {
    
    // 数据库配置（改成你自己的）
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/knowledge_share?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    /**
     * 静态代码块：加载驱动（类加载时执行一次）
     * 用静态块：因为驱动只需要加载一次
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL驱动加载失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败：" + e.getMessage());
        }
    }
    
    /**
     * 关闭连接（释放资源）
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 一次性关闭所有资源
     */
    public static void closeAll(Connection conn, PreparedStatement ps, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(ps);
        closeConnection(conn);
    }
    
    public static void closeAll(Connection conn, PreparedStatement ps) {
        closeAll(conn, ps, null);
    }
}