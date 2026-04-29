-- 岗位表
CREATE TABLE IF NOT EXISTS sys_post (
    id BIGSERIAL PRIMARY KEY,
    post_code VARCHAR(64) NOT NULL,
    post_name VARCHAR(50) NOT NULL,
    order_num INT DEFAULT 0,
    status CHAR(1) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_post IS '岗位信息表';
COMMENT ON COLUMN sys_post.id IS '岗位ID';
COMMENT ON COLUMN sys_post.post_code IS '岗位编码';
COMMENT ON COLUMN sys_post.post_name IS '岗位名称';
COMMENT ON COLUMN sys_post.order_num IS '显示顺序';
COMMENT ON COLUMN sys_post.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_post.deleted IS '删除标志（0存在 1删除）';
COMMENT ON COLUMN sys_post.remark IS '备注';

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS sys_user_post (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, post_id)
);

COMMENT ON TABLE sys_user_post IS '用户岗位关联表';
COMMENT ON COLUMN sys_user_post.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_post.post_id IS '岗位ID';

-- 初始化岗位数据
INSERT INTO sys_post (id, post_code, post_name, order_num, status, remark) VALUES
(1, 'ceo', '董事长', 1, '0', NULL),
(2, 'cto', '技术总监', 2, '0', NULL),
(3, 'pm', '项目经理', 3, '0', NULL),
(4, 'dev', '开发工程师', 4, '0', NULL),
(5, 'test', '测试工程师', 5, '0', NULL),
(6, 'hr', '人力资源', 6, '0', NULL),
(7, 'operator', '运维工程师', 7, '0', NULL),
(8, 'employee', '普通员工', 8, '0', NULL)
ON CONFLICT (id) DO NOTHING;

-- 用户岗位关联
INSERT INTO sys_user_post (user_id, post_id) VALUES
(1, 1),  -- admin -> 董事长
(2, 4),  -- zhangsan -> 开发工程师
(3, 6),  -- lisi -> 人力资源
(4, 4),  -- developer -> 开发工程师
(5, 3),  -- manager -> 项目经理
(6, 5),  -- deptmanager -> 测试工程师
(7, 8)   -- employee -> 普通员工
ON CONFLICT DO NOTHING;

SELECT setval('sys_post_id_seq', (SELECT MAX(id) FROM sys_post));
