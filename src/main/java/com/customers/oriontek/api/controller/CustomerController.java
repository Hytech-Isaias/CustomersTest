package com.customers.oriontek.api.controller;

import com.customers.oriontek.application.command.AssignLocationToCustomerCommand;
import com.customers.oriontek.application.command.CreateCustomerCommand;
import com.customers.oriontek.application.command.CustomerCommandHandler;
import com.customers.oriontek.application.dto.CustomerResponse;
import com.customers.oriontek.application.dto.LocationResponse;
import com.customers.oriontek.application.query.CustomerQueryHandler;
import com.customers.oriontek.application.query.GetCustomerByIdQuery;
import com.customers.oriontek.application.query.ListCustomerAddressesQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Endpoints for managing customers and their associated addresses")
public class CustomerController {

        private final CustomerCommandHandler commandHandler;
        private final CustomerQueryHandler queryHandler;

        public CustomerController(final CustomerCommandHandler commandHandler,
                        final CustomerQueryHandler queryHandler) {
                this.commandHandler = commandHandler;
                this.queryHandler = queryHandler;
        }

        @PostMapping
        @Operation(summary = "Create a new customer", description = "Executes CreateCustomerCommand, persists customer, and streams CustomerCreatedEvent to Kafka.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Customer created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "409", description = "Customer email already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        public ResponseEntity<Void> createCustomer(@Valid @RequestBody CreateCustomerCommand command) {
                UUID customerId = commandHandler.handle(command);
                URI location = URI.create("/api/v1/customers/" + customerId);
                return ResponseEntity.created(location).build();
        }

        @PostMapping("/{id}/locations")
        @Operation(summary = "Assign a location to a customer", description = "Executes AssignLocationToCustomerCommand, links address to customer, and streams LocationAssignedEvent.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Location assigned successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid location request payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        public ResponseEntity<Void> assignLocation(
                        @Parameter(description = "UUID of the customer", required = true) @PathVariable("id") UUID customerId,
                        @Valid @RequestBody AssignLocationToCustomerCommand request) {

                // Enforce path ID alignment with command payload
                AssignLocationToCustomerCommand command = new AssignLocationToCustomerCommand(
                                customerId,
                                request.streetAddress(),
                                request.city(),
                                request.stateProvince(),
                                request.country(),
                                request.postalCode(),
                                request.isPrimary());

                UUID locationId = commandHandler.handle(command);
                URI locationUri = URI.create("/api/v1/customers/" + customerId + "/locations");
                return ResponseEntity.status(HttpStatus.CREATED).location(locationUri).build();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get customer details by ID", description = "Executes GetCustomerByIdQuery to fetch customer master and all associated address locations.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer details retrieved successfully", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        public ResponseEntity<CustomerResponse> getCustomerById(
                        @Parameter(description = "UUID of the customer", required = true) @PathVariable("id") UUID customerId) {
                CustomerResponse response = queryHandler.handle(new GetCustomerByIdQuery(customerId));
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(summary = "List all active customers (Paginated)", description = "Retrieves a paginated list of active customers with their locations.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Paginated customer list retrieved successfully")
        })
        public ResponseEntity<Page<CustomerResponse>> listCustomers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "DESC") String direction) {

                Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<CustomerResponse> customers = queryHandler.findAll(pageable);
                return ResponseEntity.ok(customers);
        }

        @GetMapping("/{id}/locations")
        @Operation(summary = "List all locations for a customer", description = "Executes ListCustomerAddressesQuery to return all linked locations for a customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Locations list retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
        public ResponseEntity<List<LocationResponse>> getCustomerLocations(
                        @Parameter(description = "UUID of the customer", required = true) @PathVariable("id") UUID customerId) {
                List<LocationResponse> locations = queryHandler.handle(new ListCustomerAddressesQuery(customerId));
                return ResponseEntity.ok(locations);
        }
}
