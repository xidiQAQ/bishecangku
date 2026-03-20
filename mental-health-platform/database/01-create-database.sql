-- ============================================================================
-- 大学生心理健康关怀与预约平台 - 数据库初始化脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mental_health 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE mental_health;

-- 验证
SELECT 'Database created successfully!' AS Status;
