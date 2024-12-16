CREATE DATABASE Order_MKU;

USE Order_MKU;

-- Order Table
CREATE TABLE `Order` (
    order_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_date DATETIME NOT NULL, -- Changed to DATETIME to capture both date and time
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date DATETIME,
    status ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    user_id INT UNSIGNED,
    cart_id INT UNSIGNED,
    payment_id INT UNSIGNED -- Ensures a one-to-one relationship with Payment (can be NULL if no payment exists)
    -- FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE, -- if the User is deleted, related orders are also deleted
    -- FOREIGN KEY (cart_id) REFERENCES Cart(cart_id) ON DELETE SET NULL, -- retains order record if cart is deleted
    -- FOREIGN KEY (payment_id) REFERENCES Payment(payment_id) ON DELETE SET NULL -- retains order record if payment is deleted
);
-- Product Table
CREATE TABLE Product (
    product_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    imageURL VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    brand VARCHAR(50),
    category VARCHAR(50),
    availability BOOLEAN DEFAULT TRUE,
    stock INT DEFAULT 0,
    admin_id INT UNSIGNED
    -- FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE SET NULL -- retains product record if admin is deleted
);
-- Order_Product Table (many-to-many relationship between Order and Product)
CREATE TABLE Order_Product (
    order_id INT UNSIGNED NOT NULL,
    product_id INT UNSIGNED NOT NULL,
    quantity INT DEFAULT 1,
    PRIMARY KEY (order_id, product_id)
    -- FOREIGN KEY (order_id) REFERENCES `Order`(order_id) ON DELETE CASCADE, -- removes all related order-product links if order is deleted
    -- FOREIGN KEY (product_id) REFERENCES Product(product_id) -- retains product if order is deleted
);

INSERT INTO `Order` (user_id, cart_id, order_date, amount, transaction_date, status, payment_id) VALUES
(1, 1, '2024-10-01 12:00:00', 59.97, '2024-11-06 02:28:17', 'Shipped', 1),
(2, 2, '2024-10-02 13:00:00', 39.98, '2024-11-06 02:28:17', 'Delivered', 2),
(3, 3, '2024-10-03 14:00:00', 19.99, NULL, 'Pending', NULL),
(4, 4, '2024-10-04 15:00:00', 79.95, '2024-11-06 02:28:17', 'Cancelled', 4),
(5, 5, '2024-10-05 16:00:00', 99.90, '2024-11-06 02:28:17', 'Shipped', 5);
-- (1, 1, '2024-10-02 13:00:00', 39.98, 'Delivered', 2),
-- (1, 1, '2024-10-03 14:00:00', 19.99, 'Pending', NULL),

-- Insert Products
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
