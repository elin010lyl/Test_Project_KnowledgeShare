# -*- coding: utf-8 -*-
"""全局 fixture：会话管理与业务数据构造。

会话策略：
- api          ：匿名会话，session 级共享（只做无登录态请求）
- admin_session：管理员会话，session 级共享（对管理员自身只读）
- user_session ：普通用户会话，function 级（每条用例注册全新唯一用户，天然隔离）
- another_user ：第二个普通用户（用于越权类用例）

数据策略：所有前置数据（用户/知识）用例内自建，不依赖执行顺序与预置数据。
"""
import pytest

from common.api_client import ApiClient
from common.helpers import publish_and_get_id, register_and_login
from config.settings import ADMIN_ACCOUNT


@pytest.fixture(scope="session")
def api() -> ApiClient:
    """匿名会话（未登录）"""
    return ApiClient()


@pytest.fixture(scope="session")
def admin_session() -> ApiClient:
    """管理员登录态"""
    client = ApiClient()
    resp = client.login(ADMIN_ACCOUNT["username"], ADMIN_ACCOUNT["password"])
    body = resp.json()
    assert body.get("flag") == "success", f"管理员登录失败，请检查预置账号: {body}"
    client.admin_id = body["data"]["id"]
    return client


@pytest.fixture(scope="function")
def user_session() -> ApiClient:
    """普通用户会话：唯一用户注册 + 登录"""
    return register_and_login()


@pytest.fixture(scope="function")
def another_user() -> ApiClient:
    """第二个普通用户（越权类用例使用）"""
    return register_and_login()


@pytest.fixture(scope="function")
def published_knowledge(user_session):
    """当前用户已发布（待审核 status=0）的知识"""
    kid, title = publish_and_get_id(user_session)
    return {"id": kid, "title": title}


@pytest.fixture(scope="function")
def approved_knowledge(user_session, admin_session):
    """当前用户已发布且管理员审核通过（status=1）的知识"""
    kid, title = publish_and_get_id(user_session)
    resp = admin_session.knowledge_post("audit", id=kid, auditAction="approve")
    assert resp.json().get("flag") == "success", f"前置审核失败: {resp.text}"
    return {"id": kid, "title": title}
