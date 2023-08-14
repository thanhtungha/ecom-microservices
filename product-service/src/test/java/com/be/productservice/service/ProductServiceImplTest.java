package com.be.productservice.service;

import com.be.productservice.AbstractContainerBaseTest;
import com.be.productservice.dto.*;
import com.be.productservice.model.Product;
import com.be.productservice.model.Review;
import com.be.productservice.repository.IProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceImplTest extends AbstractContainerBaseTest {

    @Autowired
    public IProductService service;
    @Autowired
    public IProductRepository repository;

    @BeforeEach
    void setUp() {
        if (userDTO == null) {
            createTestUser();
        }
    }

    @Test
    @Order(0)
    void register() {
        RqRegisterArgs registerArgs = new RqRegisterArgs("testProduct", 300);
        ProductDTO responseProduct = service.register(authorizationHeader,
                registerArgs);
        Optional<Product> createdProduct = repository.findByName("testProduct");

        if (createdProduct.isPresent()) {
            Product dbProduct = createdProduct.get();
            assertEquals(registerArgs.getName(), dbProduct.getName());
            assertEquals(registerArgs.getPrice(), dbProduct.getPrice());
            assertEquals(userDTO.getId(), dbProduct.getOwnerId());

            assertEquals(registerArgs.getName(), responseProduct.getName());
            assertEquals(registerArgs.getPrice(), responseProduct.getPrice());
            assertEquals(userDTO.getId(), responseProduct.getOwnerId());
            assertEquals(userDTO.getId(),
                    responseProduct.getOwner()
                            .getId());
            testProduct = responseProduct;
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void update() {
        RqUpdateArgs updateArgs = new RqUpdateArgs(testProduct.getId()
                .toString(), "updatedName", 300, 10);
        ProductDTO responseProduct = service.update(authorizationHeader,
                updateArgs);
        Optional<Product> createdProduct = repository.findByName("updatedName");

        if (createdProduct.isPresent()) {
            Product dbProduct = createdProduct.get();
            assertEquals(updateArgs.getName(), dbProduct.getName());
            assertEquals(updateArgs.getQuantity(), dbProduct.getQuantity());
            assertEquals(updateArgs.getPrice(), dbProduct.getPrice());

            assertEquals(updateArgs.getName(), responseProduct.getName());
            assertEquals(updateArgs.getQuantity(),
                    responseProduct.getQuantity());
            assertEquals(updateArgs.getPrice(), responseProduct.getPrice());

            updateArgs.setName("testProduct");
            updateArgs.setPrice(0);
            updateArgs.setQuantity(0);
            service.update(authorizationHeader, updateArgs);
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(2)
    void remove() {
        boolean result = service.remove(authorizationHeader,
                testProduct.getId()
                        .toString());
        Optional<Product> removedProduct = repository.findByName("testProduct");

        if (removedProduct.isPresent() || !result) {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void getProduct() {
        ProductDTO responseProduct = service.getProduct(authorizationHeader,
                testProduct.getId()
                        .toString());
        Optional<Product> createdProduct = repository.findByName("testProduct");

        if (createdProduct.isPresent()) {
            Product dbProduct = createdProduct.get();
            assertEquals(dbProduct.getName(), responseProduct.getName());
            assertEquals(dbProduct.getQuantity(),
                    responseProduct.getQuantity());
            assertEquals(dbProduct.getPrice(), responseProduct.getPrice());
            assertEquals(dbProduct.getRating(), responseProduct.getRating());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void addReview() {
        RqAddReviewArgs rateArgs = new RqAddReviewArgs(testProduct.getId()
                .toString(), 5, "review text");

        ProductDTO responseProduct = service.addReview(authorizationHeader,
                rateArgs);
        List<ReviewDTO> reviews = responseProduct.getReviews();
        //response test
        if (!reviews.isEmpty()) {
            ReviewDTO review = reviews.get(0);
            assertEquals(rateArgs.getRating(), review.getRate());
            assertEquals(rateArgs.getReview(), review.getReview());
            assertEquals(userDTO.getId(), review.getReviewerId());
            assertEquals(userDTO.getId(),
                    review.getReviewer()
                            .getId());
        } else {
            fail("test case failed!");
        }

        //DB test
        Optional<Product> createdProduct = repository.findByName("testProduct");
        if (createdProduct.isPresent()) {
            Product dbProduct = createdProduct.get();
            List<Review> dbReviews = new ArrayList<>(dbProduct.getReviews());
            if (!dbReviews.isEmpty()) {
                Review review = dbReviews.get(0);
                assertEquals(rateArgs.getRating(), review.getRate());
                assertEquals(rateArgs.getReview(), review.getReview());
                assertEquals(userDTO.getId(), review.getReviewerId());
                return;
            }
        }
        fail("test case failed!");
    }
}