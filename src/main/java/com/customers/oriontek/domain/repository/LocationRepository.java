package com.customers.oriontek.domain.repository;

import com.customers.oriontek.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

        /**
         * Find an existing location by its full address composite to avoid duplicates.
         * Matches the UNIQUE constraint {@code uq_locations_address} in the schema.
         */
        @Query("SELECT l FROM Location l "
                        + "WHERE l.streetAddress = :streetAddress "
                        + "AND l.city = :city "
                        + "AND l.country = :country "
                        + "AND l.postalCode = :postalCode")
        Optional<Location> findByAddress(
                        @Param("streetAddress") String streetAddress,
                        @Param("city") String city,
                        @Param("country") String country,
                        @Param("postalCode") String postalCode);
}
