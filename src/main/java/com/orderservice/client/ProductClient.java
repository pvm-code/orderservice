package com.orderservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.orderservice.dto.client.ProductClientResponse;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ProductClientResponse getProduct(UUID productId) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/products/{id}")
                        .build(productId))
                .retrieve()
                .body(ProductClientResponse.class);
    }

    public void decreaseStock(UUID productId, int quantity) {

        restClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/inventory/{id}/reserve")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .toBodilessEntity();
    }

    public void increaseStock(UUID productId, int quantity) {

        restClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/inventory/{id}/release")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .toBodilessEntity();
    }
}