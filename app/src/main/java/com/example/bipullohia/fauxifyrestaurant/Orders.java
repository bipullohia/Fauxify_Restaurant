package com.example.bipullohia.fauxifyrestaurant;


class Orders {
    private String orderId, totalItems, totalPrice, customerName, customerEmail, orderTime,
            orderConfirmed, orderDelivered, totalItemsPrice, customerAddress, customerOrder, deliveryFee;

    Orders(String orderId, String totalItems, String totalPrice, String customerName, String customerEmail,
           String orderTime, String orderConfirmed, String orderDelivered,
           String totalItemsPrice, String customerAddress, String customerOrder, String deliveryFee) {
        this.orderId = orderId;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.orderTime = orderTime;
        this.orderConfirmed = orderConfirmed;
        this.orderDelivered = orderDelivered;
        this.customerOrder = customerOrder;
        this.customerAddress = customerAddress;
        this.totalItemsPrice = totalItemsPrice;
        this.deliveryFee = deliveryFee;
    }

    String getOrderId() {
        return orderId;
    }

    public void setOrderId(String name) {
        this.orderId = name;
    }

    String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String name) {
        this.totalItems = name;
    }

    String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String name) {
        this.totalPrice = name;
    }

    String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String name) {
        this.customerName = name;
    }

    String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String name) {
        this.orderTime = name;
    }

    String getOrderConfirmed() {
        return orderConfirmed;
    }

    public void setOrderConfirmed(String name) {
        this.orderConfirmed = name;
    }

    String getOrderDelivered() {
        return orderDelivered;
    }

    public void setOrderDelivered(String name) {
        this.orderDelivered = name;
    }

    String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String name) {
        this.customerEmail = name;
    }

    String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String name) {
        this.customerAddress = name;
    }

    String getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(String name) {
        this.customerOrder = name;
    }

    String getTotalItemsPrice() {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice(String name) {
        this.totalItemsPrice = name;
    }

    String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String name) {
        this.deliveryFee = name;
    }
}
