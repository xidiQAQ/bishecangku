-- Article Collect Table
CREATE TABLE IF NOT EXISTS article_collect (
    id BIGINT PRIMARY KEY COMMENT 'Primary Key ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    article_id BIGINT NOT NULL COMMENT 'Article ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Collection Time',
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_user_id (user_id),
    INDEX idx_article_id (article_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Article Collection Table';
