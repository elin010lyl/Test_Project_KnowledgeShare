package edu.wtbu.pojo;

import java.util.Date;

/**
 * 收藏实体类
 * 这是一个关联表：记录哪个用户收藏了哪个知识
 */
public class Favorite {
	private Long id;           // 收藏记录ID
    private Long userId;       // 用户ID（谁收藏的）
    private Long knowledgeId;  // 知识ID（收藏的什么）
    private Date createTime;   // 收藏时间
    
    public Favorite() {}
    
	public Favorite(Long userId, Long knowledgeId) {
		this.userId = userId;
		this.knowledgeId = knowledgeId;
		this.createTime = new Date();
	}
	
	//  getter和setter方法
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(Long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
