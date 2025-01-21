CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE products (
                          product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Unique identifier for the product as UUID
                          name VARCHAR(255) NOT NULL, -- Product name
                          description TEXT, -- Detailed description of the product
                          price DECIMAL(10, 2) NOT NULL CHECK (price >= 0), -- Product price, must be non-negative
                          stock_quantity INT NOT NULL CHECK (stock_quantity >= 0), -- Quantity in stock
                          category VARCHAR(100) NOT NULL, -- Product category
                          seller_id INT NOT NULL, -- Seller's unique identifier (to be added later with foreign key)
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Product creation timestamp
                          status VARCHAR(50) DEFAULT 'available' CHECK (status IN ('available', 'unavailable', 'out of stock')), -- Product status
                          image_url VARCHAR(500) -- URL for the product image
);
