package com.be.orderservice.service;

import com.be.orderservice.dto.RqCreateArgs;
import com.be.orderservice.dto.RqUpdateArgs;
import com.be.orderservice.dto.OrderDTO;

public interface IOrderService {
    OrderDTO create(String authorizationHeader, RqCreateArgs createArgs);
    OrderDTO update(String authorizationHeader, RqUpdateArgs updateArgs);
    boolean cancel(String authorizationHeader, String orderId);
    OrderDTO getOrder(String authorizationHeader, String orderId);
}
