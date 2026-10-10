# -*- coding: utf-8 -*-
"""知识列表 GET /knowledge?action=list（对应手工用例 KSS-LIST-001~005）"""
import allure

from common import assertion as apt


@allure.feature("知识模块")
@allure.story("知识列表")
class TestKnowledgeList:

    @allure.title("未登录可获取已审核知识列表")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_list_without_login(self, api):
        apt.assert_is_list(api.knowledge_get("list"))

    @allure.title("列表仅包含已审核通过(status=1)的知识")
    def test_list_only_approved(self, api):
        data = apt.assert_is_list(api.knowledge_get("list"))
        for item in data:
            assert item["status"] == 1, f"公开列表混入非通过状态知识: {item}"

    @allure.title("列表按创建时间倒序排列")
    def test_list_sorted_desc(self, api):
        data = apt.assert_is_list(api.knowledge_get("list"))
        if len(data) >= 2:
            assert data[0]["createTime"] >= data[-1]["createTime"], "列表未按创建时间倒序"

    @allure.title("列表元素字段完整性校验")
    def test_list_item_fields(self, api):
        data = apt.assert_is_list(api.knowledge_get("list"))
        if data:
            for key in ("id", "title", "content", "category", "status",
                        "viewCount", "createTime", "publisherName"):
                assert key in data[0], f"列表元素缺少字段: {key}"

    @allure.title("新审核通过的知识出现在公开列表（发布-审核-展示全链路）")
    def test_approved_knowledge_in_list(self, api, approved_knowledge):
        data = apt.assert_is_list(api.knowledge_get("list"))
        assert any(k["id"] == approved_knowledge["id"] for k in data)

    @allure.title("待审核知识不出现在公开列表")
    def test_pending_knowledge_not_in_list(self, api, published_knowledge):
        data = apt.assert_is_list(api.knowledge_get("list"))
        assert all(k["id"] != published_knowledge["id"] for k in data)

    @allure.title("不携带action参数返回未知操作")
    def test_missing_action(self, api):
        apt.assert_fail(api.knowledge_get(), "未知操作")

    @allure.title("非法action返回未知操作")
    def test_invalid_action(self, api):
        apt.assert_fail(api.knowledge_get("hack"), "未知操作")
