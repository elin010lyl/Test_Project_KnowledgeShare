# -*- coding: utf-8 -*-
"""知识删除 POST /knowledge?action=delete（对应手工用例 KSS-DEL 系列）"""
import allure
import pytest

from common import assertion as apt


@allure.feature("知识模块")
@allure.story("知识删除")
class TestDeleteKnowledge:

    @allure.title("删除自己发布的知识成功")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_delete_own_knowledge(self, user_session, published_knowledge):
        resp = user_session.knowledge_post("delete", id=published_knowledge["id"])
        apt.assert_success(resp)
        assert "删除成功" in resp.json()["data"]

    @allure.title("删除后我的发布列表不再包含该知识")
    def test_deleted_not_in_my_knowledge(self, user_session, published_knowledge):
        kid = published_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("delete", id=kid))
        data = apt.assert_is_list(user_session.knowledge_get("myKnowledge"))
        assert all(k["id"] != kid for k in data)

    @allure.title("删除后详情查询提示知识不存在")
    def test_deleted_detail_not_found(self, user_session, published_knowledge):
        kid = published_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("delete", id=kid))
        apt.assert_fail(user_session.knowledge_get("detail", id=kid), "知识不存在")

    @allure.title("不能删除他人发布的知识（越权校验）")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_delete_others_knowledge(self, another_user, published_knowledge):
        resp = another_user.knowledge_post("delete", id=published_knowledge["id"])
        apt.assert_fail(resp, "只能删除自己发布的知识")

    @allure.title("未登录删除知识提示请先登录")
    def test_delete_without_login(self, api, published_knowledge):
        resp = api.knowledge_post("delete", id=published_knowledge["id"])
        apt.assert_fail(resp, "请先登录")

    @allure.title("不传id提示知识ID不能为空")
    def test_delete_missing_id(self, user_session):
        apt.assert_fail(user_session.knowledge_post("delete"), "知识ID不能为空")

    @allure.title("id传非数字应返回业务失败而非500（关联BUG-002修复后行为）")
    @pytest.mark.xfail(reason="BUG-002：当前版本Long.parseLong未捕获异常返回HTTP 500；开发修复后自动转为xpass")
    def test_delete_non_numeric_id(self, user_session):
        resp = user_session.knowledge_post("delete", id="abc")
        apt.assert_fail(resp)

    @allure.title("删除不存在的知识返回失败")
    def test_delete_nonexistent(self, user_session):
        resp = user_session.knowledge_post("delete", id=99999999)
        apt.assert_fail(resp)
