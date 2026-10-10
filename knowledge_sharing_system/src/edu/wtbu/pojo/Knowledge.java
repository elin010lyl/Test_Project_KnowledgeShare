package edu.wtbu.pojo;

import java.util.Date;

/**
 * 知识实体类
 * 对应数据库的 knowledge 表
 */
public class Knowledge {
	
	 private Long id;              // 知识ID
	 private String title;         // 标题
	 private String content;       // 内容
	 private String category;      // 分类
	 private Long userId;          // 发布者ID
	 private Integer status;       // 状态：0=待审核，1=通过，2=不通过
	 private Date createTime;      // 发布时间
	
	 private String publisherName; // 这个不存数据库，只是显示用，是用于多表查询（关联用户表获取发布者名称）
	    
	    public Knowledge() {}

	 // 发布知识时的构造方法
		public Knowledge(String title, String content, String category, Long userId) {
			super();
			this.title = title;
			this.content = content;
			this.category = category;
			this.userId = userId;
			this.status = 0;        // 默认待审核
	        this.createTime = new Date();
		}
		//  getter和setter方法
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}


		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public String getPublisherName() {
			return publisherName;
		}

		public void setPublisherName(String publisherName) {
			this.publisherName = publisherName;
		}
	    
		// 辅助方法：获取状态描述
		public String getStatusDesc() {
	        if (status == null) return "未知";
	        switch (status) {
	            case 0: return "待审核";
	            case 1: return "已通过";
	            case 2: return "未通过";
	            default: return "未知";
	        }
	    }
	
}
