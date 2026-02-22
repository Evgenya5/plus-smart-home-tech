CREATE SCHEMA IF NOT EXISTS payments;

CREATE TABLE IF NOT EXISTS payments.payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_cost NUMERIC(19, 2),
    delivery_total NUMERIC(19, 2),
    fee_total NUMERIC(19, 2),
    total_payment NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL
);
