package com.orderservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.kafka.event.PaymentSuccessEvent;
import com.orderservice.service.OrderService;
import com.orderservice.service.ProcessedEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSuccessEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentSuccessEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final ProcessedEventService processedEventService;

    public PaymentSuccessEventConsumer(
            ObjectMapper objectMapper,
            OrderService orderService,
            ProcessedEventService processedEventService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.processedEventService = processedEventService;
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "order-service"
    )
    public void consume(String message) {

        try {
            PaymentSuccessEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentSuccessEvent.class
                    );

            if (event.getEventId() == null) {
                throw new IllegalStateException(
                        "Payment success eventId is required"
                );
            }

            if (event.getOrderId() == null) {
                throw new IllegalStateException(
                        "Payment success orderId is required"
                );
            }

            log.info(
                    "Received PAYMENT_SUCCESS event. eventId={}, paymentId={}, orderId={}, transactionId={}",
                    event.getEventId(),
                    event.getPaymentId(),
                    event.getOrderId(),
                    event.getTransactionId()
            );

            if (processedEventService.alreadyProcessed(
                    event.getEventId())) {

                log.info(
                        "Duplicate PAYMENT_SUCCESS event ignored. eventId={}, orderId={}",
                        event.getEventId(),
                        event.getOrderId()
                );

                return;
            }

            orderService.confirmOrder(event.getOrderId());

            boolean marked =
                    processedEventService.markProcessed(
                            event.getEventId(),
                            "PAYMENT_SUCCESS"
                    );

            if (!marked) {
                log.info(
                        "PAYMENT_SUCCESS event was already processed concurrently. eventId={}, orderId={}",
                        event.getEventId(),
                        event.getOrderId()
                );

                return;
            }

            log.info(
                    "PAYMENT_SUCCESS processed successfully. eventId={}, orderId={}",
                    event.getEventId(),
                    event.getOrderId()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to process PAYMENT_SUCCESS event",
                    ex
            );

            throw new RuntimeException(
                    "Failed to process PAYMENT_SUCCESS event",
                    ex
            );
        }
    }
}