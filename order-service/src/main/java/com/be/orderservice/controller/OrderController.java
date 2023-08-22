package com.be.orderservice.controller;

import com.be.orderservice.dto.*;
import com.be.orderservice.exception.BaseException;
import com.be.orderservice.exception.RestExceptions;
import com.be.orderservice.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/order")
public class OrderController {
    private static final Logger logger =
            LoggerFactory.getLogger(OrderController.class);
    private final IOrderService service;

    @GetMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(new BaseResponse(
                "Hello! This is Order Service."));
    }

    @PostMapping(path = "/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqCreateArgs orderArgs) {
        try {
            OrderDTO orderDTO = service.create(authorizationHeader, orderArgs);
            return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/update-order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqUpdateArgs orderArgs) {
        try {
            OrderDTO orderDTO = service.update(authorizationHeader, orderArgs);
            return new ResponseEntity<>(orderDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/cancel-order")
    @ResponseStatus(HttpStatus.OK)
    public void cancelOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqCancelArgs orderArgs) {
        try {
            service.cancel(authorizationHeader, orderArgs.getId());
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @GetMapping(path = "/get-order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("id") String id) {
        try {
            OrderDTO order = service.getOrder(authorizationHeader, id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }
}
