-- ============================================================================
-- 初始化数据
-- ============================================================================

USE mental_health;

-- 插入管理员账号 (密码: admin123，需要在应用中使用BCrypt加密)
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

SELECT 'Initial data inserted successfully!' AS Status;
SELECT '管理员账号: admin' AS Info;
SELECT '管理员密码: admin123' AS Info;
