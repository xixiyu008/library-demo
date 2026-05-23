USE library_db;
SET NAMES utf8mb4;

INSERT INTO sys_user(username, password, role, enabled, create_time, update_time)
VALUES
('admin', '$2a$10$3vVuXR5vZxSURIb9.UJ4CeH805P8t12mnG8oEZQDpTUUmTnbelZtq', 'ADMIN', 1, NOW(), NOW()),
('librarian', '$2a$10$3vVuXR5vZxSURIb9.UJ4CeH805P8t12mnG8oEZQDpTUUmTnbelZtq', 'LIBRARIAN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
password = VALUES(password),
role = VALUES(role),
enabled = VALUES(enabled),
update_time = VALUES(update_time);

INSERT INTO book(isbn, title, author, publisher, category, total_count, available_count, status, create_time, update_time)
VALUES
('9787111213826', '软件工程导论', '张海藩', '清华大学出版社', '软件工程', 5, 5, 'AVAILABLE', NOW(), NOW()),
('9787115546082', 'Java核心技术', 'Cay S. Horstmann', '机械工业出版社', '编程语言', 3, 3, 'AVAILABLE', NOW(), NOW());

INSERT INTO reader(student_no, name, college, phone, status, create_time, update_time)
VALUES
('20240001', '张三', '计算机学院', '13800000001', 'ACTIVE', NOW(), NOW()),
('20240002', '李四', '软件学院', '13800000002', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE
name = VALUES(name),
college = VALUES(college),
phone = VALUES(phone),
status = VALUES(status),
update_time = VALUES(update_time);
