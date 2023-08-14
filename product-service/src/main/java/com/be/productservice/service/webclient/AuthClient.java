package com.be.productservice.service.webclient;

import com.be.productservice.dto.ListUsers;
import com.be.productservice.dto.UserDTO;
import com.be.productservice.exception.RestExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthClient {
    private final WebClient.Builder webClientBuilder;
    private static final String AUTH_BASE_API =
            "http://localhost:8080/api" + "/auth";

    public UserDTO verifyToken(String authorizationHeader) {
        UserDTO userDTO = webClientBuilder.build()
                .get()
                .uri(AUTH_BASE_API + "/verify-auth")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();

        if (userDTO != null) {
            return userDTO;
        }
        throw new RestExceptions.Forbidden("Invalid accessToken!");
    }

    public List<UserDTO> getListUserDTO(String authorizationHeader,
                                        List<String> ids) {
        ListUsers listUsers = webClientBuilder.build()
                .get()
                .uri(AUTH_BASE_API + "/list-user",
                        uriBuilder -> uriBuilder.queryParam("ids", ids)
                                .build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(ListUsers.class)
                .block();

        if (listUsers != null) {
            return listUsers.getUsers();
        }
        throw new RestExceptions.InternalServerError("Failed to get " +
                "reviewers" + ".");
    }
}
