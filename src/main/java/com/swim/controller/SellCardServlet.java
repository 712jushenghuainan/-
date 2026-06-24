package com.swim.controller;

import com.swim.dao.CardDao;
import com.swim.entity.Card;
import com.swim.entity.Member;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/sellCard")
public class SellCardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        try {
            // 1. 获取表单参数
            Member member = new Member();
            member.setName(req.getParameter("name"));
            member.setPhone(req.getParameter("phone"));
            member.setIdNumber(req.getParameter("idNumber"));
            member.setAddress(req.getParameter("address"));

            Card card = new Card();
            card.setCardType(req.getParameter("cardType"));
            card.setBalance(new BigDecimal(req.getParameter("balance")));
            card.setDiscountRate(new BigDecimal(req.getParameter("discountRate")));

            // 2. 调用 DAO 执行售卡
            CardDao dao = new CardDao();
            String cardId = dao.sellCard(member, card);

            // 3. 返回成功页面
            resp.getWriter().println("<html><head><meta charset='UTF-8'><title>售卡结果</title>");
            resp.getWriter().println("<style>body{font-family:'Microsoft YaHei';padding:30px;text-align:center;}</style>");
            resp.getWriter().println("</head><body>");
            resp.getWriter().println("<div style='max-width:400px;margin:0 auto;background:#f0fdf4;padding:30px;border-radius:10px;border:2px solid #22c55e;'>");
            resp.getWriter().println("<h2 style='color:#22c55e;'>✅ 售卡成功！</h2>");
            resp.getWriter().println("<p style='font-size:18px;'>新卡号：<strong style='color:#2563eb;font-size:24px;'>" + cardId + "</strong></p>");
            resp.getWriter().println("<p><a href='sellCard.jsp' style='color:#2563eb;'>继续售卡</a> | <a href='index.jsp' style='color:#2563eb;'>返回首页</a></p>");
            resp.getWriter().println("</div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("<html><head><meta charset='UTF-8'><title>售卡失败</title>");
            resp.getWriter().println("<style>body{font-family:'Microsoft YaHei';padding:30px;text-align:center;}</style>");
            resp.getWriter().println("</head><body>");
            resp.getWriter().println("<div style='max-width:400px;margin:0 auto;background:#fef2f2;padding:30px;border-radius:10px;border:2px solid #ef4444;'>");
            resp.getWriter().println("<h2 style='color:#ef4444;'>❌ 售卡失败</h2>");
            resp.getWriter().println("<p style='color:red;'>" + e.getMessage() + "</p>");
            resp.getWriter().println("<p><a href='sellCard.jsp' style='color:#2563eb;'>返回重试</a></p>");
            resp.getWriter().println("</div></body></html>");
        }
    }
}