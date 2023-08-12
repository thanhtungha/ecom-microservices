package com.be.productservice.controller;

import com.be.productservice.AbstractContainerBaseTest;
import com.be.productservice.repository.IProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest extends AbstractContainerBaseTest {
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public IProductRepository repository;
    private static String BASE_API = "/api/product";

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