-- 树洞点赞记录表
CREATE TABLE IF NOT EXISTS `moment_like` (
  `id` BIGINT NOT NULL PRIMARY KEY COMMENT '主键ID',
  `moment_id` BIGINT NOT NULL COMMENT '树洞ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  UNIQUE KEY `uk_moment_user` (`moment_id`, `user_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_moment_id` (`moment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='树洞点赞记录表';

-- 树洞举报表
CREATE TABLE IF NOT EXISTS `moment_report` (
  `id` BIGINT NOT NULL PRIMARY KEY COMMENT '主键ID',
  `moment_id` BIGINT NOT NULL COMMENT '树洞ID',
  `reporter_id` BIGINT NOT NULL COMMENT '举报人ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态 0-待处理 1-已处理 2-已忽略',
  `handler_id` BIGINT COMMENT '处理人ID',
  `handle_result` VARCHAR(500) COMMENT '处理结果',
  `handle_time` DATETIME COMMENT '处理时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  KEY `idx_moment_id` (`moment_id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='树洞举报表';

-- 评论点赞记录表
CREATE TABLE IF NOT EXISTS `moment_comment_like` (
  `id` BIGINT NOT NULL PRIMARY KEY COMMENT '主键ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞记录表';
