package com.example.bipullohia.fauxifyrestaurant;


class Orders {
    private String orderId, totalitems, totalprice, customername, customeremail, ordertime,
            orderconfirmed, orderdelivered, totalitemsprice, customeraddress, customerorder, deliveryFee;

    Orders(String orderId, String totalitems, String totalprice, String customername, String customeremail,
           String ordertime, String orderconfirmed, String orderdelivered,
           String totalitemsprice, String customeraddress, String customerorder, String deliveryFee) {
        this.orderId = orderId;
        this.totalitems = totalitems;
        this.totalprice = totalprice;
        this.customeremail = customeremail;
        this.customername = customername;
        this.ordertime = ordertime;
        this.orderconfirmed = orderconfirmed;
        this.orderdelivered = orderdelivered;
        this.customerorder = customerorder;
        this.customeraddress = customeraddress;
        this.totalitemsprice = totalitemsprice;
        this.deliveryFee = deliveryFee;
    }

    String getOrderId() {
        return orderId;
    }

    public void setOrderId(String name) {
        this.orderId = name;
    }

    String getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(String name) {
        this.totalitems = name;
    }

    String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String name) {
        this.totalprice = name;
    }

    String getCustomername() {
        return customername;
    }

    public void setCustomername(String name) {
        this.customername = name;
    }

    String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String name) {
        this.ordertime = name;
    }

    String getOrderconfirmed() {
        return orderconfirmed;
    }

    public void setOrderconfirmed(String name) {
        this.orderconfirmed = name;
    }

    String getOrderdelivered() {
        return orderdelivered;
    }

    public void setOrderdelivered(String name) {
        this.orderdelivered = name;
    }

    String getCustomeremail() {
        return customeremail;
    }

    public void setCustomeremail(String name) {
        this.customeremail = name;
    }

    String getCustomeraddress() {
        return customeraddress;
    }

    public void setCustomeraddress(String name) {
        this.customeraddress = name;
    }

    String getCustomerorder() {
        return customerorder;
    }

    public void setCustomerorder(String name) {
        this.customerorder = name;
    }

    String getTotalitemsprice() {
        return totalitemsprice;
    }

    public void setTotalitemsprice(String name) {
        this.totalitemsprice = name;
    }

    String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String name) {
        this.deliveryFee = name;
    }
}
