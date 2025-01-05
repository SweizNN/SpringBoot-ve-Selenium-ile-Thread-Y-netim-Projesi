package com.example.Proje3.Tablolar;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "unapprovedorders")
public class UnapprovedOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @JoinColumn(name = "CustomerID", nullable = false)
    private Integer customerID;

    @JoinColumn(name = "ProductID", nullable = false)
    private Integer productID;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "TotalPrice", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "OrderDate", nullable = false)
    private Date orderDate;

    @Column(name = "waiting_time",nullable = false)
    private Timestamp waitingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "OrderStatus", nullable = false)
    private OrderStatus orderStatus;


    public UnapprovedOrders(Integer orderId, Integer customerID, Integer productID, Integer quantity, BigDecimal totalPrice, Date orderDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customerID = customerID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    public UnapprovedOrders() {}

    public Timestamp getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Timestamp waitingTime) {
        this.waitingTime = waitingTime;
    }


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customer) {
        this.customerID = customer;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer product) {
        this.productID = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }


    public enum OrderStatus {
        Bekliyor,Isleniyor,Tamamlandı
    }
}