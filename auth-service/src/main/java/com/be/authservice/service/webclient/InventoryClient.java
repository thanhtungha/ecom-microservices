package com.be.authservice.service.webclient;

import com.be.authservice.exception.RestExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;
    private static final String AUTH_BASE_API =
            "http://localhost:8080/api/inventory";

    public boolean createInventory() {
        ResponseEntity<?> response = webClientBuilder.build()
                .get()
                .uri(AUTH_BASE_API + "/create-inventory")
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block();

        if (response != null) {
            return response.getStatusCode().is2xxSuccessful();
        }
        throw new RestExceptions.InternalServerError("Failed to create Inventory.");
    }
}
