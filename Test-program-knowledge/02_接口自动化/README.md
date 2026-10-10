# 知识共享后台系统 接口自动化测试工程

> 技术栈：**Python + pytest + requests + Allure**
> 被测对象：知识共享后台系统 21 个 HTTP 接口（Servlet + MySQL，Session-Cookie 鉴权）
> 用例规模：10 个测试类 / 92 个用例函数（参数化展开后 96 条），覆盖全部 21 个接口的正反向场景

---

## 一、环境准备

### 1.1 安装依赖

```bash
cd 02_接口自动化
pip install -r requirements.txt          # pytest / requests / allure-pytest
```

### 1.2 安装 Allure 命令行（生成报告用）

- 依赖 JDK 1.8+；
- Windows：`scoop install allure`，或下载 allurereport 压缩包解压后将其 `bin` 目录加入 PATH；
- 验证：`allure --version`。

### 1.3 启动被测服务

1. MySQL 8.0 启动，导入 `knowledge_share` 库（预置账号：testuser1/123456、testuser2/123456、admin/admin123）；
2. Tomcat 9.0 部署 `knowledge_sharing_system` 并启动；
3. 验证基址可访问：`http://localhost:8080/knowledge_sharing_system/knowledge?action=list`。
   被测地址若不同，改 [config/settings.py](config/settings.py) 的 `BASE_URL` 即可。

---

## 二、目录结构

```
02_接口自动化
├── pytest.ini                  # pytest配置：用例路径、allure结果目录
├── requirements.txt            # 依赖清单
├── config/
│   └── settings.py             # 环境配置：BASE_URL、超时、预置账号
├── common/                     # 公共工具层
│   ├── api_client.py           # requests.Session封装：基址拼接/日志/Allure附件/业务方法
│   ├── assertion.py            # 通用断言：assert_success/assert_fail/assert_is_list
│   ├── helpers.py              # 业务前置：注册登录/发布知识并反查ID
│   └── logger.py               # 统一日志（控制台 + logs/run.log）
├── data/
│   └── test_data.py            # 测试数据工厂：唯一用户名/邮箱/手机号生成
├── testcases/                  # 用例层（按模块拆分）
│   ├── conftest.py             # 全局fixture：api/admin_session/user_session/知识数据
│   ├── test_01_register.py     # 注册（10条）
│   ├── test_02_login.py        # 登录（11条）
│   ├── test_03_logout.py       # 登出与会话失效（5条）
│   ├── test_04_knowledge_list.py  # 知识列表（8条）
│   ├── test_05_knowledge_publish.py  # 知识发布（7条）
│   ├── test_06_knowledge_favorite.py # 收藏/取消/我的收藏（11条）
│   ├── test_07_knowledge_delete.py   # 删除知识/越权（8条）
│   ├── test_08_user_info.py    # 修改资料/修改密码（13条）
│   ├── test_09_admin_audit.py  # 管理员审核/删除知识（13条）
│   └── test_10_admin_user.py   # 管理员用户管理（10条）
├── reports/                    # Allure 结果与报告输出目录
└── logs/                       # 运行日志（自动生成）
```

## 三、框架设计说明（面试可讲）

### 3.1 三层分离

- **用例层** `testcases/`：只描述"测试什么 + 断言什么"，不出现 URL 拼接和 requests 细节；
- **业务/驱动层** `common/`：`ApiClient` 统一处理基址、超时、日志、Allure 附件；断言统一收口 `assertion.py`，响应结构变化只改一处；
- **配置/数据层** `config/ + data/`：环境切换改 settings；测试数据用**毫秒时间戳+随机数**生成唯一用户名/邮箱/手机号，保证可重复执行、无用例间污染。

### 3.2 fixture 会话管理（conftest.py）

| fixture | 作用域 | 说明 |
|---|---|---|
| `api` | session | 匿名会话，测无需登录的接口 |
| `admin_session` | session | 管理员登录态，附带 `admin_id` |
| `user_session` | function | **每条用例注册全新唯一用户并登录**，天然数据隔离 |
| `another_user` | function | 第二个普通用户，专测越权 |
| `published_knowledge` | function | 已发布（待审核）知识，自动反查知识ID |
| `approved_knowledge` | function | 已审核通过知识（发布+管理员approve） |

> 细节：发布接口成功后**不返回知识ID**，`publish_and_get_id` 通过 `myKnowledge` 列表按唯一标题反查——这也是对手工测试发现的接口设计问题的自动化适配。

### 3.3 与缺陷报告的联动（xfail 机制）

BUG-001/002/003/004/005 相关的**预期修复后行为**用例标注 `@pytest.mark.xfail(reason="BUG-xxx...")`：

- 当前系统未修复 → 用例显示 xfail（预期失败，不算挂）；
- 开发修复后 → 自动变为 xpass（预期通过），无需改代码即可感知修复生效。

### 3.4 Allure 报告增强

- 每条用例 `@allure.feature/story/title/severity` 分级分层；
- `ApiClient` 自动把**完整请求/响应报文**作为附件挂到报告，失败用例可直接看到报文定位问题。

---

## 四、运行方式

```bash
cd 02_接口自动化

# 1. 执行全部用例并生成 Allure 结果
pytest

# 2. 执行单个模块
pytest testcases/test_02_login.py

# 3. 按严重级别执行（P0冒烟）
pytest -m "smoke"

# 4. 生成并打开 Allure 可视化报告
allure serve reports/allure-results
# 或生成静态报告：
allure generate reports/allure-results -o reports/allure-report --clean
allure open reports/allure-report
```

## 五、常见问题

| 现象 | 处理 |
|---|---|
| 大量 `ConnectionError` | 被测服务未启动或 BASE_URL 不对，检查 Tomcat 与 settings.py |
| `前置注册失败` | 注册接口异常，先用 Postman 单独验证 /register |
| xfail 用例 | 属于已知遗留缺陷的预期行为校验，见用例注释关联的 BUG 编号 |
| 中文乱码 | 确认终端/IDE 使用 UTF-8 |
