CREATE TABLE cart_items (
                            id UUID PRIMARY KEY,
                            product_id UUID NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            quantity INT NOT NULL,
                            price NUMERIC(10, 2) NOT NULL,
                            user_id UUID NOT NULL
);
