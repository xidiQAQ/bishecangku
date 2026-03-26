USE mental_health;

SET NAMES utf8mb4;

-- 插入测试通知数据
-- user_id = 100 是 student1 的ID
INSERT INTO notification (id, user_id, type, title, content, related_id, is_read, created_at, updated_at, deleted) VALUES
(201, 100, 'system', '欢迎使用心理健康平台', '欢迎您使用大学生心理健康关怀平台！我们致力于为您提供专业的心理健康服务。如有任何问题，请随时联系我们。', NULL, 0, NOW(), NOW(), 0),
(202, 100, 'system', '新文章推荐', '我们为您推荐了一篇新文章《如何应对考试焦虑》，点击查看详情。', 101, 0, NOW(), NOW(), 0),
(203, 100, 'appointment', '预约提醒', '您预约的心理咨询将在明天下午2点开始，请准时参加。', NULL, 0, NOW(), NOW(), 0),
(204, 100, 'system', '平台更新通知', '平台已更新至最新版本，新增了更多实用功能，快来体验吧！', NULL, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
(205, 100, 'comment', '评论回复', '您在心灵树洞的动态收到了新的评论回复。', NULL, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 0);

SELECT '通知数据插入成功！' AS Status;
