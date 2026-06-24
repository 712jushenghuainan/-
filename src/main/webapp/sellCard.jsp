<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>游泳馆 - 售卡管理</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; padding: 20px; background: #f5f7fa; }
        .container { max-width: 500px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h2 { color: #2563eb; text-align: center; }
        label { display: inline-block; width: 80px; font-weight: bold; }
        input, select { width: 200px; padding: 6px 10px; margin: 5px 0; border: 1px solid #ddd; border-radius: 4px; }
        .btn { background: #2563eb; color: white; border: none; padding: 10px 30px; border-radius: 5px; cursor: pointer; font-size: 16px; }
        .btn:hover { background: #1d4ed8; }
        .form-row { margin: 10px 0; }
    </style>
</head>
<body>
    <div class="container">
        <h2>🏊 会员卡销售</h2>
        <form action="sellCard" method="post">
            <div class="form-row">
                <label>姓名：</label>
                <input type="text" name="name" required placeholder="请输入持卡人姓名"/>
            </div>
            <div class="form-row">
                <label>电话：</label>
                <input type="text" name="phone" placeholder="手机号码"/>
            </div>
            <div class="form-row">
                <label>证件号：</label>
                <input type="text" name="idNumber" placeholder="身份证号"/>
            </div>
            <div class="form-row">
                <label>地址：</label>
                <input type="text" name="address" placeholder="通讯地址"/>
            </div>
            <div class="form-row">
                <label>卡类型：</label>
                <select name="cardType">
                    <option value="储值卡">储值卡</option>
                    <option value="折扣卡">折扣卡</option>
                </select>
            </div>
            <div class="form-row">
                <label>充值金额：</label>
                <input type="number" name="balance" step="0.01" value="0.00"/>
            </div>
            <div class="form-row">
                <label>折扣率：</label>
                <input type="number" name="discountRate" step="0.01" value="1.00" placeholder="1.00=无折扣"/>
            </div>
            <div style="text-align: center; margin-top: 20px;">
                <input type="submit" value="确认售卡" class="btn"/>
            </div>
        </form>
        <p style="text-align:center; margin-top:15px; color:#6b7280; font-size:14px;">
            <a href="index.jsp" style="color:#2563eb;">← 返回首页</a>
        </p>
    </div>
</body>
</html>