package com.orderservice.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {
	
	@NotNull
	private UUID userId;
	
	@NotEmpty
	@Valid
	private List<CreateOrderItemRequest> items;

	public @NotNull UUID getUserId() {
		return userId;
	}

	public void setUserId(@NotNull UUID userId) {
		this.userId = userId;
	}

	public List<CreateOrderItemRequest> getItems() {
		return items;
	}

	public void setItems(List<CreateOrderItemRequest> items) {
		this.items = items;
	}
	
	
	

}
