package com.customers.oriontek.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "customer_locations")
public class CustomerLocation {

    @EmbeddedId
    private CustomerLocationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("locationId")
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    protected CustomerLocation() {
    }

    public CustomerLocation(Customer customer, Location location, boolean isPrimary) {
        this.customer = customer;
        this.location = location;
        this.isPrimary = isPrimary;
        this.id = new CustomerLocationId(customer.getId(), location.getId());
    }

    @PrePersist
    protected void onAssign() {
        this.assignedAt = LocalDateTime.now();
    }

    public CustomerLocationId getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        this.isPrimary = primary;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomerLocation that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CustomerLocation{" + id + ", isPrimary=" + isPrimary + '}';
    }
}
