package com.be.cartservice.service;

import com.be.cartservice.dto.*;
import com.be.cartservice.exception.RestExceptions;
import com.be.cartservice.mappers.ICartMapper;
import com.be.cartservice.model.Cart;
import com.be.cartservice.model.CartItem;
import com.be.cartservice.repository.ICartRepository;
import com.be.cartservice.service.webclient.AuthClient;
import com.be.cartservice.service.webclient.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartServiceImpl implements ICartService {
    private final ICartRepository repository;
    private final ICartMapper mapper;
    private final AuthClient authClient;
    private final ProductClient productClient;

    @Override
    public CartDTO createCart(String authorizationHeader) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Cart> storedModel = repository.findByOwnerId(
                user.getId());
        if (storedModel.isEmpty()) {
            Cart cart = new Cart();
            cart.setCreateDate(new Date());
            cart.setUpdateDate(new Date());
            cart.setOwnerId(user.getId());
            repository.save(cart);
            storedModel = repository.findByOwnerId(user.getId());
        }
        Cart cart = storedModel.get();

        CartDTO cartDTO = mapper.CartToDTO(cart);
        cartDTO.setOwner(user);
        return cartDTO;
    }

    @Override
    public CartDTO addProduct(String authorizationHeader,
                              RqProductArgs productArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Cart> storedModel = repository.findByOwnerId(user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Cart does not existed!");
        }
        Cart cart = storedModel.get();
        boolean isAdded = false;
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProductId().equals(productArgs.getProductId())) {
                isAdded = true;
                break;
            }
        }
        if (!isAdded) {
            CartItem cartItem = new CartItem();
            cartItem.setCreateDate(new Date());
            cartItem.setUpdateDate(new Date());
            cartItem.setProductId(productArgs.getProductId());
            cart.getCartItems().add(cartItem);
        }
        repository.save(cart);

        return generateCartDTO(cart, user, authorizationHeader);
    }

    @Override
    public CartDTO removeProduct(String authorizationHeader,
                                 RqProductArgs productArgs) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Cart> storedModel = repository.findByOwnerId(user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Cart does not existed!");
        }
        Cart cart = storedModel.get();
        CartItem storedCartItem = null;
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProductId().equals(productArgs.getProductId())) {
                storedCartItem = cartItem;
                break;
            }
        }
        if (storedCartItem != null) {
            cart.getCartItems().remove(storedCartItem);
        }
        repository.save(cart);

        return generateCartDTO(cart, user, authorizationHeader);
    }

    @Override
    public CartDTO getCart(String authorizationHeader) {
        UserDTO user = authClient.verifyToken(authorizationHeader);
        Optional<Cart> storedModel = repository.findByOwnerId(user.getId());
        if (storedModel.isEmpty()) {
            throw new RestExceptions.NotFound("Cart does not existed!");
        }

        Cart cart = storedModel.get();
        return generateCartDTO(cart, user, authorizationHeader);
    }

    private CartDTO generateCartDTO(Cart cart, UserDTO userDTO,
                                    String authorizationHeader) {
        List<CartItem> cartItems = cart.getCartItems();
        CartDTO result = mapper.CartToDTO(cart);
        result.setOwner(userDTO);

        //Get Product List
        if (!cartItems.isEmpty()) {
            List<String> productIds = cartItems.stream()
                    .map(cartItem -> cartItem.getProductId().toString())
                    .toList();
            List<ProductDTO> products = productClient.getListProducts(
                    authorizationHeader, productIds);
            for (CartItemDTO cartItemDTO : result.getCartItems()) {
                UUID productId = cartItemDTO.getProductId();
                products.stream()
                        .filter(productDTO -> productDTO.getId()
                                .equals(productId))
                        .findFirst()
                        .ifPresent(cartItemDTO::setProduct);
            }
        }
        return result;
    }
}
