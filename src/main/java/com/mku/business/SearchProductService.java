package com.mku.business;

import com.mku.persistence.SearchProduct_CRUD;
import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.ArrayList;

public class SearchProductService {

    // Search products based on the input query, brand, and category
    public ProductsXML searchByQuery(String searchInput, String brandSelect, String categorySelect) {
        ArrayList<ProductInfo> products = SearchProduct_CRUD.searchForProducts(searchInput, brandSelect, categorySelect);
        return new ProductsXML(products); // Convert the list to XML format
    }

    // Set brands and categories for filtering
    public void setBrandsAndCategories(ArrayList<String> brands, ArrayList<String> categories) {
        SearchProduct_CRUD.setBrandAndCategoryAttributes(brands, categories); // Populating lists from CRUD layer
    }

    // Add a product to the cart via Cart microservice
    public boolean addProductToCart(int userId, int productId) throws IOException {
         // Construct the URL for the Cart microservice's endpoint
         String cartUrl = "http://localhost:8080/version3/api/carts/add/" + userId + "?cartId=" + userId + "&productId=" + productId;
         System.out.println("Calling Cart API: " + cartUrl); // For debugging

         // Open a connection to the Cart microservice
         URL url = new URL(cartUrl);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");
         connection.setDoOutput(true);

         // Check if the request was successful (HTTP 200 OK)
         int responseCode = connection.getResponseCode();
         return responseCode == HttpURLConnection.HTTP_OK;
     }

}
