package com.be.productservice.controller;

import com.be.authservice.controller.AuthController;
import com.be.productservice.AbstractContainerBaseTest;
import com.be.productservice.dto.ProductDTO;
import com.be.productservice.dto.RqRegisterArgs;
import com.be.productservice.model.Product;
import com.be.productservice.repository.IProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

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

    @BeforeEach
    void setUp() {
        if (userDTO == null) {
            createTestUser();
        }
    }

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