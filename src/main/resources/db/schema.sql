-- ============================================================
-- 存款记录表 (t_deposit_record)  — 成员C负责
-- ============================================================
CREATE TABLE IF NOT EXISTS t_deposit_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '存款记录ID',
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号（关联 t_card.card_id）',
    deposit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '存款时间',
    amount DECIMAL(10,2) NOT NULL COMMENT '存款金额',
    operator_id INT COMMENT '操作员ID（关联 t_user.user_id）',
    remark VARCHAR(255) COMMENT '备注'
) COMMENT='存款记录表';

-- ============================================================
-- 退卡记录表 (t_refund_record)  — 成员C负责
-- ============================================================
CREATE TABLE IF NOT EXISTS t_refund_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '退卡记录ID',
    card_id VARCHAR(20) NOT NULL COMMENT '会员卡号（关联 t_card.card_id）',
    refund_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '退卡时间',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额（卡内余额）',
    operator_id INT COMMENT '操作员ID（关联 t_user.user_id）',
    reason VARCHAR(255) COMMENT '退卡原因'
) COMMENT='退卡记录表';