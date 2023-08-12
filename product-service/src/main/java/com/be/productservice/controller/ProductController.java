package com.be.productservice.controller;

import com.be.productservice.dto.*;
import com.be.productservice.exception.BaseException;
import com.be.productservice.exception.RestExceptions;
import com.be.productservice.mappers.IProductMapper;
import com.be.productservice.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/product")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(
            ProductController.class);
    private final IProductService service;
    private final IProductMapper mapper;

    @PostMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(new BaseResponse(
                "Hello! This is Product Service."));
    }

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RqRegisterArgs registerArgs) {
        throw new RestExceptions.NotImplemented();
    }

    @PostMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> update(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqUpdateArgs updateArgs) {
        throw new RestExceptions.NotImplemented();
    }

    @PostMapping(path = "/remove-product")
    @ResponseStatus(HttpStatus.OK)
    public void remove(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RqProductArgs productArgs) {
        throw new RestExceptions.NotImplemented();
    }

    @PostMapping(path = "/get-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProduct(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RqProductArgs productArgs) {
        throw new RestExceptions.NotImplemented();
    }

    @PostMapping(path = "/add-review")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addReview(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqAddReviewArgs reviewArgs) {
        throw new RestExceptions.NotImplemented();
    }
}
