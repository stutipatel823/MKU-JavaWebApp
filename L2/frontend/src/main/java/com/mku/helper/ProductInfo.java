/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mku.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author stutipatel
 */
@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInfo {
    private int productId;
    private int adminId;
    private String name;
    private String description;
    private String imageURL;
    private double price;
    private String brand;
    private String category;
    private boolean availability;
    private int stock;
    private int quantity; // quanitity in user's cart

    public ProductInfo() {
    }
   
    public ProductInfo(int productId, int adminId, String name, String description, String imageURL, double price, String brand, String category, boolean availability, int stock, int quantity) {
        this.productId = productId;
        this.adminId = adminId;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.brand = brand;
        this.category = category;
        this.availability = availability;
        this.stock = stock;
        this.quantity = quantity;
    }
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}