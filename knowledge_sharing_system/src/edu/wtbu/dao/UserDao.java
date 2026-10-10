package edu.wtbu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wtbu.helper.DBHelper;
import edu.wtbu.pojo.User;

/**
 * 用户数据访问类
 * 负责：用户注册、登录、查询、修改、删除
 * 每个实体类对应一个 Dao
 */
public class UserDao {
// ========== 增- register注册用户 ==========
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return true=成功, false=失败
     * 
     * PreparedStatement：防止SQL注入攻击
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password, email, phone, role) VALUES (?, ?, ?, ?, 0)";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
 // ========== 查-- login登录验证 ==========
    
    /**
     * 用户登录验证
     * 支持：用户名、邮箱、手机号 三种方式登录
     * SQL中的 OR 连接多个条件，实现多方式登录
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ? OR phone = ?) AND password = ? ";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();// 1. 获取数据库连接
            ps = conn.prepareStatement(sql); // 2. 预编译SQL语句
//             3.多方式登录
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, username);
            ps.setString(4, password);
            rs = ps.executeQuery();
            
            if (rs.next()) {//rs.next()判断是否有下一行数据，有就返回true并移动到该行
                User user = new User();//有则将结果封装成User对象
                user.setId(rs.getLong("id"));//从结果集中取出id，设置到user对象
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getInt("role"));
                return user;//// 返回完整的User对象
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return null;
    }
    
    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        String sql = "SELECT id, username, email, phone, role FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return null;
    }
    
    /**
     * 查询所有用户（管理员用）
     */
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT id, username, email, phone, role FROM users ORDER BY id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getInt("role"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return userList;
    }
    
    // ========== 改（Update）==========
    
    /**
     * 修改用户信息
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, phone = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setLong(4, user.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    /**
     * 修改密码
     */
    public boolean updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    // ==========删除 ==========
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
   // ========== 辅助验证方法 ==========
    
    public boolean isUsernameExist(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        return checkExist(sql, username);
    }
    
    public boolean isEmailExist(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        return checkExist(sql, email);
    }
    
    public boolean isPhoneExist(String phone) {
        String sql = "SELECT id FROM users WHERE phone = ?";
        return checkExist(sql, phone);
    }
    
    private boolean checkExist(String sql, String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
    }
    
    
}
