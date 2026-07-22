CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE customers (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMP             DEFAULT NULL,

    CONSTRAINT uq_customers_email UNIQUE (email)
);

COMMENT ON TABLE  customers            IS 'Customer master entity';
COMMENT ON COLUMN customers.deleted_at IS 'Soft-delete marker; NULL = active';

CREATE INDEX idx_customers_active ON customers (email)
    WHERE deleted_at IS NULL;

CREATE TABLE locations (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    street_address  VARCHAR(300) NOT NULL,
    city            VARCHAR(150) NOT NULL,
    state_province  VARCHAR(150),
    country         VARCHAR(100) NOT NULL,
    postal_code     VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT uq_locations_address UNIQUE (street_address, city, country, postal_code)
);

COMMENT ON TABLE locations IS 'Reusable address/location master entity';

CREATE TABLE customer_locations (
    customer_id  UUID      NOT NULL,
    location_id  UUID      NOT NULL,
    is_primary   BOOLEAN   NOT NULL DEFAULT FALSE,
    assigned_at  TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_customer_locations PRIMARY KEY (customer_id, location_id),

    CONSTRAINT fk_cl_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT fk_cl_location FOREIGN KEY (location_id)
        REFERENCES locations (id) ON DELETE CASCADE
);

COMMENT ON TABLE  customer_locations            IS 'M:N junction between customers and locations';
COMMENT ON COLUMN customer_locations.is_primary IS 'Whether this is the customers primary address';

CREATE INDEX idx_cl_customer ON customer_locations (customer_id);
CREATE INDEX idx_cl_location ON customer_locations (location_id);

CREATE UNIQUE INDEX uq_cl_one_primary_per_customer
    ON customer_locations (customer_id)
    WHERE is_primary = TRUE;
