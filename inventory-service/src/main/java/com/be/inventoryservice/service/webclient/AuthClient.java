package com.be.inventoryservice.service.webclient;

import com.be.inventoryservice.dto.UserDTO;
import com.be.inventoryservice.exception.RestExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class AuthClient {
    private final WebClient.Builder webClientBuilder;
    private static final String BASE_API = "http://localhost:8080/api/auth";

    public UserDTO verifyToken(String authorizationHeader) {
        UserDTO userDTO = webClientBuilder.build()
                .get()
                .uri(BASE_API + "/verify-auth")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();

        if (userDTO != null) {
            return userDTO;
        }
        throw new RestExceptions.Forbidden("Invalid accessToken!");
    }
}
