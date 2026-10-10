package edu.wtbu.service;

import java.util.List;

import edu.wtbu.dao.KnowledgeDao;
import edu.wtbu.pojo.Knowledge;

/**
 * 知识业务逻辑类
 */
public class KnowledgeService {
    
    private KnowledgeDao knowledgeDao = new KnowledgeDao();
    
    /**
     * 发布知识
     */
    public boolean publish(Knowledge knowledge) {
        if (knowledge.getTitle() == null || knowledge.getTitle().trim().isEmpty()) {
            return false;
        }
        if (knowledge.getContent() == null || knowledge.getContent().trim().isEmpty()) {
            return false;
        }
        return knowledgeDao.publish(knowledge);
    }
    
    /**
     * 获取审核通过的知识列表
     */
 // 获取知识列表
    public List<Knowledge> getApprovedKnowledge() {
        return knowledgeDao.findApproved();
    }
    
    /**
     * 获取待审核的知识列表
     */
    public List<Knowledge> getPendingKnowledge() {
        return knowledgeDao.findPending();
    }
    
    /**
     * 获取单个的知识详情
     */
    public Knowledge getKnowledgeDetail(Long id) {
        Knowledge knowledge = knowledgeDao.findById(id);
        return knowledge;
    }
    
    /**
     * 审核知识
     */
    public boolean auditKnowledge(Long knowledgeId, Integer status) {
        return knowledgeDao.audit(knowledgeId, status);
    }
    
    /**
     * 管理员删除知识
     */
    public boolean adminDeleteKnowledge(Long knowledgeId) {
        return knowledgeDao.deleteKnowledge(knowledgeId);
    }
    
    /**
     * 删除知识（只能删除自己发布的）
     */
    public boolean deleteKnowledge(Long knowledgeId, Long userId) {
        if (knowledgeDao.isOwner(knowledgeId, userId)) {
            return knowledgeDao.deleteKnowledge(knowledgeId);
        }
        return false;
    }
    
    /**
     * 获取我发布的知识
     */
    public List<Knowledge> getMyKnowledge(Long userId) {
        return knowledgeDao.findByUserId(userId);
    }
    
    
}