package org.learning.kafka.service;

import org.learning.kafka.rest.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel product) {
        String productId = UUID.randomUUID().toString();

        //TODO: Persist product details into database table before publishing an Event.

        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(productId, product.getTitle(), product.getPrice(), product.getQuantity());

        //async producer.
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
        //lambda is invoked when the future completes.
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send message: {}", ex.getMessage());
            } else {
                logger.info("Message sent successfully: {}", result.getRecordMetadata());
            }
        });

        //This will make the current thread
        //wait till the future resolves. Basically
        //making the producer synchronous.
        //future.join();

        return productId;
    }
}
