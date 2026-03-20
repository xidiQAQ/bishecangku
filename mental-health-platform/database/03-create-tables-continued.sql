-- ============================================================================
-- 创建数据表（续）
-- ============================================================================

USE mental_health;

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

SELECT 'More tables created successfully!' AS Status;
