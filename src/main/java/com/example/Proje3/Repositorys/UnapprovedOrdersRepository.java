package com.example.Proje3.Repositorys;

import com.example.Proje3.Tablolar.UnapprovedOrders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnapprovedOrdersRepository extends JpaRepository<UnapprovedOrders, Integer> {
    List<UnapprovedOrders> findByCustomerIDAndProductID(Integer CustomerId, Integer ProductId);
}
