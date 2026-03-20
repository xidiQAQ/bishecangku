-- ============================================================================
-- 大学生心理健康关怀与预约平台 - 完整初始化脚本
-- 一次性执行即可完成所有数据库、表和初始数据的创建
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mental_health 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE mental_health;

-- ============================================================================
-- 创建数据表
-- ============================================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    user_type TINYINT NOT NULL COMMENT '用户类型 1-学生 2-咨询师 3-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 咨询师信息表
CREATE TABLE IF NOT EXISTS counselor_info (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户表ID',
    title VARCHAR(50) COMMENT '职称',
    specialty VARCHAR(500) COMMENT '擅长领域(JSON)',
    introduction TEXT COMMENT '个人简介',
    education VARCHAR(200) COMMENT '教育背景',
    experience TEXT COMMENT '工作经验',
    certificate VARCHAR(500) COMMENT '资质证书(JSON)',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分',
    rating_count INT DEFAULT 0 COMMENT '评价数量',
    appointment_count INT DEFAULT 0 COMMENT '预约总数',
    completed_count INT DEFAULT 0 COMMENT '完成咨询数',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-正常 2-停用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_status (status),
    INDEX idx_rating (rating),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询师信息表';

-- 3. 文章分类表
CREATE TABLE IF NOT EXISTS article_category (
    id BIGINT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- 4. 文章表
CREATE TABLE IF NOT EXISTS article (
    id BIGINT PRIMARY KEY COMMENT '文章ID',
    category_id BIGINT COMMENT '分类ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) COMMENT '文章摘要',
    content LONGTEXT COMMENT '文章内容',
    cover_image VARCHAR(255) COMMENT '封面图片URL',
    author VARCHAR(50) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    status TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-已发布 2-下架',
    publish_time DATETIME COMMENT '发布时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_publish_time (publish_time),
    FULLTEXT INDEX ft_title_content (title, content),
    FOREIGN KEY (category_id) REFERENCES article_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 5. 文章收藏表
CREATE TABLE IF NOT EXISTS article_collect (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_user_id (user_id),
    INDEX idx_article_id (article_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (article_id) REFERENCES article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- 6. 心理测试表
CREATE TABLE IF NOT EXISTS psychological_test (
    id BIGINT PRIMARY KEY COMMENT '测试ID',
    name VARCHAR(100) NOT NULL COMMENT '测试名称',
    description TEXT COMMENT '测试说明',
    test_type VARCHAR(50) COMMENT '测试类型',
    total_questions INT DEFAULT 0 COMMENT '题目总数',
    time_limit INT COMMENT '时间限制(分钟)',
    cover_image VARCHAR(255) COMMENT '封面图',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_test_type (test_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心理测试表';

-- 7. 测试题目表
CREATE TABLE IF NOT EXISTS test_question (
    id BIGINT PRIMARY KEY COMMENT '题目ID',
    test_id BIGINT NOT NULL COMMENT '测试ID',
    question_no INT NOT NULL COMMENT '题号',
    question_text TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项(JSON)',
    question_type TINYINT DEFAULT 1 COMMENT '题型 1-单选 2-多选',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_test_id (test_id),
    INDEX idx_question_no (question_no),
    FOREIGN KEY (test_id) REFERENCES psychological_test(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试题目表';

-- 8. 测试结果表
CREATE TABLE IF NOT EXISTS test_result (
    id BIGINT PRIMARY KEY COMMENT '结果ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    test_id BIGINT NOT NULL COMMENT '测试ID',
    answers TEXT COMMENT '答案(JSON)',
    total_score INT COMMENT '总分',
    result_level VARCHAR(20) COMMENT '结果等级',
    result_analysis TEXT COMMENT '结果分析',
    suggestions TEXT COMMENT '建议',
    test_duration INT COMMENT '测试用时(秒)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '测试时间',
    INDEX idx_user_id (user_id),
    INDEX idx_test_id (test_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (test_id) REFERENCES psychological_test(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试结果表';

-- 9. 咨询师时间表
CREATE TABLE IF NOT EXISTS counselor_schedule (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    counselor_id BIGINT NOT NULL COMMENT '咨询师ID',
    schedule_date DATE NOT NULL COMMENT '日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段',
    status TINYINT DEFAULT 1 COMMENT '状态 0-休息 1-可预约 2-已预约 3-已完成',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_counselor_date_time (counselor_id, schedule_date, time_slot),
    INDEX idx_counselor_id (counselor_id),
    INDEX idx_schedule_date (schedule_date),
    INDEX idx_status (status),
    FOREIGN KEY (counselor_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询师时间表';

-- 10. 预约记录表
CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT PRIMARY KEY COMMENT '预约ID',
    appointment_no VARCHAR(32) NOT NULL UNIQUE COMMENT '预约编号',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    counselor_id BIGINT NOT NULL COMMENT '咨询师ID',
    schedule_id BIGINT COMMENT '时间表ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段',
    consult_type VARCHAR(50) COMMENT '咨询类型',
    problem_desc TEXT COMMENT '问题描述',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待确认 1-已确认 2-已完成 3-已取消 4-已拒绝',
    cancel_reason VARCHAR(500) COMMENT '取消/拒绝原因',
    student_remark VARCHAR(500) COMMENT '学生备注',
    counselor_remark VARCHAR(500) COMMENT '咨询师备注',
    rating TINYINT COMMENT '学生评分 1-5星',
    comment TEXT COMMENT '学生评价',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_student_id (student_id),
    INDEX idx_counselor_id (counselor_id),
    INDEX idx_schedule_id (schedule_id),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status),
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (counselor_id) REFERENCES sys_user(id),
    FOREIGN KEY (schedule_id) REFERENCES counselor_schedule(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';


-- 11. 咨询笔记表（高安全级别）
CREATE TABLE IF NOT EXISTS counseling_note (
    id BIGINT PRIMARY KEY COMMENT '笔记ID',
    appointment_id BIGINT NOT NULL COMMENT '预约ID',
    counselor_id BIGINT NOT NULL COMMENT '咨询师ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    note_content TEXT COMMENT '笔记内容(加密)',
    problem_analysis TEXT COMMENT '问题分析(加密)',
    counseling_plan TEXT COMMENT '咨询方案(加密)',
    follow_up_plan TEXT COMMENT '后续跟进计划(加密)',
    next_appointment DATE COMMENT '下次预约建议日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_counselor_id (counselor_id),
    INDEX idx_student_id (student_id),
    FOREIGN KEY (appointment_id) REFERENCES appointment(id),
    FOREIGN KEY (counselor_id) REFERENCES sys_user(id),
    FOREIGN KEY (student_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询笔记表';

-- 12. 树洞内容表
CREATE TABLE IF NOT EXISTS moment (
    id BIGINT PRIMARY KEY COMMENT '内容ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    anonymous_id VARCHAR(32) NOT NULL COMMENT '匿名ID',
    content TEXT NOT NULL COMMENT '内容',
    image_urls TEXT COMMENT '图片URL(JSON)',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态 0-待审核 1-已通过 2-已拒绝',
    audit_reason VARCHAR(500) COMMENT '审核意见',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_audit_status (audit_status),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='树洞内容表';

-- 13. 树洞评论表
CREATE TABLE IF NOT EXISTS moment_comment (
    id BIGINT PRIMARY KEY COMMENT '评论ID',
    moment_id BIGINT NOT NULL COMMENT '树洞内容ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    anonymous_id VARCHAR(32) NOT NULL COMMENT '匿名ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_moment_id (moment_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (moment_id) REFERENCES moment(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='树洞评论表';

-- 14. 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    word VARCHAR(100) NOT NULL UNIQUE COMMENT '敏感词',
    level TINYINT DEFAULT 1 COMMENT '级别 1-警告 2-拦截',
    category VARCHAR(50) COMMENT '分类',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词表';

-- 15. 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(200) COMMENT '配置说明',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 16. 操作日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT '操作地点',
    execute_time INT COMMENT '执行时长(毫秒)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================================
-- 插入初始数据
-- ============================================================================

-- 插入管理员账号 (密码: admin123，BCrypt加密后的值)
INSERT INTO sys_user (id, username, password, real_name, user_type, status, created_at, updated_at, deleted)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 3, 1, NOW(), NOW(), 0);

-- 插入文章分类
INSERT INTO article_category (id, name, description, sort_order, status, created_at, updated_at, deleted)
VALUES 
(1, '焦虑情绪', '关于焦虑情绪的心理知识', 1, 1, NOW(), NOW(), 0),
(2, '抑郁情绪', '关于抑郁情绪的心理知识', 2, 1, NOW(), NOW(), 0),
(3, '人际关系', '关于人际交往的心理知识', 3, 1, NOW(), NOW(), 0),
(4, '学业压力', '关于学业压力的心理知识', 4, 1, NOW(), NOW(), 0),
(5, '情感困扰', '关于情感问题的心理知识', 5, 1, NOW(), NOW(), 0);

-- 插入系统配置
INSERT INTO sys_config (id, config_key, config_value, config_desc, config_type, created_at, updated_at)
VALUES 
(1, 'appointment.cancel.hours', '24', '预约取消提前小时数', 'number', NOW(), NOW()),
(2, 'moment.auto.audit', 'false', '树洞内容是否自动审核', 'string', NOW(), NOW()),
(3, 'platform.name', '大学生心理健康关怀与预约平台', '平台名称', 'string', NOW(), NOW());

-- 插入示例心理测试
INSERT INTO psychological_test (id, name, description, test_type, total_questions, time_limit, status, created_at, updated_at, deleted)
VALUES 
(1, '焦虑自评量表(SAS)', '用于评估焦虑状态的标准化量表', 'SAS', 20, 10, 1, NOW(), NOW(), 0),
(2, '抑郁自评量表(SDS)', '用于评估抑郁状态的标准化量表', 'SDS', 20, 10, 1, NOW(), NOW(), 0);

-- ============================================================================
-- 完成提示
-- ============================================================================

SELECT '============================================' AS '';
SELECT '数据库初始化完成！' AS '状态';
SELECT '============================================' AS '';
SELECT 'mental_health' AS '数据库名称';
SELECT '16' AS '数据表数量';
SELECT 'admin' AS '管理员账号';
SELECT 'admin123' AS '管理员密码';
SELECT '============================================' AS '';
