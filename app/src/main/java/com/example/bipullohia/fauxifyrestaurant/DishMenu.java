package com.example.bipullohia.fauxifyrestaurant;


class DishMenu {
    private String dishName, dishPrice, dishId; private Integer isVeg;

    DishMenu(String currentdishName, String currentdishPrice, String dishId, Integer isVeg) {
        this.dishName = currentdishName;
        this.dishPrice = currentdishPrice;
        this.dishId = dishId;
        this.isVeg = isVeg;
    }

    String getdishName() {
        return dishName;
    }

    public void setdishName(String name) {
        this.dishName = name;
    }

    String getdishPrice() {
        return dishPrice;
    }

    public void setdishPrice(String name) {
        this.dishPrice = name;
    }

    String getDishId() {
        return dishId;
    }

    public void setDishId(String name) {
        this.dishId = name;
    }

    Integer getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Integer name) {
        this.isVeg = name;
    }

}
