package com.customers.oriontek.infrastructure.kafka;

import com.customers.oriontek.application.command.CreateCustomerCommand;
import com.customers.oriontek.application.command.CustomerCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerSeedConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomerSeedConsumer.class);

    private final CustomerCommandHandler commandHandler;
    private final ObjectMapper objectMapper;

    public CustomerSeedConsumer(final CustomerCommandHandler commandHandler,
            final ObjectMapper objectMapper) {
        this.commandHandler = commandHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "oriontek.seed.customers", groupId = "${spring.kafka.consumer.group-id:oriontek-group}")
    public void consumeCustomerSeed(Object rawPayload) {
        log.info("Received raw seed customer payload: {}", rawPayload);
        try {
            CreateCustomerCommand command;
            if (rawPayload instanceof Map) {
                command = objectMapper.convertValue(rawPayload, CreateCustomerCommand.class);
            } else if (rawPayload instanceof String str) {
                command = objectMapper.readValue(str, CreateCustomerCommand.class);
            } else {
                command = objectMapper.convertValue(rawPayload, CreateCustomerCommand.class);
            }

            log.info("Seeding customer for commercial name: {}", command.commercialName());
            commandHandler.handle(command);
        } catch (Exception e) {
            log.error("Failed to ingest seed customer payload: {}", e.getMessage(), e);
        }
    }
}
