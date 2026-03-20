# 大学生心理健康关怀与预约平台

## 项目简介

这是一个完整的大学生心理健康关怀与预约平台，旨在为大学生提供便捷的心理健康服务和知识获取渠道，帮助心理咨询师高效管理预约和咨询记录，为管理员提供统一的平台管理和数据分析工具。

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3
- MySQL 8.0
- Redis 3.0.504
- JWT + BCrypt
- Knife4j (API文档)
- Hutool (工具类)

### 前端
- Vue 3.3.4
- Element Plus 2.x
- Vite 4.x
- Pinia (状态管理)
- Axios (HTTP客户端)

## 核心功能

### 1. 用户认证系统
- 用户注册（学生/咨询师/管理员）
- 用户登录
- JWT Token认证
- BCrypt密码加密

### 2. 文章管理模块
- 文章分类管理
- 文章列表（分页、搜索、筛选）
- 文章详情查看
- 文章收藏功能

### 3. 预约管理模块
- 咨询师列表展示
- 咨询师时间表管理
- 在线预约功能
- 预约状态管理
- 学生评价功能

### 4. 心理测试模块
- 标准化心理测评（SAS/SDS）
- 自动评分算法
- 测试结果分析
- 测试历史记录

### 5. 树洞模块
- 匿名发布内容
- 敏感词过滤（DFA算法）
- 内容审核机制
- 评论和点赞功能

### 6. 咨询笔记模块
- 咨询笔记记录
- AES-256加密存储
- 严格权限控制
- 笔记管理功能

### 7. 管理后台模块
- 数据统计看板
- 用户管理
- 树洞审核
- 系统配置

## 项目特色

### 1. 安全性
- BCrypt密码加密
- JWT Token认证
- AES-256数据加密
- 敏感词过滤
- 严格的权限控制

### 2. 算法实现
- SAS/SDS心理测评自动评分
- DFA敏感词过滤算法
- 匿名ID生成算法

### 3. 隐私保护
- 咨询笔记加密存储
- 树洞匿名发布
- 发布时间模糊处理
- 测试结果隐私保护

## 快速开始

### 1. 环境要求
- JDK 17
- Maven 3.6+
- MySQL 8.0
- Redis 3.0+
- Node.js 16+

### 2. 数据库初始化

```bash
cd mental-health-platform/database
mysql -uroot -p123456 < all-in-one.sql
```

### 3. 启动后端

```bash
cd mental-health-platform/backend
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd mental-health-platform/frontend
npm install
npm run dev
```

### 5. 访问系统

- 后端API文档：http://localhost:8080/doc.html
- 前端页面：http://localhost:3000

## 测试账号

- 管理员：admin / admin123
- 学生：需要注册（userType=1）
- 咨询师：需要注册（userType=2）

## API接口

### 认证接口（2个）
- POST /api/auth/login
- POST /api/auth/register

### 文章接口（5个）
- GET /api/articles/categories
- GET /api/articles/page
- GET /api/articles/{id}
- POST /api/articles/{id}/collect
- DELETE /api/articles/{id}/collect

### 预约接口（11个）
- GET /api/appointments/counselors
- POST /api/appointments
- GET /api/appointments/my
- PUT /api/appointments/{id}/confirm
- PUT /api/appointments/{id}/rate
- ...

### 心理测试接口（5个）
- GET /api/tests
- GET /api/tests/{testId}/questions
- POST /api/tests/submit
- GET /api/tests/history
- GET /api/tests/results/{resultId}

### 树洞接口（8个）
- POST /api/moments
- GET /api/moments
- POST /api/moments/{momentId}/like
- POST /api/moments/comments
- ...

### 咨询笔记接口（5个）
- POST /api/counseling-notes
- GET /api/counseling-notes
- GET /api/counseling-notes/{noteId}
- PUT /api/counseling-notes/{noteId}
- DELETE /api/counseling-notes/{noteId}

### 管理后台接口（5个）
- GET /api/admin/statistics
- GET /api/admin/users
- GET /api/admin/moments/pending
- PUT /api/admin/moments/{momentId}/audit
- ...

**总计：41个API接口**

## 项目结构

```
mental-health-platform/
├── backend/                 # 后端SpringBoot项目
│   ├── src/main/java/
│   │   └── com/mental/health/
│   │       ├── common/      # 公共模块
│   │       ├── config/      # 配置类
│   │       ├── controller/  # 控制器（8个）
│   │       ├── dto/         # 数据传输对象（7个）
│   │       ├── entity/      # 实体类（14个）
│   │       ├── mapper/      # 数据访问层（14个）
│   │       ├── service/     # 业务逻辑层（7个）
│   │       └── vo/          # 视图对象（10个）
│   └── src/main/resources/
│       └── application.yml  # 配置文件
│
├── frontend/                # 前端Vue3项目
│   ├── src/
│   │   ├── views/          # 页面
│   │   ├── components/     # 组件
│   │   ├── router/         # 路由
│   │   ├── stores/         # 状态管理
│   │   └── api/            # API接口
│   └── package.json
│
├── database/                # 数据库脚本
│   ├── all-in-one.sql      # 完整初始化脚本
│   ├── 07-insert-test-data.sql  # 心理测试数据
│   └── 08-insert-sensitive-words.sql  # 敏感词数据
│
├── 需求文档.txt             # 详细需求文档
├── 开发进度.md              # 开发进度跟踪
├── 快速启动指南.md          # 快速启动指南
└── 后端开发完成总结.md      # 功能总结
```

## 数据库设计

共16张数据表：
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

## 开发进度

- ✅ 用户认证系统
- ✅ 文章管理模块
- ✅ 预约管理模块
- ✅ 心理测试模块
- ✅ 树洞模块
- ✅ 咨询笔记模块
- ✅ 管理后台模块（基础功能）
- ✅ 前端页面开发（学生端11个页面）
- ⏳ 咨询师端页面
- ⏳ 管理员端页面
- ⏳ 系统优化

## 文档说明

- `需求文档.txt` - 完整的需求规格说明书
- `开发进度.md` - 详细的开发进度跟踪
- `快速启动指南.md` - 快速启动和测试指南
- `后端开发完成总结.md` - 后端功能完成总结
- `心理测试模块说明.md` - 心理测试模块详细说明
- `树洞和咨询笔记模块说明.md` - 树洞和咨询笔记模块说明

## 常见问题

### 1. 数据库连接失败
检查MySQL服务是否启动，用户名密码是否正确。

### 2. Redis连接失败
确保Redis服务正在运行。

### 3. 端口被占用
修改application.yml中的端口配置。

### 4. API测试
使用Swagger文档进行API测试：http://localhost:8080/doc.html

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 联系方式

如有问题，请查看项目文档或提交Issue。

---

**项目状态**：后端核心功能已完成 ✅

**最后更新**：2024年
