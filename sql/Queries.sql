-- View passwords using HEX or CHAR casting since VARBINARY hides the passwords.
SELECT user_id, firstname, lastname, email, HEX(password) AS password, street, city, province, country, postalcode 
FROM User;

SELECT user_id, firstname, lastname, email, CAST(password AS CHAR) AS password, street, city, province, country, postalcode 
FROM User;


ALTER TABLE `Order` AUTO_INCREMENT = 7;
