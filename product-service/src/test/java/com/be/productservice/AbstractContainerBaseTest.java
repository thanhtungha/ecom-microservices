package com.be.productservice;

import com.be.productservice.dto.ListUsers;
import com.be.productservice.dto.ProductDTO;
import com.be.productservice.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractContainerBaseTest {
    static final MySQLContainer MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest");
        MY_SQL_CONTAINER.start();
    }

    @BeforeAll
    static void beforeAll() {
    }

    public final String AUTH_BASE_API = "/api/auth";
    public final String PRODUCT_BASE_API = "/api/product";
    public static String authorizationHeader;
    public static UserDTO userDTO;
    public static ProductDTO testProduct;
    @Autowired
    public ObjectMapper objectMapper;
    private static WireMockServer wireMockServer;

    public void createTestUser() throws Exception {
        userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID());
        userDTO.setCreateDate(new Date());
        userDTO.setUpdateDate(new Date());
        userDTO.setUserName("userName");
        userDTO.setPhoneNumber("0123457879");
        userDTO.setAddress("asdds");
        authorizationHeader = "Bearer " +
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJpc3MiOiJhc2QiLCJleHAiOjE2OTIwNjQ3MjcsImlhdCI6MTY5MTk3ODMyN30.FE3F3cYeKZ364eGXRGmDrHSe0yBCPKt5CniMLGVq9Do";

        WireMock.configureFor("localhost", 8080);
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        String body = objectMapper.writeValueAsString(userDTO);
        stubFor(get(urlEqualTo(AUTH_BASE_API + "/verify-auth")).withHeader(
                        "Authorization",
                        equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

        ListUsers listUsers = new ListUsers(new ArrayList<>());
        listUsers.getUsers()
                .add(userDTO);
        body = objectMapper.writeValueAsString(listUsers);

        stubFor(get(urlEqualTo(AUTH_BASE_API + "/list-user?ids=" + userDTO.getId()
                .toString())).withHeader("Authorization",
                        equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

    }
}
