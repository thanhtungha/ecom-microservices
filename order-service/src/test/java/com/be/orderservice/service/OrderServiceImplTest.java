package com.be.orderservice.service;

import com.be.orderservice.AbstractContainerBaseTest;
import com.be.orderservice.dto.OrderDTO;
import com.be.orderservice.dto.OrderItemDTO;
import com.be.orderservice.dto.RqCreateArgs;
import com.be.orderservice.dto.RqUpdateArgs;
import com.be.orderservice.model.Order;
import com.be.orderservice.model.OrderItem;
import com.be.orderservice.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceImplTest extends AbstractContainerBaseTest {
    @Autowired
    public IOrderService service;
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
    void create() {
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

        OrderDTO response = service.create(authorizationHeader, createArgs);
        Optional<Order> createdOrder = repository.findById(response.getId());
        if (createdOrder.isPresent()) {
            Order dbOrder = createdOrder.get();
            List<OrderItem> orderItemList = dbOrder.getOrderItems();
            assertEquals(orderItems.size(), orderItemList.size());

            assertEquals(orderItems.size(),
                    response.getOrderItems()
                            .size());
            orderDTO = response;
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void update() {
        List<OrderItemDTO> orderItems = new ArrayList<>();
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setProductId(productDTO1.getId());
        orderItemDTO1.setQuantity(77);
        orderItems.add(orderItemDTO1);

        OrderItemDTO orderItemDTO2 = new OrderItemDTO();
        orderItemDTO2.setProductId(productDTO2.getId());
        orderItemDTO2.setQuantity(36);
        orderItems.add(orderItemDTO2);

        RqUpdateArgs updateArgs = new RqUpdateArgs(orderDTO.getId(),
                orderItems);

        OrderDTO response = service.update(authorizationHeader, updateArgs);
        Optional<Order> createdOrder = repository.findById(orderDTO.getId());
        if (createdOrder.isPresent()) {
            Order dbOrder = createdOrder.get();
            List<OrderItem> orderItemList = dbOrder.getOrderItems();
            //db
            assertEquals(orderItems.size(), orderItemList.size());
            assertEquals(orderItems.get(0)
                            .getProductId(),
                    orderItemList.get(0)
                            .getProductId());
            assertEquals(orderItems.get(0)
                            .getQuantity(),
                    orderItemList.get(0)
                            .getQuantity());

            //response
            assertEquals(orderItems.size(),
                    response.getOrderItems()
                            .size());
            assertEquals(orderItems.get(0)
                            .getProductId(),
                    response.getOrderItems()
                            .get(0)
                            .getProductId());
            assertEquals(orderItems.get(0)
                            .getQuantity(),
                    response.getOrderItems()
                            .get(0)
                            .getQuantity());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void cancel() {
        boolean result = service.cancel(authorizationHeader,
                orderDTO.getId()
                        .toString());
        Optional<Order> createdOrder = repository.findById(orderDTO.getId());
        if (createdOrder.isPresent() || !result) {
            fail("test case failed!");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void getOrder() {
        OrderDTO response = service.getOrder(authorizationHeader,
                orderDTO.getId()
                        .toString());
        Optional<Order> createdOrder = repository.findById(orderDTO.getId());
        if (createdOrder.isPresent()) {
            Order dbOrder = createdOrder.get();
            List<OrderItem> orderItemList = dbOrder.getOrderItems();
            assertEquals(orderDTO.getOrderItems()
                    .size(), orderItemList.size());

            assertEquals(orderDTO.getOrderItems()
                            .size(),
                    response.getOrderItems()
                            .size());
        } else {
            fail("test case failed!");
        }
    }
}