package com.orderservice.controller;

import com.orderservice.OrderserviceApplication;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request){
		
		
		OrderResponse order =orderService.createOrder(request);
		
		ApiResponse<OrderResponse> response = new ApiResponse<>(
				
				true,
				"order created successfuly",
				order
				
				);
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
		
	}
	
	
	
}
