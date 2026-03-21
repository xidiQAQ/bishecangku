-- Create notification table
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL COMMENT 'Notification ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `title` VARCHAR(100) NOT NULL COMMENT 'Notification title',
  `content` VARCHAR(500) NOT NULL COMMENT 'Notification content',
  `type` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT 'Notification type: system, appointment, comment',
  `related_id` BIGINT NULL COMMENT 'Related ID (e.g., appointment ID)',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT 'Read status: 0-unread, 1-read',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0-not deleted, 1-deleted',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Notification table';
