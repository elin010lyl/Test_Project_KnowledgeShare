# -*- coding: utf-8 -*-
"""管理员-知识审核 /knowledge?action=pending|audit|adminDelete（对应手工用例 KSS-PEND/AUDIT/ADEL 系列）"""
import allure
import pytest

from common import assertion as apt


@allure.feature("管理员模块")
@allure.story("知识审核")
class TestAdminAudit:

    @allure.title("未登录获取待审核列表失败")
    def test_pending_without_login(self, api):
        apt.assert_fail(api.knowledge_get("pending"), "无权限")

    @allure.title("普通用户访问待审核列表提示无权限（越权）")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_pending_as_normal_user(self, user_session):
        apt.assert_fail(user_session.knowledge_get("pending"), "无权限")

    @allure.title("管理员获取待审核列表成功且包含新发布知识")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_pending_as_admin(self, admin_session, published_knowledge):
        data = apt.assert_is_list(admin_session.knowledge_get("pending"))
        assert any(k["id"] == published_knowledge["id"] for k in data), "待审核列表未包含刚发布知识"

    @allure.title("审核通过后知识进入公开列表")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_audit_approve(self, admin_session, api, published_knowledge):
        kid = published_knowledge["id"]
        resp = admin_session.knowledge_post("audit", id=kid, auditAction="approve")
        apt.assert_success(resp)
        assert "审核通过" in resp.json()["data"]
        data = apt.assert_is_list(api.knowledge_get("list"))
        assert any(k["id"] == kid for k in data)

    @allure.title("审核拒绝后知识不出现在公开列表")
    def test_audit_reject(self, admin_session, api, published_knowledge):
        kid = published_knowledge["id"]
        resp = admin_session.knowledge_post("audit", id=kid, auditAction="reject")
        apt.assert_success(resp)
        assert "审核拒绝" in resp.json()["data"]
        data = apt.assert_is_list(api.knowledge_get("list"))
        assert all(k["id"] != kid for k in data)

    @allure.title("auditAction传非法值应报参数错误（关联BUG-004修复后行为）")
    @pytest.mark.xfail(reason="BUG-004：当前版本非approve一律按拒绝处理返回success；开发修复后自动转为xpass")
    def test_audit_invalid_action(self, admin_session, published_knowledge):
        resp = admin_session.knowledge_post("audit", id=published_knowledge["id"],
                                            auditAction="approv")
        apt.assert_fail(resp)

    @allure.title("已审核通过的知识不允许重复审核（关联BUG-005修复后行为）")
    @pytest.mark.xfail(reason="BUG-005：当前版本未校验知识状态，可重复审核；开发修复后自动转为xpass")
    def test_audit_twice(self, admin_session, approved_knowledge):
        resp = admin_session.knowledge_post("audit", id=approved_knowledge["id"],
                                            auditAction="reject")
        apt.assert_fail(resp)

    @allure.title("审核id传非数字应返回业务失败（关联BUG-002修复后行为）")
    @pytest.mark.xfail(reason="BUG-002：当前版本返回HTTP 500；开发修复后自动转为xpass")
    def test_audit_non_numeric_id(self, admin_session):
        resp = admin_session.knowledge_post("audit", id="abc", auditAction="approve")
        apt.assert_fail(resp)

    @allure.title("未登录执行审核失败")
    def test_audit_without_login(self, api, published_knowledge):
        resp = api.knowledge_post("audit", id=published_knowledge["id"], auditAction="approve")
        apt.assert_fail(resp)

    @allure.title("管理员删除任意状态知识成功")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_admin_delete_knowledge(self, admin_session, published_knowledge):
        kid = published_knowledge["id"]
        resp = admin_session.knowledge_post("adminDelete", id=kid)
        apt.assert_success(resp)
        assert "删除成功" in resp.json()["data"]

    @allure.title("管理员删除后详情查询提示知识不存在")
    def test_admin_deleted_detail_not_found(self, admin_session, api, published_knowledge):
        kid = published_knowledge["id"]
        apt.assert_success(admin_session.knowledge_post("adminDelete", id=kid))
        apt.assert_fail(api.knowledge_get("detail", id=kid), "知识不存在")

    @allure.title("普通用户执行管理员删除提示无权限")
    def test_admin_delete_as_normal_user(self, user_session, published_knowledge):
        resp = user_session.knowledge_post("adminDelete", id=published_knowledge["id"])
        apt.assert_fail(resp, "无权限")

    @allure.title("管理员删除id传非数字应返回业务失败（关联BUG-002修复后行为）")
    @pytest.mark.xfail(reason="BUG-002：当前版本返回HTTP 500；开发修复后自动转为xpass")
    def test_admin_delete_non_numeric_id(self, admin_session):
        resp = admin_session.knowledge_post("adminDelete", id="abc")
        apt.assert_fail(resp)
