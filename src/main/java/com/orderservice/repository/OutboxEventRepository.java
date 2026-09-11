package com.orderservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderservice.entity.OutboxEvent;
import com.orderservice.entity.OutboxStatus;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );

    @Modifying
    @Query("""
        UPDATE OutboxEvent e
        SET e.status = :processing,
            e.processingStartedAt = :processingStartedAt
        WHERE e.id = :id
          AND e.status = :pending
    """)
    int claimEvent(
            @Param("id") UUID id,
            @Param("pending") OutboxStatus pending,
            @Param("processing") OutboxStatus processing,
            @Param("processingStartedAt") LocalDateTime processingStartedAt
    );

    @Modifying
    @Query("""
        UPDATE OutboxEvent e
        SET e.status = :pending,
            e.processingStartedAt = NULL
        WHERE e.status = :processing
          AND e.processingStartedAt < :cutoff
    """)
    int resetStaleProcessingEvents(
            @Param("processing") OutboxStatus processing,
            @Param("pending") OutboxStatus pending,
            @Param("cutoff") LocalDateTime cutoff
    );
}