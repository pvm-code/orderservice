package com.orderservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import javax.naming.InsufficientResourcesException;

import org.springframework.stereotype.Service;

import com.orderservice.client.ProductClient;
import com.orderservice.dto.client.ProductClientResponse;
import com.orderservice.dto.client.ProductData;
import com.orderservice.dto.request.CreateOrderItemRequest;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.response.OrderItemResponse;
import com.orderservice.dto.response.OrderResponse;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatus;
import com.orderservice.exception.InsufficientStockException;
import com.orderservice.exception.OrderNotFoundException;
import com.orderservice.exception.ProductServiceException;
import com.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    
    private final ProductClient productClient;

    

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
		super();
		this.orderRepository = orderRepository;
		this.productClient = productClient;
	}

	public OrderResponse createOrder(CreateOrderRequest request,UUID userId)  {
    	
    	Order order = new Order();
    	order.setUserId(userId);
    	order.setStatus(OrderStatus.PENDING);
    	
    	 //order.setOrderItems(new ArrayList<>());
    	
    	BigDecimal totalAmount= BigDecimal.ZERO;
    	
    	
    	for(CreateOrderItemRequest itemRequest : request.getItems()) {
    		
    		ProductClientResponse productResponse = 
    				productClient.getProduct(itemRequest.getProductId());
    		
    		
    		if(!productResponse.isSuccess() || productResponse.getData() == null) {
    			
    			
    			throw new ProductServiceException("unable to fetch product:"+itemRequest.getProductId());
    		}
    		
    	ProductData product = productResponse.getData();
    	
    	if(itemRequest.getQuantity() > product.getStock()) {
    		

            throw new InsufficientStockException(
                    "Insufficient stock for product: "
                    + product.getName()
                    + ". Available: "
                    + product.getStock()
                    + ", requested: "
                    + itemRequest.getQuantity()
            );    	}
    	
    	BigDecimal subtotal= product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
    	
    	OrderItem orderItem = new OrderItem();
    	
    	orderItem.setProductId(product.getId());
    	orderItem.setProductName(product.getName());
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setSubtotal(subtotal);
        
        order.addOrderItem(orderItem);
        totalAmount = totalAmount.add(subtotal);
    	
     
    }
    	
    	order.setTotalAmount(totalAmount);
    	Order savedOrder = orderRepository.save(order);
    	
    	return mapToResponse(savedOrder);
     
    }

    public OrderResponse getOrder(UUID id,UUID userId) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        ));
        if(!order.getUserId().equals(userId)) {
        	throw new OrderNotFoundException(
        			
        			"Order not found with id:" + id
        			);
        	
        }

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