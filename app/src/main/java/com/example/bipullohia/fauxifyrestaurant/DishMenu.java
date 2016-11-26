package com.example.bipullohia.fauxifyrestaurant;

/**
 * Created by Bipul Lohia on 11/6/2016.
 */


public class DishMenu {
    public String dishName, dishPrice, dishId; public Integer isVeg;

    public DishMenu(String currentdishName, String currentdishPrice, String dishId, Integer isVeg) {
        this.dishName = currentdishName;
        this.dishPrice = currentdishPrice;
        this.dishId = dishId;
        this.isVeg = isVeg;
    }

    public String getdishName() {
        return dishName;
    }

    public void setdishName(String name) {
        this.dishName = name;
    }

    public String getdishPrice() {
        return dishPrice;
    }

    public void setdishPrice(String name) {
        this.dishPrice = name;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String name) {
        this.dishId = name;
    }

    public Integer getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Integer name) {
        this.isVeg = name;
    }

}
