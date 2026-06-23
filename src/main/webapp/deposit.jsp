<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>存款管理</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container mt-5">
    <h2>💰 会员存款</h2>
    <div class="row">
        <div class="col-md-6">
            <form id="depositForm">
                <div class="mb-3">
                    <label for="cardId" class="form-label">会员卡号</label>
                    <input type="text" class="form-control" id="cardId" name="cardId" required>
                </div>
                <div class="mb-3">
                    <label for="amount" class="form-label">存款金额（元）</label>
                    <input type="number" class="form-control" id="amount" name="amount" step="0.01" required>
                </div>
                <div class="mb-3">
                    <label for="remark" class="form-label">备注</label>
                    <input type="text" class="form-control" id="remark" name="remark" placeholder="可选">
                </div>
                <button type="submit" class="btn btn-primary">确认存款</button>
            </form>
            <div id="resultArea" class="mt-3"></div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        $("#depositForm").submit(function(event) {
            event.preventDefault();

            $.ajax({
                type: "POST",
                url: "/api/deposit",
                data: {
                    cardId: $("#cardId").val(),
                    amount: $("#amount").val(),
                    operatorId: 1,
                    remark: $("#remark").val()
                },
                success: function(response) {
                    if (response.success) {
                        $("#resultArea").html('<div class="alert alert-success">✅ 存款成功！新余额：<strong>' + response.newBalance + '</strong> 元</div>');
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