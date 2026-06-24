package com.swim.entity;

import java.sql.Date;

public class Member {
    private Integer memberId;
    private String name;
    private String phone;
    private String idNumber;
    private String address;
    private Date registerDate;
    private Integer status;

    // 生成 Getter 和 Setter（IDEA快捷键：Alt + Insert -> Getter and Setter）
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Date getRegisterDate() { return registerDate; }
    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
