package com.orderservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
	
	@Id
	@Column(nullable = false,updatable = false)
	private UUID id=UUID.randomUUID();

	@Column(name = "event_type",nullable = false)
	private String eventType;
	
	@Column(name = "aggregate_id",nullable = false)
	private UUID aggregateId;
	
	@Column(nullable = false,columnDefinition = "TEXT")
	private String payload;
	
	@Column(name="created_at",nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private boolean published = false;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
	
	

	public UUID getId() {
		return id;
	}



	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public UUID getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(UUID aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
	
	
	
	
}
