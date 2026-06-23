package com.swim.dao;

import com.swim.entity.DepositRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 存款记录 DAO - 对应 t_deposit_record 表
 */
public class DepositDAO {

    /**
     * 插入存款记录
     */
    public Integer insert(Connection conn, DepositRecord record) throws SQLException {
        String sql = "INSERT INTO t_deposit_record (card_id, deposit_time, amount, operator_id, remark) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, record.getCardId());
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setBigDecimal(3, record.getAmount());
            pstmt.setInt(4, record.getOperatorId());
            pstmt.setString(5, record.getRemark());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("插入存款记录失败");
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
     * 查询某张卡的所有存款记录
     */
    public List<DepositRecord> findByCardId(Connection conn, String cardId) throws SQLException {
        String sql = "SELECT * FROM t_deposit_record WHERE card_id = ? ORDER BY deposit_time DESC";
        List<DepositRecord> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DepositRecord record = new DepositRecord();
                    record.setId(rs.getInt("id"));
                    record.setCardId(rs.getString("card_id"));
                    record.setDepositTime(rs.getTimestamp("deposit_time"));
                    record.setAmount(rs.getBigDecimal("amount"));
                    record.setOperatorId(rs.getInt("operator_id"));
                    record.setRemark(rs.getString("remark"));
                    list.add(record);
                }
            }
        }
        return list;
    }
}