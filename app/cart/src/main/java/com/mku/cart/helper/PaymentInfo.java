/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mku.cart.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author stutipatel
 */
@XmlRootElement(name = "Payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentInfo {
    private int paymentId;
    private String paymentMethod;
    private String cardNumber;
    private String expiryDate;
    private String paypalEmail;
    private int userId;

    public PaymentInfo(int paymentId, String paymentMethod, String cardNumber, String expiryDate, int userId) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.userId = userId;
    }

    public PaymentInfo(int paymentId, String paymentMethod, String paypalEmail, int userId) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.paypalEmail = paypalEmail;
        this.userId = userId;
    }

    public PaymentInfo() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
}

