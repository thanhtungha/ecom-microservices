package com.be.cartservice;

import com.be.cartservice.dto.ListProducts;
import com.be.cartservice.dto.ProductDTO;
import com.be.cartservice.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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

    public final String AUTH_BASE_API = "/api/auth";
    public final String PRODUCT_BASE_API = "/api/product";
    public final String CART_BASE_API = "/api/cart";
    public static String authorizationHeader;
    public static UserDTO userDTO;
    public static ProductDTO productDTO1;
    public static ProductDTO productDTO2;
    @Autowired
    public ObjectMapper objectMapper;
    private static WireMockServer wireMockServer;

    public void createTestUser() throws Exception {
        WireMock.configureFor("localhost", 8080);
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        setAuthMock();
        setProductMock();
    }

    private void setAuthMock() throws JsonProcessingException {
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
                PRODUCT_BASE_API + "/list-product?ids=" + productDTO2.getId()
                        .toString() + "&ids=" + productDTO1.getId()
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

        stubFor(get(urlEqualTo(
                PRODUCT_BASE_API + "/list-product?ids=" + productDTO2.getId()
                        .toString())).withHeader("Authorization",
                        equalTo(authorizationHeader))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }
}
