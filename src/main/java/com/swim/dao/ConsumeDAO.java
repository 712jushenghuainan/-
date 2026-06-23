package com.swim.dao;

import com.swim.bean.ConsumeRecord;
import com.swim.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsumeDAO {

    // 1. 新增消费记录（增删改查中的 增 ）
    public boolean addConsume(ConsumeRecord record) {
        String sql = "INSERT INTO t_consume_record(card_id, consume_time, amount, remark, operator_id) VALUES(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, record.getCardId());
            ps.setTimestamp(2, new Timestamp(record.getConsumeTime().getTime()));
            ps.setDouble(3, record.getAmount());
            ps.setString(4, record.getRemark());
            ps.setInt(5, record.getOperatorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, ps, null);
        }
    }

    // 2. 按卡号和时间段查询消费明细（用于统计模块，查 ）
    public List<ConsumeRecord> listByCardAndDate(String cardId, String startDate, String endDate) {
        List<ConsumeRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM t_consume_record WHERE card_id = ? AND consume_time BETWEEN ? AND ? ORDER BY consume_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, cardId);
            ps.setString(2, startDate);
            ps.setString(3, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                ConsumeRecord r = new ConsumeRecord();
                r.setId(rs.getInt("id"));
                r.setCardId(rs.getString("card_id"));
                r.setConsumeTime(rs.getTimestamp("consume_time"));
                r.setAmount(rs.getDouble("amount"));
                r.setRemark(rs.getString("remark"));
                r.setOperatorId(rs.getInt("operator_id"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, ps, rs);
        }
        return list;
    }

    // 3. 查询视图（演示高阶技术 - 视图调用）
    public List<String> getDailyReport() {
        List<String> reports = new ArrayList<>();
        String sql = "SELECT * FROM v_daily_consume";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String line = rs.getString("day") + " | " + rs.getString("card_id") +
                        " | 次数:" + rs.getInt("consume_times") + " | 总额:" + rs.getDouble("total_amount");
                reports.add(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
        return reports;
    }
}