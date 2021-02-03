package com.project.ams.automatedmess;

import java.util.Map;

public class Order_MessProvider {
    private Double convCharges;
    private Map<String, Map<String, Item>> items;
    private String orderGivenBy;
    private String status;
    private Double totalAmountCustomer;
    private Double totalAmountMessProvider;
    private String transactionId;

    public Order_MessProvider(Double convCharges, Map<String, Map<String, Item>> items, String orderGivenBy, String status, Double totalAmountCustomer, Double totalAmountMessProvider, String transactionId) {
        this.convCharges = convCharges;
        this.items = items;
        this.orderGivenBy = orderGivenBy;
        this.status = status;
        this.totalAmountCustomer = totalAmountCustomer;
        this.totalAmountMessProvider = totalAmountMessProvider;
        this.transactionId = transactionId;
    }

    public Order_MessProvider() {
    }

    public Double getConvCharges() {
        return convCharges;
    }

    public void setConvCharges(Double convCharges) {
        this.convCharges = convCharges;
    }

    public Map<String, Map<String, Item>> getItems() {
        return items;
    }

    public void setItems(Map<String, Map<String, Item>> items) {
        this.items = items;
    }

    public String getOrderGivenBy() {
        return orderGivenBy;
    }

    public void setOrderGivenBy(String orderGivenBy) {
        this.orderGivenBy = orderGivenBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmountCustomer() {
        return totalAmountCustomer;
    }

    public void setTotalAmountCustomer(Double totalAmountCustomer) {
        this.totalAmountCustomer = totalAmountCustomer;
    }

    public Double getTotalAmountMessProvider() {
        return totalAmountMessProvider;
    }

    public void setTotalAmountMessProvider(Double totalAmountMessProvider) {
        this.totalAmountMessProvider = totalAmountMessProvider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
