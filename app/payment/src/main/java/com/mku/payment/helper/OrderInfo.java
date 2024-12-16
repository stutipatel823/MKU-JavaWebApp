package com.mku.payment.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInfo {
    private int orderId;
    private String orderDate;
    private double amount;
    private String transactionDate;
    private String status;
    private int userId;
    private int cartId;
    private int paymentId;
    private ProductsXML productsOrdered;

    // Default constructor
    public OrderInfo() {}

    // Constructor with parameters
    public OrderInfo(int orderId, String orderDate, double amount, String transactionDate, 
                     String status, int userId, int cartId, int paymentId, ProductsXML productsOrdered) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
        this.userId = userId;
        this.cartId = cartId;
        this.paymentId = paymentId;
        this.productsOrdered = productsOrdered;
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

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public ProductsXML getProductsOrdered() {
        return productsOrdered;
    }

    public void setProductsOrdered(ProductsXML productsOrdered) {
        this.productsOrdered = productsOrdered;
    }
}
