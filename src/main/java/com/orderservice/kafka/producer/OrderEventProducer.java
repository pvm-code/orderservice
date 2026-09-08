package com.orderservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.kafka.event.OrderCancelledEvent;
import com.orderservice.kafka.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class OrderEventProducer {
	
	
	private static final String TOPIC = "order-created";
	
	private static final Logger log =
	        LoggerFactory.getLogger(OrderEventProducer.class);
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		super();
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}
	
	public void publishOrderCreated(OrderCreatedEvent event) {
		
		
		try {
			
			String message = objectMapper.writeValueAsString(event);
			log.info("Publishing OrderCreatedEvent to Kafka: {}", message);
			
			kafkaTemplate.send(
					
					TOPIC,
					event.getOrderId().toString(),
					message
					
					
					
					);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("failed to serialize orderCreatedEvent",e);
		}
		
		
		
	}
	
	public void publishOrderCancelled(OrderCancelledEvent event) {

	    try {
	        String message = objectMapper.writeValueAsString(event);

	        log.info(
	                "Publishing OrderCancelledEvent to Kafka: {}",
	                message
	        );

	        kafkaTemplate.send(
	                "order-cancelled",
	                event.getOrderId().toString(),
	                message
	        );

	    } catch (JsonProcessingException e) {
	        throw new RuntimeException(
	                "Failed to serialize OrderCancelledEvent",
	                e
	        );
	    }
	}

}
