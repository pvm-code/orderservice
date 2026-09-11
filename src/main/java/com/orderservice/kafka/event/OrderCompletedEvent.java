package com.orderservice.kafka.event;

import java.util.UUID;

public class OrderCompletedEvent implements DomainEvent {

    private UUID eventId;
    private UUID orderId;
    private UUID userId;
    private String email;

    public OrderCompletedEvent() {
    }

    public OrderCompletedEvent(
            UUID orderId,
            UUID userId,
            String email) {
        this.orderId = orderId;
        this.userId = userId;
        this.email = email;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}