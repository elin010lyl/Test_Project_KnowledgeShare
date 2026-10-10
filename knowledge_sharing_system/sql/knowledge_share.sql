/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50515
Source Host           : localhost:3306
Source Database       : knowledge_share

Target Server Type    : MYSQL
Target Server Version : 50515
File Encoding         : 65001

Date: 2026-05-22 16:11:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `favorite`
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏记录唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '收藏者ID',
  `knowledge_id` bigint(20) NOT NULL COMMENT '被收藏的知识ID',
  `create_time` datetime DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_knowledge` (`user_id`,`knowledge_id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  CONSTRAINT `fk_favorite_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_favorite_knowledge_id` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES ('1', '2', '3', null);
INSERT INTO `favorite` VALUES ('2', '2', '5', null);
INSERT INTO `favorite` VALUES ('3', '2', '8', null);
INSERT INTO `favorite` VALUES ('4', '2', '9', null);
INSERT INTO `favorite` VALUES ('5', '2', '4', null);
INSERT INTO `favorite` VALUES ('6', '3', '2', null);
INSERT INTO `favorite` VALUES ('7', '3', '4', null);
INSERT INTO `favorite` VALUES ('8', '3', '5', null);
INSERT INTO `favorite` VALUES ('9', '3', '7', null);
INSERT INTO `favorite` VALUES ('10', '3', '10', null);
INSERT INTO `favorite` VALUES ('11', '4', '1', null);
INSERT INTO `favorite` VALUES ('12', '4', '3', null);
INSERT INTO `favorite` VALUES ('13', '4', '5', null);
INSERT INTO `favorite` VALUES ('14', '4', '8', null);
INSERT INTO `favorite` VALUES ('15', '5', '2', null);
INSERT INTO `favorite` VALUES ('16', '5', '6', null);
INSERT INTO `favorite` VALUES ('17', '5', '7', null);
INSERT INTO `favorite` VALUES ('18', '5', '9', null);
INSERT INTO `favorite` VALUES ('19', '5', '10', null);
INSERT INTO `favorite` VALUES ('20', '6', '1', null);
INSERT INTO `favorite` VALUES ('21', '6', '2', null);
INSERT INTO `favorite` VALUES ('22', '7', '5', null);
INSERT INTO `favorite` VALUES ('23', '7', '8', null);
INSERT INTO `favorite` VALUES ('24', '7', '9', null);
INSERT INTO `favorite` VALUES ('25', '8', '3', null);
INSERT INTO `favorite` VALUES ('26', '8', '4', null);
INSERT INTO `favorite` VALUES ('27', '8', '7', null);
INSERT INTO `favorite` VALUES ('28', '8', '10', null);
INSERT INTO `favorite` VALUES ('29', '9', '1', null);
INSERT INTO `favorite` VALUES ('30', '9', '2', null);
INSERT INTO `favorite` VALUES ('31', '9', '6', null);
INSERT INTO `favorite` VALUES ('32', '10', '4', null);
INSERT INTO `favorite` VALUES ('33', '10', '5', null);
INSERT INTO `favorite` VALUES ('34', '10', '8', null);
INSERT INTO `favorite` VALUES ('35', '10', '9', null);

-- ----------------------------
-- Table structure for `knowledge`
-- ----------------------------
DROP TABLE IF EXISTS `knowledge`;
CREATE TABLE `knowledge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识唯一标识',
  `title` varchar(255) NOT NULL COMMENT '知识标题',
  `content` text NOT NULL COMMENT '知识详细内容',
  `category` varchar(50) DEFAULT NULL COMMENT '分类（Java/Python/前端等）',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `user_id` bigint(20) DEFAULT NULL COMMENT '发布者ID',
  `status` tinyint(4) DEFAULT '0' COMMENT '审核状态：0=待审核，1=审核通过，2=审核不通过',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数量',
  `favorite_count` int(11) DEFAULT '0' COMMENT '收藏数量',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `create_time` datetime DEFAULT NULL COMMENT '发布时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_knowledge_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='知识信息表';

-- ----------------------------
-- Records of knowledge
-- ----------------------------
INSERT INTO `knowledge` VALUES ('1', 'Java从入门到精通', 'Java是一种面向对象的编程语言...', 'Java', null, '2', '1', '1250', '86', '45', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('2', 'Python数据分析实战', 'Pandas、NumPy、Matplotlib...', 'Python', null, '2', '1', '890', '67', '32', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('3', 'Vue3核心原理详解', 'Vue3使用Proxy代理实现数据响应式...', '前端', null, '3', '1', '2100', '156', '89', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('4', 'MySQL索引优化实战', 'B+Tree索引、Hash索引...', '数据库', null, '3', '1', '1560', '98', '67', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('5', 'Spring Boot快速开发指南', '自动配置、起步依赖...', 'Java', null, '4', '1', '3200', '234', '120', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('6', 'Docker容器化部署实践', 'docker run、docker build...', 'DevOps', null, '5', '1', '780', '45', '28', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('7', 'Git团队协作最佳实践', 'Git Flow工作流...', '版本控制', null, '5', '1', '950', '67', '34', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('8', 'Redis缓存设计与实战', '缓存穿透、雪崩、击穿...', '中间件', null, '7', '1', '1850', '123', '78', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('9', '微服务架构设计模式', '服务拆分原则...', '架构', null, '8', '1', '2400', '189', '95', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('10', 'Linux服务器运维手册', 'ps、top、netstat...', 'Linux', null, '9', '1', '670', '34', '21', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('11', '人工智能入门指南', '机器学习基础...', 'AI', null, '2', '0', '0', '0', '0', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('12', 'Kubernetes容器编排实战', 'Pod、Service...', 'DevOps', null, '4', '0', '0', '0', '0', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('13', 'Rust编程语言入门', '所有权、借用...', '编程语言', null, '7', '0', '0', '0', '0', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('14', 'Hadoop大数据生态', 'HDFS架构...', '大数据', null, '10', '0', '0', '0', '0', null, null, null, null, null, '0');
INSERT INTO `knowledge` VALUES ('15', '网络安全攻防基础', 'SQL注入、XSS...', '安全', null, '5', '0', '0', '0', '0', null, null, null, null, null, '0');

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role` tinyint(4) DEFAULT '0' COMMENT '角色：0=普通用户，1=管理员',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：0=禁用，1=启用',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'admin', 'admin123', 'admin@knowledge.com', '13800000000', null, '1', '1', null, null, '0');
INSERT INTO `users` VALUES ('2', '张三', 'zhangsan123', 'zhangsan@qq.com', '13812345678', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('3', '李四', 'lisi2024', 'lisi@163.com', '13987654321', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('4', '王小明', 'wxm888888', 'wangxm@gmail.com', '13712345678', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('5', '赵丽颖', 'zly5201314', 'zhaoly@126.com', '13687654321', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('6', '陈小明', 'chen123456', 'chenxm@sina.com', '13511223344', null, '0', '0', null, null, '0');
INSERT INTO `users` VALUES ('7', '刘芳', 'liufang789', 'liufang@qq.com', '15099887766', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('8', '周杰', 'zhoujie666', 'zhoujie@163.com', '15166778899', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('9', '吴婷婷', 'wtt111111', 'wutt@outlook.com', '15233445566', null, '0', '1', null, null, '0');
INSERT INTO `users` VALUES ('10', '郑伟', 'zhengwei999', 'zhengwei@qq.com', '15322334455', null, '0', '1', null, null, '0');
