-- 创建默认角色
INSERT INTO sys_role (id, role_name, role_key, order_num, data_scope, status) VALUES
(4, '开发人员', 'developer', 4, '1', '0'),
(5, '管理人员', 'manager', 5, '2', '0'),
(6, '部门经理', 'dept_manager', 6, '3', '0'),
(7, '普通员工', 'employee', 7, '5', '0')
ON CONFLICT (id) DO UPDATE SET
  role_name = EXCLUDED.role_name,
  role_key = EXCLUDED.role_key;

-- 开发人员权限（所有菜单和按钮）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 系统管理
(4, 1), (4, 100), (4, 101), (4, 102), (4, 103),
(4, 1000), (4, 1001), (4, 1002), (4, 1003), (4, 1004),
(4, 1010), (4, 1011), (4, 1012), (4, 1013),
(4, 1020), (4, 1021), (4, 1022), (4, 1023),
(4, 1030), (4, 1031), (4, 1032), (4, 1033),
-- 考勤管理
(4, 2), (4, 200), (4, 201), (4, 202),
-- 审批流程
(4, 3), (4, 300), (4, 301), (4, 302),
-- 公告通知
(4, 4), (4, 400)
ON CONFLICT DO NOTHING;

-- 管理人员权限（所有菜单，无按钮权限）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(5, 1), (5, 100), (5, 101), (5, 102), (5, 103),
(5, 2), (5, 200), (5, 201), (5, 202),
(5, 3), (5, 300), (5, 301), (5, 302),
(5, 4), (5, 400)
ON CONFLICT DO NOTHING;

-- 部门经理权限（考勤、审批、公告）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(6, 2), (6, 200), (6, 201), (6, 202),
(6, 3), (6, 300), (6, 301), (6, 302),
(6, 4), (6, 400)
ON CONFLICT DO NOTHING;

-- 普通员工权限（基本功能）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(7, 2), (7, 200), (7, 201),
(7, 3), (7, 300), (7, 301),
(7, 4), (7, 400)
ON CONFLICT DO NOTHING;

-- 创建测试用户
INSERT INTO sys_user (id, dept_id, username, nickname, email, phone, gender, password, status) VALUES
(4, 101, 'developer', '开发人员', 'developer@yunzhou.com', '13800138003', '0', '$2a$10$54Ox1RhPIn.IAi4f0g2jmuBvIL26AeaXu6DuhyiHN1TXzf1RNw94.', '0'),
(5, 100, 'manager', '管理人员', 'manager@yunzhou.com', '13800138004', '0', '$2a$10$54Ox1RhPIn.IAi4f0g2jmuBvIL26AeaXu6DuhyiHN1TXzf1RNw94.', '0'),
(6, 102, 'deptmanager', '部门经理', 'deptmanager@yunzhou.com', '13800138005', '1', '$2a$10$54Ox1RhPIn.IAi4f0g2jmuBvIL26AeaXu6DuhyiHN1TXzf1RNw94.', '0'),
(7, 103, 'employee', '普通员工', 'employee@yunzhou.com', '13800138006', '0', '$2a$10$54Ox1RhPIn.IAi4f0g2jmuBvIL26AeaXu6DuhyiHN1TXzf1RNw94.', '0')
ON CONFLICT (id) DO UPDATE SET
  username = EXCLUDED.username,
  nickname = EXCLUDED.nickname;

-- 分配角色给用户
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> 超级管理员
(2, 2),  -- zhangsan -> 普通角色
(3, 6),  -- lisi -> 部门经理
(4, 4),  -- developer -> 开发人员
(5, 5),  -- manager -> 管理人员
(6, 6),  -- deptmanager -> 部门经理
(7, 7)   -- employee -> 普通员工
ON CONFLICT DO NOTHING;

-- 更新序列
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));
SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));
