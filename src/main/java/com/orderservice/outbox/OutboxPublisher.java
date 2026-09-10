package com.orderservice.outbox;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orderservice.entity.OutboxEvent;
import com.orderservice.repository.OutboxEventRepository;

@Component
public class OutboxPublisher {
	
	private OutboxEventRepository outboxEventRepository;
	
	private final KafkaTemplate<String, String> kafkaTemplate;

	public OutboxPublisher(
	        OutboxEventRepository outboxEventRepository,
	        KafkaTemplate<String, String> kafkaTemplate) {

	    this.outboxEventRepository = outboxEventRepository;
	    this.kafkaTemplate = kafkaTemplate;
	}
	
	@Scheduled(fixedDelay = 5000)
	public void publishPendingEvents() {
		
		
		List<OutboxEvent> events = outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();
		
		for(OutboxEvent event : events) {
			
			
			System.out.println("found unpublished event :"
					+ event.getEventType()
					+ " | "
					+ event.getAggregateId());
			
			String topic = getTopic(event.getEventType());

			try {
			    kafkaTemplate.send(
			            topic,
			            event.getAggregateId().toString(),
			            event.getPayload()
			    ).get(10, TimeUnit.SECONDS);

			    event.setPublished(true);
			    outboxEventRepository.save(event);

			    System.out.println(
			            "Event published successfully: "
			            + event.getEventType()
			            + " | "
			            + event.getAggregateId()
			    );

			} catch (Exception e) {

			    System.out.println(
			            "Failed to publish event: "
			            + event.getEventType()
			            + " | "
			            + event.getAggregateId()
			    );

			    e.printStackTrace();
			}
			
		}
		
		
		
	}
	
	private String getTopic(String eventType) {

	    return switch (eventType) {
	        case "ORDER_CREATED" -> "order-created";
	        case "ORDER_CANCELLED" -> "order-cancelled";
	        case "ORDER_CONFIRMED" -> "order-confirmed";
	        case "ORDER_IN_TRANSIT" -> "order-in-transit";
	        case "ORDER_COMPLETED" -> "order-completed";
	        default -> throw new IllegalArgumentException(
	                "Unknown event type: " + eventType
	        );
	    };
	}
	

}
