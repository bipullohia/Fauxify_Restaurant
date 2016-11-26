package com.example.bipullohia.fauxifyrestaurant;

/**
 * Created by Bipul Lohia on 9/27/2016.
 */

public class Orders {
    public String orderId, totalitems, totalprice, customername, customeremail, ordertime,
            orderconfirmed, orderdelivered, totalitemsprice, customeraddress, customerorder;

    public Orders(String orderId, String totalitems, String totalprice, String customername, String customeremail,
                  String ordertime, String orderconfirmed, String orderdelivered,
                  String totalitemsprice, String customeraddress, String customerorder) {
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

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String name) {
        this.orderId = name;
    }

    public String getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(String name) {
        this.totalitems = name;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String name) {
        this.totalprice = name;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String name) {
        this.customername = name;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String name) {
        this.ordertime = name;
    }

    public String getOrderconfirmed() {
        return orderconfirmed;
    }

    public void setOrderconfirmed(String name) {
        this.orderconfirmed = name;
    }

    public String getOrderdelivered() {
        return orderdelivered;
    }

    public void setOrderdelivered(String name) {
        this.orderdelivered = name;
    }

    public String getCustomeremail() {
        return customeremail;
    }

    public void setCustomeremail(String name) {
        this.customeremail = name;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public void setCustomeraddress(String name) {
        this.customeraddress = name;
    }

    public String getCustomerorder() {
        return customerorder;
    }

    public void setCustomerorder(String name) {
        this.customerorder = name;
    }

    public String getTotalitemsprice() {
        return totalitemsprice;
    }

    public void setTotalitemsprice(String name) {
        this.totalitemsprice = name;
    }

}
