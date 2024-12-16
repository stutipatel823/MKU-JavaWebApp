--  search.sql
CREATE DATABASE IF NOT EXISTS Search_MKU;

USE Search_MKU;

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
	admin_id INT UNSIGNED -- Only if admin is needed for this context
   --  FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE SET NULL -- retains product record if admin is deleted
);

INSERT INTO Product (admin_id, name, description, imageURL, price, brand, category, availability, stock) VALUES
(1, 'MAC Matte Lipstick', 'Matte finish lipstick in various shades.', 'MAC_Matte_Lipstick.avif', 19.99, 'MAC', 'Lips', TRUE, 4),
(1, 'Estee Lauder Double Wear Foundation', 'Liquid foundation with full coverage.', 'Estee_Lauder_Double_Wear_Foundation.avif', 29.99, 'Estee Lauder', 'Face', TRUE, 80),
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
(1, 'Burts Bees Lip Balm', 'Moisturizing lip balm for soft lips.', 'Burts_Bees_Lip_Balm.avif', 4.99, 'Burt’s Bees', 'Lips', TRUE, 300);

