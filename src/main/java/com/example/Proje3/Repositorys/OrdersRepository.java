package com.example.Proje3.Repositorys;

import com.example.Proje3.Tablolar.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByCustomerIDAndProductID(Integer CustomerId, Integer ProductId);
}
