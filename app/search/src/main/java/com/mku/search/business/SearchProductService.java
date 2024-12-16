package com.mku.search.business;

import com.mku.search.persistence.SearchProduct_CRUD;
import com.mku.search.helper.ProductInfo;
import com.mku.search.helper.ProductsXML;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchProductService {

    public ProductsXML allProducts() throws SQLException {
        ArrayList<ProductInfo> products = SearchProduct_CRUD.allProducts();
        return new ProductsXML(products); // Convert the list to XML format
    }
    // Search products based on the input query, brand, and category
    public ProductsXML searchByQuery(String searchInput, String brandSelect, String categorySelect) {
        ArrayList<ProductInfo> products = SearchProduct_CRUD.searchForProducts(searchInput, brandSelect, categorySelect);
        return new ProductsXML(products); // Convert the list to XML format
    }

    // Set brands and categories for filtering
    public void setBrandsAndCategories(ArrayList<String> brands, ArrayList<String> categories) {
        SearchProduct_CRUD.setBrandAndCategoryAttributes(brands, categories); // Populating lists from CRUD layer
    }

}
