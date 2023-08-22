package com.be.productservice.controller;

import com.be.productservice.dto.*;
import com.be.productservice.exception.BaseException;
import com.be.productservice.exception.RestExceptions;
import com.be.productservice.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/product")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(
            ProductController.class);
    private final IProductService service;

    @GetMapping(path = "/greeting")
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
        try {
            ProductDTO productDTO = service.register(authorizationHeader,
                    registerArgs);
            return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> update(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqUpdateArgs updateArgs) {
        try {
            ProductDTO productDTO = service.update(authorizationHeader,
                    updateArgs);
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/remove-product")
    @ResponseStatus(HttpStatus.OK)
    public void remove(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RqProductArgs productArgs) {
        try {
            service.remove(authorizationHeader, productArgs.getId());
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @GetMapping(path = "/get-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProduct(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("id") String id) {
        try {
            ProductDTO productDTO = service.getProduct(authorizationHeader, id);
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/add-review")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addReview(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqAddReviewArgs reviewArgs) {
        try {
            ProductDTO productDTO = service.addReview(authorizationHeader,
                    reviewArgs);
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @GetMapping(path = "/list-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListProducts> getListProduct(
            @NotNull @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("ids") List<String> ids) {
        try {
            ListProducts products = service.getListProduct(authorizationHeader, ids);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }
}
