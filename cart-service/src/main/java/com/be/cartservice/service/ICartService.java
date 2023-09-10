package com.be.cartservice.service;

import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.dto.RqProductArgs;
import com.be.cartservice.dto.UserDTO;

public interface ICartService {
    CartDTO createCart(UserDTO user);
    CartDTO addProduct(String authorizationHeader, RqProductArgs productArgs);
    CartDTO removeProduct(String authorizationHeader, RqProductArgs productArgs);
    CartDTO getCart(String authorizationHeader);
}
