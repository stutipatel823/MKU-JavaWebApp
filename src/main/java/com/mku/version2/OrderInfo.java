/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mku.version2;

import java.util.ArrayList;

/**
 *
 * @author stutipatel
 */
public class OrderInfo {
    private int orderId;
    private String orderDate;
    private double amount;
    private String transactionDate;
    private String status;
    private int userId;
    private int paymentId;
    private ArrayList<ProductInfo> products; // List of items ordered, populated from Order_Product table

    public OrderInfo(){}
    
    public OrderInfo(int orderId, String orderDate, double amount, String transactionDate, String status, int userId, int paymentId){
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
        this.userId = userId;
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
    
    public ArrayList<ProductInfo> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductInfo> products) {
        this.products = products;
    }
}
