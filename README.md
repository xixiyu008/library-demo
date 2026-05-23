# 大学图书管理系统（简化版）Spec-Driven AI 开发包

本目录是一套可直接交给 Codex / Cursor Agent / Claude Code 的规格驱动开发文档。

## 已实现内容

- Spring Boot 3 后端：认证、用户管理、图书管理、读者管理、借书还书、借阅记录查询。
- Redis 安全能力：登录态缓存、Token 黑名单、验证码缓存接口。
- Vue 3 前端：登录、首页、用户管理、图书管理、读者管理、借阅记录、我的借阅。
- MySQL 初始化脚本与 Docker Compose 本地依赖。

## 本地运行

启动 MySQL 和 Redis：

```bash
docker compose up -d
```

启动后端：

```bash
cd backend
mvn spring-boot:run
```

启动前端：

```bash
cd frontend
npm install
npm run dev
```

默认账号：

- `admin / 123456`
- `librarian / 123456`

验证码默认关闭。需要开启时，在 `backend/src/main/resources/application.yml` 中设置：

```yaml
library:
  captcha:
    enabled: true
```

推荐使用方式：

1. 先把整个 `specs` 目录放到项目根目录。
2. 让 Codex 先阅读所有规格文档，不要立刻写代码。
3. 先生成项目骨架。
4. 再按 `08-development-roadmap.md` 分阶段实现。
5. 每完成一个阶段，按照 `09-review-checklist.md` 做审查。
6. 最后按照 `10-acceptance-test.md` 做验收。

推荐首次给 Codex 的提示词：

```text
请先阅读 specs 目录下的所有 md 文档。

注意：
1. 暂时不要写代码。
2. 请先总结系统目标、技术栈、模块边界、数据库设计、API规范、安全规则。
3. 检查这些规格之间是否存在冲突。
4. 如果没有重大问题，请输出你准备采用的项目结构和开发计划。
```
