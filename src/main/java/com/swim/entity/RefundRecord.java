package com.swim.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退卡记录实体类 - 对应 t_refund_record 表
 */
public class RefundRecord {
    private Integer id;
    private String cardId;
    private Date refundTime;
    private BigDecimal refundAmount;
    private Integer operatorId;
    private String reason;

    // ----- Getter and Setter -----
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public Date getRefundTime() { return refundTime; }
    public void setRefundTime(Date refundTime) { this.refundTime = refundTime; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}