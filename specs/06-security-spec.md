# 06 安全规格说明

## 1. 认证方式

系统采用 JWT 认证。

请求头格式：

```http
Authorization: Bearer token
```

## 2. 密码安全

1. 密码必须使用 BCrypt 加密。
2. 禁止明文存储密码。
3. 登录失败时，不提示具体是用户名错误还是密码错误。

## 3. 权限控制

基于角色进行权限控制。

角色：

- ADMIN
- LIBRARIAN
- STUDENT

权限规则见 `01-business-spec.md` 和 `05-api-spec.md`。

## 4. Token 规则

1. Token 默认有效期为 7 天。
2. Token 中至少包含：
   - userId
   - username
   - role
3. 后端每次请求需要解析 Token。
4. Token 无效、过期、缺失时返回 401。

## 5. 接口保护规则

不需要登录的接口：

- POST /api/auth/login

其他接口默认需要登录。

## 6. 敏感字段处理

返回给前端的数据中禁止包含：

- password
- token secret
- 内部异常堆栈

## 7. CORS

开发环境允许前端访问后端。

生产环境应限制具体域名。

## 8. 日志安全

禁止在日志中输出：

- 明文密码
- 完整 JWT
- 数据库连接密码
