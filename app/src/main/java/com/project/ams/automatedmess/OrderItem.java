package com.project.ams.automatedmess;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

@Entity @IgnoreExtraProperties
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String itemName;


    private Double itemPrice;


    private Integer itemQuantity;


    private String itemType;


    private String itemCategory;


    public OrderItem(int id, String itemName, Double itemPrice, Integer itemQuantity, String itemType, String itemCategory) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemType = itemType;
        this.itemCategory = itemCategory;
    }

    public OrderItem() {
    }

    // Getters and Setters for our columns
    // Required for Room to work


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
}
