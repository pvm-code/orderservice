package com.orderservice.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.orderservice.dto.client.ProductClientResponse;

@Component
public class ProductClient {

    private final RestClient productRestClient;
    private final RestClient productOAuth2RestClient;

    public ProductClient(
            @Qualifier("productRestClient") RestClient productRestClient,
            @Qualifier("productOAuth2RestClient") RestClient productOAuth2RestClient) {

        this.productRestClient = productRestClient;
        this.productOAuth2RestClient = productOAuth2RestClient;
    }

    public ProductClientResponse getProduct(UUID productId) {

        return productRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/products/{id}")
                        .build(productId))
                .retrieve()
                .body(ProductClientResponse.class);
    }

    public void decreaseStock(UUID productId, int quantity) {

        productOAuth2RestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/inventory/{id}/reserve")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .toBodilessEntity();
    }

    public void increaseStock(UUID productId, int quantity) {

        productOAuth2RestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/inventory/{id}/release")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .toBodilessEntity();
    }
}