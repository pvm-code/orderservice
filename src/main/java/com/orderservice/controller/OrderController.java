package com.orderservice.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.client.ProductClient;
import com.orderservice.dto.client.ProductClientResponse;
import com.orderservice.service.OrderService;


@RestController
@RequestMapping("api/v1/order")
public class OrderController {
	
	private final ProductClient productClient;
	
	private final OrderService orderService;
	
	public OrderController(
	        OrderService orderService,
	        ProductClient productClient) {

	    this.orderService = orderService;
	    this.productClient = productClient;
	}

	@GetMapping("/test-product/{id}")
	public ResponseEntity<ProductClientResponse> testProduct(
	        @PathVariable UUID id) {

	    return ResponseEntity.ok(
	            productClient.getProduct(id)
	    );
	}
}
