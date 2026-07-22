package com.customers.oriontek.domain.repository;

import com.customers.oriontek.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

        /**
         * Find an active (non-deleted) customer by ID.
         */
        @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.deletedAt IS NULL")
        Optional<Customer> findActiveById(@Param("id") UUID id);

        /**
         * Find an active customer by email address.
         */
        @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
        Optional<Customer> findActiveByEmail(@Param("email") String email);

        /**
         * Check whether an active customer with the given email exists.
         */
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END "
                        + "FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
        boolean existsActiveByEmail(@Param("email") String email);

        /**
         * Paginated list of all active customers.
         */
        @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
        Page<Customer> findAllActive(Pageable pageable);

        /**
         * Fetch an active customer with all linked locations eagerly loaded
         * (avoids N+1 on the junction + location relationships).
         */
        @Query("SELECT DISTINCT c FROM Customer c "
                        + "LEFT JOIN FETCH c.customerLocations cl "
                        + "LEFT JOIN FETCH cl.location "
                        + "WHERE c.id = :id AND c.deletedAt IS NULL")
        Optional<Customer> findActiveByIdWithLocations(@Param("id") UUID id);
}
