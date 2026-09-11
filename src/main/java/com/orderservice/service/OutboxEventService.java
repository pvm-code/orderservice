package com.orderservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OutboxEvent;
import com.orderservice.kafka.event.DomainEvent;
import com.orderservice.repository.OutboxEventRepository;

@Service
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {

        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void saveEvent(
            String eventType,
            UUID aggregateId,
            DomainEvent event) {

        try {
            /*
             * One unique ID represents this event everywhere:
             *
             * OutboxEvent.id
             *       =
             * event.eventId
             */
            UUID eventId = UUID.randomUUID();

            event.setEventId(eventId);

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent();

            outboxEvent.setId(eventId);
            outboxEvent.setEventType(eventType);
            outboxEvent.setAggregateId(aggregateId);
            outboxEvent.setPayload(payload);

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize outbox event",
                    e
            );
        }
    }
}