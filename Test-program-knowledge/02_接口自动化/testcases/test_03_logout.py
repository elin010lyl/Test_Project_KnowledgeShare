# -*- coding: utf-8 -*-
"""退出登录 GET /logout（对应手工用例 KSS-OUT-001~003）"""
import allure

from common import assertion as apt
from data.test_data import VALID_PASSWORD


@allure.feature("用户模块")
@allure.story("退出登录")
class TestLogout:

    @allure.title("登录后正常退出返回成功")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_logout_success(self, user_session):
        apt.assert_success(user_session.logout())

    @allure.title("退出后访问需登录接口提示请先登录（会话销毁验证）")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_session_invalid_after_logout(self, user_session):
        user_session.logout()
        apt.assert_fail(user_session.knowledge_get("myKnowledge"), "请先登录")

    @allure.title("未登录直接登出（幂等，返回success）")
    def test_logout_without_login(self, api):
        apt.assert_success(api.logout())

    @allure.title("重复登出不报错（幂等）")
    def test_logout_twice(self, user_session):
        apt.assert_success(user_session.logout())
        apt.assert_success(user_session.logout())

    @allure.title("登出后重新登录，新会话可用")
    def test_relogin_after_logout(self, user_session):
        user_session.logout()
        apt.assert_success(user_session.login(user_session.username, VALID_PASSWORD))
        apt.assert_success(user_session.knowledge_get("myKnowledge"))
