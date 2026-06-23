-- 1. 创建测试数据库（如果没有的话）
CREATE DATABASE IF NOT EXISTS swim_db DEFAULT CHARACTER SET utf8mb4;
USE swim_db;

-- 2. 创建简化版会员卡表（只包含你需要的字段）
CREATE TABLE IF NOT EXISTS t_card (
    card_id VARCHAR(20) PRIMARY KEY COMMENT '卡号',
    member_id INT COMMENT '会员ID（可忽略）',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
    card_status VARCHAR(20) DEFAULT '正常' COMMENT '卡状态：正常/已退卡/已禁用'
) COMMENT='会员卡表（测试用简化版）';

-- 3. 插入几条测试数据
INSERT INTO t_card (card_id, member_id, balance, card_status) VALUES
('CARD001', 1, 100.00, '正常'),
('CARD002', 2, 50.00, '正常'),
('CARD003', 3, 0.00, '已禁用');

-- 4. 创建存款记录表（你的）
CREATE TABLE IF NOT EXISTS t_deposit_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '存款记录ID',
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号',
    deposit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '存款时间',
    amount DECIMAL(10,2) NOT NULL COMMENT '存款金额',
    operator_id INT COMMENT '操作员ID',
    remark VARCHAR(255) COMMENT '备注'
) COMMENT='存款记录表';

-- 5. 创建退卡记录表（你的）
CREATE TABLE IF NOT EXISTS t_refund_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '退卡记录ID',
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号',
    refund_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '退卡时间',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    operator_id INT COMMENT '操作员ID',
    reason VARCHAR(255) COMMENT '退卡原因'
) COMMENT='退卡记录表';