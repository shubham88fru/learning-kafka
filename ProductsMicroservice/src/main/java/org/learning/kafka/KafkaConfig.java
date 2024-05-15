package org.learning.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    protected NewTopic createTopic() {
        return TopicBuilder
                .name("product-created-events-topic")
                .partitions(3)
                .replicas(1) //should be less than or equal to broker instances running.
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }
}
