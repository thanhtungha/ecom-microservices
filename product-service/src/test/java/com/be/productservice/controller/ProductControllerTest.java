package com.be.productservice.controller;

import com.be.productservice.AbstractContainerBaseTest;
import com.be.productservice.dto.*;
import com.be.productservice.model.Product;
import com.be.productservice.model.Review;
import com.be.productservice.repository.IProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest extends AbstractContainerBaseTest {


    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public IProductRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        if (userDTO == null) {
            createTestUser();
        }
    }

    @Test
    @Order(0)
    void register() throws Exception {
        RqRegisterArgs registerArgs = new RqRegisterArgs("productName", 100);
        String reqString = objectMapper.writeValueAsString(registerArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        PRODUCT_BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse()
                .getContentAsString();
        ProductDTO responseProduct = objectMapper.readValue(responseBody,
                ProductDTO.class);
        assertEquals(registerArgs.getName(), responseProduct.getName());
        assertEquals(registerArgs.getPrice(), responseProduct.getPrice());
        assertEquals(userDTO.getId(), responseProduct.getOwnerId());
        assertEquals(userDTO.getId(),
                responseProduct.getOwner()
                        .getId());
        testProduct = responseProduct;

        //check db
        Optional<Product> createdProduct =
                repository.findByName(registerArgs.getName());
        if (createdProduct.isPresent()) {
            Product product = createdProduct.get();
            assertEquals(registerArgs.getName(), product.getName());
            assertEquals(registerArgs.getPrice(), product.getPrice());
            assertEquals(userDTO.getId(), product.getOwnerId());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(1)
    void update() throws Exception {
        RqUpdateArgs updateArgs = new RqUpdateArgs(testProduct.getId()
                .toString(), "newName", 200, 150);
        String reqString = objectMapper.writeValueAsString(updateArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        PRODUCT_BASE_API + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse()
                .getContentAsString();
        ProductDTO responseProduct = objectMapper.readValue(responseBody,
                ProductDTO.class);
        assertEquals(updateArgs.getName(), responseProduct.getName());
        assertEquals(updateArgs.getPrice(), responseProduct.getPrice());
        assertEquals(updateArgs.getQuantity(), responseProduct.getQuantity());

        //check db
        Optional<Product> createdProduct =
                repository.findById(testProduct.getId());
        if (createdProduct.isPresent()) {
            Product product = createdProduct.get();
            assertEquals(updateArgs.getName(), product.getName());
            assertEquals(updateArgs.getPrice(), product.getPrice());
            assertEquals(updateArgs.getQuantity(), product.getQuantity());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(3)
    void remove() throws Exception {
        RqProductArgs productArgs = new RqProductArgs(testProduct.getId()
                .toString());
        String reqString = objectMapper.writeValueAsString(productArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        PRODUCT_BASE_API + "/remove-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check db
        Optional<Product> createdProduct =
                repository.findById(testProduct.getId());
        if (createdProduct.isPresent()) {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void getProduct() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        PRODUCT_BASE_API + "/get-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .param("id",
                        testProduct.getId()
                                .toString());
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse()
                .getContentAsString();
        ProductDTO responseProduct = objectMapper.readValue(responseBody,
                ProductDTO.class);

        //check db
        Optional<Product> createdProduct =
                repository.findById(testProduct.getId());
        if (createdProduct.isPresent()) {
            Product product = createdProduct.get();
            assertEquals(responseProduct.getName(), product.getName());
            assertEquals(responseProduct.getPrice(), product.getPrice());
            assertEquals(responseProduct.getQuantity(), product.getQuantity());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void addReview() throws Exception {
        RqAddReviewArgs reviewArgs = new RqAddReviewArgs(testProduct.getId()
                .toString(), 5, "review text");
        String reqString = objectMapper.writeValueAsString(reviewArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        PRODUCT_BASE_API + "/add-review")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse()
                .getContentAsString();
        ProductDTO responseProduct = objectMapper.readValue(responseBody,
                ProductDTO.class);

        List<ReviewDTO> reviews = responseProduct.getReviews();
        if (!reviews.isEmpty()) {
            ReviewDTO review = reviews.get(0);
            assertEquals(reviewArgs.getRating(), review.getRate());
            assertEquals(reviewArgs.getReview(), review.getReview());
            assertEquals(userDTO.getId(), review.getReviewerId());
            assertEquals(userDTO.getId(),
                    review.getReviewer()
                            .getId());
        } else {
            fail("test case failed!");
        }

        //check DB
        Optional<Product> createdProduct =
                repository.findById(testProduct.getId());
        if (createdProduct.isPresent()) {
            Product dbProduct = createdProduct.get();
            List<Review> dbReviews = new ArrayList<>(dbProduct.getReviews());
            if (!dbReviews.isEmpty()) {
                Review review = dbReviews.get(0);
                assertEquals(reviewArgs.getRating(), review.getRate());
                assertEquals(reviewArgs.getReview(), review.getReview());
                assertEquals(userDTO.getId(), review.getReviewerId());
                return;
            }
        }
        fail("test case failed!");
    }
}