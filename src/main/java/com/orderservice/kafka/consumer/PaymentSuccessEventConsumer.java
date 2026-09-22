package com.orderservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.kafka.event.PaymentSuccessEvent;
import com.orderservice.service.OrderService;
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

    public PaymentSuccessEventConsumer(
            ObjectMapper objectMapper,
            OrderService orderService) {

        this.objectMapper = objectMapper;
        this.orderService = orderService;
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
                        "PAYMENT_SUCCESS eventId is required"
                );
            }

            if (event.getOrderId() == null) {
                throw new IllegalStateException(
                        "PAYMENT_SUCCESS orderId is required"
                );
            }

            orderService.confirmOrderFromPayment(
                    event.getOrderId(),
                    event.getEventId()
            );

            log.info(
                    "PAYMENT_SUCCESS processed successfully. eventId={}, orderId={}, paymentId={}",
                    event.getEventId(),
                    event.getOrderId(),
                    event.getPaymentId()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to process PAYMENT_SUCCESS event. message={}",
                    message,
                    ex
            );

            throw new RuntimeException(
                    "Failed to process PAYMENT_SUCCESS event",
                    ex
            );
        }
    }
}