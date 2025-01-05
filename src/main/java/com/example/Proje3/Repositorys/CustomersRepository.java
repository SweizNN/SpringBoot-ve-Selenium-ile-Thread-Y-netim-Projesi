package com.example.Proje3.Repositorys;

import com.example.Proje3.Tablolar.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomersRepository extends JpaRepository<Customers, Long> {
    Customers findByCustomerName(String CustomerName);
    Customers findByCustomerId(long id);

}
