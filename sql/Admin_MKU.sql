CREATE DATABASE Admin_MKU;

USE Admin_MKU;

CREATE TABLE Admin (
    admin_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL -- Changed to VARCHAR for storing hashed passwords as a string
);
INSERT INTO Admin (firstname, lastname, email, password) VALUES
('John', 'Doe', 'john.doe@example.com', 'password123'),
('Jane', 'Smith', 'jane.smith@example.com', 'password123'),
('Emily', 'Davis', 'emily.davis@example.com', 'password123');
