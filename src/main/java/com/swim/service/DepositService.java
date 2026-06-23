package com.swim.service;

import com.swim.dao.CardDAO;
import com.swim.dao.DepositDAO;
import com.swim.entity.Card;
import com.swim.entity.DepositRecord;
import com.swim.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;

/**
 * 存款业务服务 - 演示事务的 ACID 特性
 *
 * 核心事务场景：存款操作 = ①插入存款记录 + ②更新会员卡余额
 */
public class DepositService {

    private DepositDAO depositDAO = new DepositDAO();
    private CardDAO cardDAO = new CardDAO();

    /**
     * 存款操作 - 包含完整的事务控制
     *
     * @param cardId     会员卡号
     * @param amount     存款金额
     * @param operatorId 操作员ID
     * @param remark     备注
     * @return 存款后的新余额
     * @throws Exception 事务回滚时抛出
     */
    public BigDecimal deposit(String cardId, BigDecimal amount, Integer operatorId, String remark) throws Exception {
        // 参数校验
        if (cardId == null || cardId.trim().isEmpty()) {
            throw new IllegalArgumentException("会员卡号不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("存款金额必须大于0");
        }

        Connection conn = null;
        try {
            // 1. 获取数据库连接，设置隔离级别为 READ_COMMITTED
            conn = DBUtil.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);

            // 2. 查询会员卡是否存在
            Card card = cardDAO.findByCardId(conn, cardId);
            if (card == null) {
                throw new RuntimeException("会员卡不存在，卡号：" + cardId);
            }
            if ("已退卡".equals(card.getCardStatus())) {
                throw new RuntimeException("该卡已退卡，无法存款");
            }
            if ("已禁用".equals(card.getCardStatus())) {
                throw new RuntimeException("该卡已被禁用，无法存款");
            }

            // 3. 插入存款记录
            DepositRecord record = new DepositRecord();
            record.setCardId(cardId);
            record.setAmount(amount);
            record.setOperatorId(operatorId);
            record.setRemark(remark);
            Integer recordId = depositDAO.insert(conn, record);
            System.out.println("[事务] 存款记录插入成功，记录ID：" + recordId);

            // 4. 更新会员卡余额
            int updatedRows = cardDAO.addBalance(conn, cardId, amount);
            if (updatedRows != 1) {
                throw new RuntimeException("更新余额失败，卡可能不存在");
            }

            // 5. 查询更新后的余额
            BigDecimal newBalance = cardDAO.getBalance(conn, cardId);

            // 6. 提交事务
            conn.commit();
            System.out.println("[事务] 存款事务提交成功，卡号：" + cardId + "，新余额：" + newBalance);

            return newBalance;

        } catch (Exception e) {
            // 回滚事务
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[事务] 存款事务回滚，原因：" + e.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("存款失败：" + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}