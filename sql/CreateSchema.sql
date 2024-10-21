-- Use the mku database
USE mku;

-- Create the Customer table with address fields allowing NULL
CREATE TABLE Customer (
    CustomerID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Firstname VARCHAR(255) NOT NULL,
    Lastname VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Phonenumber VARCHAR(10),
    Password VARCHAR(255) NOT NULL,
    Street VARCHAR(255) NULL,
    City VARCHAR(255) NULL,
    Province VARCHAR(255) NULL,
    Country VARCHAR(255) NULL,
    PostalCode VARCHAR(255) NULL
);

-- Create the Admin table
CREATE TABLE Admin (
    AdminID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Firstname VARCHAR(255) NOT NULL,
    Lastname VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE
);

-- Create the Product table
CREATE TABLE Product (
    ProductID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    AdminID INT UNSIGNED NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Description TEXT NOT NULL,
    ImageURL VARCHAR(255) NULL,
    Price DECIMAL(8, 2) NOT NULL,
    Brand VARCHAR(255) NOT NULL,
    Category VARCHAR(255) NOT NULL,
    Availability BOOLEAN NOT NULL,
    Stock INT NOT NULL,
    FOREIGN KEY (AdminID) REFERENCES Admin(AdminID)
);

-- Create the Cart table
CREATE TABLE Cart (
    CartID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT UNSIGNED NOT NULL,
    CreatedAt DATETIME NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Create the CartItem table
CREATE TABLE CartItem (
    CartItemID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ProductID INT UNSIGNED NOT NULL,
    CartID INT UNSIGNED NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (CartID) REFERENCES Cart(CartID)
);

-- Create the Order table
CREATE TABLE `Order` (
    OrderID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT UNSIGNED NOT NULL,
    OrderDate DATETIME NOT NULL,
    TotalAmount DECIMAL(8, 2) NOT NULL,
    Status VARCHAR(255) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Create the Payment table
CREATE TABLE Payment (
    PaymentID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    OrderID INT UNSIGNED NOT NULL,
    Amount DECIMAL(8, 2) NOT NULL,
    PaymentMethod VARCHAR(255) NOT NULL,
    TransactionDate DATETIME NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID)
);
