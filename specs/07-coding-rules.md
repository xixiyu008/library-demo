# 07 编码规则

## 1. Java 后端规则

### 1.1 命名规则

- 类名使用 PascalCase
- 方法名使用 camelCase
- 字段名使用 camelCase
- 常量使用 UPPER_CASE
- 表名使用 snake_case
- 字段名使用 snake_case

### 1.2 分层规则

- Controller 只处理 HTTP 请求和响应。
- Service 处理业务逻辑。
- Mapper 只处理数据库访问。
- Entity 对应数据库表。
- DTO 用于接收请求。
- VO 用于返回响应。

### 1.3 返回规则

所有 Controller 返回 `Result<T>`。

禁止直接返回：

- Entity
- Map
- String
- Object

### 1.4 异常规则

业务异常使用 `BusinessException`。

系统异常由 `GlobalExceptionHandler` 统一处理。

### 1.5 事务规则

以下方法必须加事务：

- 借书
- 还书
- 删除图书
- 删除读者

借书和还书必须保证图书数量变化与借阅记录变化在同一事务中完成。

## 2. 前端规则

### 2.1 API 规则

所有 HTTP 请求必须封装在 `src/api` 目录中。

禁止在 Vue 页面中直接写 Axios 原始调用。

### 2.2 状态管理规则

登录用户信息和 Token 存放在 Pinia 的 auth store 中。

### 2.3 路由规则

未登录用户访问业务页面时跳转到登录页。

### 2.4 页面规则

页面至少包含：

- 登录页
- 首页
- 图书管理页
- 读者管理页
- 借阅记录页

## 3. 注释规则

关键业务方法需要注释说明业务规则。

普通 getter/setter 不需要注释。

## 4. 禁止事项

1. 禁止 controller 直接操作 mapper。
2. 禁止把业务判断写在 mapper。
3. 禁止返回 password。
4. 禁止把所有代码写在一个类里。
5. 禁止前端页面直接拼接后端 URL。
6. 禁止硬编码 JWT 密钥。
