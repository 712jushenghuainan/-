package com.swim.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swim.service.DepositService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 存款接口 - URL 映射：/api/deposit
 */
public class DepositServlet extends HttpServlet {

    private DepositService depositService = new DepositService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String cardId = req.getParameter("cardId");
            String amountStr = req.getParameter("amount");
            String operatorIdStr = req.getParameter("operatorId");
            String remark = req.getParameter("remark");

            if (cardId == null || cardId.trim().isEmpty()) {
                throw new IllegalArgumentException("卡号不能为空");
            }
            if (amountStr == null || amountStr.trim().isEmpty()) {
                throw new IllegalArgumentException("金额不能为空");
            }

            BigDecimal amount = new BigDecimal(amountStr.trim());
            Integer operatorId = (operatorIdStr == null || operatorIdStr.trim().isEmpty())
                    ? 1 : Integer.parseInt(operatorIdStr.trim());

            BigDecimal newBalance = depositService.deposit(cardId.trim(), amount, operatorId, remark);

            result.put("success", true);
            result.put("message", "存款成功");
            result.put("newBalance", newBalance);
            result.put("cardId", cardId);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            e.printStackTrace();
        }

        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}