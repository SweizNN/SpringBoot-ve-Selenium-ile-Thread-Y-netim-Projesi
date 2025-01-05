package com.example.Proje3.Repositorys;

import com.example.Proje3.Tablolar.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    public Products findByProductId(int productId);
}
