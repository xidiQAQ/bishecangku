# 数据库初始化指南

## 方式一：使用MySQL Workbench（推荐）

1. 打开MySQL Workbench
2. 连接到你的MySQL服务器（root/123456）
3. 按顺序执行以下SQL文件：
   - `01-create-database.sql` - 创建数据库
   - `02-create-tables.sql` - 创建基础表
   - `03-create-tables-continued.sql` - 创建更多表
   - `04-create-tables-final.sql` - 创建剩余表
   - `05-init-data.sql` - 插入初始数据

## 方式二：使用命令行

在 `mental-health-platform/database` 目录下执行：

```bash
mysql -u root -p123456 < 01-create-database.sql
mysql -u root -p123456 < 02-create-tables.sql
mysql -u root -p123456 < 03-create-tables-continued.sql
mysql -u root -p123456 < 04-create-tables-final.sql
mysql -u root -p123456 < 05-init-data.sql
```

## 验证数据库

执行完成后，在MySQL中运行：

```sql
USE mental_health;
SHOW TABLES;
```

应该看到16张表：
1. sys_user - 用户表
2. counselor_info - 咨询师信息表
3. article_category - 文章分类表
4. article - 文章表
5. article_collect - 文章收藏表
6. psychological_test - 心理测试表
7. test_question - 测试题目表
8. test_result - 测试结果表
9. counselor_schedule - 咨询师时间表
10. appointment - 预约记录表
11. counseling_note - 咨询笔记表
12. moment - 树洞内容表
13. moment_comment - 树洞评论表
14. sensitive_word - 敏感词表
15. sys_config - 系统配置表
16. sys_log - 操作日志表

## 默认账号

- 管理员账号：admin
- 管理员密码：admin123

## 注意事项

- 确保MySQL服务正在运行
- 确保使用的是MySQL 8.0版本
- 数据库字符集为utf8mb4
