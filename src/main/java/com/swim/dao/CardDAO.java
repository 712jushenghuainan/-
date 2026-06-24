package com.swim.dao;

import com.swim.entity.Card;

import java.math.BigDecimal;
import java.sql.*;

/**
 * 会员卡 DAO - 对应 t_card 表
 */
public class CardDAO {

    /**
     * 根据卡号查询会员卡信息
     * 修改点：将 SELECT * 改为显式字段列表，明确包含 creator_id
     */
    public Card findByCardId(Connection conn, String cardId) throws SQLException {
        // 显式列出所有字段，确保包含 creator_id
        String sql = "SELECT card_id, member_id, creator_id, card_type, balance, discount_rate, " +
                "total_consume, total_deposit, card_status, issue_date " +
                "FROM t_card WHERE card_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCard(rs);
                }
                return null;
            }
        }
    }

    /**
     * 更新余额（存款时调用）- SQL 层面原子加法
     */
    public int addBalance(Connection conn, String cardId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE t_card SET balance = balance + ? WHERE card_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, amount);
            pstmt.setString(2, cardId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 更新卡状态（退卡时调用）
     */
    public int updateCardStatus(Connection conn, String cardId, String status) throws SQLException {
        String sql = "UPDATE t_card SET card_status = ? WHERE card_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, cardId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 查询当前余额
     */
    public BigDecimal getBalance(Connection conn, String cardId) throws SQLException {
        String sql = "SELECT balance FROM t_card WHERE card_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
                return BigDecimal.ZERO;
            }
        }
    }

    /**
     * 将 ResultSet 当前行映射为 Card 对象
     * 注意：字段名必须与 SQL 查询列名一致
     */
    private Card mapRowToCard(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardId(rs.getString("card_id"));
        card.setMemberId(rs.getInt("member_id"));
        card.setCreatorId(rs.getInt("creator_id"));   // 现在一定能取到
        card.setCardType(rs.getString("card_type"));
        card.setBalance(rs.getBigDecimal("balance"));
        card.setDiscountRate(rs.getBigDecimal("discount_rate"));
        card.setTotalConsume(rs.getBigDecimal("total_consume"));
        card.setTotalDeposit(rs.getBigDecimal("total_deposit"));
        card.setCardStatus(rs.getString("card_status"));
        card.setIssueDate(rs.getDate("issue_date"));
        return card;
    }
}