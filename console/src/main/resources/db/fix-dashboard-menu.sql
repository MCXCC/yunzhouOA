-- 添加首页菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, visible, status, order_num) VALUES
(1000, 0, '首页', 'C', 'dashboard', 'dashboard/index', 'House', '0', '0', 0)
ON CONFLICT (id) DO NOTHING;

-- 将首页菜单添加到所有角色
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1000),  -- 超级管理员
(2, 1000),  -- 普通角色
(3, 1000),  -- 部门经理
(4, 1000),  -- 开发人员
(5, 1000),  -- 管理人员
(6, 1000),  -- 部门经理
(7, 1000)   -- 普通员工
ON CONFLICT DO NOTHING;

-- 更新岗位管理菜单的父ID（如果还没有）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, visible, status, order_num) VALUES
(104, 1, '岗位管理', 'C', 'post', 'system/post/index', 'Postcard', '0', '0', 5)
ON CONFLICT (id) DO NOTHING;

-- 将岗位管理菜单添加到管理员角色
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 104),  -- 超级管理员
(4, 104)   -- 开发人员
ON CONFLICT DO NOTHING;
