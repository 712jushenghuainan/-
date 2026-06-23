package com.swim.service;

import com.swim.dao.CardDAO;
import com.swim.dao.RefundDAO;
import com.swim.entity.Card;
import com.swim.entity.RefundRecord;
import com.swim.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;

/**
 * 退卡业务服务 - 演示事务的 ACID 特性
 *
 * 核心事务场景：退卡操作 = ①插入退卡记录 + ②更新会员卡状态
 */
public class RefundService {

    private RefundDAO refundDAO = new RefundDAO();
    private CardDAO cardDAO = new CardDAO();

    /**
     * 退卡操作 - 包含完整的事务控制
     *
     * @param cardId     会员卡号
     * @param operatorId 操作员ID
     * @param reason     退卡原因
     * @return 退款金额（卡内余额）
     * @throws Exception 事务回滚时抛出
     */
    public BigDecimal refund(String cardId, Integer operatorId, String reason) throws Exception {
        if (cardId == null || cardId.trim().isEmpty()) {
            throw new IllegalArgumentException("会员卡号不能为空");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);

            // 1. 查询会员卡信息
            Card card = cardDAO.findByCardId(conn, cardId);
            if (card == null) {
                throw new RuntimeException("会员卡不存在，卡号：" + cardId);
            }
            if ("已退卡".equals(card.getCardStatus())) {
                throw new RuntimeException("该卡已退卡，不能重复退卡");
            }

            BigDecimal balance = card.getBalance();
            System.out.println("[事务] 当前余额：" + balance);

            // 2. 插入退卡记录
            RefundRecord record = new RefundRecord();
            record.setCardId(cardId);
            record.setRefundAmount(balance);
            record.setOperatorId(operatorId);
            record.setReason(reason);
            Integer recordId = refundDAO.insert(conn, record);
            System.out.println("[事务] 退卡记录插入成功，记录ID：" + recordId);

            // 3. 更新会员卡状态为"已退卡"
            int updatedRows = cardDAO.updateCardStatus(conn, cardId, "已退卡");
            if (updatedRows != 1) {
                throw new RuntimeException("更新卡状态失败");
            }

            // 4. 提交事务
            conn.commit();
            System.out.println("[事务] 退卡事务提交成功，卡号：" + cardId + "，退款金额：" + balance);

            return balance;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[事务] 退卡事务回滚，原因：" + e.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("退卡失败：" + e.getMessage(), e);
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