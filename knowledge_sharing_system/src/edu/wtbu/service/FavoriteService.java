package edu.wtbu.service;

import java.util.ArrayList;
import java.util.List;

import edu.wtbu.dao.FavoriteDao;
import edu.wtbu.dao.KnowledgeDao;
import edu.wtbu.pojo.Knowledge;

/**
 * 收藏业务逻辑类
 */
public class FavoriteService {
    
    private FavoriteDao favoriteDao = new FavoriteDao();
    private KnowledgeDao knowledgeDao = new KnowledgeDao();
    
    /**
     * 添加收藏
     */
    public boolean addFavorite(Long userId, Long knowledgeId) {
        // 检查是否已收藏
        if (favoriteDao.isFavorited(userId, knowledgeId)) {
            return false;
        }
        boolean result = favoriteDao.addFavorite(userId, knowledgeId);
        return result;
    }
    
    /**
     * 取消收藏
     */
    public boolean removeFavorite(Long userId, Long knowledgeId) {
        boolean result = favoriteDao.removeFavorite(userId, knowledgeId);
        return result;
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long knowledgeId) {
        return favoriteDao.isFavorited(userId, knowledgeId);
    }
    
    /**
     * 获取用户收藏的知识列表
     */
    public List<Knowledge> getUserFavorites(Long userId) {//只能存放 Knowledge 对象的列表
        List<Long> favoriteIds = favoriteDao.getUserFavoriteIds(userId);
        List<Knowledge> favorites = new ArrayList<>();
        for (Long id : favoriteIds) {//遍历 ID 列表，一个一个去 Knowledge 表查文章详情
            Knowledge k = knowledgeDao.findById(id);
            if (k != null && k.getStatus() == 1) {  // 只返回审核通过的知识
                favorites.add(k);
            }
        }
        return favorites;
    }
}