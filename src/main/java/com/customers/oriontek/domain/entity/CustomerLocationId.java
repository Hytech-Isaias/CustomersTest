package com.customers.oriontek.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CustomerLocationId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    protected CustomerLocationId() {
    }

    public CustomerLocationId(UUID customerId, UUID locationId) {
        this.customerId = customerId;
        this.locationId = locationId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomerLocationId that))
            return false;
        return Objects.equals(customerId, that.customerId)
                && Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, locationId);
    }

    @Override
    public String toString() {
        return "CustomerLocationId{customerId=" + customerId + ", locationId=" + locationId + '}';
    }
}
