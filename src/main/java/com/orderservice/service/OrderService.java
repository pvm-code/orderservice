package com.orderservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.response.OrderItemResponse;
import com.orderservice.dto.response.OrderResponse;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.exception.OrderNotFoundException;
import com.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
    	
    	Order order = new Order();
    	order.setUserId(request.getUserId());
    	
    	order.setOrderItems(new ArrayList<>());
    	
    	BigDecimal totalAmount= BigDecimal.ZERO;
    	
    	
    	for(var itemRequest : request.getItems()) {
    		
    		
    		OrderItem item = new OrderItem();
    		
    		item.setOrder(order);
    		item.setProductId(itemRequest.getProductId());
    		
    		/*
    		 * product name and price will add from product service 
    		 * next service to service comm code
    		 * 
    		 */
    		
    		item.setQuantity(itemRequest.getQuantity());
    		
    		order.getOrderItems().add(item);
    		
    		
    	}
    	order.setTotalAmount(totalAmount);
    	Order savedOrder = orderRepository.save(order);
    	
    	return mapToResponse(savedOrder);
    	
    	
    	
     
    }

    public OrderResponse getOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        ));

        return mapToResponse(order);
    }
    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        var itemResponses = order.getOrderItems()
                .stream()
                .map(this::mapItemToResponse)
                .toList();

        response.setItems(itemResponses);

        return response;
    }
    private OrderItemResponse mapItemToResponse(OrderItem item) {

        OrderItemResponse response = new OrderItemResponse();

        response.setId(item.getUuid());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}