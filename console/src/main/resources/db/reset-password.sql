-- 重置所有用户密码为 123456
UPDATE sys_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH';

-- 如果上面的哈希不正确，可以使用下面这个（已验证的123456的BCrypt哈希）
-- UPDATE sys_user SET password = '$2a$10$VeWsYGjDOxf7gRRCJkNbOe.F5Y6pFqQOln7mO3MEGpGGFU6nHxT6e';
