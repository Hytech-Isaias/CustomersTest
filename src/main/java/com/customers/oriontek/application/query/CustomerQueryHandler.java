package com.customers.oriontek.application.query;

import com.customers.oriontek.api.exception.ResourceNotFoundException;
import com.customers.oriontek.application.dto.CustomerResponse;
import com.customers.oriontek.application.dto.LocationResponse;
import com.customers.oriontek.domain.entity.Customer;
import com.customers.oriontek.domain.entity.CustomerLocation;
import com.customers.oriontek.domain.repository.CustomerLocationRepository;
import com.customers.oriontek.domain.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerQueryHandler {

        private final CustomerRepository customerRepository;
        private final CustomerLocationRepository customerLocationRepository;

        public CustomerQueryHandler(final CustomerRepository customerRepository,
                        final CustomerLocationRepository customerLocationRepository) {
                this.customerRepository = customerRepository;
                this.customerLocationRepository = customerLocationRepository;
        }

        /**
         * Executes {@link GetCustomerByIdQuery}: fetches customer and maps to
         * projection DTO.
         */
        public CustomerResponse handle(GetCustomerByIdQuery query) {
                Customer customer = customerRepository.findActiveByIdWithLocations(query.customerId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found with ID: " + query.customerId()));

                List<LocationResponse> locations = customer.getCustomerLocations().stream()
                                .map(cl -> new LocationResponse(
                                                cl.getLocation().getId(),
                                                cl.getLocation().getStreetAddress(),
                                                cl.getLocation().getCity(),
                                                cl.getLocation().getStateProvince(),
                                                cl.getLocation().getCountry(),
                                                cl.getLocation().getPostalCode(),
                                                cl.isPrimary(),
                                                cl.getAssignedAt()))
                                .toList();

                return new CustomerResponse(
                                customer.getId(),
                                customer.getCommercialName(),
                                customer.getOwnerName(),
                                customer.getEmail(),
                                customer.getPhone(),
                                customer.getRnc(),
                                customer.getCreatedAt(),
                                customer.getUpdatedAt(),
                                locations);
        }

        /**
         * Executes {@link ListCustomerAddressesQuery}: retrieves all locations assigned
         * to a customer.
         */
        public List<LocationResponse> handle(ListCustomerAddressesQuery query) {
                if (!customerRepository.existsById(query.customerId())) {
                        throw new ResourceNotFoundException("Customer not found with ID: " + query.customerId());
                }

                List<CustomerLocation> customerLocations = customerLocationRepository
                                .findByCustomerIdWithLocations(query.customerId());

                return customerLocations.stream()
                                .map(cl -> new LocationResponse(
                                                cl.getLocation().getId(),
                                                cl.getLocation().getStreetAddress(),
                                                cl.getLocation().getCity(),
                                                cl.getLocation().getStateProvince(),
                                                cl.getLocation().getCountry(),
                                                cl.getLocation().getPostalCode(),
                                                cl.isPrimary(),
                                                cl.getAssignedAt()))
                                .toList();
        }

        /**
         * Retrieves a paginated list of active customers with their locations.
         */
        public Page<CustomerResponse> findAll(Pageable pageable) {
                Page<Customer> page = customerRepository.findAllActive(pageable);

                return page.map(customer -> {
                        List<LocationResponse> locations = customer.getCustomerLocations().stream()
                                        .map(cl -> new LocationResponse(
                                                        cl.getLocation().getId(),
                                                        cl.getLocation().getStreetAddress(),
                                                        cl.getLocation().getCity(),
                                                        cl.getLocation().getStateProvince(),
                                                        cl.getLocation().getCountry(),
                                                        cl.getLocation().getPostalCode(),
                                                        cl.isPrimary(),
                                                        cl.getAssignedAt()))
                                        .toList();

                        return new CustomerResponse(
                                        customer.getId(),
                                        customer.getCommercialName(),
                                        customer.getOwnerName(),
                                        customer.getEmail(),
                                        customer.getPhone(),
                                        customer.getRnc(),
                                        customer.getCreatedAt(),
                                        customer.getUpdatedAt(),
                                        locations);
                });
        }
}
