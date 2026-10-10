package edu.wtbu.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wtbu.helper.DBHelper;
import edu.wtbu.pojo.Knowledge;

/**
 * 知识数据访问类
 * 负责：发布知识、查询知识、审核知识、删除知识
 * 
 * 包含多表查询：knowledge 表 JOIN users 表
 */
public class KnowledgeDao {
    
    // ========== 增-发布知识 ==========
    
    public boolean publish(Knowledge knowledge) {
        String sql = "INSERT INTO knowledge (title, content, category, user_id, status, create_time) VALUES (?, ?, ?, ?, 0, NOW())";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, knowledge.getTitle());
            ps.setString(2, knowledge.getContent());
            ps.setString(3, knowledge.getCategory());
            ps.setLong(4, knowledge.getUserId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    // ========== 查-包含多表查询 ==========
    
    /**
     * 查询所有审核通过的知识
     * 多表查询：LEFT JOIN users 表获取发布者用户名
     * 为什么用LEFT JOIN？保留知识数据，即使发布者被删除也能显示
     */
 // 获取知识列表
    public List<Knowledge> findApproved() {
        List<Knowledge> list = new ArrayList<>();
        String sql = "SELECT k.*, u.username as publisher_name FROM knowledge k LEFT JOIN users u ON k.user_id = u.id WHERE k.status = 1 ORDER BY k.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Knowledge k = extractKnowledge(rs);//// 调用这个方法，基础字段全部封装好
                k.setPublisherName(rs.getString("publisher_name"));//这个字段sql里没有，需要单独赋值
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return list;
    }
    
    /**
     * ==查询待审核的知识（管理员用）===
     */
    public List<Knowledge> findPending() {
        List<Knowledge> list = new ArrayList<>();
        String sql = "SELECT k.*, u.username as publisher_name FROM knowledge k LEFT JOIN users u ON k.user_id = u.id WHERE k.status = 0 ORDER BY k.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Knowledge k = extractKnowledge(rs);
                k.setPublisherName(rs.getString("publisher_name"));
                list.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();//控制台打印报错日志
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return list;
    }
    
    /**
     * ===根据知识ID查询单个知识===
     */
    public Knowledge findById(Long id) {
        String sql = "SELECT k.*, u.username as publisher_name FROM knowledge k LEFT JOIN users u ON k.user_id = u.id WHERE k.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Knowledge k = extractKnowledge(rs);
                k.setPublisherName(rs.getString("publisher_name"));
                return k;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return null;
    }
    
    /**
     * 查询某用户发布的所有文章，用户id
     */
    public List<Knowledge> findByUserId(Long userId) {
        List<Knowledge> list = new ArrayList<>();
        String sql = "SELECT * FROM knowledge WHERE user_id = ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(extractKnowledge(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
        return list;
    }
    
    // ========== 改（Update）- 审核、统计 ==========
    
    /**
     * 审核知识，修改审核状态
     */
    public boolean audit(Long knowledgeId, Integer status) {
        String sql = "UPDATE knowledge SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);      // 状态：1=通过，2=不通过
            ps.setLong(2, knowledgeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    
    // ========== 删（Delete）- 逻辑删除 ==========
    
    /**
     * 删除知识
     */
//   成员变量 全局创建一个实例 可以节省资源空间，不需要每次都new了
    private FavoriteDao favoriteDao = new FavoriteDao();
    public boolean deleteKnowledge(Long knowledgeId) {
        // 1. 先删除该知识的所有收藏，不然指向空地址会报错
        favoriteDao.removeAllByKnowledge(knowledgeId);  // 需要持有 FavoriteDao 实例
        // 2. 再删除知识本身
        String sql = "DELETE FROM knowledge WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, knowledgeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps);
        }
    }
    
    /**
     * 验证知识是否属于当前用户，判断是否为作者
     */
    public boolean isOwner(Long knowledgeId, Long userId) {
        String sql = "SELECT id FROM knowledge WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBHelper.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, knowledgeId);
            ps.setLong(2, userId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBHelper.closeAll(conn, ps, rs);
        }
    }
    
    // ========== 辅助方法 ==========
    //为了可以代码复用，所以单独写，要用直接调就行
    /*    extractKnowledge 读 rs 时出错，抛出异常；
		异常跳出 extractKnowledge，回到 findApproved 的 while 循环；
   		直接被外层 try-catch 接住打印错误。*/
    private Knowledge extractKnowledge(ResultSet rs) throws SQLException {
        Knowledge k = new Knowledge();//空的知识实体对象，用来存放数据库的数据。
        k.setId(rs.getLong("id"));
        k.setTitle(rs.getString("title"));
        k.setContent(rs.getString("content"));
        k.setCategory(rs.getString("category"));
        k.setUserId(rs.getLong("user_id"));
        k.setStatus(rs.getInt("status"));
        k.setCreateTime(rs.getTimestamp("create_time"));//年月日 时分秒
        return k;
    }
}
