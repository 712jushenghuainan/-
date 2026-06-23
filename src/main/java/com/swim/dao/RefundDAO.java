package com.swim.dao;

import com.swim.entity.RefundRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 退卡记录 DAO - 对应 t_refund_record 表
 */
public class RefundDAO {

    /**
     * 插入退卡记录
     */
    public Integer insert(Connection conn, RefundRecord record) throws SQLException {
        String sql = "INSERT INTO t_refund_record (card_id, refund_time, refund_amount, operator_id, reason) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, record.getCardId());
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setBigDecimal(3, record.getRefundAmount());
            pstmt.setInt(4, record.getOperatorId());
            pstmt.setString(5, record.getReason());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("插入退卡记录失败");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("无法获取自增ID");
                }
            }
        }
    }

    /**
     * 查询某张卡的所有退卡记录
     */
    public List<RefundRecord> findByCardId(Connection conn, String cardId) throws SQLException {
        String sql = "SELECT * FROM t_refund_record WHERE card_id = ? ORDER BY refund_time DESC";
        List<RefundRecord> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RefundRecord record = new RefundRecord();
                    record.setId(rs.getInt("id"));
                    record.setCardId(rs.getString("card_id"));
                    record.setRefundTime(rs.getTimestamp("refund_time"));
                    record.setRefundAmount(rs.getBigDecimal("refund_amount"));
                    record.setOperatorId(rs.getInt("operator_id"));
                    record.setReason(rs.getString("reason"));
                    list.add(record);
                }
            }
        }
        return list;
    }
}