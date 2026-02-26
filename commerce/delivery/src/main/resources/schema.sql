CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    from_country VARCHAR(255),
    from_city VARCHAR(255),
    from_street VARCHAR(255),
    from_house VARCHAR(100),
    from_flat VARCHAR(100),
    to_country VARCHAR(255),
    to_city VARCHAR(255),
    to_street VARCHAR(255),
    to_house VARCHAR(100),
    to_flat VARCHAR(100),
    delivery_state VARCHAR(50) NOT NULL,
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile BOOLEAN
);
