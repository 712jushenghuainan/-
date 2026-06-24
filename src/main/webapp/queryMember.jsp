<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.swim.entity.*" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>会员资料查询</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; padding: 30px; background: #f5f7fa; }
        .container { max-width: 700px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h2 { color: #2563eb; text-align: center; }
        .search-box { text-align: center; margin: 20px 0; }
        .search-box input { width: 250px; padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; }
        .search-box button { padding: 8px 20px; background: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .search-box button:hover { background: #1d4ed8; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }
        th { background: #f1f5f9; }
        .highlight { background: #f0fdf4; }
        .no-result { text-align: center; color: #ef4444; padding: 20px; }
        .btn-back { display: inline-block; margin-top: 15px; color: #2563eb; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <h2>🔍 会员资料查询</h2>

    <!-- 查询表单 -->
    <div class="search-box">
        <form action="queryMember" method="post">
            <input type="text" name="keyword" placeholder="输入卡号 或 姓名" value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>"/>
            <button type="submit">查询</button>
        </form>
    </div>

    <!-- 查询结果 -->
    <%
        List<Map<String, Object>> results = (List<Map<String, Object>>) request.getAttribute("results");
        if (results != null) {
            if (results.isEmpty()) {
    %>
                <div class="no-result">❌ 未找到匹配的会员信息</div>
    <%
            } else {
    %>
                <table>
                    <tr>
                        <th>卡号</th>
                        <th>持卡人</th>
                        <th>电话</th>
                        <th>卡类型</th>
                        <th>余额</th>
                        <th>卡状态</th>
                    </tr>
    <%
                for (Map<String, Object> row : results) {
    %>
                    <tr>
                        <td><strong><%= row.get("card_id") %></strong></td>
                        <td><%= row.get("name") %></td>
                        <td><%= row.get("phone") != null ? row.get("phone") : "-" %></td>
                        <td><%= row.get("card_type") %></td>
                        <td>¥<%= row.get("balance") %></td>
                        <td><%= row.get("card_status") %></td>
                    </tr>
    <%
                }
    %>
                </table>
                <p style="margin-top:10px;color:#64748b;font-size:14px;">共 <%= results.size() %> 条记录</p>
    <%
            }
        }
    %>

    <p style="text-align:center;margin-top:20px;">
        <a href="index.jsp" class="btn-back">← 返回首页</a>
    </p>
</div>
</body>
</html>