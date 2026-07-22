package com.customers.oriontek.domain.repository;

import com.customers.oriontek.domain.entity.CustomerLocation;
import com.customers.oriontek.domain.entity.CustomerLocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerLocationRepository extends JpaRepository<CustomerLocation, CustomerLocationId> {

        /**
         * Find all location assignments for a given customer,
         * eagerly fetching the linked {@code Location} to avoid N+1.
         */
        @Query("SELECT cl FROM CustomerLocation cl "
                        + "JOIN FETCH cl.location "
                        + "WHERE cl.customer.id = :customerId")
        List<CustomerLocation> findByCustomerIdWithLocations(@Param("customerId") UUID customerId);

        /**
         * Check if a customer already has a primary address assigned.
         */
        @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END "
                        + "FROM CustomerLocation cl "
                        + "WHERE cl.customer.id = :customerId AND cl.isPrimary = true")
        boolean existsPrimaryForCustomer(@Param("customerId") UUID customerId);

        /**
         * Find all location assignments for a given customer (without eager fetch).
         */
        List<CustomerLocation> findByCustomerId(UUID customerId);
}
