-- MySQL DDL: Create categories table for MerchantService
CREATE TABLE IF NOT EXISTS categories (
    category_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(100) NOT NULL UNIQUE,
    parent_category_id BIGINT       DEFAULT NULL,
    description        TEXT,
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_category_id)
        REFERENCES categories (category_id)
        ON DELETE CASCADE
);
