package com.example.Proje3.Tablolar;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "customers")
public class Customers {
    @jakarta.persistence.Id
    @Id // Birincil anahtar olarak customerId'yi kullanıyoruz
    @GeneratedValue(strategy = GenerationType.IDENTITY) // customerId alanı için otomatik değer üretme
    @Column(name = "CustomerID", unique = true)
    private Integer customerId;

    @Column(name = "CustomerName", nullable = false) // Sadece gerekli anotasyonu kullanıyoruz
    private String customerName;

    @Column(name = "Budget", nullable = false)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "CustomerType", nullable = false)
    private CustomerType customerType;

    @Column(name = "TotalSpent", nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO;


    @Column(name = "priority_score", nullable = false)
    private double priorityScore ;



    public Customers() {}




    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public enum CustomerType {
        Premium, Normal
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
}
