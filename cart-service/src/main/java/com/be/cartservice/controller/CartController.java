package com.be.cartservice.controller;

import com.be.cartservice.dto.BaseResponse;
import com.be.cartservice.dto.CartDTO;
import com.be.cartservice.dto.RqProductArgs;
import com.be.cartservice.exception.BaseException;
import com.be.cartservice.exception.RestExceptions;
import com.be.cartservice.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(
            CartController.class);
    private final ICartService service;

    @GetMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(
                new BaseResponse("Hello! This is Cart Service."));
    }

    @PostMapping(path = "/create-cart")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createCart(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            CartDTO cartDTO = service.createCart(authorizationHeader);
            return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/add-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqProductArgs productArgs) {
        try {
            CartDTO cartDTO = service.addProduct(authorizationHeader,
                    productArgs);
            return new ResponseEntity<>(cartDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/remove-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> removeProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqProductArgs productArgs) {
        try {
            CartDTO cartDTO = service.removeProduct(authorizationHeader,
                    productArgs);
            return new ResponseEntity<>(cartDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @GetMapping(path = "/get-cart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCart(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            CartDTO cartDTO = service.getCart(authorizationHeader);
            return new ResponseEntity<>(cartDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }
}
