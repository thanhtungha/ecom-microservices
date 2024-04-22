package com.ecom.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO: tunght13

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service",
                        r -> r.path("/api/auth/**").uri("lb://auth-service"))
                .route("cart-service",
                        r -> r.path("/api/cart/**").uri("lb://cart-service"))
                .route("inventory-service",
                        r -> r.path("/api/inventory/**").uri("lb://inventory-service"))
                .route("order-service",
                        r -> r.path("/api/order/**").uri("lb://order-service"))
                .route("product-service",
                        r -> r.path("/api/product/**").uri("lb://product-service"))
                .route("discovery-server", r -> r.path("/api/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://discovery-server:8761"))
                .route("discovery-server-static", r -> r.path("/api/eureka/**")
                        .uri("http://discovery-server:8761"))
                .build();
    }
}
