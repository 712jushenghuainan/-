package com.swim.controller;

import com.swim.util.DBUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/queryMember")
public class QueryMemberServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String keyword = req.getParameter("keyword");
        req.setAttribute("keyword", keyword);

        List<Map<String, Object>> results = new ArrayList<>();

        String sql = "SELECT " +
                "    c.card_id, " +
                "    m.name, " +
                "    m.phone, " +
                "    c.card_type, " +
                "    c.balance, " +
                "    c.card_status " +
                "FROM t_member m " +
                "JOIN t_card c ON m.member_id = c.member_id " +
                "WHERE c.card_id LIKE ? OR m.name LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likePattern = "%" + keyword + "%";
            ps.setString(1, likePattern);
            ps.setString(2, likePattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("card_id", rs.getString("card_id"));
                    row.put("name", rs.getString("name"));
                    row.put("phone", rs.getString("phone"));
                    row.put("card_type", rs.getString("card_type"));
                    row.put("balance", rs.getBigDecimal("balance"));
                    row.put("card_status", rs.getString("card_status"));
                    results.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("results", results);
        req.getRequestDispatcher("queryMember.jsp").forward(req, resp);
    }
}