package org.learning.kafka.emailnotificationmicroservice.handler;

import org.learning.kafka.core.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics="product-created-events-topic")
public class ProductCreatedEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    @KafkaHandler
    public void handle(ProductCreatedEvent event) {
        logger.info("Received a new event: {}", event);
    }
}
