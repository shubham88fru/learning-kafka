package org.learning.kafka.emailnotificationmicroservice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.learning.kafka.emailnotificationmicroservice.exception.NonRetryableException;
import org.learning.kafka.emailnotificationmicroservice.exception.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        /**
         * This deserializer acts as a wrapper over JsonDeserializer with an added advantage
         * that it allows the consumer to gracefully handle deserialization error on
         * message consumption.
         *
         * When deserialization errors are handled, it allows the consumer to move on
         * from the malformed message and continue consuming next messages.
         */
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("spring.kafka.consumer.group-id"));


        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        //By default, the dead letter topic is named the same
        //as the og topic whose message the consumer failed read, with
        //and an extension .dlt
        //e.g.product-created-event.dlt
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        new DeadLetterPublishingRecoverer(kafkaTemplate), //publish to dead letter.
                        new FixedBackOff(5000, 3) //add backoff between retries.
                );

        //configure the error handler with a list of not-retryable exceptions.
        errorHandler.addNotRetryableExceptions(NonRetryableException.class);

        //configure the error handler with a list of retryable exceptions.
        errorHandler.addRetryableExceptions(RetryableException.class);


        ConcurrentKafkaListenerContainerFactory<String, Object> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    /**
     * Note that we are KafkaProducer here, in a Consumer Config?
     * This is because this producer (wrapped by KafkaTemplate)
     * is what will be used to send message to the Dead letter topic
     * that we have configured in the above bean.
     */
    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
}
