package com.be.authservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBadRequestException(BaseException ex) {
        return new ResponseEntity<>(ex.getBody(), ex.getStatus());
    }
}
