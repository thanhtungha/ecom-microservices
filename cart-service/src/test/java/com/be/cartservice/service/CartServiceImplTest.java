package com.be.cartservice.service;

import com.be.cartservice.AbstractContainerBaseTest;
import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.dto.CartItemDTO;
import com.be.cartservice.dto.RqProductArgs;
import com.be.cartservice.model.Cart;
import com.be.cartservice.model.CartItem;
import com.be.cartservice.repository.ICartRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartServiceImplTest extends AbstractContainerBaseTest {
    @Autowired
    public ICartService service;
    @Autowired
    public ICartRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        if (userDTO == null) {
            createTestUser();
        }
    }

    @Test
    @Order(0)
    void createCart() {
        CartDTO response = service.createCart(authorizationHeader);
        Optional<Cart> createdCart = repository.findById(response.getId());
        if (createdCart.isPresent()) {
            Cart dbCart = createdCart.get();
            assertEquals(response.getId(), dbCart.getId());
            cartDTO = response;
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void addProduct() {
        RqProductArgs productArgs = new RqProductArgs(cartDTO.getId(),
                productDTO1.getId());

        CartDTO response = service.addProduct(authorizationHeader, productArgs);
        Optional<Cart> createdCart = repository.findById(response.getId());
        if (createdCart.isPresent()) {
            Cart dbCart = createdCart.get();
            List<CartItem> cartItemList = dbCart.getCartItems();
            //check db
            assertEquals(1, cartItemList.size());

            //check response
            assertEquals(1, response.getCartItems().size());
            cartDTO = response;
        } else {
            fail("test case failed!");
        }

        productArgs = new RqProductArgs(cartDTO.getId(), productDTO2.getId());
        cartDTO = service.addProduct(authorizationHeader, productArgs);
    }

    @Test
    @Order(3)
    void removeProduct() {
        RqProductArgs productArgs = new RqProductArgs(cartDTO.getId(),
                productDTO1.getId());

        CartDTO response = service.removeProduct(authorizationHeader,
                productArgs);
        Optional<Cart> createdCart = repository.findById(response.getId());
        if (createdCart.isPresent()) {
            Cart dbCart = createdCart.get();
            List<CartItem> cartItems = dbCart.getCartItems();
            //check db
            assertEquals(1, cartItems.size());

            //check response
            assertEquals(1, response.getCartItems().size());
            cartDTO = response;
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void getCart() {
        CartDTO response = service.getCart(authorizationHeader,
                cartDTO.getId().toString());
        Optional<Cart> createdCart = repository.findById(cartDTO.getId());
        if (createdCart.isPresent()) {
            Cart dbCart = createdCart.get();
            assertEquals(dbCart.getCartItems().size(),
                    response.getCartItems().size());
        } else {
            fail("test case failed!");
        }
    }
}