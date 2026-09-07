package com.orderservice.controller;

import com.orderservice.OrderserviceApplication;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.orderservice.security.AuthenticatedUser;
import com.orderservice.security.CurrentUser;
import com.orderservice.service.OrderService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/v1/order")
public class OrderController {
	

	
	private final OrderService orderService;
	
    private final CurrentUser currentUser;

	
	public OrderController(
	        OrderService orderService,
	        ProductClient productClient, OrderserviceApplication orderserviceApplication,CurrentUser currentUser) {

	    this.orderService = orderService;
		this.currentUser = currentUser;

	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
			@Valid @RequestBody CreateOrderRequest request,
			Authentication authentication){
		
		
		
		AuthenticatedUser user = currentUser.get(authentication);

		
		OrderResponse order =orderService.createOrder(request,user.getUserId(),user.getEmail());
		
		ApiResponse<OrderResponse> response = new ApiResponse<>(
				
				true,
				"order created successfuly",
				order
				
				);
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
		
	}
	

	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
	        @PathVariable UUID id,
	        Authentication authentication) {

		 AuthenticatedUser user =
	                currentUser.get(authentication);

	    OrderResponse order = orderService.getOrder(id, user.getUserId(),user.isAdmin());

	    ApiResponse<OrderResponse> response = new ApiResponse<>(
	            true,
	            "Order fetched successfully",
	            order
	    );

	    return ResponseEntity.ok(response);
	}
	
	
}
