package com.project.ams.automatedmess;

import java.util.ArrayList;

/**
 * Created by surajpulloor on 23/3/18.
 */

public class Category {
    private String name;
    private String foodType;
    private double price;
    private ArrayList<MenuItem> menuItems = new ArrayList<>();



    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public Category(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Category() {
        this.name = "";
        this.price = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void calculatePriceFromMenuItems() {
        for (MenuItem item : menuItems) {
            price += item.getItemPrice();
        }
    }

}
