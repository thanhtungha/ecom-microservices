package com.be.authservice.controller;

import com.be.authservice.AbstractContainerBaseTest;
import com.be.authservice.dto.*;
import com.be.authservice.model.User;
import com.be.authservice.repository.IAuthRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends AbstractContainerBaseTest {
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public IAuthRepository repository;
    private static String BASE_API = "/api/auth";

    @Test
    @Order(0)
    void register() throws Exception {
        RqRegisterArgs registerArgs = new RqRegisterArgs("controllerUser",
                "userPassword",
                "1234567890");
        String reqString = objectMapper.writeValueAsString(registerArgs);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());

        //register user 2
        registerArgs = new RqRegisterArgs("controllerUser2",
                "user2Password",
                "1111111111");
        reqString = objectMapper.writeValueAsString(registerArgs);
        requestBuilder = MockMvcRequestBuilders.post(BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    @Order(1)
    void login() throws Exception {
        RqLoginArgs loginArgs = new RqLoginArgs("controllerUser",
                "userPassword");
        String reqString = objectMapper.writeValueAsString(loginArgs);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
    }

    @Test
    @Order(3)
    void logout() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + getAccessToken());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        //check response

        //check db
        Optional<User> createdUser = repository.findByUserName(
                "controllerUser");
        if (createdUser.isPresent()) {
            if (createdUser.get()
                    .getAccessToken()
                    .isEmpty()) {
                return;
            }
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void changePassword() throws Exception {
        RqChangePasswordArgs changePasswordArgs = new RqChangePasswordArgs(
                "newPassword");
        String reqString = objectMapper.writeValueAsString(changePasswordArgs);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        //check response

        //check db
        Optional<User> createdUser = repository.findByUserName(
                "controllerUser");
        if (createdUser.isPresent()) {
            assertEquals(createdUser.get()
                    .getUserPassword(), changePasswordArgs.getNewPassword());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void update() throws Exception {
        RqUpdateArgs updateArgs = new RqUpdateArgs("0987654321",
                "new " + "Address");
        String reqString = objectMapper.writeValueAsString(updateArgs);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        //check response

        //check db
        Optional<User> createdUser = repository.findByUserName(
                "controllerUser");
        if (createdUser.isPresent()) {
            assertEquals(createdUser.get()
                    .getPhoneNumber(), updateArgs.getPhoneNumber());
            assertEquals(createdUser.get()
                    .getAddress(), updateArgs.getAddress());
            return;
        }
        fail("test case failed!");
    }

    @Test
    @Order(2)
    void forgotPassword() throws Exception {
        RqForgotPasswordArgs forgotPasswordArgs = new RqForgotPasswordArgs("controllerUser");
        String reqString = objectMapper.writeValueAsString(forgotPasswordArgs);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(BASE_API + "/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                        .content(reqString);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void verifyAuth() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(BASE_API + "/verify-auth")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + getAccessToken());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
    }

    @Test
    @Order(2)
    void getListUser() throws Exception {
        List<String> ids = repository.findAll()
                .stream()
                .map(user -> user.getId()
                        .toString())
                .toList();
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(BASE_API + "/list-user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .queryParam("ids", ids.toArray(new String[0]));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        //check response
    }

    @Test
    @Order(4)
    void delete() throws Exception {
        RqLoginArgs loginArgs = new RqLoginArgs("controllerUser",
                "newPassword");
        String reqString = objectMapper.writeValueAsString(loginArgs);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);
        mockMvc.perform(requestBuilder);

        requestBuilder = MockMvcRequestBuilders.post(BASE_API + "/delete" +
                        "-account")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + getAccessToken());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        //check response

        //check db
        Optional<User> createdUser = repository.findByUserName(
                "controllerUser");
        if (createdUser.isPresent()) {
            fail("test case failed!");
        }
    }

    String getAccessToken() {
        Optional<User> createdUser = repository.findByUserName(
                "controllerUser");
        if (createdUser.isEmpty()) {
            fail("test case failed!");
        }
        return createdUser.get()
                .getAccessToken();
    }
}