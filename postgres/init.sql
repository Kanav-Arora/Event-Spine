CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS customers (
    cust_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cust_name VARCHAR(100) NOT NULL,
    cust_email VARCHAR(100) UNIQUE NOT NULL,
    cust_address VARCHAR,
    cust_city VARCHAR(100),
    cust_country VARCHAR(100),
    cust_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(100) NOT NULL,
    product_description VARCHAR,
    category VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_name VARCHAR(100) NOT NULL,
    warehouse_city VARCHAR(100) NOT NULL,
    warehouse_country VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    inventory_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID NOT NULL REFERENCES products(product_id),
    warehouse_id    UUID NOT NULL REFERENCES warehouses(warehouse_id),
    quantity INT NOT NULL,
    updated_at      TIMESTAMP DEFAULT NOW(),
    UNIQUE (product_id, warehouse_id)
);

CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cust_id UUID NOT NULL REFERENCES customers(cust_id),
    order_status TEXT NOT NULL CHECK (order_status IN (
        'CREATED',
        'PAYMENT_SUCCESSFULL',
        'PAYMENT_FAILED',
        'SHIPMENT_SUCCESSFULL',
        'SHIPMENT_FAILED',
        'CANCELLED',
        'REJECTED',
        'COMPLETED'
    )),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(product_id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(warehouse_id),
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS events (
    event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
