# 04 数据库设计

## 1. 数据库名称

```sql
CREATE DATABASE library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. 用户表 sys_user

```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);
```

## 3. 读者表 reader

```sql
CREATE TABLE reader (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    college VARCHAR(100),
    phone VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_id BIGINT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);
```

## 4. 图书表 book

```sql
CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(30),
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100),
    publisher VARCHAR(100),
    category VARCHAR(50),
    total_count INT NOT NULL DEFAULT 0,
    available_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);
```

## 5. 借阅记录表 borrow_record

```sql
CREATE TABLE borrow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reader_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_time DATETIME NOT NULL,
    due_time DATETIME NOT NULL,
    return_time DATETIME,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);
```

## 6. 推荐索引

```sql
CREATE INDEX idx_book_title ON book(title);
CREATE INDEX idx_book_isbn ON book(isbn);
CREATE INDEX idx_reader_student_no ON reader(student_no);
CREATE INDEX idx_borrow_reader_id ON borrow_record(reader_id);
CREATE INDEX idx_borrow_book_id ON borrow_record(book_id);
CREATE INDEX idx_borrow_status ON borrow_record(status);
```

## 7. 初始化数据

```sql
INSERT INTO sys_user(username, password, role, enabled, create_time, update_time)
VALUES
('admin', '$2a$10$replace_with_bcrypt_password', 'ADMIN', 1, NOW(), NOW()),
('librarian', '$2a$10$replace_with_bcrypt_password', 'LIBRARIAN', 1, NOW(), NOW());

INSERT INTO book(isbn, title, author, publisher, category, total_count, available_count, status, create_time, update_time)
VALUES
('9787111213826', '软件工程导论', '张海藩', '清华大学出版社', '软件工程', 5, 5, 'AVAILABLE', NOW(), NOW()),
('9787115546082', 'Java核心技术', 'Cay S. Horstmann', '机械工业出版社', '编程语言', 3, 3, 'AVAILABLE', NOW(), NOW());

INSERT INTO reader(student_no, name, college, phone, status, create_time, update_time)
VALUES
('20240001', '张三', '计算机学院', '13800000001', 'ACTIVE', NOW(), NOW()),
('20240002', '李四', '软件学院', '13800000002', 'ACTIVE', NOW(), NOW());
```

注意：实际项目中，密码必须由 BCrypt 工具生成，不能直接明文入库。
