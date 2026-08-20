package com.orderservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "orders")
public class Order {
	
	@Id
	@Column(name = "id", nullable = false,updatable = false)
	private UUID id = UUID.randomUUID();
	
	@Column(name = "user_id", nullable = false)
	private @NotNull UUID userId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;
	
	@Column(name = "total_amount" , nullable = false, precision = 19 , scale = 2)
	private BigDecimal totalAmount;
	
	@Column(name = "created_at" , nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;


	@OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	
	public void addOrderItem(OrderItem orderItem) {
		
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
	
	public void removeOrderItem(OrderItem orderItem) {
		
		orderItems.remove(orderItem);
		orderItem.setOrder(null);
		
	}
	
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	

	

	public @NotNull UUID getUserId() {
		return userId;
	}

	public void setUserId(@NotNull UUID uuid) {
		this.userId = uuid;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}


	public UUID getId() {
		return id;
	}
	
	@PrePersist
	protected void onCreate() {
		
		LocalDateTime now=LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if(status == null) {
			
			status =OrderStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt=LocalDateTime.now();
	}
	
}
