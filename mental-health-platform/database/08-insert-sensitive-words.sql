-- 插入敏感词数据
-- level: 1-警告（需人工审核） 2-拦截（直接拒绝）

-- 拦截级敏感词（直接拒绝发布）
INSERT INTO sensitive_word (word, level, category, status, created_at, updated_at) VALUES
('自杀', 2, '危险行为', 1, NOW(), NOW()),
('轻生', 2, '危险行为', 1, NOW(), NOW()),
('结束生命', 2, '危险行为', 1, NOW(), NOW()),
('不想活了', 2, '危险行为', 1, NOW(), NOW()),
('想死', 2, '危险行为', 1, NOW(), NOW()),
('自残', 2, '危险行为', 1, NOW(), NOW()),
('割腕', 2, '危险行为', 1, NOW(), NOW()),
('跳楼', 2, '危险行为', 1, NOW(), NOW()),
('服毒', 2, '危险行为', 1, NOW(), NOW()),
('暴力', 2, '危险行为', 1, NOW(), NOW()),
('伤害他人', 2, '危险行为', 1, NOW(), NOW()),
('报复社会', 2, '危险行为', 1, NOW(), NOW());

-- 警告级敏感词（需要人工审核）
INSERT INTO sensitive_word (word, level, category, status, created_at, updated_at) VALUES
('抑郁', 1, '心理状态', 1, NOW(), NOW()),
('焦虑', 1, '心理状态', 1, NOW(), NOW()),
('崩溃', 1, '心理状态', 1, NOW(), NOW()),
('绝望', 1, '心理状态', 1, NOW(), NOW()),
('痛苦', 1, '心理状态', 1, NOW(), NOW()),
('无助', 1, '心理状态', 1, NOW(), NOW()),
('孤独', 1, '心理状态', 1, NOW(), NOW()),
('迷茫', 1, '心理状态', 1, NOW(), NOW()),
('失眠', 1, '心理状态', 1, NOW(), NOW()),
('噩梦', 1, '心理状态', 1, NOW(), NOW()),
('恐惧', 1, '心理状态', 1, NOW(), NOW()),
('害怕', 1, '心理状态', 1, NOW(), NOW()),
('压力大', 1, '心理状态', 1, NOW(), NOW()),
('撑不下去', 1, '心理状态', 1, NOW(), NOW()),
('没有希望', 1, '心理状态', 1, NOW(), NOW()),
('活着没意义', 1, '心理状态', 1, NOW(), NOW());

-- 其他需要关注的词汇
INSERT INTO sensitive_word (word, level, category, status, created_at, updated_at) VALUES
('药物', 1, '其他', 1, NOW(), NOW()),
('酗酒', 1, '其他', 1, NOW(), NOW()),
('逃避', 1, '其他', 1, NOW(), NOW()),
('放弃', 1, '其他', 1, NOW(), NOW());
