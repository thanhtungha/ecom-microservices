package com.be.cartservice.controller;

import com.be.cartservice.AbstractContainerBaseTest;
import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.dto.RqProductArgs;
import com.be.cartservice.model.Cart;
import com.be.cartservice.model.CartItem;
import com.be.cartservice.repository.ICartRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerTest extends AbstractContainerBaseTest {
    @Autowired
    public MockMvc mockMvc;
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
    void createCart() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        CART_BASE_API + "/create-cart")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        CartDTO responseProduct = objectMapper.readValue(responseBody,
                CartDTO.class);
        assertEquals(userDTO.getId(), responseProduct.getOwnerId());
        assertEquals(userDTO.getId(), responseProduct.getOwner().getId());
        cartDTO = responseProduct;

        //check db
        Optional<Cart> cartOptional = repository.findById(
                responseProduct.getId());
        if (cartOptional.isPresent()) {
            assertEquals(userDTO.getId(), cartOptional.get().getOwnerId());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(1)
    void addProduct() throws Exception {
        RqProductArgs productArgs = new RqProductArgs(cartDTO.getId(),
                productDTO1.getId());

        String reqString = objectMapper.writeValueAsString(productArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        CART_BASE_API + "/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        CartDTO responseProduct = objectMapper.readValue(responseBody,
                CartDTO.class);
        assertEquals(1, responseProduct.getCartItems().size());
        cartDTO = responseProduct;

        //check db
        Optional<Cart> cartOptional = repository.findById(
                responseProduct.getId());
        if (cartOptional.isPresent()) {
            Cart dbCart = cartOptional.get();
            List<CartItem> cartItemList = dbCart.getCartItems();
            assertEquals(1, cartItemList.size());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(3)
    void removeProduct() throws Exception {
        RqProductArgs productArgs = new RqProductArgs(cartDTO.getId(),
                productDTO1.getId());

        String reqString = objectMapper.writeValueAsString(productArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        CART_BASE_API + "/remove-product")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        CartDTO responseProduct = objectMapper.readValue(responseBody,
                CartDTO.class);
        assertEquals(0, responseProduct.getCartItems().size());
        cartDTO = responseProduct;

        //check db
        Optional<Cart> cartOptional = repository.findById(
                responseProduct.getId());
        if (cartOptional.isPresent()) {
            Cart dbCart = cartOptional.get();
            List<CartItem> cartItemList = dbCart.getCartItems();
            assertEquals(0, cartItemList.size());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void getCart() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        CART_BASE_API + "/get-cart")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .param("id", cartDTO.getId().toString());
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        CartDTO responseProduct = objectMapper.readValue(responseBody,
                CartDTO.class);
        assertEquals(1, responseProduct.getCartItems().size());
        cartDTO = responseProduct;

        //check db
        Optional<Cart> cartOptional = repository.findById(
                responseProduct.getId());
        if (cartOptional.isPresent()) {
            Cart dbCart = cartOptional.get();
            List<CartItem> cartItemList = dbCart.getCartItems();
            assertEquals(1, cartItemList.size());
            return;
        }
        fail("test case failed!");
    }
}