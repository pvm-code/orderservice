package com.orderservice.service;

import com.orderservice.entity.ProcessedEvent;
import com.orderservice.repository.ProcessedEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    public ProcessedEventService(
            ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional(readOnly = true)
    public boolean alreadyProcessed(UUID eventId) {
        return processedEventRepository.existsByEventId(eventId);
    }

    @Transactional
    public boolean markProcessed(
            UUID eventId,
            String eventType) {

        if (processedEventRepository.existsByEventId(eventId)) {
            return false;
        }

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(eventId);
        processedEvent.setEventType(eventType);

        try {
            processedEventRepository.saveAndFlush(processedEvent);
            return true;

        } catch (DataIntegrityViolationException ex) {
            /*
             * Another consumer/thread processed the same event
             * concurrently. The unique event_id constraint protects us.
             */
            return false;
        }
    }
}