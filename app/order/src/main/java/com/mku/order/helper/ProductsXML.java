package com.mku.order.helper;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Products")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsXML {

    @XmlElement(name = "product")
    private ArrayList<ProductInfo> products;

    // Default constructor
    public ProductsXML(){
        
    }

    // Constructor with products initialization
    public ProductsXML(ArrayList<ProductInfo> products) {
        this.products = products;
    }

    public ArrayList<ProductInfo> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductInfo> products) {
        this.products = products;
    }
}
