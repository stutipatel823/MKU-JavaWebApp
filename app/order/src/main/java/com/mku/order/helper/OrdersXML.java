/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mku.order.helper;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author stutipatel
 */
@XmlRootElement(name = "Orders")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrdersXML {

    @XmlElement(name = "order")
    private ArrayList<OrderInfo> orders;

    // Default constructor
    public OrdersXML(){
        
    }

    // Constructor with products initialization
    public OrdersXML(ArrayList<OrderInfo> orders) {
        this.orders = orders;
    }

    public ArrayList<OrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderInfo> orders) {
        this.orders = orders;
    }
}
