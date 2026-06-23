<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>退卡管理</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container mt-5">
    <h2>🔙 会员退卡</h2>
    <div class="row">
        <div class="col-md-6">
            <form id="refundForm">
                <div class="mb-3">
                    <label for="cardId" class="form-label">会员卡号</label>
                    <input type="text" class="form-control" id="cardId" name="cardId" required>
                    <div class="form-text">退卡后将退还卡内余额，卡状态变为"已退卡"</div>
                </div>
                <div class="mb-3">
                    <label for="reason" class="form-label">退卡原因</label>
                    <input type="text" class="form-control" id="reason" name="reason" placeholder="如：会员主动退卡">
                </div>
                <button type="submit" class="btn btn-danger">确认退卡</button>
            </form>
            <div id="resultArea" class="mt-3"></div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        $("#refundForm").submit(function(event) {
            event.preventDefault();

            $.ajax({
                type: "POST",
                url: "/api/refund",
                data: {
                    cardId: $("#cardId").val(),
                    operatorId: 1,
                    reason: $("#reason").val()
                },
                success: function(response) {
                    if (response.success) {
                        $("#resultArea").html('<div class="alert alert-success">✅ 退卡成功！退款金额：<strong>' + response.refundAmount + '</strong> 元</div>');
                    } else {
                        $("#resultArea").html('<div class="alert alert-danger">❌ ' + response.message + '</div>');
                    }
                },
                error: function() {
                    $("#resultArea").html('<div class="alert alert-danger">请求失败，请检查网络</div>');
                }
            });
        });
    });
</script>
</body>
</html>