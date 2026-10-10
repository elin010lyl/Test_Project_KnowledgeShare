# -*- coding: utf-8 -*-
"""用户注册 POST /register（对应手工用例 KSS-REG-001~012）"""
import allure
import pytest

from common import assertion as apt
from data.test_data import VALID_PASSWORD, unique_email, unique_phone, unique_username


@allure.feature("用户模块")
@allure.story("用户注册")
class TestRegister:

    @allure.title("合法数据注册成功")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_register_success(self, api):
        username = unique_username()
        resp = api.register(username, VALID_PASSWORD, VALID_PASSWORD,
                            unique_email(username), unique_phone())
        apt.assert_success(resp)
        assert "注册成功" in resp.json()["data"]

    @allure.title("重复用户名注册被拦截")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_register_duplicate_username(self, api):
        resp = api.register("testuser1", VALID_PASSWORD, VALID_PASSWORD,
                            unique_email(), unique_phone())
        apt.assert_fail(resp, "已被注册")

    @allure.title("重复邮箱注册被拦截")
    def test_register_duplicate_email(self, api):
        resp = api.register(unique_username(), VALID_PASSWORD, VALID_PASSWORD,
                            "testuser1@qq.com", unique_phone())
        apt.assert_fail(resp, "已被注册")

    @allure.title("重复手机号注册被拦截")
    def test_register_duplicate_phone(self, api):
        resp = api.register(unique_username(), VALID_PASSWORD, VALID_PASSWORD,
                            unique_email(), "13800138001")
        apt.assert_fail(resp, "已被注册")

    @allure.title("用户名为空或纯空格注册失败")
    @pytest.mark.parametrize("username", ["", "   "], ids=["空字符串", "纯空格"])
    def test_register_empty_username(self, api, username):
        resp = api.register(username, VALID_PASSWORD, VALID_PASSWORD,
                            unique_email(), unique_phone())
        apt.assert_fail(resp, "用户名不能为空")

    @allure.title("密码不足6位注册失败（边界值）")
    def test_register_short_password(self, api):
        resp = api.register(unique_username(), "123", "123",
                            unique_email(), unique_phone())
        apt.assert_fail(resp, "6位")

    @allure.title("两次密码不一致注册失败")
    def test_register_password_mismatch(self, api):
        resp = api.register(unique_username(), VALID_PASSWORD, "999999",
                            unique_email(), unique_phone())
        apt.assert_fail(resp, "不一致")

    @allure.title("非法邮箱格式应被校验拦截（关联BUG-001修复后行为）")
    @pytest.mark.xfail(reason="BUG-001：当前版本未校验邮箱格式，非法邮箱仍注册成功；开发修复后自动转为xpass")
    def test_register_invalid_email(self, api):
        resp = api.register(unique_username(), VALID_PASSWORD, VALID_PASSWORD,
                            "not-an-email", unique_phone())
        apt.assert_fail(resp)

    @allure.title("注册成功后新账号可直接登录（业务闭环）")
    def test_register_then_login(self, api):
        username = unique_username()
        apt.assert_success(api.register(username, VALID_PASSWORD, VALID_PASSWORD,
                                        unique_email(username), unique_phone()))
        apt.assert_success(api.login(username, VALID_PASSWORD))
