package com.be.cartservice.mappers;

import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICartMapper {
    @Mapping(source = "id", target = "id")
    CartDTO CartToDTO(Cart cart);
}
