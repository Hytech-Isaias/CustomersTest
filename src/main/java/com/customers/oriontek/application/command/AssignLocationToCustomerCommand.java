package com.customers.oriontek.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AssignLocationToCustomerCommand(
                @NotNull(message = "Customer ID is required") UUID customerId,

                @NotBlank(message = "Street address is required") @Size(max = 300, message = "Street address must not exceed 300 characters") String streetAddress,

                @NotBlank(message = "City is required") @Size(max = 150, message = "City must not exceed 150 characters") String city,

                @Size(max = 150, message = "State/Province must not exceed 150 characters") String stateProvince,

                @NotBlank(message = "Country is required") @Size(max = 100, message = "Country must not exceed 100 characters") String country,

                @NotBlank(message = "Postal code is required") @Size(max = 20, message = "Postal code must not exceed 20 characters") String postalCode,

                boolean isPrimary) {
}
