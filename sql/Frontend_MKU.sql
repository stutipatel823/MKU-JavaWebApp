CREATE DATABASE Frontend_MKU;

use Frontend_MKU;
CREATE TABLE User (
    user_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phonenumber VARCHAR(15),
    password VARCHAR(255) NOT NULL, 
    street VARCHAR(100),
    city VARCHAR(50),
    province VARCHAR(50),
    country VARCHAR(50),
    postalcode VARCHAR(20)
);
INSERT INTO User (firstname, lastname, email, phonenumber, password, street, city, province, country, postalcode) VALUES
('Alice', 'Smith', 'alice@example.com', '1234567890', 'password123', '123 Main St', 'Springfield', 'IL', 'USA', '62701'),
('Bob', 'Johnson', 'bob@example.com', '2345678901', 'password123', '456 Elm St', 'Centerville', 'OH', 'USA', '45459'),
('Cathy', 'Williams', 'cathy@example.com', '3456789012', 'password123', '789 Oak St', 'Metropolis', 'NY', 'USA', '10001'),
('David', 'Brown', 'david@example.com', '4567890123', 'password123', '321 Pine St', 'Gotham', 'NJ', 'USA', '07001'),
('Eva', 'Jones', 'eva@example.com', '5678901234', 'password123', '654 Maple St', 'Star City', 'CA', 'USA', '90001');

