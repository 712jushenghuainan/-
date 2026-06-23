package com.swim.servlet;

import com.swim.bean.ConsumeRecord;
import com.swim.dao.ConsumeDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

@WebServlet("/consume")  // 这个路径就是前端表单提交的地址
public class ConsumeServlet extends HttpServlet {

    // 处理 GET 请求：展示查询统计页面
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 简单演示：调用视图查看日报表
        ConsumeDAO dao = new ConsumeDAO();
        List<String> reports = dao.getDailyReport();

        out.println("<h2>📊 今日消费日报表（来自视图 v_daily_consume）</h2>");
        out.println("<ul>");
        for (String line : reports) {
            out.println("<li>" + line + "</li>");
        }
        out.println("</ul>");
        out.println("<a href='consume.jsp'>返回消费登记</a>");
    }

    // 处理 POST 请求：处理消费登记表单
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String cardId = req.getParameter("cardId");
        String amountStr = req.getParameter("amount");
        String remark = req.getParameter("remark");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 简单校验
        if (cardId == null || cardId.trim().isEmpty() || amountStr == null || amountStr.trim().isEmpty()) {
            out.println("❌ 卡号和金额不能为空！<a href='consume.jsp'>返回</a>");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            ConsumeRecord record = new ConsumeRecord();
            record.setCardId(cardId);
            record.setConsumeTime(new Date()); // 当前时间
            record.setAmount(amount);
            record.setRemark(remark);
            record.setOperatorId(1); // 默认操作员ID

            ConsumeDAO dao = new ConsumeDAO();
            boolean success = dao.addConsume(record);

            if (success) {
                out.println("✅ 消费登记成功！<a href='consume.jsp'>继续登记</a> | <a href='consume'>查看日报表</a>");
            } else {
                out.println("❌ 消费登记失败，请检查！<a href='consume.jsp'>返回</a>");
            }
        } catch (NumberFormatException e) {
            out.println("❌ 金额格式错误，请输入数字！<a href='consume.jsp'>返回</a>");
        }
    }
}