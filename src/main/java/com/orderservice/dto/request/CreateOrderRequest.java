package com.orderservice.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class CreateOrderRequest {
	
	
	
	@NotEmpty
	@Valid
	private List<CreateOrderItemRequest> items;

	

	public List<CreateOrderItemRequest> getItems() {
		return items;
	}

	public void setItems(List<CreateOrderItemRequest> items) {
		this.items = items;
	}
	
	
	

}
