package com.be.inventoryservice.service.webclient;

import com.be.inventoryservice.dto.ListProducts;
import com.be.inventoryservice.dto.ProductDTO;
import com.be.inventoryservice.exception.RestExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductClient {
    private final WebClient.Builder webClientBuilder;
    private static final String BASE_API = "http://localhost:8081/api/product";

    public List<ProductDTO> getListProducts(String authorizationHeader,
                                            List<String> ids) {
        ListProducts listProducts = webClientBuilder.build()
                .get()
                .uri(BASE_API + "/list-product",
                        uriBuilder -> uriBuilder.queryParam("ids", ids).build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(ListProducts.class)
                .block();

        if (listProducts != null) {
            return listProducts.getProducts();
        }
        throw new RestExceptions.InternalServerError("Failed to get products.");
    }
}
