package com.customers.oriontek.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an event to a specified Kafka topic.
     *
     * @param topic the target topic
     * @param key   partition key (e.g. customer ID)
     * @param event event payload object
     */
    public void publish(String topic, String key, Object event) {
        log.info("Publishing event [{}] to topic [{}] with key [{}]", event.getClass().getSimpleName(), topic, key);
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event [{}] to topic [{}]: {}",
                                event.getClass().getSimpleName(), topic, ex.getMessage(), ex);
                    } else {
                        log.debug("Successfully published event [{}] offset [{}]",
                                event.getClass().getSimpleName(), result.getRecordMetadata().offset());
                    }
                });
    }
}
