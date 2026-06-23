package com.swim.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员卡实体类 - 对应 t_card 表（成员B负责设计）
 * 你只需要用到 cardId, balance, cardStatus 三个字段
 */
public class Card {
    private String cardId;           // 卡号（主键）
    private Integer memberId;        // 会员ID
    private Integer creatorId;       // 创建人
    private String cardType;         // 卡类型
    private BigDecimal balance;      // 余额（存款/退卡时操作）
    private BigDecimal discountRate; // 折扣率
    private BigDecimal totalConsume; // 累计消费
    private BigDecimal totalDeposit; // 累计存款
    private String cardStatus;       // 卡状态：正常/已退卡/已禁用
    private Date issueDate;          // 发卡日期

    // ----- Getter and Setter -----
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public Integer getCreatorId() { return creatorId; }
    public void setCreatorId(Integer creatorId) { this.creatorId = creatorId; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public BigDecimal getTotalConsume() { return totalConsume; }
    public void setTotalConsume(BigDecimal totalConsume) { this.totalConsume = totalConsume; }

    public BigDecimal getTotalDeposit() { return totalDeposit; }
    public void setTotalDeposit(BigDecimal totalDeposit) { this.totalDeposit = totalDeposit; }

    public String getCardStatus() { return cardStatus; }
    public void setCardStatus(String cardStatus) { this.cardStatus = cardStatus; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
}