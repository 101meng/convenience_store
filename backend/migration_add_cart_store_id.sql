-- Migration: Add store_id to cart table for multi-store cart isolation
ALTER TABLE cart ADD COLUMN store_id INT NULL DEFAULT NULL AFTER user_id;
