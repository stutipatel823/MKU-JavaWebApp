-- Select the database
USE mku;

-- User Table
CREATE TABLE User (
    user_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phonenumber VARCHAR(15),
    password VARBINARY(255) NOT NULL, -- VARBINARY to securely store hashed passwords
    street VARCHAR(100),
    city VARCHAR(50),
    province VARCHAR(50),
    country VARCHAR(50),
    postalcode VARCHAR(20)
);

-- Cart Table
CREATE TABLE Cart (
    cart_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT UNSIGNED,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE -- if the User is deleted, all related carts are removed for data integrity
);

-- Payment Table
CREATE TABLE Payment (
    payment_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    payment_method VARCHAR(20) NOT NULL,
    card_number VARBINARY(256), -- Store as VARCHAR for encrypted card numbers
    card_expiration_date DATE,
    paypal_email VARCHAR(100),
    user_id INT UNSIGNED,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE -- if User is deleted, all related payment records are removed
);

-- Order Table
CREATE TABLE `Order` (
    order_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_date DATETIME NOT NULL, -- Changed to DATETIME to capture both date and time
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    user_id INT UNSIGNED,
    cart_id INT UNSIGNED,
    payment_id INT UNSIGNED, -- Ensures a one-to-one relationship with Payment (can be NULL if no payment exists)
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE, -- if the User is deleted, related orders are also deleted
    FOREIGN KEY (cart_id) REFERENCES Cart(cart_id) ON DELETE SET NULL, -- retains order record if cart is deleted
    FOREIGN KEY (payment_id) REFERENCES Payment(payment_id) ON DELETE SET NULL -- retains order record if payment is deleted
);

-- Admin Table
CREATE TABLE Admin (
    admin_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARBINARY(255) NOT NULL -- VARBINARY to securely store hashed passwords
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
    admin_id INT UNSIGNED,
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE SET NULL -- retains product record if admin is deleted
);

-- Cart_Product Table (many-to-many relationship between Cart and Product)
CREATE TABLE Cart_Product (
    cart_id INT UNSIGNED NOT NULL,
    product_id INT UNSIGNED NOT NULL,
    quantity INT DEFAULT 1,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES Cart(cart_id) ON DELETE CASCADE, -- removes all related cart-product links if cart is deleted
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

