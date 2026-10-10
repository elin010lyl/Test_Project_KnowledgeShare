# -*- coding: utf-8 -*-
"""测试数据工厂：唯一数据生成 + 常量数据。

唯一性策略：毫秒时间戳 + 随机数，保证多轮执行、并发执行不冲突。
"""
import random
import time

# 数据库预置账号密码（详见 01_测试文档/01_项目概述与测试计划.md）
VALID_PASSWORD = "123456"
NEW_PASSWORD = "np654321"  # 修改密码用例的新密码（>=6位）


def unique_username(prefix: str = "auto") -> str:
    return f"{prefix}_{int(time.time() * 1000)}{random.randint(100, 999)}"


def unique_email(prefix: str = "auto") -> str:
    return f"{unique_username(prefix)}@qq.com"


def unique_phone() -> str:
    """138 开头的 11 位手机号"""
    return f"138{random.randint(10000000, 99999999)}"


def unique_title(prefix: str = "知识") -> str:
    return f"自动化{prefix}_{unique_username('t')}"
