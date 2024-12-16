package com.mku.frontend;

import com.mku.business.SearchProductService;
import com.mku.helper.ProductsXML;
import com.mku.helper.ProductInfo;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SearchProductsServlet", urlPatterns = {"/searchproducts", "/addToCart"})
public class SearchProductsServlet extends HttpServlet {

    private SearchProductService searchProductService = new SearchProductService();

    // Search for products
    private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchInput = request.getParameter("searchInput");
        String brandSelect = request.getParameter("brandSelect");
        String categorySelect = request.getParameter("categorySelect");

        // Call the business service to search for products
        ProductsXML productsXML = searchProductService.searchByQuery(searchInput, brandSelect, categorySelect);
        ArrayList<ProductInfo> products = productsXML.getProducts();
        request.setAttribute("AllProducts", products);
        request.setAttribute("brandValue", brandSelect);
        request.setAttribute("categoryValue", categorySelect);

        // Get brands and categories for filters
        ArrayList<String> brands = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        searchProductService.setBrandsAndCategories(brands, categories);

        request.setAttribute("brands", brands);
        request.setAttribute("categories", categories);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/searchproducts.jsp");
        dispatcher.forward(request, response);
    }

    // Add a product to the cart via Cart microservice
    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int productId = Integer.parseInt(request.getParameter("productId"));

            // Call the service to add the product to the cart
            boolean success = searchProductService.addProductToCart(userId, productId);

            // Set response content type to plain text
            response.setContentType("text/plain");

            // Return "success" or "error" based on the outcome
            if (success) {
                response.getWriter().write("success");
            } else {
                response.getWriter().write("error");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400 Bad Request
            response.getWriter().write("Invalid user ID or product ID");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
            response.getWriter().write("An error occurred while processing your request.");
            e.printStackTrace();  // Log the exception for debugging
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        search(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("search".equals(action)) {
            search(request, response);
        } else if ("addToCart".equals(action)) {
            addToCart(request, response);  // Call the backend service here
        }
    }
    @Override
    public String getServletInfo() {
        return "SearchProductsServlet handles search, brand, and category filtering for products, and adding products to the cart.";
    }
}
