package com.swim.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 存款记录实体类 - 对应 t_deposit_record 表
 */
public class DepositRecord {
    private Integer id;
    private String cardId;          // 关联 t_card.card_id
    private Date depositTime;
    private BigDecimal amount;
    private Integer operatorId;     // 关联 t_user.user_id
    private String remark;

    // ----- Getter and Setter -----
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public Date getDepositTime() { return depositTime; }
    public void setDepositTime(Date depositTime) { this.depositTime = depositTime; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}