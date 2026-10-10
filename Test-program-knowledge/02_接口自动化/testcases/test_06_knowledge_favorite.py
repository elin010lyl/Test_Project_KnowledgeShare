# -*- coding: utf-8 -*-
"""知识收藏 POST /knowledge?action=favorite（对应手工用例 KSS-FAV/UNFAV/MYFAV 系列）"""
import allure
import pytest

from common import assertion as apt


@allure.feature("知识模块")
@allure.story("知识收藏")
class TestFavorite:

    @allure.title("收藏已审核通过的知识成功")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_favorite_add_success(self, user_session, approved_knowledge):
        resp = user_session.knowledge_post("favorite", favAction="add",
                                           knowledgeId=approved_knowledge["id"])
        apt.assert_success(resp)
        assert "收藏成功" in resp.json()["data"]

    @allure.title("重复收藏提示已经收藏过了")
    def test_favorite_duplicate(self, user_session, approved_knowledge):
        kid = approved_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("favorite", favAction="add", knowledgeId=kid))
        apt.assert_fail(user_session.knowledge_post("favorite", favAction="add", knowledgeId=kid),
                        "已经收藏过了")

    @allure.title("收藏后我的收藏列表可见")
    def test_favorite_in_my_favorites(self, user_session, approved_knowledge):
        kid = approved_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("favorite", favAction="add", knowledgeId=kid))
        data = apt.assert_is_list(user_session.knowledge_get("myFavorites"))
        assert any(k["id"] == kid for k in data), "我的收藏中未找到刚收藏的知识"

    @allure.title("取消收藏成功")
    def test_favorite_remove_success(self, user_session, approved_knowledge):
        kid = approved_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("favorite", favAction="add", knowledgeId=kid))
        resp = user_session.knowledge_post("favorite", favAction="remove", knowledgeId=kid)
        apt.assert_success(resp)
        assert "取消收藏成功" in resp.json()["data"]

    @allure.title("取消收藏后我的收藏列表不再展示")
    def test_removed_not_in_my_favorites(self, user_session, approved_knowledge):
        kid = approved_knowledge["id"]
        apt.assert_success(user_session.knowledge_post("favorite", favAction="add", knowledgeId=kid))
        apt.assert_success(user_session.knowledge_post("favorite", favAction="remove", knowledgeId=kid))
        data = apt.assert_is_list(user_session.knowledge_get("myFavorites"))
        assert all(k["id"] != kid for k in data)

    @allure.title("未收藏的知识也可直接取消收藏（幂等）")
    def test_remove_without_add(self, user_session, approved_knowledge):
        resp = user_session.knowledge_post("favorite", favAction="remove",
                                           knowledgeId=approved_knowledge["id"])
        apt.assert_success(resp)

    @allure.title("我的收藏仅展示已审核通过的知识")
    def test_my_favorites_only_approved(self, user_session, published_knowledge):
        # 待审核知识允许被收藏（当前行为），但我的收藏中不应展示
        apt.assert_success(user_session.knowledge_post(
            "favorite", favAction="add", knowledgeId=published_knowledge["id"]))
        data = apt.assert_is_list(user_session.knowledge_get("myFavorites"))
        assert all(k["id"] != published_knowledge["id"] for k in data)

    @allure.title("未登录收藏提示请先登录")
    def test_favorite_without_login(self, api, approved_knowledge):
        resp = api.knowledge_post("favorite", favAction="add",
                                  knowledgeId=approved_knowledge["id"])
        apt.assert_fail(resp, "请先登录")

    @allure.title("不传knowledgeId提示知识ID不能为空")
    def test_favorite_missing_id(self, user_session):
        resp = user_session.knowledge_post("favorite", favAction="add")
        apt.assert_fail(resp, "知识ID不能为空")

    @allure.title("收藏不存在的知识应被拦截（关联BUG-003修复后行为）")
    @pytest.mark.xfail(reason="BUG-003：当前版本不校验知识存在性，可收藏不存在的ID；开发修复后自动转为xpass")
    def test_favorite_nonexistent_knowledge(self, user_session):
        resp = user_session.knowledge_post("favorite", favAction="add", knowledgeId=99999999)
        apt.assert_fail(resp)

    @allure.title("favAction非法值提示操作类型错误")
    def test_favorite_invalid_fav_action(self, user_session, approved_knowledge):
        resp = user_session.knowledge_post("favorite", favAction="copy",
                                           knowledgeId=approved_knowledge["id"])
        apt.assert_fail(resp, "操作类型错误")
