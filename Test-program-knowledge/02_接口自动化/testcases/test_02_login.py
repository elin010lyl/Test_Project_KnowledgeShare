# -*- coding: utf-8 -*-
"""用户登录 POST /login（对应手工用例 KSS-LOGIN-001~011）"""
import allure
import pytest

from common import assertion as apt
from data.test_data import VALID_PASSWORD, unique_email, unique_username


@allure.feature("用户模块")
@allure.story("用户登录")
class TestLogin:

    @allure.title("用户名+正确密码登录成功")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_login_by_username(self, api):
        resp = api.login("testuser1", "123456")
        apt.assert_success(resp)
        data = resp.json()["data"]
        assert data["username"] == "testuser1"
        assert data["role"] == 0

    @allure.title("使用邮箱登录成功（三种凭据之一）")
    def test_login_by_email(self, api):
        apt.assert_success(api.login("testuser1@qq.com", "123456"))

    @allure.title("使用手机号登录成功（三种凭据之一）")
    def test_login_by_phone(self, api):
        apt.assert_success(api.login("13800138001", "123456"))

    @allure.title("管理员登录成功且role=1")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_login_admin(self, api):
        resp = api.login("admin", "admin123")
        apt.assert_success(resp)
        assert resp.json()["data"]["role"] == 1

    @allure.title("密码错误登录失败")
    def test_login_wrong_password(self, api):
        apt.assert_fail(api.login("testuser1", "wrongpwd"), "用户名或密码错误")

    @allure.title("不存在的用户登录失败")
    def test_login_user_not_exist(self, api):
        apt.assert_fail(api.login("no_such_user_999", "123456"), "用户名或密码错误")

    @allure.title("用户名或密码为空登录失败")
    @pytest.mark.parametrize("username,password", [
        ("", "123456"), ("testuser1", ""), ("   ", "123456"),
    ], ids=["用户名为空", "密码为空", "用户名纯空格"])
    def test_login_empty_params(self, api, username, password):
        apt.assert_fail(api.login(username, password), "不能为空")

    @allure.title("登录后会话可访问需登录接口")
    def test_login_session_works(self, api):
        username = unique_username()
        apt.assert_success(api.register(username, VALID_PASSWORD, VALID_PASSWORD,
                                        unique_email(username), "13800000000"))
        apt.assert_success(api.login(username, VALID_PASSWORD))
        apt.assert_success(api.knowledge_get("myKnowledge"))

    @allure.title("连续密码错误后正确密码仍可登录")
    def test_login_retry_after_failure(self, api):
        apt.assert_fail(api.login("testuser2", "bad111"), "用户名或密码错误")
        apt.assert_fail(api.login("testuser2", "bad222"), "用户名或密码错误")
        apt.assert_success(api.login("testuser2", "123456"))
