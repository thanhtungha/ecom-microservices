package com.be.orderservice.controller;

import com.be.orderservice.AbstractContainerBaseTest;
import com.be.orderservice.dto.*;
import com.be.orderservice.model.Order;
import com.be.orderservice.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest extends AbstractContainerBaseTest {
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public IOrderRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        if (userDTO == null) {
            createTestUser();
        }
    }

    @Test
    @org.junit.jupiter.api.Order(0)
    void createOrder() throws Exception {
        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setProductId(productDTO1.getId());
        orderItemDTO1.setQuantity(13);
        orderItems.add(orderItemDTO1);

        OrderItemDTO orderItemDTO2 = new OrderItemDTO();
        orderItemDTO2.setProductId(productDTO2.getId());
        orderItemDTO2.setQuantity(17);
        orderItems.add(orderItemDTO2);

        RqCreateArgs createArgs = new RqCreateArgs(orderItems);

        String reqString = objectMapper.writeValueAsString(createArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        ORDER_BASE_API + "/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderDTO responseProduct = objectMapper.readValue(responseBody,
                OrderDTO.class);
        assertEquals(createArgs.getOrderItems().size(),
                responseProduct.getOrderItems().size());
        assertEquals(userDTO.getId(), responseProduct.getOwnerId());
        assertEquals(userDTO.getId(), responseProduct.getOwner().getId());
        orderDTO = responseProduct;

        //check db
        Optional<Order> orderOptional = repository.findById(orderDTO.getId());
        if (orderOptional.isPresent()) {
            Order dbOrder = orderOptional.get();
            if (!dbOrder.getOrderItems().isEmpty()) {
                assertEquals(createArgs.getOrderItems().size(),
                        dbOrder.getOrderItems().size());
                assertEquals(userDTO.getId(), dbOrder.getOwnerId());
                return;
            }
        }
        fail("test case failed!");
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void updateOrder() throws Exception {
        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setProductId(productDTO1.getId());
        orderItemDTO1.setQuantity(100);
        orderItems.add(orderItemDTO1);

        RqUpdateArgs updateArgs = new RqUpdateArgs(orderDTO.getId(),
                orderItems);
        String reqString = objectMapper.writeValueAsString(updateArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        ORDER_BASE_API + "/update-order")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderDTO responseProduct = objectMapper.readValue(responseBody,
                OrderDTO.class);
        assertEquals(updateArgs.getOrderItems().size(),
                responseProduct.getOrderItems().size());
        assertEquals(updateArgs.getOrderItems().get(0).getQuantity(),
                responseProduct.getOrderItems().get(0).getQuantity());
        orderDTO = responseProduct;

        //check db
        Optional<Order> orderOptional = repository.findById(orderDTO.getId());
        if (orderOptional.isPresent()) {
            Order dbOrder = orderOptional.get();
            if (!dbOrder.getOrderItems().isEmpty()) {
                assertEquals(updateArgs.getOrderItems().size(),
                        dbOrder.getOrderItems().size());
                assertEquals(updateArgs.getOrderItems().get(0).getQuantity(),
                        dbOrder.getOrderItems().get(0).getQuantity());
                return;
            }
        }
        fail("test case failed!");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void cancelOrder() throws Exception {
        RqCancelArgs args = new RqCancelArgs(orderDTO.getId().toString());
        String reqString = objectMapper.writeValueAsString(args);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        ORDER_BASE_API + "/cancel-order")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .content(reqString);
        mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

        //check db
        Optional<Order> orderOptional = repository.findById(orderDTO.getId());
        if (orderOptional.isPresent()) {
            fail("test case failed!");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void getOrder() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        ORDER_BASE_API + "/get-order")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .param("id", orderDTO.getId().toString());
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderDTO responseProduct = objectMapper.readValue(responseBody,
                OrderDTO.class);
        assertEquals(orderDTO.getOrderItems().size(),
                responseProduct.getOrderItems().size());
        assertEquals(orderDTO.getOrderItems().get(0).getQuantity(),
                responseProduct.getOrderItems().get(0).getQuantity());

        //check db
        Optional<Order> orderOptional = repository.findById(orderDTO.getId());
        if (orderOptional.isPresent()) {
            Order dbOrder = orderOptional.get();
            if (!dbOrder.getOrderItems().isEmpty()) {
                assertEquals(orderDTO.getOrderItems().size(),
                        dbOrder.getOrderItems().size());
                assertEquals(orderDTO.getOrderItems().get(0).getQuantity(),
                        dbOrder.getOrderItems().get(0).getQuantity());
                return;
            }
        }
        fail("test case failed!");
    }

}