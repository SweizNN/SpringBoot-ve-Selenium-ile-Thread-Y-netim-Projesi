package com.example.Proje3.Controllers;

import com.example.Proje3.Repositorys.CustomersRepository;
import com.example.Proje3.Tablolar.Customers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Component
public class InitializationService {

    @Autowired
    CustomersRepository customersRepository;

    private boolean isInitialized = false; // Müşteri oluşturulup oluşturulmadığını kontrol eden bayrak

    public synchronized void initializeCustomers() {
        if (isInitialized) {
            return; // Zaten müşteriler oluşturulmuş, tekrar oluşturmayalım
        }

        Random random = new Random();
        List<Customers> customersList = new ArrayList<>();

        // İlk olarak 2 Premium müşteri ekleyelim
        for (int i = 0; i < 2; i++) {
            customersList.add(createCustomer("PremiumCustomer" + (i + 1), "Premium"));
        }
        // Kalan müşteri sayısını dolduruyoruz
        for (int i = 2; i < random.nextInt(6) + 5; i++) {
            customersList.add(createCustomer("Customer" + (i + 1), "Normal"));
        }

        customersRepository.saveAll(customersList);

        isInitialized = true;
    }

    private Customers createCustomer(String name, String type) {
        Random random = new Random();
        Customers customer = new Customers();
        customer.setCustomerName(name);
        customer.setCustomerType(Customers.CustomerType.valueOf(type));
        customer.setBudget(BigDecimal.valueOf(random.nextInt(2501) + 500)); // Bütçe 500-3000 TL arasında
        customer.setTotalSpent(BigDecimal.ZERO); // Başlangıçta harcama 0
        if(Objects.equals(customer.getCustomerType().toString(), "Premium")){
            customer.setPriorityScore(15);
        } else {
            customer.setPriorityScore(10);
        }
        return customer;
    }


    public List<String> getCustomerNames() {
        return customersRepository.findAll().stream().map(Customers::getCustomerName).toList();
    }

}