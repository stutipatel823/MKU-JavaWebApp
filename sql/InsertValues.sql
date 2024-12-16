USE mku;

-- 1. Insert Users
INSERT INTO User (firstname, lastname, email, phonenumber, password, street, city, province, country, postalcode) VALUES
('Alice', 'Smith', 'alice@example.com', '1234567890', 'password123', '123 Main St', 'Springfield', 'IL', 'USA', '62701'),
('Bob', 'Johnson', 'bob@example.com', '2345678901', 'password123', '456 Elm St', 'Centerville', 'OH', 'USA', '45459'),
('Cathy', 'Williams', 'cathy@example.com', '3456789012', 'password123', '789 Oak St', 'Metropolis', 'NY', 'USA', '10001'),
('David', 'Brown', 'david@example.com', '4567890123', 'password123', '321 Pine St', 'Gotham', 'NJ', 'USA', '07001'),
('Eva', 'Jones', 'eva@example.com', '5678901234', 'password123', '654 Maple St', 'Star City', 'CA', 'USA', '90001');

-- 2. Insert Admins
INSERT INTO Admin (firstname, lastname, email, password) VALUES
('John', 'Doe', 'john.doe@example.com', 'password123'),
('Jane', 'Smith', 'jane.smith@example.com', 'password123'),
('Emily', 'Davis', 'emily.davis@example.com', 'password123');

-- 3. Insert Products
INSERT INTO Product (admin_id, name, description, imageURL, price, brand, category, availability, stock) VALUES
(1, 'MAC Matte Lipstick', 'Matte finish lipstick in various shades.', 'MAC_Matte_Lipstick.avif', 19.99, 'MAC', 'Lips', TRUE, 4),
(1, 'Estée Lauder Double Wear Foundation', 'Liquid foundation with full coverage.', 'Estee_Lauder_Double_Wear_Foundation.avif', 29.99, 'Estée Lauder', 'Face', TRUE, 80),
(1, 'Maybelline Lash Sensational Mascara', 'Waterproof mascara for volume.', 'Maybelline_Lash_Sensational_Mascara.avif', 24.99, 'Maybelline', 'Eyes', TRUE, 120),
(2, 'Urban Decay Naked Eyeshadow Palette', 'A palette with 12 shades.', 'Urban_Decay_Naked_Eyeshadow_Palette.avif', 39.99, 'Urban Decay', 'Eyes', TRUE, 60),
(2, 'NARS Blush', 'Powder blush for a natural glow.', 'NARS_Blush.avif', 18.99, 'NARS', 'Face', TRUE, 150),
(2, 'Becca Shimmering Skin Perfector Highlighter', 'Creamy highlighter for a dewy look.', 'Becca_Shimmering_Skin_Perfector_Highlighter.avif', 22.99, 'Becca', 'Face', TRUE, 90),
(3, 'Neutrogena Makeup Remover Cleansing Towelettes', 'Gentle makeup remover wipes.', 'Neutrogena_Makeup_Remover.avif', 15.99, 'Neutrogena', 'Skincare', TRUE, 200),
(3, 'OPI Nail Lacquer', 'Vibrant colors for nail art.', 'OPI_Nail_Lacquer.avif', 9.99, 'OPI', 'Nails', TRUE, 110),
(3, 'Urban Decay All Nighter Setting Spray', 'Long-lasting makeup setting spray.', 'Urban_Decay_All_Nighter_Setting_Spray.avif', 21.99, 'Urban Decay', 'Face', TRUE, 75),
(1, 'Garnier BB Cream', 'All-in-one BB cream with SPF.', 'Garnier_BB_Cream.avif', 27.99, 'Garnier', 'Face', TRUE, 95),
(2, 'Fenty Beauty Gloss Bomb', 'Shiny lip gloss with a hint of color.', 'Fenty_Beauty_Gloss_Bomb.avif', 14.99, 'Fenty Beauty', 'Lips', TRUE, 130),
(2, 'Kat Von D Tattoo Liner', 'Liquid eyeliner for precise application.', 'Kat_Von_D_Tattoo_Liner.avif', 12.99, 'Kat Von D', 'Eyes', TRUE, 85),
(3, 'The Ordinary Hydrating Face Mask', 'Hydrating face mask for all skin types.', 'The_Ordinary_Hydrating_Face_Mask.avif', 5.99, 'The Ordinary', 'Skincare', TRUE, 200),
(1, 'CeraVe Hydrating Facial Cleanser', 'Gentle cleanser for daily use.', 'CeraVe_Hydrating_Facial_Cleanser.avif', 10.99, 'CeraVe', 'Skincare', TRUE, 140),
(1, 'Clinique Moisture Surge', 'Hydrating moisturizer for dry skin.', 'Clinique_Moisture_Surge.avif', 23.99, 'Clinique', 'Skincare', TRUE, 90),
(2, 'La Roche-Posay Anthelios Sunscreen', 'Broad-spectrum SPF 50 sunscreen.', 'La_Roche_Posay_Anthelios_Sunscreen.avif', 19.99, 'La Roche-Posay', 'Skincare', TRUE, 120),
(2, 'Olay Regenerist Micro-Sculpting Serum', 'Anti-aging serum for youthful skin.', 'Olay_Regenerist_Serum.avif', 49.99, 'Olay', 'Skincare', TRUE, 50),
(3, 'Anastasia Beverly Hills Brow Wiz', 'Define your eyebrows with ease.', 'Anastasia_Beverly_Hills_Brow_Wiz.avif', 11.99, 'Anastasia Beverly Hills', 'Eyes', TRUE, 130),
(3, 'Smashbox Photo Finish Foundation Primer', 'Smooth primer for even application.', 'Smashbox_Photo_Finish_Primer.avif', 25.99, 'Smashbox', 'Face', TRUE, 70),
(1, 'Burt’s Bees Lip Balm', 'Moisturizing lip balm for soft lips.', 'Burts_Bees_Lip_Balm.avif', 4.99, 'Burt’s Bees', 'Lips', TRUE, 300);

-- 4. Create Carts for Each User
INSERT INTO Cart (user_id) VALUES
(1),
(2),
(3),
(4),
(5);

-- 5. Insert Payments
INSERT INTO Payment (payment_method, user_id, card_number, card_expiration_date, paypal_email) VALUES
('Credit Card', 1, '4111111111111111', '2026-12', NULL),
('Debit Card', 2, '4222222222222222', '2025-11', NULL),
('PayPal', 3, NULL, NULL, 'cathy@example.com'),
('Credit Card', 4, '4333333333333333', '2024-10', NULL),
('PayPal', 5, NULL, NULL, 'eva@example.com');

-- 6. Insert Orders with Payment IDs
INSERT INTO `Order` (user_id, cart_id, order_date, amount, transaction_date, status, payment_id) VALUES
(1, 1, '2024-10-01 12:00:00', 59.97, '2024-11-06 02:28:17', 'Shipped', 1),
(2, 2, '2024-10-02 13:00:00', 39.98, '2024-11-06 02:28:17', 'Delivered', 2),
(3, 3, '2024-10-03 14:00:00', 19.99, NULL, 'Pending', NULL),
(4, 4, '2024-10-04 15:00:00', 79.95, '2024-11-06 02:28:17', 'Cancelled', 4),
(5, 5, '2024-10-05 16:00:00', 99.90, '2024-11-06 02:28:17', 'Shipped', 5);
-- (1, 1, '2024-10-02 13:00:00', 39.98, 'Delivered', 2),
-- (1, 1, '2024-10-03 14:00:00', 19.99, 'Pending', NULL),



-- 7. Insert Cart_Product (linking Carts and Products)
INSERT INTO Cart_Product (cart_id, product_id, quantity) VALUES
(1, 1, 2),
(1, 5, 1),
(2, 2, 1),
(2, 7, 3),
(3, 4, 2),
(4, 10, 1),
(5, 11, 1),
(5, 15, 2),
(5, 20, 1);

-- Insert Products Ordered for Each Order
INSERT INTO Order_Product (order_id, product_id, quantity) VALUES
(1, 1, 2),    -- Order 1 includes 2 units of Product 1 (MAC Matte Lipstick)
(1, 5, 1),    -- Order 1 includes 1 unit of Product 5 (NARS Blush)
(2, 2, 1),    -- Order 2 includes 1 unit of Product 2 (Estée Lauder Double Wear Foundation)
(2, 7, 3),    -- Order 2 includes 3 units of Product 7 (Neutrogena Makeup Remover Cleansing Towelettes)
(3, 4, 2),    -- Order 3 includes 2 units of Product 4 (Urban Decay Naked Eyeshadow Palette)
(4, 10, 1),   -- Order 4 includes 1 unit of Product 10 (Garnier BB Cream)
(5, 11, 1),   -- Order 5 includes 1 unit of Product 11 (Fenty Beauty Gloss Bomb)
(5, 15, 2),   -- Order 5 includes 2 units of Product 15 (Clinique Moisture Surge)
(5, 20, 1);   -- Order 5 includes 1 unit of Product 20 (Burt’s Bees Lip Balm)

-- (7, 11, 1),   -- Order 5 includes 1 unit of Product 11 (Fenty Beauty Gloss Bomb)
-- (7, 7, 3),    -- Order 2 includes 3 units of Product 7 (Neutrogena Makeup Remover Cleansing Towelettes)
-- (7, 10, 1),   -- Order 4 includes 1 unit of Product 10 (Garnier BB Cream)
-- (7, 15, 2),   -- Order 5 includes 2 units of Product 15 (Clinique Moisture Surge)
-- (7, 2, 1),    -- Order 2 includes 1 unit of Product 2 (Estée Lauder Double Wear Foundation)
-- (8, 20, 1);   -- Order 5 includes 1 unit of Product 20 (Burt’s Bees Lip Balm)
-- (8, 4, 2),    -- Order 3 includes 2 units of Product 4 (Urban Decay Naked Eyeshadow Palette)