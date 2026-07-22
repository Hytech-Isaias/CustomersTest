package com.customers.oriontek.application.command;

import com.customers.oriontek.api.exception.DuplicateResourceException;
import com.customers.oriontek.api.exception.ResourceNotFoundException;
import com.customers.oriontek.application.event.CustomerCreatedEvent;
import com.customers.oriontek.application.event.LocationAssignedEvent;
import com.customers.oriontek.domain.entity.Customer;
import com.customers.oriontek.domain.entity.CustomerLocation;
import com.customers.oriontek.domain.entity.Location;
import com.customers.oriontek.domain.repository.CustomerLocationRepository;
import com.customers.oriontek.domain.repository.CustomerRepository;
import com.customers.oriontek.domain.repository.LocationRepository;
import com.customers.oriontek.infrastructure.kafka.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomerCommandHandler.class);
    private static final String TOPIC_CUSTOMER_EVENTS = "oriontek.customer.events";
    private static final String TOPIC_LOCATION_EVENTS = "oriontek.location.events";

    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final CustomerLocationRepository customerLocationRepository;
    private final EventPublisher eventPublisher;

    public CustomerCommandHandler(final CustomerRepository customerRepository,
            final LocationRepository locationRepository,
            final CustomerLocationRepository customerLocationRepository,
            final EventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.locationRepository = locationRepository;
        this.customerLocationRepository = customerLocationRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes {@link CreateCustomerCommand}: validates email uniqueness, persists
     * Customer,
     * and emits {@link CustomerCreatedEvent}.
     */
    public UUID handle(CreateCustomerCommand command) {
        log.info("Executing CreateCustomerCommand for email: {}", command.email());

        if (customerRepository.existsActiveByEmail(command.email())) {
            throw new DuplicateResourceException("Customer with email [" + command.email() + "] already exists");
        }

        Customer customer = new Customer(
                command.commercialName(),
                command.ownerName(),
                command.email(),
                command.phone(),
                command.rnc());

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Created customer with ID: {}", savedCustomer.getId());

        CustomerCreatedEvent event = new CustomerCreatedEvent(
                savedCustomer.getId(),
                savedCustomer.getCommercialName(),
                savedCustomer.getOwnerName(),
                savedCustomer.getEmail(),
                savedCustomer.getPhone(),
                savedCustomer.getRnc());
        eventPublisher.publish(TOPIC_CUSTOMER_EVENTS, savedCustomer.getId().toString(), event);

        return savedCustomer.getId();
    }

    /**
     * Executes {@link AssignLocationToCustomerCommand}: verifies customer
     * existence,
     * deduplicates/persists Location, manages primary location invariant, creates
     * junction entity,
     * and emits {@link LocationAssignedEvent}.
     */
    public UUID handle(AssignLocationToCustomerCommand command) {
        log.info("Executing AssignLocationToCustomerCommand for customer ID: {}", command.customerId());

        Customer customer = customerRepository.findActiveById(command.customerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer not found with ID: " + command.customerId()));

        Location location = locationRepository.findByAddress(
                command.streetAddress(),
                command.city(),
                command.country(),
                command.postalCode())
                .orElseGet(() -> locationRepository.save(new Location(
                        command.streetAddress(),
                        command.city(),
                        command.stateProvince(),
                        command.country(),
                        command.postalCode())));

        // Check if customer already has a primary location when command requests
        // primary
        if (command.isPrimary()) {
            List<CustomerLocation> existingLocations = customerLocationRepository.findByCustomerId(customer.getId());
            for (CustomerLocation cl : existingLocations) {
                if (cl.isPrimary()) {
                    cl.setPrimary(false);
                    customerLocationRepository.save(cl);
                }
            }
        }

        CustomerLocation customerLocation = new CustomerLocation(customer, location, command.isPrimary());
        customerLocationRepository.save(customerLocation);

        log.info("Assigned location ID [{}] to customer ID [{}]", location.getId(), customer.getId());

        LocationAssignedEvent event = new LocationAssignedEvent(
                customer.getId(),
                location.getId(),
                location.getStreetAddress(),
                location.getCity(),
                location.getStateProvince(),
                location.getCountry(),
                location.getPostalCode(),
                command.isPrimary());
        eventPublisher.publish(TOPIC_LOCATION_EVENTS, customer.getId().toString(), event);

        return location.getId();
    }
}
