package com.example.Proje3.Tablolar;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.sql.Date;

@Entity
@Table(name = "logs")
public class Logs {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Integer logId;


    @JoinColumn(name = "CustomerID", nullable = false)
    private Integer customerID;


    @Column(name = "LogDate", nullable = false)
    private Date logDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "LogType", nullable = false)
    private LogType logType;

    @Column(name = "LogDetails", nullable = false)
    private String logDetails;

    @Column(name = "ProductName",nullable = false)
    private String productName;

    @Column(name = "Miktar",nullable = false)
    private String miktar;

    @Enumerated(EnumType.STRING)
    @Column(name = "CustomerType", nullable = false)
    private Customers.CustomerType customerType;


    public Customers.CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Customers.CustomerType customerType) {
        this.customerType = customerType;
    }

    public Logs() {}


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMiktar() {
        return miktar;
    }

    public void setMiktar(String miktar) {
        this.miktar = miktar;
    }

    public enum LogType {
        Hata,Bilgilendirme,Uyarı
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customer) {
        this.customerID = customer;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public String getLogDetails() {
        return logDetails;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }


}