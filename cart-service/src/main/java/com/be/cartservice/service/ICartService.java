package com.be.cartservice.service;

import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.dto.RqProductArgs;

public interface ICartService {
    CartDTO createCart(String authorizationHeader);
    CartDTO addProduct(String authorizationHeader, RqProductArgs productArgs);
    CartDTO removeProduct(String authorizationHeader, RqProductArgs productArgs);
    CartDTO getCart(String authorizationHeader);
}
