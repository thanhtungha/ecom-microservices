package com.be.inventoryservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class BaseException extends RuntimeException {
    @Getter
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    BaseException(String message) {
        super(message);
    }

    public BaseException setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    protected Map<String, Object> getBody() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("exception", this.getClass().getSimpleName());
        result.put("status", this.getStatus().value());
        result.put("message", this.getMessage());
        return result;
    }
}
