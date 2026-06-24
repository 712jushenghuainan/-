<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>游泳馆会员管理系统</title>
    <style>
        body {
            font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
            background: #f0f4f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            background: white;
            padding: 40px 50px;
            border-radius: 16px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.12);
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        h1 {
            color: #1e293b;
            font-size: 28px;
            margin-top: 0;
            margin-bottom: 8px;
        }
        .subtitle {
            color: #64748b;
            font-size: 14px;
            margin-bottom: 30px;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 16px;
        }
        .menu {
            display: flex;
            flex-direction: column;
            gap: 12px;
            text-align: left;
        }
        .menu-item {
            display: block;
            background: #f8fafc;
            padding: 12px 20px;
            border-radius: 8px;
            text-decoration: none;
            color: #1e293b;
            font-weight: 500;
            border-left: 4px solid #3b82f6;
            transition: all 0.2s;
        }
        .menu-item:hover {
            background: #e2e8f0;
            transform: translateX(4px);
            border-left-color: #1d4ed8;
        }
        .menu-item span {
            float: right;
            color: #94a3b8;
            font-weight: 400;
            font-size: 13px;
        }
        .footer {
            margin-top: 30px;
            font-size: 12px;
            color: #94a3b8;
            border-top: 1px solid #e2e8f0;
            padding-top: 18px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>🏊 游泳馆会员管理系统</h1>
    <div class="subtitle">数据库应用课程设计 · 题目29</div>

    <div class="menu">
        <!-- 成员A 负责的功能 -->
        <a href="sellCard.jsp" class="menu-item">售卡管理 <span>成员A</span></a>
        <a href="queryMember.jsp" class="menu-item">会员资料查询 <span>成员A</span></a>

        <!-- 其他功能后续由其他成员添加，暂时保留注释占位 -->
        <!--
        <a href="#" class="menu-item">会员资料管理 <span>成员A</span></a>
        <a href="#" class="menu-item">消费管理 <span>成员B</span></a>
        <a href="#" class="menu-item">存款管理 <span>成员C</span></a>
        <a href="#" class="menu-item">退卡管理 <span>成员C</span></a>
        <a href="#" class="menu-item">会员禁用/解禁 <span>成员D</span></a>
        <a href="#" class="menu-item">系统管理 <span>成员D</span></a>
        -->
    </div>

    <div class="footer">
        小组成员：A · B · C · D &nbsp;|&nbsp; 贡献度各 25%
    </div>
</div>
</body>
</html>