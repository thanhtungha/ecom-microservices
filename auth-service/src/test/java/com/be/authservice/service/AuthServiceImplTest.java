package com.be.authservice.service;

import com.be.authservice.AbstractContainerBaseTest;
import com.be.authservice.dto.*;
import com.be.authservice.model.User;
import com.be.authservice.repository.IAuthRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceImplTest extends AbstractContainerBaseTest {
    @Autowired
    public IAuthService service;
    @Autowired
    public IAuthRepository repository;

    @Test
    @Order(0)
    void register() {
        RqRegisterArgs registerArgs = new RqRegisterArgs("serviceUser",
                "userPassword",
                "0123456789");
        service.register(registerArgs);
        Optional<User> createdUser =
                repository.findByUserName(registerArgs.getUserName());
        if (createdUser.isPresent()) {
            User user = createdUser.get();
            assertEquals(registerArgs.getUserName(), user.getUserName());
            assertEquals(registerArgs.getUserPassword(),
                    user.getUserPassword());
            assertEquals(registerArgs.getPhoneNumber(), user.getPhoneNumber());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(1)
    void login() {
        RqLoginArgs loginArgs = new RqLoginArgs("serviceUser", "userPassword");
        UserDTO userInfo = service.login(loginArgs);
        if (userInfo == null) {
            fail("test case failed!");
        }
    }

    @Test
    @Order(3)
    void logout() {
        RqLoginArgs loginArgs = new RqLoginArgs("serviceUser", "userPassword");
        service.login(loginArgs);

        String bearerToken = getAuthorizationHeader();
        boolean result = service.logout(bearerToken);
        if (result) {
            Optional<User> createdUser = repository.findByAccessToken(
                    bearerToken);
            if (createdUser.isPresent()) {
                fail("test case failed!");
            }
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void changePassword() {
        RqChangePasswordArgs changePasswordArgs = new RqChangePasswordArgs(
                "newPassword");
        boolean result = service.changePassword(getAuthorizationHeader(),
                changePasswordArgs);
        if (result) {
            Optional<User> createdUser = repository.findByUserName(
                    "serviceUser");
            createdUser.ifPresent(info -> assertEquals("newPassword",
                    info.getUserPassword()));

            changePasswordArgs.setNewPassword("userPassword");
            service.changePassword(getAuthorizationHeader(),
                    changePasswordArgs);
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void update() {
        RqUpdateArgs updateArgs = new RqUpdateArgs("0987654321", "new address");
        UserDTO userDTO = service.update(getAuthorizationHeader(), updateArgs);
        if (userDTO != null) {
            assertEquals(updateArgs.getPhoneNumber(), userDTO.getPhoneNumber());
            assertEquals(updateArgs.getAddress(), userDTO.getAddress());
        } else {
            fail("test case failed!");
        }
    }

    @Test
    @Order(2)
    void getUserInformation() {
        UserDTO userDTO = service.getUserInformation(getAuthorizationHeader());
        if (userDTO == null) {
            fail("test case failed!");
        }
    }

    @Test
    @Order(4)
    void delete() {
        RqLoginArgs loginArgs = new RqLoginArgs("serviceUser", "userPassword");
        service.login(loginArgs);

        boolean result = service.deleteUser(getAuthorizationHeader());
        if (result) {
            Optional<User> createdUser = repository.findByUserName(
                    "serviceUser");
            if (createdUser.isPresent()) {
                fail("test case failed!");
            }
            return;
        }
        fail("test case failed!");
    }

    String getAuthorizationHeader() {
        Optional<User> createdUser = repository.findByUserName("serviceUser");
        if (createdUser.isEmpty()) {
            fail("test case failed!");
        }
        return "Bearer " + createdUser.get().getAccessToken();
    }
}