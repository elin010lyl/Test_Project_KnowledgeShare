# 知识共享系统测试项目
> 被测系统：Java Servlet + MySQL 知识共享后台服务
> 项目完整实现软件测试全流程：需求分析 → 测试计划 → 用例设计 → 手工接口测试 → 缺陷管理 → pytest接口自动化回归。

## 🛠 技术栈
- 后端被测服务：Java Servlet、Tomcat 9
- 数据库：MySQL 8（Navicat作可视化）
- 手工测试：Postman、Excel
- 接口自动化：Python、pytest、requests
- 测试报告：Allure
- 版本管理：Git、GitHub

## 项目目录
- `knowledge_sharing_system`：JavaWeb被测系统原始源码，仅作为被测对象，未修改业务代码
- `Test-program-knowledge`：全套测试产出物
  - 00_需求分析：系统需求规格说明书
  - 01_测试文档：测试计划、接口清单、测试点分析、测试用例、缺陷报告、测试总结、源码修复建议
  - 02_接口自动化：pytest+requests+allure接口自动化测试框架

## 项目要点
1. 共21个JSON接口，Session‑Cookie鉴权，区分普通用户、管理员角色
2. 手工设计109条测试用例，发现10个缺陷，缺陷修复后执行回归测试，严重缺陷全部修复，回归通过率98.2%
3. 自动化96条用例，覆盖全部接口正向、反向、鉴权越权场景，用于版本迭代快速回归测试

> 仓库访问：本仓库为公开测试项目，用于展示软件测试实践产出。
