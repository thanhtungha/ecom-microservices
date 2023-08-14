package com.be.orderservice;

import com.be.orderservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    public final String ORDER_BASE_API = "/api/order";
    public static String authorizationHeader;
    public static UserDTO userDTO;
    public static OrderDTO orderDTO;
    public static ProductDTO productDTO1;
    public static ProductDTO productDTO2;
    @Autowired
    public ObjectMapper objectMapper;
    private static WireMockServer wireMockAuthServer;
    private static WireMockServer wireMockProductServer;

    public void createTestUser() throws Exception {
        setAuthMock();
        setProductMock();
    }

    private void setAuthMock() throws JsonProcessingException {
        WireMock.configureFor("localhost", 8080);
        wireMockAuthServer = new WireMockServer(8080);
        wireMockAuthServer.start();

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

        String body = objectMapper.writeValueAsString(userDTO);
        stubFor(get(urlEqualTo(AUTH_BASE_API + "/verify-auth")).withHeader(
                        "Authorization", equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    private void setProductMock() throws JsonProcessingException {
        WireMock.configureFor("localhost", 8081);
        wireMockProductServer = new WireMockServer(8081);
        wireMockProductServer.start();

        productDTO1 = new ProductDTO();
        productDTO1.setId(UUID.randomUUID());
        productDTO1.setName("product 1");
        productDTO1.setPrice(1200);

        productDTO2 = new ProductDTO();
        productDTO2.setId(UUID.randomUUID());
        productDTO2.setName("product 2");
        productDTO2.setPrice(1300);

        ListProducts listProducts = new ListProducts(new ArrayList<>());
        listProducts.getProducts().add(productDTO1);
        listProducts.getProducts().add(productDTO2);

        String body = objectMapper.writeValueAsString(listProducts);

        stubFor(get(urlEqualTo(
                PRODUCT_BASE_API + "/list-product?ids=" + productDTO1.getId()
                        .toString() + "&ids=" + productDTO2.getId()
                        .toString())).withHeader("Authorization",
                        equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

        stubFor(get(urlEqualTo(
                PRODUCT_BASE_API + "/list-product?ids=" + productDTO1.getId()
                        .toString())).withHeader("Authorization",
                        equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

    }
}
