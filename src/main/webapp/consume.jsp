<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>游泳馆消费登记</title>
</head>
<body>
    <h2>🏊 会员消费登记</h2>
    <form action="consume" method="post">
        <label>会员卡号：</label><input type="text" name="cardId" placeholder="例如 C001"><br/><br/>
        <label>消费金额：</label><input type="text" name="amount" placeholder="例如 50.00"><br/><br/>
        <label>备  注：</label><input type="text" name="remark" placeholder="例如 游泳门票"><br/><br/>
        <input type="submit" value="确认消费">
    </form>
    <hr/>
    <a href="consume">📊 查看今日消费日报表（调用视图）</a>
</body>
</html>