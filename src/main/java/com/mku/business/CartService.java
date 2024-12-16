package com.mku.business;

import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;
import com.mku.persistence.Cart_CRUD;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Service layer responsible for managing cart operations such as adding, updating, removing products,
 * and retrieving the items in a user's cart.
 */
public class CartService {

    /**
     * Retrieves all products in the user's cart.
     *
     * @param userId the ID of the user whose cart is being retrieved.
     * @return a ProductsXML object containing the list of products in the cart.
     * @throws SQLException if an error occurs during the database query.
     * @throws IllegalArgumentException if the cart is empty or does not exist for the user.
     */
    public ProductsXML getCartItemsByUserId(int userId) throws SQLException {
        Map<Integer, ProductInfo> cart = Cart_CRUD.getCartItemsByUserId(userId);
        ProductsXML productsXML = new ProductsXML();

        if (cart.isEmpty()) {
            // Option 1: Returning an empty ProductsXML object with no products
            return productsXML;

            // Option 2: return a message indicating the cart is empty
        } else {
            ArrayList<ProductInfo> products = new ArrayList<>(cart.values());
            productsXML.setProducts(products);
        }
        return productsXML;
    }


    /**
     * Adds a product to the user's cart.
     *
     * @param userId the ID of the user.
     * @param cartId the ID of the user's cart.
     * @param productId the ID of the product to add to the cart.
     * @throws SQLException if an error occurs during the database operation.
     */
    public void addProductToCart(int userId, int cartId, int productId) throws SQLException {
        Cart_CRUD.insertProductToCart(cartId, productId);
    }

    /**
     * Updates the quantity of a product in the user's cart.
     *
     * @param userId the ID of the user.
     * @param cartId the ID of the user's cart.
     * @param productId the ID of the product whose quantity is to be updated.
     * @param quantity the new quantity of the product.
     * @throws SQLException if an error occurs during the database operation.
     */
    public void updateProductQuantity(int userId, int cartId, int productId, int quantity) throws SQLException {
        Cart_CRUD.updateProductQuantity(cartId, productId, quantity);
    }

    /**
     * Removes a product from the user's cart.
     *
     * @param userId the ID of the user.
     * @param cartId the ID of the user's cart.
     * @param productId the ID of the product to be removed from the cart.
     * @throws SQLException if an error occurs during the database operation.
     */
    public void removeProductFromCart(int userId, int cartId, int productId) throws SQLException {
        Cart_CRUD.removeProductFromCart(cartId, productId);
    }

    /**
     * Creates a new cart for the user.
     *
     * @param userId the ID of the user for whom the cart is to be created.
     * @throws SQLException if an error occurs during the database operation.
     */
    public void createCart(int userId) throws SQLException {
        Cart_CRUD.createCart(userId);
    }
}
