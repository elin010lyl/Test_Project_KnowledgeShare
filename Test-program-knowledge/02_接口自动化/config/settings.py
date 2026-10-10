# -*- coding: utf-8 -*-
"""被测环境配置：切换环境只需修改本文件"""

BASE_URL = "http://localhost:8080/knowledge_sharing_system"
TIMEOUT = 10  # 请求超时时间（秒）

# 数据库预置账号（与 db 初始化数据保持一致）
ADMIN_ACCOUNT = {"username": "admin", "password": "admin123"}
EXISTING_USER = {"username": "testuser1", "password": "123456",
                 "email": "testuser1@qq.com", "phone": "13800138001"}
EXISTING_USER2 = {"username": "testuser2", "password": "123456",
                  "email": "testuser2@qq.com", "phone": "13800138002"}
