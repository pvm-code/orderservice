package com.orderservice.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
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
import com.orderservice.kafka.event.OrderCreatedEvent;
import com.orderservice.kafka.producer.OrderEventProducer;
import com.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    
    private final ProductClient productClient;
    
    private final OrderEventProducer orderEventProducer;
    

    

    public OrderService(OrderRepository orderRepository, ProductClient productClient, OrderEventProducer orderEventProducer) {
		super();
		this.orderRepository = orderRepository;
		this.productClient = productClient;
		this.orderEventProducer = orderEventProducer;
	}

    
	public OrderResponse createOrder(CreateOrderRequest request,UUID userId,String email)  {
    	
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
    	
    	OrderCreatedEvent event = new OrderCreatedEvent(
    			
    			savedOrder.getId(),
    			savedOrder.getUserId(),
    			savedOrder.getTotalAmount(),
    			email
    			
    			);
    	orderEventProducer.publishOrderCreated(event);
    	
    	return mapToResponse(savedOrder);
     
    }

    public OrderResponse getOrder(UUID id,UUID userId,boolean isAdmin) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        ));
        System.out.println("CURRENT USER ID = " + userId);
        System.out.println("IS ADMIN = " + isAdmin);
        System.out.println("ORDER USER ID = " + order.getUserId());

        if(!isAdmin && !order.getUserId().equals(userId)) {
        	throw new AccessDeniedException(
        			
        			"you do not have permission to access this order"
        			);
        	
        }

        return mapToResponse(order);
    }
    
    
    
    public OrderResponse cancelOrder(UUID id,UUID userId, boolean isAdmin) {
    	
    	
    	Order order =  orderRepository.findById(id).orElseThrow(
    			
    					() -> new OrderNotFoundException("order not found with id:" + id)
    			
    			);
    	
    	if(!isAdmin && !order.getUserId().equals(userId)) {
    		
    		throw new AccessDeniedException("you do not have permission to cancel this order");
    	}
    	
    	
    	if(order.getStatus() != OrderStatus.PENDING) {
    		throw new IllegalStateException("only pending order can be cancelled");
    	}
    	
    	order.setStatus(OrderStatus.CANCELLED);
    	
    	Order savedOrder = orderRepository.save(order);
    	
    	
    	return mapToResponse(savedOrder);
    	
    	
    }
    public OrderResponse confirmOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        ));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING orders can be confirmed"
            );
        }

        order.setStatus(OrderStatus.CONFIRMED);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
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