package org.learning.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.learning.kafka.core.ProductCreatedEvent;
import org.learning.kafka.rest.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel product) throws ExecutionException, InterruptedException {
        String productId = UUID.randomUUID().toString();

        //TODO: Persist product details into database table before publishing an Event.

        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(productId, product.getTitle(), product.getPrice(), product.getQuantity());

        logger.info("Publishing event...");
        //sendAsync("product-created-events-topic", productId, productCreatedEvent);
        sendSync("product-created-events-topic", productId, productCreatedEvent);


        return productId;
    }

    private void sendSync(String topic, String productId, ProductCreatedEvent event)
            throws ExecutionException, InterruptedException {

        ProducerRecord<String, ProductCreatedEvent> record =
                new ProducerRecord<>(
                        topic,
                        productId,
                        event
                );
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send(record).get(); //use producer record to set message headers.
                //kafkaTemplate.send(topic, productId, event).get(); //When not needing to send message header.

        logger.info("Sent message sync.. {}", result.getRecordMetadata());
    }

    private void sendAsync(String topic, String productId, ProductCreatedEvent event) {
        //async producer.
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send(topic, productId, event);

        //lambda is invoked when the future completes.
        //Asynchronous.
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
    }
}
