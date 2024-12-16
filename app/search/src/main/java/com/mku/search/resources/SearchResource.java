package com.mku.search.resources;

import com.mku.search.business.SearchProductService;
import com.mku.search.helper.ProductsXML;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Path("/search")
public class SearchResource {

    private final SearchProductService searchProductService = new SearchProductService();
    /**
     * Search for products based on query, brand, and category.
     *
     * @return Response containing all products in db.
     */
    @GET
    @Path("/temp")
    @Produces(MediaType.APPLICATION_XML)
    public Response searchProducts() {
        try {
            ProductsXML products = searchProductService.allProducts();

            if (products.getProducts() == null || products.getProducts().isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(products).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred while searching for products.")
                    .build();
        }
    }
    /**
     * Search for products based on query, brand, and category.
     *
     * @param query     The search input query.
     * @param brand     The brand filter.
     * @param category  The category filter.
     * @return Response containing the search results in XML format.
     */
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_XML)
    public Response searchProducts(@QueryParam("query") String query,
                                   @QueryParam("brand") String brand,
                                   @QueryParam("category") String category) {
        try {
            ProductsXML products = searchProductService.searchByQuery(query, brand, category);

            if (products.getProducts() == null || products.getProducts().isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(products).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred while searching for products.")
                    .build();
        }
    }

    
    /**
     * Retrieve distinct brands and categories for filtering.
     *
     * @return Response containing lists of brands and categories in JSON format.
     */
    @GET
    @Path("/filters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBrandsAndCategories() {
        try {
            ArrayList<String> brands = new ArrayList<>();
            ArrayList<String> categories = new ArrayList<>();
            searchProductService.setBrandsAndCategories(brands, categories);

            // Create a map to hold brands and categories
            Map<String, Object> filters = new HashMap<>();
            filters.put("brands", brands);
            filters.put("categories", categories);

            return Response.ok(filters).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred while fetching brands and categories.")
                    .build();
        }
    }


//    /**
//     * Add a product to the user's cart.
//     *
//     * @param userId    The ID of the user.
//     * @param productId The ID of the product to add.
//     * @return Response indicating success or failure.
//     */
//    @POST
//    @Path("/cart/{userId}/add")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response addProductToCart(@PathParam("userId") int userId,
//                                     @QueryParam("productId") int productId) {
//        try {
//            boolean success = searchProductService.addProductToCart(userId, productId);
//
//            if (success) {
//                return Response.ok("Product successfully added to the cart.").build();
//            } else {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Failed to add product to the cart.")
//                        .build();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("Error occurred while adding product to the cart.")
//                    .build();
//        }
//    }
}
