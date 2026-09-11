package com.orderservice.kafka.event;

import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    void setEventId(UUID eventId);
}