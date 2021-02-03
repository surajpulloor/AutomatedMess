package com.project.ams.automatedmess;

import java.util.List;
import java.util.Map;

public class Order_Customers {
    private Map<String, Map<String, Item>> items;
    private String orderGivenTo;
    private String status;
    private Double totalAmount;
    private String transactionId;


    public Order_Customers(Map<String, Map<String, Item>> items, String orderGivenTo, String status, Double totalAmount, String transactionId) {
        this.items = items;
        this.orderGivenTo = orderGivenTo;
        this.status = status;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
    }

    public Order_Customers() {
    }

    public Map<String, Map<String, Item>> getItems() {
        return items;
    }

    public void setItems(Map<String, Map<String, Item>> items) {
        this.items = items;
    }

    public String getOrderGivenTo() {
        return orderGivenTo;
    }

    public void setOrderGivenTo(String orderGivenTo) {
        this.orderGivenTo = orderGivenTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
