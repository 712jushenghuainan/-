package com.swim.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swim.service.RefundService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 退卡接口 - URL 映射：/api/refund
 */
public class RefundServlet extends HttpServlet {

    private RefundService refundService = new RefundService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String cardId = req.getParameter("cardId");
            String operatorIdStr = req.getParameter("operatorId");
            String reason = req.getParameter("reason");

            if (cardId == null || cardId.trim().isEmpty()) {
                throw new IllegalArgumentException("卡号不能为空");
            }

            Integer operatorId = (operatorIdStr == null || operatorIdStr.trim().isEmpty())
                    ? 1 : Integer.parseInt(operatorIdStr.trim());

            BigDecimal refundAmount = refundService.refund(cardId.trim(), operatorId, reason);

            result.put("success", true);
            result.put("message", "退卡成功");
            result.put("refundAmount", refundAmount);
            result.put("cardId", cardId);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}