package com.example.Proje3.Tablolar;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Table(name = "Products")
public class Products {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "Stock", nullable = false)
    private Integer stock;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;


    public Products(Integer productId, String productName, Integer stock, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
        this.price = price;

    }

    public Products() {}

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}