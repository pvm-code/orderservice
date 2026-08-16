package com.orderservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order ,UUID> {
	
	
	List<Order> findByUserId(Long userId);

}
