package edu.wtbu.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wtbu.helper.DBHelper;

/**
 * 收藏数据访问类
 * 负责：添加收藏、取消收藏、查询收藏状态
 */
public class FavoriteDao {
    
	/**
     * 添加收藏
     */
    public boolean addFavorite(Long userId, Long knowledgeId) {
        String sql = "INSERT INTO favorite (user_id, knowledge_id, create_time) VALUES (?, ?, NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, knowledgeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    /**
     * 取消收藏
     */
    public boolean removeFavorite(Long userId, Long knowledgeId) {
        String sql = "DELETE FROM favorite WHERE user_id = ? AND knowledge_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, knowledgeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long knowledgeId) {
        String sql = "SELECT id FROM favorite WHERE user_id = ? AND knowledge_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, knowledgeId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
    }
    
    /**
     * 获取用户所有收藏的知识ID
     */
    public List<Long> getUserFavoriteIds(Long userId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT knowledge_id FROM favorite WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ids.add(rs.getLong("knowledge_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return ids;
    }
    
    
    /**
     * 删除某个用户的所有收藏记录（级联删除用户时使用）
     */
    public void removeAllByUser(Long userId) {
        String sql = "DELETE FROM favorite WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }

    /**
     * 删除某个知识的所有收藏记录（级联删除知识时使用）
     */
    public void removeAllByKnowledge(Long knowledgeId) {
        String sql = "DELETE FROM favorite WHERE knowledge_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, knowledgeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
   }