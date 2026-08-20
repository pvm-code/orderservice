package com.orderservice.dto.client;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductClientResponse {
	
	
	private boolean success;
	
	private String message;
	
	private ProductData data;
	
    @JsonProperty("timestamp")
	private LocalDateTime timeStamp;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ProductData getData() {
		return data;
	}

	public void setData(ProductData data) {
		this.data = data;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	

}
