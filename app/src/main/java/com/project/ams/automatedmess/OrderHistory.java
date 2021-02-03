package com.project.ams.automatedmess;

public class OrderHistory {

    private Integer orderNo;
    private Double totalAmount;
    private String orderStatus;

    public OrderHistory(Integer orderNo, Double totalAmount, String orderStatus) {
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public OrderHistory() {
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
