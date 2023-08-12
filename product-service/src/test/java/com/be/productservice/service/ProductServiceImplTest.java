package com.be.productservice.service;

import com.be.productservice.AbstractContainerBaseTest;
import com.be.productservice.repository.IProductRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceImplTest extends AbstractContainerBaseTest {
    @Autowired
    public IProductService service;
    @Autowired
    public IProductRepository repository;

    @Test
    void register() {
    }

    @Test
    void update() {
    }

    @Test
    void remove() {
    }

    @Test
    void getProduct() {
    }

    @Test
    void addReview() {
    }
}