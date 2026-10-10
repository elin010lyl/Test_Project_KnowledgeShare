# -*- coding: utf-8 -*-
"""管理员-用户管理 /admin/user（对应手工用例 KSS-AUL/AUU/AUD 系列）"""
import allure
import pytest

from common import assertion as apt
from data.test_data import VALID_PASSWORD, unique_email, unique_phone


@allure.feature("管理员模块")
@allure.story("用户管理")
class TestAdminUser:

    @allure.title("未登录获取用户列表失败")
    def test_user_list_without_login(self, api):
        apt.assert_fail(api.admin_user_get(), "无权限")

    @allure.title("普通用户获取用户列表提示无权限（越权）")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_user_list_as_normal_user(self, user_session):
        apt.assert_fail(user_session.admin_user_get(), "无权限")

    @allure.title("管理员获取用户列表且不返回密码字段")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_user_list_as_admin(self, admin_session):
        data = apt.assert_is_list(admin_session.admin_user_get())
        assert len(data) >= 3, "用户列表数据异常，请检查预置数据"
        for item in data:
            for key in ("id", "username", "email", "phone", "role"):
                assert key in item, f"用户列表缺少字段: {key}"
            assert "password" not in item, "用户列表泄露密码字段"

    @allure.title("管理员修改用户邮箱手机号成功并回查生效")
    def test_admin_update_user(self, admin_session, another_user):
        new_email, new_phone = unique_email(), unique_phone()
        resp = admin_session.admin_user_post("update", id=another_user.user_id,
                                             username=another_user.username,
                                             email=new_email, phone=new_phone)
        apt.assert_success(resp)
        data = apt.assert_is_list(admin_session.admin_user_get())
        item = next((u for u in data if u["id"] == another_user.user_id), None)
        assert item is not None and item["email"] == new_email, "修改后的邮箱未生效"

    @allure.title("管理员修改用户不传id提示用户ID不能为空")
    def test_admin_update_missing_id(self, admin_session):
        resp = admin_session.admin_user_post("update", username="x",
                                             email=unique_email(), phone=unique_phone())
        apt.assert_fail(resp, "用户ID不能为空")

    @allure.title("管理员修改用户id传非数字应返回业务失败（关联BUG-002修复后行为）")
    @pytest.mark.xfail(reason="BUG-002：当前版本返回HTTP 500；开发修复后自动转为xpass")
    def test_admin_update_non_numeric_id(self, admin_session):
        resp = admin_session.admin_user_post("update", id="abc", username="x")
        apt.assert_fail(resp)

    @allure.title("管理员将用户名改为已存在用户名时修改失败")
    def test_admin_update_duplicate_username(self, admin_session, another_user):
        resp = admin_session.admin_user_post("update", id=another_user.user_id,
                                             username="testuser1",
                                             email=unique_email(), phone=unique_phone())
        apt.assert_fail(resp, "修改失败")

    @allure.title("管理员不能删除自己")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_admin_delete_self(self, admin_session):
        resp = admin_session.admin_user_post("delete", id=admin_session.admin_id)
        apt.assert_fail(resp, "不能删除自己")

    @allure.title("管理员删除用户成功且该用户无法再登录")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_admin_delete_user(self, admin_session, another_user):
        uid, uname = another_user.user_id, another_user.username
        resp = admin_session.admin_user_post("delete", id=uid)
        apt.assert_success(resp)
        assert "删除成功" in resp.json()["data"]
        apt.assert_fail(another_user.login(uname, VALID_PASSWORD), "用户名或密码错误")

    @allure.title("未登录执行删除用户失败")
    def test_admin_delete_without_login(self, api):
        resp = api.admin_user_post("delete", id=999)
        apt.assert_fail(resp, "无权限")
