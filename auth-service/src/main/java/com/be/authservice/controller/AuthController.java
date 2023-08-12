package com.be.authservice.controller;

import com.be.authservice.dto.*;
import com.be.authservice.exception.BaseException;
import com.be.authservice.exception.RestExceptions;
import com.be.authservice.mappers.IAuthMapper;
import com.be.authservice.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
public class AuthController {
    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);
    private final IAuthService service;
    private final IAuthMapper mapper;

    @PostMapping(path = "/greeting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> greeting() {
        logger.info("greeting");
        return ResponseEntity.ok(new BaseResponse("Hello! This is Auth " +
                "Service."));
    }

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(
            @Valid @RequestBody RqRegisterArgs registerArgs) {
        try {
            UserDTO userDTO = service.register(registerArgs);
            return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@Valid @RequestBody RqLoginArgs loginArgs) {
        try {
            UserDTO userDTO = service.login(loginArgs);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            service.logout(authorizationHeader);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/change-password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RqChangePasswordArgs changePasswordArgs) {
        try {
            service.changePassword(authorizationHeader, changePasswordArgs);
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
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RqUpdateArgs updateArgs) {
        try {
            UserDTO userDTO = service.update(authorizationHeader, updateArgs);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }


    @GetMapping(path = "/verify-auth")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> verifyAuth(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            UserDTO userDTO = service.getUserInformation(authorizationHeader);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }

    @PostMapping(path = "/delete-account")
    @ResponseStatus(HttpStatus.OK)
    public void delete(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            service.deleteUser(authorizationHeader);
        } catch (Exception ex) {
            if (ex instanceof BaseException) {
                throw ex;
            }
            throw new RestExceptions.InternalServerError(ex.getMessage());
        }
    }
}