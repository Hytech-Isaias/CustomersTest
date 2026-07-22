package com.customers.oriontek.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationResponse(
                UUID id,
                String streetAddress,
                String city,
                String stateProvince,
                String country,
                String postalCode,
                boolean isPrimary,
                LocalDateTime assignedAt) {
}
