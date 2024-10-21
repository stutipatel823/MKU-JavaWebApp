-- Use the correct database
USE mku;

-- 1. Insert Customers
INSERT INTO Customer (Firstname, Lastname, Email, Phonenumber, Password, Street, City, Province, Country, PostalCode) VALUES
('Alice', 'Smith', 'alice@example.com', 1234567890, 'password123', '123 Main St', 'Springfield', 'IL', 'USA', '62701'),
('Bob', 'Johnson', 'bob@example.com', 2345678901, 'password123', '456 Elm St', 'Centerville', 'OH', 'USA', '45459'),
('Cathy', 'Williams', 'cathy@example.com', 3456789012, 'password123', '789 Oak St', 'Metropolis', 'NY', 'USA', '10001'),
('David', 'Brown', 'david@example.com', 4567890123, 'password123', '321 Pine St', 'Gotham', 'NJ', 'USA', '07001'),
('Eva', 'Jones', 'eva@example.com', 5678901234, 'password123', '654 Maple St', 'Star City', 'CA', 'USA', '90001');

-- 2. Insert Admins
INSERT INTO Admin (Firstname, Lastname, Password, Email) VALUES
('John', 'Doe', 'adminpass1', 'john.doe@example.com'),
('Jane', 'Smith', 'adminpass2', 'jane.smith@example.com'),
('Emily', 'Davis', 'adminpass3', 'emily.davis@example.com');

-- 3. Insert Products
INSERT INTO Product (AdminID, Name, Description, ImageURL, Price, Brand, Category, Availability, Stock) VALUES
(1, 'Lipstick', 'Matte finish lipstick in various shades.', 'url_to_image_1', 19.99, 'Brand A', 'Lips', TRUE, 100),
(1, 'Foundation', 'Liquid foundation with full coverage.', 'url_to_image_2', 29.99, 'Brand B', 'Face', TRUE, 80),
(1, 'Mascara', 'Waterproof mascara for volume.', 'url_to_image_3', 24.99, 'Brand C', 'Eyes', TRUE, 120),
(2, 'Eyeshadow Palette', 'A palette with 12 shades.', 'url_to_image_4', 39.99, 'Brand D', 'Eyes', TRUE, 60),
(2, 'Blush', 'Powder blush for a natural glow.', 'url_to_image_5', 18.99, 'Brand E', 'Face', TRUE, 150),
(2, 'Highlighter', 'Creamy highlighter for a dewy look.', 'url_to_image_6', 22.99, 'Brand F', 'Face', TRUE, 90),
(3, 'Makeup Remover', 'Gentle makeup remover wipes.', 'url_to_image_7', 15.99, 'Brand G', 'Skincare', TRUE, 200),
(3, 'Nail Polish', 'Vibrant colors for nail art.', 'url_to_image_8', 9.99, 'Brand H', 'Nails', TRUE, 110),
(3, 'Setting Spray', 'Long-lasting makeup setting spray.', 'url_to_image_9', 21.99, 'Brand I', 'Face', TRUE, 75),
(1, 'BB Cream', 'All-in-one BB cream with SPF.', 'url_to_image_10', 27.99, 'Brand J', 'Face', TRUE, 95),
(2, 'Lip Gloss', 'Shiny lip gloss with a hint of color.', 'url_to_image_11', 14.99, 'Brand K', 'Lips', TRUE, 130),
(2, 'Eyeliner', 'Liquid eyeliner for precise application.', 'url_to_image_12', 12.99, 'Brand L', 'Eyes', TRUE, 85),
(3, 'Face Mask', 'Hydrating face mask for all skin types.', 'url_to_image_13', 5.99, 'Brand M', 'Skincare', TRUE, 200),
(1, 'Face Wash', 'Gentle cleanser for daily use.', 'url_to_image_14', 10.99, 'Brand N', 'Skincare', TRUE, 140),
(1, 'Moisturizer', 'Hydrating moisturizer for dry skin.', 'url_to_image_15', 23.99, 'Brand O', 'Skincare', TRUE, 90),
(2, 'Sunscreen', 'Broad-spectrum SPF 50 sunscreen.', 'url_to_image_16', 19.99, 'Brand P', 'Skincare', TRUE, 120),
(2, 'Serum', 'Anti-aging serum for youthful skin.', 'url_to_image_17', 49.99, 'Brand Q', 'Skincare', TRUE, 50),
(3, 'Eyebrow Pencil', 'Define your eyebrows with ease.', 'url_to_image_18', 11.99, 'Brand R', 'Eyes', TRUE, 130),
(3, 'Face Primer', 'Smooth primer for even application.', 'url_to_image_19', 25.99, 'Brand S', 'Face', TRUE, 70),
(1, 'Lip Balm', 'Moisturizing lip balm for soft lips.', 'url_to_image_20', 4.99, 'Brand T', 'Lips', TRUE, 300);

-- 4. Create Carts for Each Customer
INSERT INTO Cart (CustomerID, CreatedAt) VALUES
(1, NOW()),
(2, NOW()),
(3, NOW()),
(4, NOW()),
(5, NOW());

-- 5. Inserting CartItems for Each Cart
INSERT INTO CartItem (ProductID, CartID, Quantity) VALUES
(1, 1, 2),  -- Alice adds 2 Lipsticks
(2, 1, 1),  -- Alice adds 1 Foundation
(3, 2, 3),  -- Bob adds 3 Mascaras
(4, 2, 1),  -- Bob adds 1 Eyeshadow Palette
(5, 3, 2),  -- Cathy adds 2 Blushes
(6, 3, 1),  -- Cathy adds 1 Highlighter
(7, 4, 2),  -- David adds 2 Makeup Remover
(8, 4, 2),  -- David adds 2 Nail Polishes
(9, 5, 1),  -- Eva adds 1 Setting Spray
(10, 5, 2); -- Eva adds 2 BB Creams