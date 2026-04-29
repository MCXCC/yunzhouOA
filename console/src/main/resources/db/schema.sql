-- 云州OA系统数据库表结构

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(500) DEFAULT '',
    dept_name VARCHAR(100) NOT NULL,
    order_num INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status CHAR(1) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.order_num IS '显示顺序';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_dept.deleted IS '删除标志（0存在 1删除）';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    dept_id BIGINT,
    username VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    gender CHAR(1) DEFAULT '0',
    avatar VARCHAR(200),
    password VARCHAR(200) NOT NULL,
    status CHAR(1) DEFAULT '0',
    login_ip VARCHAR(50),
    login_date TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.username IS '用户账号';
COMMENT ON COLUMN sys_user.nickname IS '用户昵称';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号码';
COMMENT ON COLUMN sys_user.gender IS '性别（0男 1女 2未知）';
COMMENT ON COLUMN sys_user.avatar IS '头像地址';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN sys_user.deleted IS '删除标志（0存在 1删除）';
COMMENT ON COLUMN sys_user.remark IS '备注';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_key VARCHAR(100) NOT NULL,
    order_num INT DEFAULT 0,
    data_scope CHAR(1) DEFAULT '1',
    status CHAR(1) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_key IS '角色权限字符串';
COMMENT ON COLUMN sys_role.order_num IS '显示顺序';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）';
COMMENT ON COLUMN sys_role.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN sys_role.deleted IS '删除标志（0存在 1删除）';
COMMENT ON COLUMN sys_role.remark IS '备注';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    order_num INT DEFAULT 0,
    path VARCHAR(200) DEFAULT '',
    component VARCHAR(200),
    query VARCHAR(255),
    route_name VARCHAR(50) DEFAULT '',
    is_frame CHAR(1) DEFAULT '1',
    is_cache CHAR(1) DEFAULT '0',
    menu_type CHAR(1) NOT NULL,
    visible CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    perms VARCHAR(100),
    icon VARCHAR(100) DEFAULT '#',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.order_num IS '显示顺序';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.query IS '路由参数';
COMMENT ON COLUMN sys_menu.route_name IS '路由名称';
COMMENT ON COLUMN sys_menu.is_frame IS '是否为外链（0是 1否）';
COMMENT ON COLUMN sys_menu.is_cache IS '是否缓存（0缓存 1不缓存）';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型（M目录 C菜单 F按钮）';
COMMENT ON COLUMN sys_menu.visible IS '菜单状态（0显示 1隐藏）';
COMMENT ON COLUMN sys_menu.status IS '菜单状态（0正常 1停用）';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.deleted IS '删除标志（0存在 1删除）';
COMMENT ON COLUMN sys_menu.remark IS '备注';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户和角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色和菜单关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

-- 通知公告表
CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    type CHAR(1) NOT NULL,
    content TEXT,
    status CHAR(1) DEFAULT '0',
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_notice IS '通知公告表';
COMMENT ON COLUMN sys_notice.id IS '公告ID';
COMMENT ON COLUMN sys_notice.title IS '公告标题';
COMMENT ON COLUMN sys_notice.type IS '公告类型（1通知 2公告）';
COMMENT ON COLUMN sys_notice.content IS '公告内容';
COMMENT ON COLUMN sys_notice.status IS '状态（0正常 1关闭）';
COMMENT ON COLUMN sys_notice.create_by IS '创建者';
COMMENT ON COLUMN sys_notice.deleted IS '删除标志（0存在 1删除）';
COMMENT ON COLUMN sys_notice.remark IS '备注';

-- 用户消息表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type CHAR(1) DEFAULT '1',
    sender_id BIGINT,
    receiver_id BIGINT NOT NULL,
    read_status CHAR(1) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_message IS '用户消息表';
COMMENT ON COLUMN sys_message.id IS '消息ID';
COMMENT ON COLUMN sys_message.title IS '消息标题';
COMMENT ON COLUMN sys_message.content IS '消息内容';
COMMENT ON COLUMN sys_message.type IS '消息类型（1系统 2待办 3提醒）';
COMMENT ON COLUMN sys_message.sender_id IS '发送者ID';
COMMENT ON COLUMN sys_message.receiver_id IS '接收者ID';
COMMENT ON COLUMN sys_message.read_status IS '阅读状态（0未读 1已读）';
COMMENT ON COLUMN sys_message.deleted IS '删除标志（0存在 1删除）';

-- 初始化数据

-- 初始化部门
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, order_num, leader, status) VALUES
(100, 0, '0', '云州科技', 0, '管理员', '0'),
(101, 100, '0,100', '研发部门', 1, '张三', '0'),
(102, 100, '0,100', '市场部门', 2, '李四', '0'),
(103, 100, '0,100', '测试部门', 3, '王五', '0'),
(104, 101, '0,100,101', '研发一组', 1, '赵六', '0'),
(105, 101, '0,100,101', '研发二组', 2, '钱七', '0');

-- 初始化用户（密码为 123456 的 BCrypt 加密）
INSERT INTO sys_user (id, dept_id, username, nickname, email, phone, gender, password, status) VALUES
(1, 100, 'admin', '管理员', 'admin@yunzhou.com', '13800138000', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0'),
(2, 101, 'zhangsan', '张三', 'zhangsan@yunzhou.com', '13800138001', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0'),
(3, 102, 'lisi', '李四', 'lisi@yunzhou.com', '13800138002', '1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0');

-- 初始化角色
INSERT INTO sys_role (id, role_name, role_key, order_num, data_scope, status) VALUES
(1, '超级管理员', 'admin', 1, '1', '0'),
(2, '普通角色', 'common', 2, '2', '0'),
(3, '部门经理', 'manager', 3, '3', '0');

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 初始化菜单
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon) VALUES
-- 一级菜单
(1, '系统管理', 0, 5, 'system', NULL, 'M', '0', '0', NULL, 'Setting'),
(2, '考勤管理', 0, 1, 'attendance', NULL, 'M', '0', '0', NULL, 'Clock'),
(3, '审批流程', 0, 2, 'workflow', NULL, 'M', '0', '0', NULL, 'Share'),
(4, '公告通知', 0, 3, 'notice', NULL, 'M', '0', '0', NULL, 'Bell'),
(5, '个人中心', 0, 4, 'profile', NULL, 'M', '1', '0', NULL, 'User'),
-- 系统管理子菜单
(100, '用户管理', 1, 1, 'user', 'system/user/index', 'C', '0', '0', 'system:user:list', 'User'),
(101, '部门管理', 1, 2, 'dept', 'system/dept/index', 'C', '0', '0', 'system:dept:list', 'OfficeBuilding'),
(102, '角色管理', 1, 3, 'role', 'system/role/index', 'C', '0', '0', 'system:role:list', 'UserFilled'),
(103, '菜单管理', 1, 4, 'menu', 'system/menu/index', 'C', '0', '0', 'system:menu:list', 'Menu'),
-- 考勤管理子菜单
(200, '打卡记录', 2, 1, 'record', 'attendance/record/index', 'C', '0', '0', 'attendance:record:list', 'Clock'),
(201, '请假申请', 2, 2, 'leave', 'attendance/leave/index', 'C', '0', '0', 'attendance:leave:list', 'Document'),
(202, '考勤统计', 2, 3, 'statistics', 'attendance/statistics/index', 'C', '0', '0', 'attendance:statistics:list', 'DataLine'),
-- 审批流程子菜单
(300, '我的申请', 3, 1, 'my-apply', 'workflow/my-apply/index', 'C', '0', '0', 'workflow:apply:list', 'Edit'),
(301, '我的待办', 3, 2, 'my-task', 'workflow/my-task/index', 'C', '0', '0', 'workflow:task:list', 'Clock'),
(302, '已办任务', 3, 3, 'done-task', 'workflow/done-task/index', 'C', '0', '0', 'workflow:done:list', 'CircleCheck'),
-- 公告通知子菜单
(400, '公告列表', 4, 1, 'list', 'notice/list/index', 'C', '0', '0', 'notice:list:list', 'Bell'),
-- 用户管理按钮
(1000, '用户查询', 100, 1, '', '', 'F', '0', '0', 'system:user:query', '#'),
(1001, '用户新增', 100, 2, '', '', 'F', '0', '0', 'system:user:add', '#'),
(1002, '用户修改', 100, 3, '', '', 'F', '0', '0', 'system:user:edit', '#'),
(1003, '用户删除', 100, 4, '', '', 'F', '0', '0', 'system:user:remove', '#'),
(1004, '用户导出', 100, 5, '', '', 'F', '0', '0', 'system:user:export', '#'),
-- 部门管理按钮
(1010, '部门查询', 101, 1, '', '', 'F', '0', '0', 'system:dept:query', '#'),
(1011, '部门新增', 101, 2, '', '', 'F', '0', '0', 'system:dept:add', '#'),
(1012, '部门修改', 101, 3, '', '', 'F', '0', '0', 'system:dept:edit', '#'),
(1013, '部门删除', 101, 4, '', '', 'F', '0', '0', 'system:dept:remove', '#'),
-- 角色管理按钮
(1020, '角色查询', 102, 1, '', '', 'F', '0', '0', 'system:role:query', '#'),
(1021, '角色新增', 102, 2, '', '', 'F', '0', '0', 'system:role:add', '#'),
(1022, '角色修改', 102, 3, '', '', 'F', '0', '0', 'system:role:edit', '#'),
(1023, '角色删除', 102, 4, '', '', 'F', '0', '0', 'system:role:remove', '#'),
-- 菜单管理按钮
(1030, '菜单查询', 103, 1, '', '', 'F', '0', '0', 'system:menu:query', '#'),
(1031, '菜单新增', 103, 2, '', '', 'F', '0', '0', 'system:menu:add', '#'),
(1032, '菜单修改', 103, 3, '', '', 'F', '0', '0', 'system:menu:edit', '#'),
(1033, '菜单删除', 103, 4, '', '', 'F', '0', '0', 'system:menu:remove', '#');

-- 初始化角色菜单关联
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 超级管理员拥有所有菜单
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 100), (1, 101), (1, 102), (1, 103),
(1, 200), (1, 201), (1, 202),
(1, 300), (1, 301), (1, 302),
(1, 400),
(1, 1000), (1, 1001), (1, 1002), (1, 1003), (1, 1004),
(1, 1010), (1, 1011), (1, 1012), (1, 1013),
(1, 1020), (1, 1021), (1, 1022), (1, 1023),
(1, 1030), (1, 1031), (1, 1032), (1, 1033),
-- 普通角色拥有部分菜单
(2, 2), (2, 3), (2, 4), (2, 5),
(2, 200), (2, 201), (2, 202),
(2, 300), (2, 301), (2, 302),
(2, 400);

-- 初始化通知
INSERT INTO sys_notice (id, title, type, content, status, create_by) VALUES
(1, '系统更新通知', '1', '系统将于本周六凌晨2点进行升级维护，届时将暂停服务，请提前做好准备。', '0', 'admin'),
(2, '端午节放假通知', '2', '根据国家法定节假日安排，端午节放假时间为6月22日至6月24日，共3天。', '0', 'admin'),
(3, '新功能上线通知', '1', '工作流审批功能已上线，支持请假、报销等多种审批流程。', '0', 'admin');

-- 设置序列值
SELECT setval('sys_dept_id_seq', (SELECT MAX(id) FROM sys_dept));
SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));
SELECT setval('sys_menu_id_seq', (SELECT MAX(id) FROM sys_menu));
SELECT setval('sys_notice_id_seq', (SELECT MAX(id) FROM sys_notice));