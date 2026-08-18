package com.orderservice.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY,optional = false)
	@JoinColumn(name = "order_id",nullable = false)
	private Order order;
	
	@Column(name = "product_id",nullable = false)
	private Long productId;
	
	@Column(name = "product_name" ,nullable = false)
	private String productName;
	
	@Column(name = "quantity",nullable = false)
	private Integer quantity;
	
	@Column(name = "unit_price",nullable = false,precision = 19,scale = 2)
	private BigDecimal unitPrice;
	
	@Column(name = "subtotal",nullable = false,precision = 19,scale = 2)
	private BigDecimal subtotal;

	public OrderItem(UUID id, Order order, Long productId, String productName, Integer quantity, BigDecimal unitPrice,
			BigDecimal subtotal) {
		super();
		this.id = id;
		this.order = order;
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subtotal = subtotal;
	}

	public OrderItem() {
		
	}

	public UUID getUuid() {
		return id;
	}

	public void setUuid(UUID id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
