package com.customers.oriontek.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerResponse(
                UUID id,
                String commercialName,
                String ownerName,
                String email,
                String phone,
                String rnc,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                List<LocationResponse> locations) {
}
