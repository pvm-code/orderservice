package com.orderservice.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderItemRequest {
	
	@NotNull
	private UUID productId;
	
	@NotNull
	@Positive
	private Integer quantity;

	public @NotNull UUID getProductId() {
		return productId;
	}

	public void setProductId(@NotNull UUID productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	 

}
