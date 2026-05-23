# 02 架构规格说明

## 1. 技术栈

### 1.1 后端

- Java 21
- Spring Boot 3
- MyBatis plus
- MySQL 8
- Redis
- JWT
- Maven

### 1.2 前端

- Vue 3
- TypeScript
- Vite
- Element Plus
- Pinia
- Axios
- Vue Router

## 2. 架构风格

采用前后端分离架构。

后端提供 RESTful API。

前端通过 Axios 调用后端 API。

## 3. 后端分层结构

后端采用经典分层架构：

```text
controller  ->  service  ->  mapper  ->  database
```

### 3.1 controller 层

职责：

- 接收 HTTP 请求
- 参数校验
- 调用 service
- 返回统一结果

禁止：

- 不允许写复杂业务逻辑
- 不允许直接调用 mapper
- 不允许直接操作数据库

### 3.2 service 层

职责：

- 实现业务规则
- 控制事务
- 调用 mapper
- 处理业务异常

### 3.3 mapper 层

职责：

- 数据库 CRUD
- SQL 映射

禁止：

- 不允许写业务判断
- 不允许处理权限逻辑

### 3.4 entity 层

职责：

- 对应数据库表结构

禁止：

- 不直接返回给前端

### 3.5 dto 层

职责：

- 接收前端请求数据

### 3.6 vo 层

职责：

- 返回前端响应数据

### 3.7 common 层

职责：

- 统一响应对象
- 错误码
- 通用工具类

### 3.8 exception 层

职责：

- 自定义业务异常
- 全局异常处理

### 3.9 security 层

职责：

- JWT 生成与解析
- 登录认证
- 权限校验

## 4. 后端推荐包结构

```text
com.example.library
  ├── LibraryApplication.java
  ├── common
  │   ├── Result.java
  │   ├── ErrorCode.java
  │   └── PageResult.java
  ├── config
  │   ├── WebConfig.java
  │   └── SecurityConfig.java
  ├── controller
  │   ├── AuthController.java
  │   ├── BookController.java
  │   ├── ReaderController.java
  │   └── BorrowController.java
  ├── dto
  │   ├── LoginRequest.java
  │   ├── BookCreateRequest.java
  │   ├── BookUpdateRequest.java
  │   ├── ReaderCreateRequest.java
  │   ├── ReaderUpdateRequest.java
  │   └── BorrowRequest.java
  ├── entity
  │   ├── User.java
  │   ├── Book.java
  │   ├── Reader.java
  │   └── BorrowRecord.java
  ├── enums
  │   ├── UserRole.java
  │   ├── BookStatus.java
  │   ├── ReaderStatus.java
  │   └── BorrowStatus.java
  ├── exception
  │   ├── BusinessException.java
  │   └── GlobalExceptionHandler.java
  ├── mapper
  │   ├── UserMapper.java
  │   ├── BookMapper.java
  │   ├── ReaderMapper.java
  │   └── BorrowRecordMapper.java
  ├── security
  │   ├── JwtTokenProvider.java
  │   ├── JwtAuthenticationFilter.java
  │   └── LoginUser.java
  ├── service
  │   ├── AuthService.java
  │   ├── BookService.java
  │   ├── ReaderService.java
  │   └── BorrowService.java
  ├── service.impl
  │   ├── AuthServiceImpl.java
  │   ├── BookServiceImpl.java
  │   ├── ReaderServiceImpl.java
  │   └── BorrowServiceImpl.java
  └── vo
      ├── LoginResponse.java
      ├── BookVO.java
      ├── ReaderVO.java
      └── BorrowRecordVO.java
```

## 5. 前端推荐目录结构

```text
src
  ├── api
  │   ├── auth.ts
  │   ├── book.ts
  │   ├── reader.ts
  │   └── borrow.ts
  ├── router
  │   └── index.ts
  ├── stores
  │   └── auth.ts
  ├── views
  │   ├── LoginView.vue
  │   ├── DashboardView.vue
  │   ├── BookListView.vue
  │   ├── ReaderListView.vue
  │   └── BorrowRecordView.vue
  ├── components
  ├── utils
  │   ├── request.ts
  │   └── auth.ts
  └── main.ts
```

## 6. 架构禁止事项

1. 禁止 controller 直接访问 mapper。
2. 禁止前端直接依赖数据库字段命名。
3. 禁止后端直接返回 entity。
4. 禁止多个模块随意互相调用 mapper。
5. 禁止把权限判断写散在各个 controller 中。
6. 禁止无统一异常处理。
7. 禁止无统一响应格式。
