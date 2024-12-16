CREATE DATABASE Payment_MKU;

USE Payment_MKU;

CREATE TABLE Payment (
    payment_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    payment_method ENUM('Credit Card', 'Debit Card', 'PayPal') NOT NULL,
    card_number VARCHAR(16), 
    card_expiration_date CHAR(7),  -- Store month and year in 'MM-YYYY' format
    paypal_email VARCHAR(100),
    user_id INT UNSIGNED
    -- FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE -- if User is deleted, all related payment records are removed
);

INSERT INTO Payment (payment_method, user_id, card_number, card_expiration_date, paypal_email) VALUES
('Credit Card', 1, '4111111111111111', '2026-12', NULL),
('Debit Card', 2, '4222222222222222', '2025-11', NULL),
('PayPal', 3, NULL, NULL, 'cathy@example.com'),
('Credit Card', 4, '4333333333333333', '2024-10', NULL),
('PayPal', 5, NULL, NULL, 'eva@example.com');
