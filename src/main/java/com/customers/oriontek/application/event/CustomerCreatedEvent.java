package com.customers.oriontek.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID customerId,
        String commercialName,
        String ownerName,
        String email,
        String phone,
        String rnc,
        LocalDateTime occurredAt) {
    public CustomerCreatedEvent(UUID customerId, String commercialName, String ownerName, String email, String phone,
            String rnc) {
        this(customerId, commercialName, ownerName, email, phone, rnc, LocalDateTime.now());
    }
}
