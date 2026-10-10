# -*- coding: utf-8 -*-
"""个人信息维护 POST /user（对应手工用例 KSS-INFO/PWD 系列）"""
import allure
import pytest

from common import assertion as apt
from data.test_data import NEW_PASSWORD, VALID_PASSWORD, unique_email, unique_phone


@allure.feature("用户模块")
@allure.story("个人信息维护")
class TestUserInfo:

    @allure.title("修改邮箱手机号成功")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_update_info_success(self, user_session):
        resp = user_session.user_post("updateInfo", email=unique_email(), phone=unique_phone())
        apt.assert_success(resp)
        assert "修改成功" in resp.json()["data"]

    @allure.title("修改邮箱后可用新邮箱登录（业务闭环）")
    def test_login_with_new_email(self, user_session):
        new_email = unique_email()
        apt.assert_success(user_session.user_post("updateInfo", email=new_email, phone=unique_phone()))
        apt.assert_success(user_session.login(new_email, VALID_PASSWORD))

    @allure.title("未登录修改个人信息提示请先登录")
    def test_update_info_without_login(self, api):
        resp = api.user_post("updateInfo", email=unique_email(), phone=unique_phone())
        apt.assert_fail(resp, "请先登录")

    @allure.title("邮箱与他人重复时修改失败")
    def test_update_info_duplicate_email(self, user_session):
        resp = user_session.user_post("updateInfo", email="testuser2@qq.com", phone=unique_phone())
        apt.assert_fail(resp, "修改失败")

    @allure.title("非法邮箱格式应被校验拦截（关联BUG-001修复后行为）")
    @pytest.mark.xfail(reason="BUG-001：当前版本未校验邮箱格式，非法值也修改成功；开发修复后自动转为xpass")
    def test_update_info_invalid_email(self, user_session):
        resp = user_session.user_post("updateInfo", email="bad-email", phone=unique_phone())
        apt.assert_fail(resp)

    @allure.title("非法手机号格式应被校验拦截（关联BUG-001修复后行为）")
    @pytest.mark.xfail(reason="BUG-001：当前版本未校验手机号格式；开发修复后自动转为xpass")
    def test_update_info_invalid_phone(self, user_session):
        resp = user_session.user_post("updateInfo", email=unique_email(), phone="123")
        apt.assert_fail(resp)

    @allure.title("修改密码成功且新密码可登录")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_update_password_success(self, user_session):
        apt.assert_success(user_session.user_post(
            "updatePassword", oldPassword=VALID_PASSWORD, newPassword=NEW_PASSWORD))
        apt.assert_success(user_session.login(user_session.username, NEW_PASSWORD))

    @allure.title("修改密码后旧密码登录失败")
    def test_old_password_invalid_after_change(self, user_session):
        apt.assert_success(user_session.user_post(
            "updatePassword", oldPassword=VALID_PASSWORD, newPassword=NEW_PASSWORD))
        apt.assert_fail(user_session.login(user_session.username, VALID_PASSWORD),
                        "用户名或密码错误")

    @allure.title("原密码错误时修改失败")
    def test_update_password_wrong_old(self, user_session):
        resp = user_session.user_post("updatePassword", oldPassword="wrong777",
                                      newPassword=NEW_PASSWORD)
        apt.assert_fail(resp, "原密码错误")

    @allure.title("新密码不足6位修改失败（边界值）")
    @pytest.mark.parametrize("newpwd", ["12345", ""], ids=["5位", "空"])
    def test_update_password_too_short(self, user_session, newpwd):
        resp = user_session.user_post("updatePassword", oldPassword=VALID_PASSWORD,
                                      newPassword=newpwd)
        apt.assert_fail(resp, "6位")

    @allure.title("未登录修改密码提示请先登录")
    def test_update_password_without_login(self, api):
        resp = api.user_post("updatePassword", oldPassword=VALID_PASSWORD,
                             newPassword=NEW_PASSWORD)
        apt.assert_fail(resp, "请先登录")
