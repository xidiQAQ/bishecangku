-- ============================================================================
-- 创建数据表（最后部分）
-- ============================================================================

USE mental_health;

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

SELECT 'All tables created successfully!' AS Status;
