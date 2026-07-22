package com.customers.oriontek.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationAssignedEvent(
        UUID customerId,
        UUID locationId,
        String streetAddress,
        String city,
        String stateProvince,
        String country,
        String postalCode,
        boolean isPrimary,
        LocalDateTime occurredAt) {
    public LocationAssignedEvent(UUID customerId, UUID locationId, String streetAddress, String city,
            String stateProvince, String country, String postalCode, boolean isPrimary) {
        this(customerId, locationId, streetAddress, city, stateProvince, country, postalCode, isPrimary,
                LocalDateTime.now());
    }
}
