package com.be.inventoryservice.controller;

import com.be.inventoryservice.dto.*;
import com.be.inventoryservice.exception.BaseException;
import com.be.inventoryservice.exception.RestExceptions;
import com.be.inventoryservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(
            InventoryController.class);
    private final IInventoryService service;

    @GetMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(
                new BaseResponse("Hello! This is Inventory Service."));
    }

    @PostMapping(path = "/add-product")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqProductArgs productArgs) {
        try {
            InventoryDTO inventoryDTO = service.addProduct(authorizationHeader,
                    productArgs);
            return new ResponseEntity<>(inventoryDTO, HttpStatus.OK);
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
            InventoryDTO inventoryDTO = service.removeProduct(
                    authorizationHeader, productArgs);
            return new ResponseEntity<>(inventoryDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> changeQuantity(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqChangeQuantityArgs changeQuantityArgs) {
        try {
            InventoryDTO inventoryDTO = service.changeQuantity(
                    authorizationHeader, changeQuantityArgs);
            return new ResponseEntity<>(inventoryDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @GetMapping(path = "/get-inventory")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInventory(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            InventoryDTO inventoryDTO = service.getInventory(
                    authorizationHeader);
            return new ResponseEntity<>(inventoryDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }
}
