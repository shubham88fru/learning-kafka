package org.learning.kafka.emailnotificationmicroservice.handler;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.learning.kafka.core.ProductCreatedEvent;
import org.learning.kafka.emailnotificationmicroservice.io.IProcessedEventRepository;
import org.learning.kafka.emailnotificationmicroservice.io.ProcessedEventEntity;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@EmbeddedKafka
@SpringBootTest(properties="spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}")
class ProductCreatedEventHandlerTest {

    @MockBean
    IProcessedEventRepository processedEventRepository;

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    ProductCreatedEventHandler productCreatedEventHandler;

    @Test
    public void testProductCreatedEventHandler_OnProductCreated_HandlesEvent() throws ExecutionException, InterruptedException {
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        productCreatedEvent.setPrice(new BigDecimal(10));
        productCreatedEvent.setProductId(UUID.randomUUID().toString());
        productCreatedEvent.setQuantity(1);
        productCreatedEvent.setTitle("Test product");

        String messageId = UUID.randomUUID().toString();
        String messageKey = productCreatedEvent.getProductId();

        ProducerRecord<String, Object> record
                = new ProducerRecord<>(
                        "product-created-events-topic",
                        messageKey,
                    productCreatedEvent
                );

        record.headers().add("messageId", messageId.getBytes());
        record.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());

        ProcessedEventEntity processedEventEntity = new ProcessedEventEntity();
        when(processedEventRepository.findByMessageId(anyString()))
                .thenReturn(processedEventEntity);
        when(processedEventRepository.save(any(ProcessedEventEntity.class)))
                .thenReturn(null);

        String  response = "{\"key\":\"value\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                isNull(), eq(String.class))
        ).thenReturn(responseEntity);

        kafkaTemplate.send(record).get();

        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProductCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

        Mockito.verify(productCreatedEventHandler, timeout(5000).times(1)).handle(eventArgumentCaptor.capture(),
                        messageIdCaptor.capture(),
                        messageKeyCaptor.capture()
                        );

        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        assertEquals(productCreatedEvent.getProductId(), eventArgumentCaptor.getValue().getProductId());

    }

}