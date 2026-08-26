package com.orderservice.controller;

import com.orderservice.OrderserviceApplication;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.client.ProductClient;
import com.orderservice.dto.client.ProductClientResponse;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.response.ApiResponse;
import com.orderservice.dto.response.OrderResponse;
import com.orderservice.service.OrderService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/v1/order")
public class OrderController {
	
	private final OrderserviceApplication orderserviceApplication;

	private final ProductClient productClient;
	
	private final OrderService orderService;
	
	public OrderController(
	        OrderService orderService,
	        ProductClient productClient, OrderserviceApplication orderserviceApplication) {

	    this.orderService = orderService;
	    this.productClient = productClient;
		this.orderserviceApplication = orderserviceApplication;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
			@Valid @RequestBody CreateOrderRequest request,
			@AuthenticationPrincipal Jwt jwt){
		
		UUID userId = UUID.fromString(
	            jwt.getClaimAsString("userId")
	    );
		
		
		OrderResponse order =orderService.createOrder(request,userId);
		
		ApiResponse<OrderResponse> response = new ApiResponse<>(
				
				true,
				"order created successfuly",
				order
				
				);
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
		
	}
	
	@GetMapping("/welcome")
	public ResponseEntity<String> welcome(
	        @AuthenticationPrincipal Jwt jwt) {

	    String userId = jwt.getClaimAsString("userId");

	    return ResponseEntity.ok(
	            "User ID: " + userId
	    );
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
	        @PathVariable UUID id,
	        @AuthenticationPrincipal Jwt jwt) {

	    UUID userId = UUID.fromString(
	            jwt.getClaimAsString("userId")
	    );

	    OrderResponse order = orderService.getOrder(id, userId);

	    ApiResponse<OrderResponse> response = new ApiResponse<>(
	            true,
	            "Order fetched successfully",
	            order
	    );

	    return ResponseEntity.ok(response);
	}
	
	
}
