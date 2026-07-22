package com.customers.oriontek.infrastructure.kafka;

import com.customers.oriontek.application.command.AssignLocationToCustomerCommand;
import com.customers.oriontek.application.command.CustomerCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocationSeedConsumer {

    private static final Logger log = LoggerFactory.getLogger(LocationSeedConsumer.class);

    private final CustomerCommandHandler commandHandler;
    private final ObjectMapper objectMapper;

    public LocationSeedConsumer(final CustomerCommandHandler commandHandler,
            final ObjectMapper objectMapper) {
        this.commandHandler = commandHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "oriontek.seed.locations", groupId = "${spring.kafka.consumer.group-id:oriontek-group}")
    public void consumeLocationSeed(Object rawPayload) {
        log.info("Received raw seed location payload: {}", rawPayload);
        try {
            AssignLocationToCustomerCommand command;
            if (rawPayload instanceof Map) {
                command = objectMapper.convertValue(rawPayload, AssignLocationToCustomerCommand.class);
            } else if (rawPayload instanceof String str) {
                command = objectMapper.readValue(str, AssignLocationToCustomerCommand.class);
            } else {
                command = objectMapper.convertValue(rawPayload, AssignLocationToCustomerCommand.class);
            }

            log.info("Seeding location for customer ID: {}", command.customerId());
            commandHandler.handle(command);
        } catch (Exception e) {
            log.error("Failed to ingest seed location payload: {}", e.getMessage(), e);
        }
    }
}
