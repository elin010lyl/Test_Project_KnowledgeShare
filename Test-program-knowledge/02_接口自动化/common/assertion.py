# -*- coding: utf-8 -*-
"""通用断言封装。

被测系统统一响应结构：{"flag": "success|fail", "data": 字符串|对象|数组}
所有断言收口到本模块，响应结构变化时只改这里。
"""


def assert_http_200(resp):
    assert resp.status_code == 200, f"HTTP状态码异常: {resp.status_code}，body={resp.text[:300]}"


def assert_flag(resp, expected: str):
    """断言业务 flag 字段"""
    assert_http_200(resp)
    body = resp.json()
    assert body.get("flag") == expected, f"期望flag={expected}，实际响应: {body}"


def assert_success(resp):
    """断言接口返回 success"""
    assert_flag(resp, "success")


def assert_fail(resp, contains: str = None):
    """断言接口返回 fail，可选校验 data 中的关键提示文案"""
    assert_flag(resp, "fail")
    if contains is not None:
        data = str(resp.json().get("data", ""))
        assert contains in data, f"期望提示包含[{contains}]，实际data={data}"


def assert_is_list(resp):
    """断言 data 为列表并返回，便于后续业务校验"""
    assert_success(resp)
    data = resp.json().get("data")
    assert isinstance(data, list), f"data应为列表，实际: {type(data)}"
    return data
