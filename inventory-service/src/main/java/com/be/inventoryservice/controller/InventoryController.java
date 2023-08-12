package com.be.inventoryservice.controller;

import com.be.inventoryservice.dto.BaseResponse;
import com.be.inventoryservice.mappers.IInventoryMapper;
import com.be.inventoryservice.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/inventory")
public class InventoryController {
    private static final Logger logger =
            LoggerFactory.getLogger(InventoryController.class);
    private final IInventoryService service;
    private final IInventoryMapper mapper;

    @PostMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(new BaseResponse(
                "Hello! This is Inventory Service."));
    }
}
