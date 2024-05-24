package org.learning.kafka.emailnotificationmicroservice.handler;

import org.learning.kafka.core.ProductCreatedEvent;
import org.learning.kafka.emailnotificationmicroservice.exception.NonRetryableException;
import org.learning.kafka.emailnotificationmicroservice.exception.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics="product-created-events-topic")
public class ProductCreatedEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductCreatedEventHandler.class);
    private RestTemplate restTemplate;

    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent event,
                       @Header(value = "messageId", required = false) String messageId,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey) {
        logger.info("Received a new event: {}", event.getTitle());

        String theUrl = "http://localhost:8082/response/200";
        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(theUrl, HttpMethod.GET, null, String.class);

            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                logger.info("Received REST API response..");
            }
        } catch (ResourceAccessException ex) {
            logger.error("Retryable exception in REST API call: {} ", ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException ex) {
            logger.error("Non retryable exception in REST API call: {}", ex.getMessage());
            throw new NonRetryableException(ex);
        } catch (Exception ex) {
            logger.error("Exception: {}", ex.getMessage());
            throw new NonRetryableException(ex);
        }


    }
}
