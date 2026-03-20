-- 插入咨询师用户和敏感词数据

-- 1. 插入咨询师用户
INSERT INTO sys_user (id, username, password, real_name, phone, email, gender, user_type, status, created_at, updated_at, deleted) VALUES
(2, 'counselor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张心理', '13800138001', 'counselor1@example.com', 0, 2, 1, NOW(), NOW(), 0),
(3, 'counselor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李咨询', '13800138002', 'counselor2@example.com', 1, 2, 1, NOW(), NOW(), 0);

-- 2. 插入咨询师信息
INSERT INTO counselor_info (id, user_id, title, specialty, introduction, education, experience, rating, completed_count, status, created_at, updated_at, deleted) VALUES
(1, 2, '国家二级心理咨询师', '焦虑症,抑郁症,人际关系', '擅长认知行为疗法，帮助来访者应对焦虑、抑郁等情绪问题。', '北京大学心理学硕士', '8年心理咨询经验', 4.8, 0, 1, NOW(), NOW(), 0),
(2, 3, '国家二级心理咨询师', '学业压力,职业规划,情感问题', '专注于大学生心理咨询，帮助学生解决学业和情感困扰。', '清华大学心理学博士', '5年心理咨询经验', 4.9, 0, 1, NOW(), NOW(), 0);

-- 3. 插入敏感词数据
INSERT INTO sensitive_word (id, word, level, category, status, created_at, updated_at) VALUES
(1, '自杀', 3, '危险行为', 1, NOW(), NOW()),
(2, '轻生', 3, '危险行为', 1, NOW(), NOW()),
(3, '死亡', 2, '消极情绪', 1, NOW(), NOW()),
(4, '绝望', 2, '消极情绪', 1, NOW(), NOW()),
(5, '痛苦', 1, '消极情绪', 1, NOW(), NOW());
