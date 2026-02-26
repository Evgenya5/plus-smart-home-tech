CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    shopping_cart_id UUID,
    delivery_country VARCHAR(255),
    delivery_city VARCHAR(255),
    delivery_street VARCHAR(255),
    delivery_house VARCHAR(100),
    delivery_flat VARCHAR(100),
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50) NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price NUMERIC(19, 2),
    delivery_price NUMERIC(19, 2),
    product_price NUMERIC(19, 2)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);