package com.swim.bean;

import java.util.Date;

public class ConsumeRecord {
    private int id;
    private String cardId;
    private Date consumeTime;
    private double amount;
    private String remark;
    private int operatorId;

    // 生成 Getter 和 Setter（快捷键 Alt+Insert -> Getter and Setter -> 全选 -> OK）
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public Date getConsumeTime() { return consumeTime; }
    public void setConsumeTime(Date consumeTime) { this.consumeTime = consumeTime; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public int getOperatorId() { return operatorId; }
    public void setOperatorId(int operatorId) { this.operatorId = operatorId; }
}