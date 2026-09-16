package com.orderservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.orderservice.dto.client.ProductClientResponse;
import com.orderservice.security.ServiceJwtService;

@Component
public class ProductClient {
	
	private final RestClient restClient;
	
	private final ServiceJwtService serviceJwtService;

	public ProductClient(
	        RestClient restClient,
	        ServiceJwtService serviceJwtService) {

	    this.restClient = restClient;
	    this.serviceJwtService = serviceJwtService;
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

	    String token = serviceJwtService.createProductServiceToken();

	    restClient
	            .put()
	            .uri(uriBuilder -> uriBuilder
	                    .path("/internal/inventory/{id}/reserve")
	                    .queryParam("quantity", quantity)
	                    .build(productId))
	            .header("Authorization", "Bearer " + token)
	            .retrieve()
	            .toBodilessEntity();
	}
	public void increaseStock(UUID productId, int quantity) {

	    String token = serviceJwtService.createProductServiceToken();

	    restClient
	            .put()
	            .uri(uriBuilder -> uriBuilder
	                    .path("/internal/inventory/{id}/release")
	                    .queryParam("quantity", quantity)
	                    .build(productId))
	            .header("Authorization", "Bearer " + token)
	            .retrieve()
	            .toBodilessEntity();
	}
}