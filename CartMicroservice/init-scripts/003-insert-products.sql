DO $$
DECLARE
category_list TEXT[] := ARRAY['Electronics', 'Books', 'Clothing', 'Home', 'Toys', 'Sports', 'Beauty'];
    status_list TEXT[] := ARRAY['available', 'unavailable', 'out of stock'];
    i INT;
BEGIN
FOR i IN 1..100 LOOP
        INSERT INTO products (
            name, description, price, stock_quantity, category, seller_id, status, image_url
        )
        VALUES (
            'Product ' || i, -- Name
            'This is a description for product ' || i, -- Description
            round((random() * 30000)::numeric, 2), -- Random price between 0.00 and 1000.00
            floor(random() * 1000)::int, -- Random stock quantity between 0 and 100
            category_list[floor(random() * array_length(category_list, 1) + 1)], -- Random category
            floor(random() * 100 + 1)::int, -- Random seller_id between 1 and 50
            status_list[floor(random() * array_length(status_list, 1) + 1)], -- Random status
            'https://example.com/product_' || i || '.jpg' -- Random image URL
        );
END LOOP;
END $$;
