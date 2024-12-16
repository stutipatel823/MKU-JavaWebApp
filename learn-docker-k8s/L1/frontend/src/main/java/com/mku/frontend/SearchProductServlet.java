package com.mku.frontend;

import com.mku.business.CartAPIService;
import com.mku.business.SearchAPIService;
import com.mku.helper.ProductInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SearchServletOne", urlPatterns = {"/searchproducts"})
public class SearchProductServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SearchProductServlet.class.getName());
    private final String authenticationCookieName = "login_token";

    // Utility method to get the user token from cookies
    private String getUserToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(authenticationCookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Process search request and call the API
    protected void processSearchRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String searchInput = request.getParameter("searchInput");
        String brandSelect = request.getParameter("brandSelect");
        String categorySelect = request.getParameter("categorySelect");

        // Check if the parameters are null or empty and handle accordingly
        if (searchInput == null || searchInput.trim().isEmpty()) {
            searchInput = "";  // Default to empty string if no search input
        }

        // If no brand or category is selected, set them to "All"
        if (brandSelect == null || brandSelect.trim().isEmpty()) {
            brandSelect = "";  // Default to "All" if no brand is selected
        }
        if (categorySelect == null || categorySelect.trim().isEmpty()) {
            categorySelect = "";  // Default to "All" if no category is selected
        }

        // Get the user token from cookies
        String token = getUserToken(request);
        if (token == null || token.isEmpty()) {
            LOGGER.warning("Unauthorized access attempt. No token found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        try (SearchAPIService searchAPIService = new SearchAPIService()) {
            // Fetch the search results from the API
            ArrayList<ProductInfo> products = searchAPIService.fetchProducts(searchInput, brandSelect, categorySelect, token);

            // If no products are found for the query, fetch all products
            if (products == null || products.isEmpty()) {
                LOGGER.info("No products found for the search query. Fetching all products.");
                products = searchAPIService.fetchProducts("", "", "", token); // Fetch all products
            }

            // Set the fetched products to the request attributes
            request.setAttribute("AllProducts", products);
            request.setAttribute("brandValue", brandSelect);
            request.setAttribute("categoryValue", categorySelect);

            // Check if filters are already available in the session
            Map<String, Object> filters = (Map<String, Object>) request.getSession().getAttribute("filters");
            if (filters == null) {
                // Fetch filters from the API if not present in session
                filters = searchAPIService.fetchFilters(token);
                if (filters != null) {
                    // Store the fetched filters in the session
                    request.getSession().setAttribute("filters", filters);
                }
            }

            // Set the filters (brands and categories) to the request
            if (filters != null) {
                request.setAttribute("brands", filters.get("brands"));
                request.setAttribute("categories", filters.get("categories"));
            }

            // Forward to the search products JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/searchproducts.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing search request.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    protected void addProductToCart(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        System.out.println("addProductToCart was envoked");
        // Implement add to cart functionality
        String productId = request.getParameter("productId");
        String token = getUserToken(request);
        Object userIdObj = request.getSession().getAttribute("userId");
        int userId;

        if (token == null || token.isEmpty()) {
            LOGGER.warning("Unauthorized access attempt. No token found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }

        if (productId == null || productId.isEmpty()) {
            LOGGER.warning("Invalid productId received.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
            return;
        }

        if (userIdObj == null || !(userIdObj instanceof Integer)) {
            LOGGER.warning("Invalid or missing userId in session.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID not found in session.");
            return;
        } else {
            userId = (int) userIdObj;
        }

        try (CartAPIService cartAPIService = new CartAPIService()) {
            cartAPIService.addCartItem(userId, productId, token);
            LOGGER.info("Successfully added product to cart: userId=" + userId + ", productId=" + productId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Product successfully added to cart.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to add product to cart: userId=" + userId + ", productId=" + productId, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add product to cart.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processSearchRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processSearchRequest(request, response);
        addProductToCart(request, response);
    }


    @Override
    public String getServletInfo() {
        return "SearchServlet handles product search, brand, and category filtering, and returns search results.";
    }
}
