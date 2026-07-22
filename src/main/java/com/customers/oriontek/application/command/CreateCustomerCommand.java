package com.customers.oriontek.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerCommand(
                @NotBlank(message = "Commercial name is required") @Size(max = 150, message = "Commercial name must not exceed 150 characters") String commercialName,

                @NotBlank(message = "Owner name is required") @Size(max = 150, message = "Owner name must not exceed 150 characters") String ownerName,

                @NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") @Size(max = 255, message = "Email must not exceed 255 characters") String email,

                @Size(max = 30, message = "Phone must not exceed 30 characters") String phone,

                @Size(max = 50, message = "RNC must not exceed 50 characters") String rnc) {
}
