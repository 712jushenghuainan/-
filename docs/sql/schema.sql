-- ============================================================
-- 数据库: swim_club（游泳馆会员管理系统）
-- 说明: 完整建表语句，包含7张核心表
-- 作者: 成员A
-- 日期: 2026-06-23
-- ============================================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS swim_club
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE swim_club;

-- ============================================================
-- 表1: 系统用户表 (t_user)
-- 负责: 成员D（权限管理）
-- 说明: 存储系统登录账号，用于权限控制和操作日志记录
-- ============================================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户编号（自增主键）',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名（唯一）',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密密码',
    role ENUM('admin', 'cashier', 'viewer') NOT NULL DEFAULT 'viewer' COMMENT '角色: admin管理员 / cashier收银员 / viewer只读',
    real_name VARCHAR(50) COMMENT '真实姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入默认管理员（密码暂为明文，后续可用MD5加密）
INSERT INTO t_user (username, password_hash, role, real_name)
VALUES ('admin', 'admin123', 'admin', '系统管理员');


-- ============================================================
-- 表2: 会员信息表 (t_member)  ★★★ 你（成员A）负责 ★★★
-- 说明: 存储持卡人的基本资料，与会员卡表一对多关联
-- ============================================================
DROP TABLE IF EXISTS t_member;
CREATE TABLE t_member (
    member_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '会员编号（自增主键）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_number VARCHAR(18) UNIQUE COMMENT '证件号码（唯一约束）',
    address VARCHAR(100) COMMENT '通讯地址',
    register_date DATE DEFAULT (CURRENT_DATE) COMMENT '注册日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 / 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';


-- ============================================================
-- 表3: 会员卡表 (t_card)  ★★★ 你（成员A）负责 ★★★
-- 说明: 系统核心表，关联会员、操作员，并汇总消费/存款统计数据
-- ============================================================
DROP TABLE IF EXISTS t_card;
CREATE TABLE t_card (
    card_id VARCHAR(20) PRIMARY KEY COMMENT '卡号（主键，如 C2026001）',
    member_id INT NOT NULL COMMENT '所属会员ID（外键→t_member）',
    creator_id INT NOT NULL COMMENT '开卡操作员ID（外键→t_user）',
    card_type ENUM('储值卡', '折扣卡') NOT NULL COMMENT '卡类型',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '当前余额（储值卡专用）',
    discount_rate DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率（0.00~1.00，1.00表示无折扣）',
    total_consume DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费总额',
    total_deposit DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计存款总额',
    card_status ENUM('正常', '已退', '禁用') DEFAULT '正常' COMMENT '卡状态',
    issue_date DATE DEFAULT (CURRENT_DATE) COMMENT '发卡日期',

    FOREIGN KEY (member_id) REFERENCES t_member(member_id) ON DELETE RESTRICT,
    FOREIGN KEY (creator_id) REFERENCES t_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡表';


-- ============================================================
-- 表4: 消费记录表 (t_consume)  — 成员B负责
-- 说明: 记录每一笔消费流水，用于生成消费报表
-- ============================================================
DROP TABLE IF EXISTS t_consume;
CREATE TABLE t_consume (
    consume_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '消费流水号（自增主键）',
    card_id VARCHAR(20) NOT NULL COMMENT '消费的卡号（外键→t_card）',
    consume_amount DECIMAL(10,2) NOT NULL COMMENT '消费金额',
    consume_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
    remark VARCHAR(255) COMMENT '消费备注',

    FOREIGN KEY (card_id) REFERENCES t_card(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费记录表';


-- ============================================================
-- 表5: 存款记录表 (t_deposit)  — 成员B负责
-- 说明: 记录储值卡的每一笔充值流水
-- ============================================================
DROP TABLE IF EXISTS t_deposit;
CREATE TABLE t_deposit (
    deposit_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '存款流水号（自增主键）',
    card_id VARCHAR(20) NOT NULL COMMENT '充值的卡号（外键→t_card）',
    operator_id INT NOT NULL COMMENT '操作员ID（外键→t_user）',
    amount DECIMAL(10,2) NOT NULL COMMENT '存款金额',
    deposit_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '存款时间',

    FOREIGN KEY (card_id) REFERENCES t_card(card_id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES t_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存款记录表';


-- ============================================================
-- 表6: 卡片状态变更日志 (t_card_status_log)  — 成员D负责
-- 说明: 记录禁用/解禁/退卡的详细原因和历史
-- ============================================================
DROP TABLE IF EXISTS t_card_status_log;
CREATE TABLE t_card_status_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '日志编号（自增主键）',
    card_id VARCHAR(20) NOT NULL COMMENT '关联卡号（外键→t_card）',
    operator_id INT NOT NULL COMMENT '操作员ID（外键→t_user）',
    action_type ENUM('禁用', '解禁', '退卡') NOT NULL COMMENT '操作类型',
    reason VARCHAR(255) COMMENT '原因说明',
    action_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    FOREIGN KEY (card_id) REFERENCES t_card(card_id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES t_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡片状态变更日志表';


-- ============================================================
-- 表7: 会员资料变更日志 (t_member_change_log)  ★★★ 你（成员A）负责 ★★★
-- 说明: 由触发器自动记录name/phone/address的修改历史
-- ============================================================
DROP TABLE IF EXISTS t_member_change_log;
CREATE TABLE t_member_change_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '日志编号（自增主键）',
    card_id VARCHAR(20) NOT NULL COMMENT '关联卡号（外键→t_card）',
    old_name VARCHAR(50) COMMENT '修改前姓名',
    new_name VARCHAR(50) COMMENT '修改后姓名',
    old_phone VARCHAR(20) COMMENT '修改前电话',
    new_phone VARCHAR(20) COMMENT '修改后电话',
    old_address VARCHAR(100) COMMENT '修改前地址',
    new_address VARCHAR(100) COMMENT '修改后地址',
    change_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    operator VARCHAR(50) COMMENT '操作人（由前端传入）',

    FOREIGN KEY (card_id) REFERENCES t_card(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员资料变更日志表';


-- ============================================================
-- 索引优化（提升查询性能）
-- ============================================================

-- t_card 表：按会员ID查询
CREATE INDEX idx_card_member_id ON t_card(member_id);
-- t_card 表：按卡状态查询（统计有效卡）
CREATE INDEX idx_card_status ON t_card(card_status);

-- t_consume 表：按消费时间查询（B的视图会用到）
CREATE INDEX idx_consume_date ON t_consume(consume_date);
-- t_consume 表：按卡号查询消费记录
CREATE INDEX idx_consume_card_id ON t_consume(card_id);

-- t_deposit 表：按存款时间查询
CREATE INDEX idx_deposit_date ON t_deposit(deposit_date);
-- t_deposit 表：按卡号查询存款记录
CREATE INDEX idx_deposit_card_id ON t_deposit(card_id);

-- t_member 表：按姓名模糊查询（资料管理功能）
CREATE INDEX idx_member_name ON t_member(name);

-- ============================================================
-- 验证SQL（执行后可检查是否创建成功）
-- ============================================================
-- SHOW TABLES;
-- DESC t_member;
-- DESC t_card;
-- SELECT * FROM t_user;
-- ============================================================
USE swim_club;
SHOW TABLES;


 -- 一、创建售卡存储过程 sp_SellCard
DELIMITER //

DROP PROCEDURE IF EXISTS sp_SellCard //
CREATE PROCEDURE sp_SellCard(
    IN p_name VARCHAR(50),
    IN p_phone VARCHAR(20),
    IN p_id_number VARCHAR(18),
    IN p_address VARCHAR(100),
    IN p_card_type VARCHAR(20),   -- '储值卡' 或 '折扣卡'
    IN p_balance DECIMAL(10,2),   -- 储值卡首次充值金额
    IN p_discount DECIMAL(3,2),   -- 折扣率，如 1.00 或 0.85
    OUT p_card_id VARCHAR(20),
    OUT p_result INT              -- 0=成功, 1=失败
)
BEGIN
    DECLARE v_member_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 1;
    END;

    START TRANSACTION;
        -- 1. 插入会员
        INSERT INTO t_member (name, phone, id_number, address)
        VALUES (p_name, p_phone, p_id_number, p_address);
        SET v_member_id = LAST_INSERT_ID();

        -- 2. 生成卡号 (规则: C + 时间戳)
        SET p_card_id = CONCAT('C', UNIX_TIMESTAMP(NOW()));

        -- 3. 插入会员卡
        INSERT INTO t_card (card_id, member_id, creator_id, card_type, balance, discount_rate)
        VALUES (p_card_id, v_member_id, 1, p_card_type, p_balance, p_discount);

        SET p_result = 0;
    COMMIT;
END //

DELIMITER ;

-- 二、创建会员资料修改存储过程 sp_UpdateMember
DELIMITER //

DROP PROCEDURE IF EXISTS sp_UpdateMember //
CREATE PROCEDURE sp_UpdateMember(
    IN p_card_id VARCHAR(20),
    IN p_name VARCHAR(50),
    IN p_phone VARCHAR(20),
    IN p_address VARCHAR(100),
    OUT p_result INT
)
BEGIN
    DECLARE v_member_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 1;
    END;

    START TRANSACTION;
        -- 通过卡号查询会员ID (加锁防止并发)
        SELECT member_id INTO v_member_id FROM t_card WHERE card_id = p_card_id FOR UPDATE;

        IF v_member_id IS NULL THEN
            SET p_result = 2; -- 卡号不存在
            ROLLBACK;
        ELSE
            UPDATE t_member
            SET name = p_name, phone = p_phone, address = p_address
            WHERE member_id = v_member_id;
            SET p_result = 0;
            COMMIT;
        END IF;
END //

DELIMITER ;

-- 三、创建触发器 trg_LogMemberChange（自动记录资料变更日志）
DELIMITER //

DROP TRIGGER IF EXISTS trg_LogMemberChange //
CREATE TRIGGER trg_LogMemberChange
AFTER UPDATE ON t_member
FOR EACH ROW
BEGIN
    -- 只在姓名、电话、地址发生变化时记录
    IF OLD.name != NEW.name OR OLD.phone != NEW.phone OR OLD.address != NEW.address THEN
        INSERT INTO t_member_change_log (card_id, old_name, new_name, old_phone, new_phone, old_address, new_address, operator)
        SELECT card_id, OLD.name, NEW.name, OLD.phone, NEW.phone, OLD.address, NEW.address, 'system'
        FROM t_card WHERE member_id = NEW.member_id;
    END IF;
END //

DELIMITER ;