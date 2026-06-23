-- ============================================================
-- 游泳馆会员管理系统 - 统一数据库初始化脚本
-- 版本: v1.0
-- 包含: t_user, t_member, t_card, t_consume_record, t_deposit_record
-- 负责: 成员B（整合）
-- ============================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS swim_db;
USE swim_db;

-- ============================================================
-- 表1: 系统用户表 (t_user) - 成员D负责
-- ============================================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户编号',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密密码',
    role ENUM('admin', 'cashier', 'viewer') NOT NULL DEFAULT 'viewer' COMMENT '角色',
    real_name VARCHAR(50) COMMENT '真实姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入默认管理员
INSERT INTO t_user (username, password_hash, role, real_name)
VALUES ('admin', 'admin123', 'admin', '系统管理员');

-- ============================================================
-- 表2: 会员信息表 (t_member) - 成员A负责
-- ============================================================
DROP TABLE IF EXISTS t_member;
CREATE TABLE t_member (
    member_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '会员编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_number VARCHAR(18) UNIQUE COMMENT '证件号码',
    address VARCHAR(100) COMMENT '通讯地址',
    register_date DATE DEFAULT (CURRENT_DATE) COMMENT '注册日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 / 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';

-- ============================================================
-- 表3: 会员卡表 (t_card) - 成员A负责
-- ============================================================
DROP TABLE IF EXISTS t_card;
CREATE TABLE t_card (
    card_id VARCHAR(20) PRIMARY KEY COMMENT '卡号',
    member_id INT NOT NULL COMMENT '所属会员ID',
    creator_id INT NOT NULL COMMENT '开卡操作员ID',
    card_type ENUM('储值卡', '折扣卡') NOT NULL COMMENT '卡类型',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '当前余额',
    discount_rate DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率',
    total_consume DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费',
    total_deposit DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计存款',
    card_status ENUM('正常', '已退', '禁用') DEFAULT '正常' COMMENT '卡状态',
    issue_date DATE DEFAULT (CURRENT_DATE) COMMENT '发卡日期',
    FOREIGN KEY (member_id) REFERENCES t_member(member_id) ON DELETE RESTRICT,
    FOREIGN KEY (creator_id) REFERENCES t_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡表';

-- ============================================================
-- 表4: 消费记录表 (t_consume_record) - 成员B负责
-- ============================================================
CREATE TABLE IF NOT EXISTS t_consume_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号',
    consume_time DATETIME NOT NULL COMMENT '消费时间',
    amount DECIMAL(10,2) NOT NULL COMMENT '消费金额',
    remark VARCHAR(255) COMMENT '备注',
    operator_id INT COMMENT '操作员ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';

-- ============================================================
-- 表5: 存款记录表 (t_deposit_record) - 成员B负责
-- ============================================================
CREATE TABLE IF NOT EXISTS t_deposit_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号',
    deposit_time DATETIME NOT NULL COMMENT '存款时间',
    amount DECIMAL(10,2) NOT NULL COMMENT '存款金额',
    operator_id INT COMMENT '操作员ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存款记录表';

-- ============================================================
-- 测试数据（验证表关联）
-- ============================================================
INSERT INTO t_member (name, phone, id_number, address)
VALUES ('测试会员', '13800000000', '110101199001011234', '上海市浦东新区');

INSERT INTO t_card (card_id, member_id, creator_id, card_type, balance)
VALUES ('C001', 1, 1, '储值卡', 100.00);

INSERT INTO t_consume_record (card_id, consume_time, amount, remark, operator_id)
VALUES ('C001', NOW(), 30.00, '游泳门票', 1);

INSERT INTO t_deposit_record (card_id, deposit_time, amount, operator_id)
VALUES ('C001', NOW(), 200.00, 1);

-- ============================================================
-- 高阶技术1: 视图
-- ============================================================
-- 日报表视图（按天汇总消费）
CREATE OR REPLACE VIEW v_daily_consume AS
SELECT
    DATE(consume_time) AS day,
    card_id,
    COUNT(*) AS consume_times,
    SUM(amount) AS total_amount
FROM t_consume_record
GROUP BY DATE(consume_time), card_id;

-- 会员综合信息视图
CREATE OR REPLACE VIEW v_member_summary AS
SELECT
    c.card_id,
    c.balance,
    c.total_consume AS total_spent,
    c.total_deposit,
    c.card_status,
    c.card_type,
    m.name AS member_name,
    m.phone
FROM t_card c
LEFT JOIN t_member m ON c.member_id = m.member_id;

-- ============================================================
-- 高阶技术2: 索引优化（报告用，截图保存）
-- ============================================================
-- 1. 先查执行计划（不建索引，type=ALL 全表扫描）
EXPLAIN SELECT * FROM t_consume_record WHERE card_id = 'C001' AND consume_time > '2026-06-01';

-- 2. 创建复合索引
CREATE INDEX idx_card_time ON t_consume_record(card_id, consume_time);

-- 3. 再查执行计划（type=range/ref，rows明显减少）
EXPLAIN SELECT * FROM t_consume_record WHERE card_id = 'C001' AND consume_time > '2026-06-01';

-- ============================================================
-- 验证所有视图
-- ============================================================
SELECT * FROM v_member_summary;
SELECT * FROM v_daily_consume;