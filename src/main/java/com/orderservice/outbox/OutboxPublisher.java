package com.orderservice.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orderservice.entity.OutboxEvent;
import com.orderservice.entity.OutboxStatus;
import com.orderservice.repository.OutboxEventRepository;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final int KAFKA_TIMEOUT_SECONDS = 10;

    private static final int STALE_PROCESSING_MINUTES = 5;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate) {

        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        resetStaleProcessingEvents();

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop50ByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (OutboxEvent event : events) {

            int claimed =
                    outboxEventRepository.claimEvent(
                            event.getId(),
                            OutboxStatus.PENDING,
                            OutboxStatus.PROCESSING,
                            LocalDateTime.now()
                    );

            if (claimed == 0) {
                continue;
            }

            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent event) {

        String topic = getTopic(event.getEventType());

        try {

            kafkaTemplate.send(
                    topic,
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).get(
                    KAFKA_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            event.setStatus(OutboxStatus.PUBLISHED);
            event.setProcessingStartedAt(null);

            outboxEventRepository.save(event);

            log.info(
                    "Outbox event published successfully. eventId={}, type={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId()
            );

        } catch (Exception e) {

            event.setStatus(OutboxStatus.PENDING);
            event.setProcessingStartedAt(null);

            outboxEventRepository.save(event);

            log.error(
                    "Failed to publish outbox event. eventId={}, type={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId(),
                    e
            );
        }
    }

    private void resetStaleProcessingEvents() {

        LocalDateTime cutoff =
                LocalDateTime.now()
                        .minusMinutes(STALE_PROCESSING_MINUTES);

        int resetCount =
                outboxEventRepository.resetStaleProcessingEvents(
                        OutboxStatus.PROCESSING,
                        OutboxStatus.PENDING,
                        cutoff
                );

        if (resetCount > 0) {

            log.warn(
                    "Reset {} stale outbox event(s) to PENDING",
                    resetCount
            );
        }
    }

    private String getTopic(String eventType) {

        return switch (eventType) {

            case "ORDER_CREATED" ->
                    "order-created";

            case "ORDER_CANCELLED" ->
                    "order-cancelled";

            case "ORDER_CONFIRMED" ->
                    "order-confirmed";

            case "ORDER_IN_TRANSIT" ->
                    "order-intransit";

            case "ORDER_COMPLETED" ->
                    "order-completed";

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type: " + eventType
                    );
        };
    }
}