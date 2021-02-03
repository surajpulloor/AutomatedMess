package com.project.ams.automatedmess;

/**
 * Created by surajpulloor on 19/3/18.
 */

public class MenuItem {
    private String itemName;
    private double itemPrice;
    private  String itemNo;

    public String getItemName() {
        return itemName;
    }

    public MenuItem() {
    }

    public MenuItem(String itemName, double itemPrice, String itemNo) {

        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemNo = itemNo;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    @Override
    public String toString() {
        return itemNo + ":" + itemName + "-" + itemPrice;
    }
}
