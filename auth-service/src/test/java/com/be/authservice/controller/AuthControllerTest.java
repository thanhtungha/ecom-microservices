package com.be.authservice.controller;

import com.be.authservice.dto.*;
import com.be.authservice.exception.BaseExceptionHandler;
import com.be.authservice.exception.RestExceptions;
import com.be.authservice.service.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private MockMvc mockMvc;
    @Mock
    private AuthServiceImpl service;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_API = "/api/auth";
    private String accessToken;
    UserInfoDTO user;

    @BeforeEach
    public void beforeEach() {
        accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJpc3MiOiJ4eXoxMjQzMiIsImV4cCI6MTcxMjUyMzE1OCwiaWF0IjoxNzEyNDM2NzU4fQ.ZyiwRJZQGtFQtmrsiwX7f1dfc_0hMiN0PEn9aHSql8g";

        user = UserInfoDTO.builder()
                .id(UUID.randomUUID())
                .createDate(new Date())
                .updateDate(new Date())
                .userName("userName")
                .phoneNumber("0123456789")
                .address("010302 sweet home")
                .accessToken(accessToken)
                .build();

        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(service))
                .setControllerAdvice(new BaseExceptionHandler())
                .build();
    }

    @Test
    void register_success() throws Exception {
        RqRegisterArgs registerArgs = RqRegisterArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .phoneNumber("1234567890")
                .build();

        when(service.register(registerArgs)).thenReturn(user);

        String reqString = objectMapper.writeValueAsString(registerArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName", is(user.getUserName())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andReturn();
    }

    @Test
    void register_fail_conflict() throws Exception {
        RqRegisterArgs registerArgs = RqRegisterArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .phoneNumber("1234567890")
                .build();

        when(service.register(registerArgs)).thenThrow(
                new RestExceptions.Conflict("User existed"));

        String reqString = objectMapper.writeValueAsString(registerArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("User existed")))
                .andExpect(jsonPath("$.status", is(409)))
                .andReturn();
    }

    @Test
    void login_success() throws Exception {
        RqLoginArgs loginArgs = RqLoginArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .build();

        when(service.login(loginArgs)).thenReturn(user);

        String reqString = objectMapper.writeValueAsString(loginArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().toString())))
                .andExpect(jsonPath("$.userName", is(user.getUserName())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.accessToken", is(user.getAccessToken())))
                .andReturn();
    }

    @Test
    void login_fail_userNotFound() throws Exception {
        RqLoginArgs loginArgs = RqLoginArgs.builder()
                .userName("userName")
                .userPassword("userPassword")
                .build();

        when(service.login(loginArgs)).thenThrow(new RestExceptions.NotFound(
                "User not found or wrong password"));

        String reqString = objectMapper.writeValueAsString(loginArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",
                        is("User not found or wrong password")))
                .andExpect(jsonPath("$.status", is(404)))
                .andReturn();
    }

    @Test
    void logout_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        when(service.logout("Bearer " + accessToken)).thenReturn(true);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    void logout_fail() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        when(service.logout("Bearer " + accessToken)).thenThrow(
                new RestExceptions.Forbidden("Invalid accessToken!"));

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Invalid accessToken!")))
                .andExpect(jsonPath("$.status", is(403)))
                .andReturn();
    }

    @Test
    void changePassword_success() throws Exception {
        RqChangePasswordArgs changePasswordArgs = RqChangePasswordArgs.builder()
                .newPassword("newPassword")
                .build();

        when(service.changePassword("Bearer " + accessToken,
                changePasswordArgs)).thenReturn(true);

        String reqString = objectMapper.writeValueAsString(changePasswordArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    void changePassword_fail_samePassword() throws Exception {
        RqChangePasswordArgs changePasswordArgs = RqChangePasswordArgs.builder()
                .newPassword("newPassword")
                .build();

        when(service.changePassword("Bearer " + accessToken,
                changePasswordArgs))
                .thenThrow(new RestExceptions.BadRequest(
                        "New password cannot be the same as the old password" +
                                "."));

        String reqString = objectMapper.writeValueAsString(changePasswordArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("New password cannot be the same as the old " +
                                "password.")))
                .andExpect(jsonPath("$.status", is(400)))
                .andReturn();
    }

    @Test
    void update_success() throws Exception {
        RqUpdateArgs updateArgs = RqUpdateArgs.builder()
                .phoneNumber("0123456789")
                .address("010302 sweet home")
                .build();

        when(service.update("Bearer " + accessToken, updateArgs)).thenReturn(
                user);

        String reqString = objectMapper.writeValueAsString(updateArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(reqString);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().toString())))
                .andExpect(jsonPath("$.userName", is(user.getUserName())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.accessToken", is(user.getAccessToken())))
                .andReturn();
    }

    @Test
    void forgotPassword() {
    }

    @Test
    void verifyAuth() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        BASE_API + "/verify-auth")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        when(service.getUserInformation("Bearer " + accessToken)).thenReturn(
                user);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().toString())))
                .andExpect(jsonPath("$.userName", is(user.getUserName())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.accessToken", is(user.getAccessToken())))
                .andReturn();
    }

    @Test
    void getListUser() throws Exception {
        UserInfoDTO u1 = UserInfoDTO.builder()
                .id(UUID.fromString("935a0738-5992-4739-b2d3-17fb119f207f"))
                .build();
        UserInfoDTO u2 = UserInfoDTO.builder()
                .id(UUID.fromString("47dc9172-03ce-466b-aaf3-e6dd7988ab50"))
                .build();
        UserInfoDTO u3 = UserInfoDTO.builder()
                .id(UUID.fromString("97c35957-71ef-4ab8-b4af-6b592105e32e"))
                .build();

        List<String> ids = Arrays.asList("935a0738-5992-4739-b2d3-17fb119f207f",
                "47dc9172-03ce-466b-aaf3-e6dd7988ab50",
                "97c35957-71ef-4ab8-b4af-6b592105e32e");

        List<UserInfoDTO> users = Arrays.asList(u1, u2, u3);
        ListUsers listUsers = ListUsers.builder().users(users).build();

        when(service.getListUser("Bearer " + accessToken, ids)).thenReturn(
                listUsers);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(BASE_API + "/list-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken)
                        .queryParam("ids", ids.toArray(new String[0]));

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[*].id", containsInAnyOrder(
                        "935a0738-5992-4739-b2d3-17fb119f207f",
                        "47dc9172-03ce-466b-aaf3-e6dd7988ab50",
                        "97c35957-71ef-4ab8-b4af-6b592105e32e")))
                .andReturn();

    }

    @Test
    void delete() throws Exception {
        RqLoginArgs loginArgs = new RqLoginArgs("controllerUser",
                "newPassword");
        String reqString = objectMapper.writeValueAsString(loginArgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString);
        mockMvc.perform(requestBuilder);

        requestBuilder = MockMvcRequestBuilders.post(
                        BASE_API + "/delete" + "-account")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }
}