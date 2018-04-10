package com.example.android.stockkeepingassistant.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    /* Class variables */
    private UUID id;
    private String title;
    private int quantity;
    private BigDecimal price;
    private String supplierName;
    private String supplierEmail;

    public Product() {
        this(UUID.randomUUID());
    }

    public Product(UUID id) {
       this.id = id;
       quantity = 1;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }
}
