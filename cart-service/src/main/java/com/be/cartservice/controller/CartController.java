package com.be.cartservice.controller;

import com.be.cartservice.dto.BaseResponse;
import com.be.cartservice.mappers.ICartMapper;
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
    private static final Logger logger =
            LoggerFactory.getLogger(CartController.class);
    private final ICartService service;
    private final ICartMapper mapper;

    @PostMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(new BaseResponse("Hello! This is Cart " +
                "Service."));
    }
}
