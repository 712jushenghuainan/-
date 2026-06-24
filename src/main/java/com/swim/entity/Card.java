package com.swim.entity;

import java.math.BigDecimal;
import java.sql.Date;

public class Card {
    private String cardId;
    private Integer memberId;
    private Integer creatorId;
    private String cardType;
    private BigDecimal balance;
    private BigDecimal discountRate;
    private BigDecimal totalConsume;
    private BigDecimal totalDeposit;
    private String cardStatus;
    private Date issueDate;

    // 生成 Getter 和 Setter
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