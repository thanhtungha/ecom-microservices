package com.be.productservice.repository;

import com.be.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String productName);
}

