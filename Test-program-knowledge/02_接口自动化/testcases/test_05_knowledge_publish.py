# -*- coding: utf-8 -*-
"""知识发布 POST /knowledge?action=publish（对应手工用例 KSS-PUB/MYK 系列）"""
import allure

from common import assertion as apt
from data.test_data import unique_title


@allure.feature("知识模块")
@allure.story("知识发布")
class TestPublish:

    @allure.title("登录后正常发布知识，进入待审核")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_publish_success(self, user_session):
        resp = user_session.knowledge_post("publish", data={
            "title": unique_title(), "content": "自动化测试发布的知识内容"})
        apt.assert_success(resp)
        assert "发布成功" in resp.json()["data"]

    @allure.title("发布后在我的发布列表可见且状态为待审核")
    def test_publish_visible_in_my_knowledge(self, user_session, published_knowledge):
        data = apt.assert_is_list(user_session.knowledge_get("myKnowledge"))
        item = next((k for k in data if k["id"] == published_knowledge["id"]), None)
        assert item is not None, "我的发布中未找到刚发布的知识"
        assert item["status"] == 0, f"新发布知识状态应为0(待审核)，实际: {item['status']}"

    @allure.title("标题为空发布失败")
    def test_publish_empty_title(self, user_session):
        resp = user_session.knowledge_post("publish", data={"title": "   ", "content": "内容"})
        apt.assert_fail(resp, "标题不能为空")

    @allure.title("内容为空发布失败")
    def test_publish_empty_content(self, user_session):
        resp = user_session.knowledge_post("publish", data={"title": unique_title(), "content": ""})
        apt.assert_fail(resp, "内容不能为空")

    @allure.title("未登录发布知识提示请先登录")
    def test_publish_without_login(self, api):
        resp = api.knowledge_post("publish", data={"title": unique_title(), "content": "内容"})
        apt.assert_fail(resp, "请先登录")

    @allure.title("分类可为空，不影响发布成功")
    def test_publish_category_optional(self, user_session):
        resp = user_session.knowledge_post("publish", data={
            "title": unique_title("无分类"), "content": "不传category的发布"})
        apt.assert_success(resp)

    @allure.title("连续发布多条，我的发布列表均可见")
    def test_publish_multiple(self, user_session):
        t1, t2 = unique_title(), unique_title()
        apt.assert_success(user_session.knowledge_post("publish", data={"title": t1, "content": "内容1"}))
        apt.assert_success(user_session.knowledge_post("publish", data={"title": t2, "content": "内容2"}))
        data = apt.assert_is_list(user_session.knowledge_get("myKnowledge"))
        titles = {k["title"] for k in data}
        assert t1 in titles and t2 in titles
