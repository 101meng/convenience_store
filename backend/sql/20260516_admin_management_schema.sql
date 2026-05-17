SET @store_phone_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stores'
      AND COLUMN_NAME = 'phone'
);
SET @store_phone_sql := IF(
    @store_phone_exists = 0,
    'ALTER TABLE stores ADD COLUMN phone VARCHAR(30) NULL DEFAULT NULL AFTER address',
    'SELECT 1'
);
PREPARE store_phone_stmt FROM @store_phone_sql;
EXECUTE store_phone_stmt;
DEALLOCATE PREPARE store_phone_stmt;

SET @store_hours_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stores'
      AND COLUMN_NAME = 'hours'
);
SET @store_hours_sql := IF(
    @store_hours_exists = 0,
    'ALTER TABLE stores ADD COLUMN hours VARCHAR(50) NULL DEFAULT NULL AFTER phone',
    'SELECT 1'
);
PREPARE store_hours_stmt FROM @store_hours_sql;
EXECUTE store_hours_stmt;
DEALLOCATE PREPARE store_hours_stmt;

UPDATE stores
SET phone = CASE store_id
        WHEN 1 THEN '415-555-0101'
        WHEN 2 THEN '415-555-0102'
        WHEN 3 THEN '415-555-0103'
        ELSE phone
    END,
    hours = CASE store_id
        WHEN 1 THEN '07:00-23:00'
        WHEN 2 THEN '08:00-22:00'
        WHEN 3 THEN '24 Hours'
        ELSE hours
    END
WHERE store_id IN (1, 2, 3);

CREATE TABLE IF NOT EXISTS admin_accounts (
    admin_id INT NOT NULL AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL COMMENT 'Management login phone number',
    name VARCHAR(50) NOT NULL,
    role ENUM('brand_admin', 'store_manager') NOT NULL COMMENT 'Brand admin or store manager',
    store_id INT NULL DEFAULT NULL COMMENT 'Bound store for store managers',
    avatar_url VARCHAR(255) NULL DEFAULT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 active, 0 disabled',
    created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (admin_id),
    UNIQUE KEY uk_admin_phone (phone),
    KEY idx_admin_store_id (store_id),
    CONSTRAINT admin_accounts_ibfk_1 FOREIGN KEY (store_id) REFERENCES stores (store_id) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO admin_accounts (admin_id, phone, name, role, store_id, avatar_url, status, created_at, updated_at)
VALUES
    (1, '18800000001', 'Brand Admin', 'brand_admin', NULL, 'https://ui-avatars.com/api/?name=B&background=4F46E5&color=fff&size=200', 1, '2026-05-09 13:25:57', '2026-05-09 13:25:57'),
    (2, '18800000011', 'Market Street Manager', 'store_manager', 1, 'https://ui-avatars.com/api/?name=M&background=0EA5E9&color=fff&size=200', 1, '2026-05-09 13:25:57', '2026-05-09 13:25:57'),
    (3, '18800000012', 'GreenLoop Manager', 'store_manager', 2, 'https://ui-avatars.com/api/?name=G&background=10B981&color=fff&size=200', 1, '2026-05-09 13:25:57', '2026-05-09 13:25:57')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    role = VALUES(role),
    store_id = VALUES(store_id),
    avatar_url = VALUES(avatar_url),
    status = VALUES(status),
    updated_at = VALUES(updated_at);
