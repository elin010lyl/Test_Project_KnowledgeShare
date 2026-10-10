# -*- coding: utf-8 -*-
"""接口驱动层：统一封装 requests.Session。

所有会话（匿名/普通用户/管理员）统一使用 ApiClient，自动完成：
1. 基址拼接（用例中只写相对路径，如 /login）
2. 超时控制
3. 请求/响应日志（控制台 + 文件）
4. Allure 请求响应明细附件（失败时可直接在报告里看到报文）
"""
import allure
import requests

from common.logger import logger
from config.settings import BASE_URL, TIMEOUT


class ApiClient(requests.Session):

    def __init__(self, base_url: str = BASE_URL):
        super().__init__()
        self.base_url = base_url.rstrip("/")

    def request(self, method, url, **kwargs):
        if not url.startswith("http"):
            url = f"{self.base_url}/{url.lstrip('/')}"
        kwargs.setdefault("timeout", TIMEOUT)

        logger.info(">>> %-4s %s | params=%s | data=%s",
                    method.upper(), url, kwargs.get("params"), kwargs.get("data"))
        resp = super().request(method, url, **kwargs)
        logger.info("<<< HTTP %s | %s", resp.status_code, resp.text[:300])

        allure.attach(
            f"{method.upper()} {url}\n"
            f"params: {kwargs.get('params')}\n"
            f"data  : {kwargs.get('data')}\n"
            f"--------------- HTTP {resp.status_code} ---------------\n"
            f"{resp.text[:2000]}",
            name="请求响应明细",
            attachment_type=allure.attachment_type.TEXT,
        )
        return resp

    # ==================== 业务公共方法（按被测接口封装） ====================
    def register(self, username, password, confirmPassword, email, phone):
        """POST /register 用户注册"""
        return self.post("/register", data={
            "username": username, "password": password,
            "confirmPassword": confirmPassword, "email": email, "phone": phone,
        })

    def login(self, username, password):
        """POST /login 登录（支持用户名/邮箱/手机号）"""
        return self.post("/login", data={"username": username, "password": password})

    def logout(self):
        """GET /logout 退出登录"""
        return self.get("/logout")

    def knowledge_get(self, action=None, **params):
        """GET /knowledge?action=...（list/detail/myKnowledge/pending/myFavorites）"""
        if action:
            params["action"] = action
        return self.get("/knowledge", params=params)

    def knowledge_post(self, action=None, data=None, **params):
        """POST /knowledge?action=...（publish/favorite/delete/adminDelete/audit）"""
        if action:
            params["action"] = action
        return self.post("/knowledge", params=params, data=data)

    def user_post(self, action=None, **params):
        """POST /user?action=...（updateInfo/updatePassword）"""
        if action:
            params["action"] = action
        return self.post("/user", params=params)

    def admin_user_get(self):
        """GET /admin/user 管理员查看用户列表"""
        return self.get("/admin/user")

    def admin_user_post(self, action=None, **params):
        """POST /admin/user?action=...（update/delete）"""
        if action:
            params["action"] = action
        return self.post("/admin/user", params=params)
