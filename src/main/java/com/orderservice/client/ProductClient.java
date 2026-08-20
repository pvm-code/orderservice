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
}