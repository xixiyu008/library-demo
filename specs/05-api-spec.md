# 05 API 接口规格

## 1. 通用响应格式

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

错误示例：

```json
{
  "code": 40001,
  "message": "用户名或密码错误",
  "data": null
}
```

## 2. 通用错误码

| code | 含义 |
|---|---|
| 0 | 成功 |
| 40000 | 请求参数错误 |
| 40001 | 认证失败 |
| 40300 | 无权限 |
| 40400 | 资源不存在 |
| 40900 | 业务冲突 |
| 50000 | 系统内部错误 |

## 3. 认证接口

### 3.1 登录

```http
POST /api/auth/login
```

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "role": "ADMIN",
    "username": "admin"
  }
}
```

### 3.2 获取当前用户

```http
GET /api/auth/me
```

请求头：

```http
Authorization: Bearer jwt-token
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

## 4. 图书接口

### 4.1 分页查询图书

```http
GET /api/books?page=1&pageSize=10&keyword=Java
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "total": 1,
    "records": [
      {
        "id": 1,
        "isbn": "9787115546082",
        "title": "Java核心技术",
        "author": "Cay S. Horstmann",
        "publisher": "机械工业出版社",
        "category": "编程语言",
        "totalCount": 3,
        "availableCount": 3,
        "status": "AVAILABLE"
      }
    ]
  }
}
```

### 4.2 新增图书

```http
POST /api/books
```

权限：

- ADMIN
- LIBRARIAN

请求：

```json
{
  "isbn": "9787115546082",
  "title": "Java核心技术",
  "author": "Cay S. Horstmann",
  "publisher": "机械工业出版社",
  "category": "编程语言",
  "totalCount": 3,
  "availableCount": 3
}
```

### 4.3 修改图书

```http
PUT /api/books/{id}
```

权限：

- ADMIN
- LIBRARIAN

### 4.4 删除图书

```http
DELETE /api/books/{id}
```

权限：

- ADMIN
- LIBRARIAN

## 5. 读者接口

### 5.1 分页查询读者

```http
GET /api/readers?page=1&pageSize=10&keyword=张三
```

权限：

- ADMIN
- LIBRARIAN

### 5.2 新增读者

```http
POST /api/readers
```

权限：

- ADMIN
- LIBRARIAN

请求：

```json
{
  "studentNo": "20240001",
  "name": "张三",
  "college": "计算机学院",
  "phone": "13800000001"
}
```

### 5.3 修改读者

```http
PUT /api/readers/{id}
```

权限：

- ADMIN
- LIBRARIAN

### 5.4 删除读者

```http
DELETE /api/readers/{id}
```

权限：

- ADMIN
- LIBRARIAN

## 6. 借阅接口

### 6.1 借书

```http
POST /api/borrows
```

权限：

- ADMIN
- LIBRARIAN

请求：

```json
{
  "readerId": 1,
  "bookId": 1
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "borrowRecordId": 1001
  }
}
```

### 6.2 还书

```http
PUT /api/borrows/{id}/return
```

权限：

- ADMIN
- LIBRARIAN

### 6.3 查询全部借阅记录

```http
GET /api/borrows?page=1&pageSize=10&readerId=1&bookId=1&status=BORROWED
```

权限：

- ADMIN
- LIBRARIAN

### 6.4 学生查询自己的借阅记录

```http
GET /api/borrows/my
```

权限：

- STUDENT
