# 03 领域模型说明

## 1. User 用户

User 表示系统登录用户。

字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| username | String | 登录用户名 |
| password | String | 加密密码 |
| role | UserRole | 用户角色 |
| enabled | Boolean | 是否启用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

说明：

- User 用于登录认证。
- STUDENT 角色用户可以关联一个 Reader。
- ADMIN 和 LIBRARIAN 不一定关联 Reader。

## 2. Reader 读者

Reader 表示真实读者身份。

字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| studentNo | String | 学号 |
| name | String | 姓名 |
| college | String | 学院 |
| phone | String | 手机号 |
| status | ReaderStatus | 状态 |
| userId | Long | 关联登录用户ID |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

说明：

- 一个 Reader 最多关联一个 User。
- 学号唯一。

## 3. Book 图书

Book 表示图书信息。

字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| isbn | String | ISBN |
| title | String | 书名 |
| author | String | 作者 |
| publisher | String | 出版社 |
| category | String | 分类 |
| totalCount | Integer | 总数量 |
| availableCount | Integer | 可借数量 |
| status | BookStatus | 状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

说明：

- totalCount 表示馆藏总数量。
- availableCount 表示当前可借数量。

## 4. BorrowRecord 借阅记录

BorrowRecord 表示一次借书与还书过程。

字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| readerId | Long | 读者ID |
| bookId | Long | 图书ID |
| borrowTime | LocalDateTime | 借出时间 |
| dueTime | LocalDateTime | 应还时间 |
| returnTime | LocalDateTime | 实际归还时间 |
| status | BorrowStatus | 借阅状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

说明：

- BORROWED 表示借出未还。
- RETURNED 表示已归还。
- OVERDUE 可以通过定时任务更新，也可以查询时动态判断。

## 5. 核心领域关系

```text
User 1 --- 0..1 Reader
Reader 1 --- n BorrowRecord
Book 1 --- n BorrowRecord
```

## 6. 枚举定义

### UserRole

- ADMIN
- LIBRARIAN
- STUDENT

### BookStatus

- AVAILABLE
- DISABLED

### ReaderStatus

- ACTIVE
- DISABLED

### BorrowStatus

- BORROWED
- RETURNED
- OVERDUE
