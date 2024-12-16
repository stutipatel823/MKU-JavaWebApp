package com.mku.business;

import com.mku.helper.ProductInfo;
import com.mku.helper.ProductsXML;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class SearchAPIService implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(SearchAPIService.class.getName());
    String searchService = System.getenv("searchService");
    private final String BASE_SEARCH_URL = "http://"+searchService+"/api/search";
    private final Client client;

    public SearchAPIService() {
        this.client = ClientBuilder.newClient();
    }

    // Fetch products based on search parameters
    public ArrayList<ProductInfo> fetchProducts(String query, String brand, String category, String token) throws Exception {
        // URL encode the query, brand, and category parameters to handle special characters
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8.toString());

        // Encode brand to handle spaces or special characters
        String encodedBrand = URLEncoder.encode(brand, StandardCharsets.UTF_8.toString());

        // Build the URL with the encoded parameters
        String url = String.format("%s/products?query=%s&brand=%s&category=%s", BASE_SEARCH_URL, encodedQuery, encodedBrand, encodedCategory);

        LOGGER.info("Fetching products with URL: " + url);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .get();

        try {
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("No products found.");
                return new ArrayList<>(); // Return empty list if no content
            }

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMessage = "Failed to fetch products. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            ProductsXML productsXML = response.readEntity(ProductsXML.class);
            return productsXML.getProducts();
        } finally {
            response.close();
        }
    }

    // Fetch filters for brands and categories
    public Map<String, Object> fetchFilters(String token) throws Exception {
        String url = BASE_SEARCH_URL + "/filters";
        LOGGER.info("Fetching filters from URL: " + url);

        WebTarget target = client.target(url);
        Response response = target.request()
                .header("Authorization", "Bearer " + token)
                .get();

        try {
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                String errorMessage = "Failed to fetch filters. HTTP status: " + response.getStatus();
                LOGGER.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            // Read the response and use Jackson's ObjectMapper to convert it to a Map
            String jsonResponse = response.readEntity(String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> filters = objectMapper.readValue(jsonResponse, Map.class);
            return filters;
        } finally {
            response.close();
        }
    }

    @Override
    public void close() {
        // Close the client only when the service is no longer needed, typically at the end of the servlet's lifecycle
        if (client != null) {
            client.close();
            LOGGER.info("SearchAPIService client closed.");
        }
    }
}
