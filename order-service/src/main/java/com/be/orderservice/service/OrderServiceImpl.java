package com.be.orderservice.service;

import com.be.orderservice.dto.*;
import com.be.orderservice.exception.RestExceptions;
import com.be.orderservice.mappers.IOrderMapper;
import com.be.orderservice.model.Order;
import com.be.orderservice.model.OrderItem;
import com.be.orderservice.repository.IOrderRepository;
import com.be.orderservice.service.webclient.AuthClient;
import com.be.orderservice.service.webclient.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
    private final IOrderRepository repository;
    private final IOrderMapper mapper;
    private final AuthClient authClient;
    private final ProductClient productClient;

    @Override
    public OrderDTO create(String authorizationHeader,
                           RqCreateArgs createArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        List<OrderItem> orderItems = getOrderItems(createArgs.getOrderItems());
        Order order = saveOrder(new Order(), orderItems, user.getId());
        return generateOrderDTO(order, user, authorizationHeader, orderItems);
    }

    @Override
    public OrderDTO update(String authorizationHeader,
                           RqUpdateArgs updateArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        List<OrderItem> orderItems = getOrderItems(updateArgs.getOrderItems());
        Optional<Order> storedModel =
                repository.findByIdAndOwnerId(updateArgs.getOrderId(),
                user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Order does not existed!");
        }
        Order order = saveOrder(storedModel.get(), orderItems, user.getId());
        return generateOrderDTO(order, user, authorizationHeader, orderItems);
    }

    @Override
    public boolean cancel(String authorizationHeader, String orderId) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Order> storedModel =
                repository.findByIdAndOwnerId(UUID.fromString(
                orderId), user.getId());
        if (storedModel.isPresent()) {
            repository.delete(storedModel.get());
            return true;
        } else {
            throw new RestExceptions.NotFound("Order does not existed!");
        }
    }

    @Override
    public OrderDTO getOrder(String authorizationHeader, String orderId) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Order> storedModel =
                repository.findByIdAndOwnerId(UUID.fromString(
                orderId), user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Order does not existed!");
        }

        Order order = storedModel.get();
        return generateOrderDTO(order,
                user,
                authorizationHeader,
                order.getOrderItems());
    }

    private List<OrderItem> getOrderItems(List<OrderItemDTO> orderItemDTOS) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItemDTOS.forEach(dto -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setCreateDate(new Date());
            orderItem.setUpdateDate(new Date());
            orderItem.setProductId(dto.getProductId());
            orderItem.setQuantity(dto.getQuantity());
            orderItems.add(orderItem);
        });
        return orderItems;
    }

    private Order saveOrder(Order order, List<OrderItem> orderItems,
                            UUID ownerId) {
        order.setUpdateDate(new Date());
        order.getOrderItems()
                .clear();
        order.getOrderItems()
                .addAll(orderItems);
        order.setOwnerId(ownerId);
        repository.save(order);
        return order;
    }

    private OrderDTO generateOrderDTO(Order order, UserDTO userDTO,
                                      String authorizationHeader,
                                      List<OrderItem> orderItems) {
        OrderDTO result = mapper.OrderToDTO(order);
        result.setOwner(userDTO);

        //Get Product List
        List<String> productIds = orderItems.stream()
                .map(orderItem -> orderItem.getProductId()
                        .toString())
                .toList();
        List<ProductDTO> products = productClient.getListProducts(
                authorizationHeader,
                productIds);
        for (OrderItemDTO orderItemDTO : result.getOrderItems()) {
            UUID productId = orderItemDTO.getProductId();
            products.stream()
                    .filter(productDTO -> productDTO.getId()
                            .equals(productId))
                    .findFirst()
                    .ifPresent(orderItemDTO::setProduct);
        }

        return result;
    }
}
