package com.orderservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderservice.entity.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
	
	
	List<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
	
	
	
	

}
