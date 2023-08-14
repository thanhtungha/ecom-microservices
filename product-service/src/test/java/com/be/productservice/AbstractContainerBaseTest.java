package com.be.productservice;

import com.be.authservice.AuthServiceApplication;
import com.be.productservice.dto.ProductDTO;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;


import static org.junit.jupiter.api.Assertions.*;

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
        AuthServiceApplication.main(new String[]{});
    }

    @Autowired
    private WebClient.Builder webClientBuilder;
    public final String AUTH_BASE_API = "http://localhost:8080/api/auth";
    public final String PRODUCT_BASE_API = "/api/product";
    public static String authorizationHeader;
    public static TestUserDTO userDTO;
    public static ProductDTO testProduct;

    public void createTestUser() {
        String regiserValue = "{\n\"userName\": \"asd\"," + "\n" +
                "\"userPassword\":" + " \"123456\",\n\"phoneNumber\": " +
                "\"0123456789\"\n}";
        userDTO = webClientBuilder.build()
                .post()
                .uri(AUTH_BASE_API + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(regiserValue))
                .retrieve()
                .bodyToMono(TestUserDTO.class)
                .block();

        String loginValue = "{\n    \"userName\": \"asd\",\n" + "    " +
                "\"userPassword\": \"123456\"\n" + "}";
        userDTO = webClientBuilder.build()
                .post()
                .uri(AUTH_BASE_API + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(loginValue))
                .retrieve()
                .bodyToMono(TestUserDTO.class)
                .block();

        if (userDTO == null) {
            fail("Failed to register");
        }
        authorizationHeader = "Bearer " + userDTO.getAccessToken();
    }
}
