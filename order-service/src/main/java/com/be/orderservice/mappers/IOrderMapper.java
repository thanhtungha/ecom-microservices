package com.be.orderservice.mappers;

import com.be.orderservice.dto.OrderDTO;
import com.be.orderservice.dto.OrderItemDTO;
import com.be.orderservice.model.Order;
import com.be.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderMapper {
    @Mapping(source = "id", target = "id")
    OrderDTO OrderToDTO(Order order);
}
