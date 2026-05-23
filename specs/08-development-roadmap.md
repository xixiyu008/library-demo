# 08 开发路线图

## Phase 0：规格审查

目标：

- 让 Codex 阅读所有规格文档
- 检查是否存在冲突
- 输出项目结构建议
- 不写代码

Codex 提示词：

```text
请阅读 specs 目录下所有 md 文档。

暂时不要写代码。

请完成：
1. 总结系统目标。
2. 总结技术栈。
3. 总结模块划分。
4. 检查规格冲突。
5. 输出建议的后端和前端项目结构。
```

## Phase 1：后端项目骨架

目标：

- 创建 Spring Boot 后端项目
- 创建基础包结构
- 创建 Result、ErrorCode、BusinessException、GlobalExceptionHandler
- 配置 MyBatis plus
- 配置数据库连接

Codex 提示词：

```text
请根据 specs 文档创建后端 Spring Boot 3 项目骨架。

要求：
1. 只创建基础结构和通用类。
2. 暂时不要实现完整业务。
3. 严格遵守 architecture-spec 和 coding-rules。
4. 创建 Result、ErrorCode、BusinessException、GlobalExceptionHandler。
5. 创建 controller/service/mapper/entity/dto/vo/enums/security/config 包。
```

## Phase 2：数据库与实体

目标：

- 创建 SQL 脚本
- 创建 entity
- 创建 enum
- 创建 mapper 基础接口

Codex 提示词：

```text
请实现数据库与实体层。

要求：
1. 根据 04-database-design.md 创建 schema.sql 和 data.sql。
2. 创建 User、Book、Reader、BorrowRecord 实体类。
3. 创建 UserRole、BookStatus、ReaderStatus、BorrowStatus 枚举。
4. 创建 Mapper 接口。
5. 暂时不要实现业务逻辑。
```

## Phase 3：登录与 JWT

目标：

- 实现登录
- 实现 JWT 生成与解析
- 实现认证过滤器
- 实现 /api/auth/login 和 /api/auth/me

Codex 提示词：

```text
请实现认证模块。

要求：
1. 实现 AuthController、AuthService、AuthServiceImpl。
2. 使用 BCrypt 校验密码。
3. 使用 JWT 返回 token。
4. 实现 /api/auth/login。
5. 实现 /api/auth/me。
6. 不允许返回 password。
7. 登录失败统一返回“用户名或密码错误”。
```

## Phase 4：图书管理

目标：

- 实现图书 CRUD
- 实现分页查询
- 实现删除前检查

Codex 提示词：

```text
请实现图书管理模块。

要求：
1. 实现 BookController、BookService、BookServiceImpl、BookMapper。
2. 支持分页查询。
3. 支持新增、修改、删除。
4. 删除图书前检查是否存在未归还记录。
5. 不允许 availableCount 大于 totalCount。
6. 严格返回 BookVO，不直接返回 Book entity。
```

## Phase 5：读者管理

目标：

- 实现读者 CRUD
- 实现学号唯一校验
- 实现删除前检查

Codex 提示词：

```text
请实现读者管理模块。

要求：
1. 实现 ReaderController、ReaderService、ReaderServiceImpl、ReaderMapper。
2. 支持分页查询。
3. 支持新增、修改、删除。
4. 学号必须唯一。
5. 删除读者前检查是否存在未归还记录。
6. 返回 ReaderVO。
```

## Phase 6：借书还书

目标：

- 实现借书
- 实现还书
- 实现借阅记录查询

Codex 提示词：

```text
请实现借阅模块。

要求：
1. 实现 BorrowController、BorrowService、BorrowServiceImpl、BorrowRecordMapper。
2. 借书时检查读者状态、图书状态、库存、最大借阅数量、逾期记录、重复借阅。
3. 借书成功后创建 BorrowRecord，并减少 Book.availableCount。
4. 还书成功后更新 returnTime 和 status，并增加 Book.availableCount。
5. 借书和还书必须加事务。
6. 返回 BorrowRecordVO。
```

## Phase 7：前端项目骨架

目标：

- 创建 Vue3 + Vite + TypeScript 项目
- 配置 Element Plus
- 配置 Router
- 配置 Pinia
- 配置 Axios

Codex 提示词：

```text
请创建前端项目骨架。

要求：
1. 使用 Vue3 + TypeScript + Vite。
2. 使用 Element Plus。
3. 使用 Pinia 管理登录状态。
4. 使用 Axios 封装 request.ts。
5. 创建 LoginView、DashboardView、BookListView、ReaderListView、BorrowRecordView。
```

## Phase 8：前端页面实现

目标：

- 登录页
- 图书管理页
- 读者管理页
- 借阅记录页
- 借书还书操作

Codex 提示词：

```text
请实现前端页面。

要求：
1. 登录成功后保存 token。
2. Axios 请求自动携带 Authorization header。
3. 图书页面支持查询、新增、修改、删除。
4. 读者页面支持查询、新增、修改、删除。
5. 借阅页面支持借书、还书、查询记录。
6. 根据用户角色控制菜单显示。
```

## Phase 9：集成测试与修复

目标：

- 前后端联调
- 修复接口字段不一致
- 修复权限问题
- 修复异常处理问题

Codex 提示词：

```text
请根据 specs/10-acceptance-test.md 检查当前项目。

要求：
1. 找出前后端字段不一致。
2. 找出接口路径不一致。
3. 找出权限控制缺失。
4. 找出业务规则遗漏。
5. 输出问题清单并修复。
```
