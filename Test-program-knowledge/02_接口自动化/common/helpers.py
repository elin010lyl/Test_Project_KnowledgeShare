# -*- coding: utf-8 -*-
"""业务前置 Helper：供 conftest fixture 与用例复用。"""
from common.api_client import ApiClient
from data.test_data import VALID_PASSWORD, unique_email, unique_phone, unique_username


def register_and_login() -> ApiClient:
    """注册一个全新唯一用户并登录，返回带登录态的客户端。

    每次调用生成唯一用户名（毫秒时间戳+随机数），保证：
    - 用例之间数据隔离，可任意顺序执行、可重复执行
    - 不依赖数据库预置数据
    """
    client = ApiClient()
    username = unique_username()
    resp = client.register(username, VALID_PASSWORD, VALID_PASSWORD,
                           unique_email(username), unique_phone())
    assert resp.json().get("flag") == "success", f"前置注册失败: {resp.text}"

    resp = client.login(username, VALID_PASSWORD)
    body = resp.json()
    assert body.get("flag") == "success", f"前置登录失败: {resp.text}"

    client.username = username
    client.user_id = body["data"]["id"]  # 登录响应中的用户ID，供管理员用例使用
    return client


def publish_and_get_id(client: ApiClient, title: str = None):
    """发布一条知识并反查其知识ID。

    说明：发布接口成功后不返回知识ID，因此通过 /knowledge?action=myKnowledge
    列表按唯一标题匹配获取ID——这也是本项目接口的一个测试关注点。
    """
    title = title or f"自动化知识_{unique_username('kb')}"
    resp = client.knowledge_post("publish", data={"title": title, "content": "自动化测试知识内容"})
    assert resp.json().get("flag") == "success", f"前置发布失败: {resp.text}"

    resp = client.knowledge_get("myKnowledge")
    items = resp.json().get("data") or []
    item = next((k for k in items if k.get("title") == title), None)
    assert item is not None, f"未在myKnowledge中找到刚发布的知识: {title}"
    return item["id"], title
