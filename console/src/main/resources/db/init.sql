-- =====================================================
-- 云州OA系统 - 数据库初始化脚本
-- PostgreSQL 16.x（阿里云 RDS）
-- =====================================================

-- 启用必要扩展（阿里云 RDS 支持）
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- 1. 组织架构模块
-- =====================================================

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(500) DEFAULT '',
    dept_name VARCHAR(100) NOT NULL,
    order_num INTEGER DEFAULT 0,
    leader_id BIGINT,
    phone VARCHAR(20),
    email VARCHAR(100),
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_department IS '部门表';
COMMENT ON COLUMN sys_department.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_department.ancestors IS '祖级列表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    dept_id BIGINT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    gender INTEGER DEFAULT 0,
    avatar VARCHAR(500),
    status INTEGER DEFAULT 1,
    login_ip VARCHAR(50),
    login_date TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.gender IS '性别：0-未知 1-男 2-女';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    role_key VARCHAR(100) NOT NULL,
    order_num INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_role IS '角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-- 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(100) NOT NULL,
    order_num INTEGER DEFAULT 0,
    path VARCHAR(200),
    component VARCHAR(200),
    menu_type CHAR(1) DEFAULT 'M',
    perms VARCHAR(100),
    icon VARCHAR(100),
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.menu_type IS '类型：M-目录 C-菜单 F-按钮';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

-- =====================================================
-- 2. 考勤管理模块
-- =====================================================

-- 考勤规则表
CREATE TABLE IF NOT EXISTS att_rule (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    work_start_time TIME NOT NULL,
    work_end_time TIME NOT NULL,
    late_tolerance INTEGER DEFAULT 0,
    early_tolerance INTEGER DEFAULT 0,
    location_required BOOLEAN DEFAULT FALSE,
    location_name VARCHAR(200),
    location_longitude DECIMAL(10, 7),
    location_latitude DECIMAL(10, 7),
    location_radius INTEGER DEFAULT 500,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE att_rule IS '考勤规则表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS att_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    check_in_longitude DECIMAL(10, 7),
    check_in_latitude DECIMAL(10, 7),
    check_out_longitude DECIMAL(10, 7),
    check_out_latitude DECIMAL(10, 7),
    check_in_ip VARCHAR(50),
    check_out_ip VARCHAR(50),
    status INTEGER DEFAULT 0,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE att_record IS '打卡记录表';
COMMENT ON COLUMN att_record.status IS '状态：0-正常 1-迟到 2-早退 3-缺勤';

-- 请假申请表
CREATE TABLE IF NOT EXISTS att_leave (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leave_type INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration DECIMAL(5,1),
    reason VARCHAR(500),
    process_instance_id VARCHAR(100),
    status INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE att_leave IS '请假申请表';
COMMENT ON COLUMN att_leave.leave_type IS '类型：1-事假 2-病假 3-年假 4-调休 5-婚假 6-产假';

-- =====================================================
-- 3. 审批流程模块
-- =====================================================

-- 审批流程定义表
CREATE TABLE IF NOT EXISTS act_process_config (
    id BIGSERIAL PRIMARY KEY,
    process_key VARCHAR(100) NOT NULL UNIQUE,
    process_name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    form_json JSONB,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE act_process_config IS '审批流程配置表';

-- 审批记录表
CREATE TABLE IF NOT EXISTS act_approval_record (
    id BIGSERIAL PRIMARY KEY,
    process_instance_id VARCHAR(100) NOT NULL,
    task_id VARCHAR(100),
    business_key VARCHAR(100),
    approver_id BIGINT NOT NULL,
    approval_result INTEGER,
    approval_comment VARCHAR(500),
    approval_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE act_approval_record IS '审批记录表';
COMMENT ON COLUMN act_approval_record.approval_result IS '结果：0-待审批 1-通过 2-驳回 3-转交';

-- =====================================================
-- 4. 公告通知模块
-- =====================================================

-- 公告表
CREATE TABLE IF NOT EXISTS pub_notice (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    notice_type INTEGER DEFAULT 1,
    priority INTEGER DEFAULT 0,
    publish_time TIMESTAMP,
    expire_time TIMESTAMP,
    status INTEGER DEFAULT 0,
    publisher_id BIGINT,
    view_count INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE pub_notice IS '公告表';
COMMENT ON COLUMN pub_notice.status IS '状态：0-草稿 1-已发布 2-已撤回';

-- 公告阅读记录表
CREATE TABLE IF NOT EXISTS pub_notice_read (
    id BIGSERIAL PRIMARY KEY,
    notice_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(notice_id, user_id)
);

-- =====================================================
-- 5. 文档管理模块
-- =====================================================

-- 文档目录表
CREATE TABLE IF NOT EXISTS doc_folder (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    folder_name VARCHAR(200) NOT NULL,
    owner_id BIGINT,
    dept_id BIGINT,
    folder_type INTEGER DEFAULT 0,
    order_num INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE doc_folder IS '文档目录表';
COMMENT ON COLUMN doc_folder.folder_type IS '类型：0-个人 1-部门 2-公共';

-- 文档表
CREATE TABLE IF NOT EXISTS doc_file (
    id BIGSERIAL PRIMARY KEY,
    folder_id BIGINT,
    file_name VARCHAR(200) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    file_path VARCHAR(500),
    file_url VARCHAR(500),
    download_count INTEGER DEFAULT 0,
    owner_id BIGINT,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE doc_file IS '文档表';

-- =====================================================
-- 6. 系统配置模块
-- =====================================================

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL UNIQUE,
    status INTEGER DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_dict_type IS '字典类型表';

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGSERIAL PRIMARY KEY,
    dict_type VARCHAR(100) NOT NULL,
    dict_label VARCHAR(100) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    order_num INTEGER DEFAULT 0,
    css_class VARCHAR(100),
    list_class VARCHAR(100),
    status INTEGER DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_dict_data IS '字典数据表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_config IS '系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100),
    method VARCHAR(200),
    request_method VARCHAR(10),
    operator_id BIGINT,
    operator_name VARCHAR(50),
    operator_ip VARCHAR(50),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    params TEXT,
    result TEXT,
    status INTEGER DEFAULT 0,
    error_msg TEXT
);

COMMENT ON TABLE sys_operation_log IS '操作日志表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    login_ip VARCHAR(50),
    login_location VARCHAR(200),
    browser VARCHAR(100),
    os VARCHAR(100),
    status INTEGER DEFAULT 0,
    msg VARCHAR(500),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_login_log IS '登录日志表';

-- =====================================================
-- 初始数据
-- =====================================================

-- 默认部门
INSERT INTO sys_department (id, dept_name, order_num) 
SELECT 1, '云州科技', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_department WHERE id = 1);

-- 管理员用户 (密码: admin123)
INSERT INTO sys_user (id, dept_id, username, password, nickname, status) 
SELECT 1, 1, 'admin', '$2a$10$VQEEz1PwMC1Sj8fbBjCnXOGYFZhkNHMfMGYp0VJHHm5Z0JQzNqzgG', '系统管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE id = 1);

-- 超级管理员角色
INSERT INTO sys_role (id, role_name, role_key, order_num) 
SELECT 1, '超级管理员', 'admin', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE id = 1);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) 
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);
