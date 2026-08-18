package com.orderservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orderservice.entity.Order;
import com.orderservice.repository.OrderRepository;

@Service
public class OrderService {
	
	private OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
	
		this.orderRepository = orderRepository;
	}
	
	
	
	public Order createOrder(Order order) {
		
		
		return orderRepository.save(order);
		
	}
	
	public Order getOrder(UUID id) {
		
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("order not found"));
		
	}

	
}
