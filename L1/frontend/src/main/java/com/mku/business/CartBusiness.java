package com.mku.business;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import com.mku.helper.ProductsXML;
import com.mku.helper.ProductInfo;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class CartBusiness {

    public static ProductsXML getCartItems(String userToken) throws IOException {
        Client client = ClientBuilder.newClient();

        // Communicate with the Cart Service to retrieve the cart items
        WebTarget cartTarget = client.target("http://localhost:8080/CartService/webresources/cart/items");
        InputStream cartStream = cartTarget.queryParam("token", userToken)
                .request(MediaType.APPLICATION_XML)
                .get(InputStream.class);
        
        String cartXml = IOUtils.toString(cartStream, "utf-8");
        ProductsXML cartItems = cartxmlToProducts(cartXml);  // Convert the cart XML to ProductsXML

        // Check product availability in inventory
//        if (cartItems != null) {
//            Client inventoryClient = ClientBuilder.newClient();
//            WebTarget inventoryTarget = inventoryClient.target("http://" + inventoryService + "/InventoryService/webresources/inventory/check");
//            
//            for (ProductInfo product : cartItems.getProducts()) {
//                // Check product availability from the inventory service
//                InputStream availabilityStream = inventoryTarget.path(String.valueOf(product.getProductId()))
//                        .queryParam("quantity", product.getQuantity())
//                        .request(MediaType.APPLICATION_XML)
//                        .get(InputStream.class);
//
//                String availabilityXml = IOUtils.toString(availabilityStream, "utf-8");
//                boolean isAvailable = checkAvailability(availabilityXml);
//                product.setAvailability(isAvailable);
//            }
//        }

        return cartItems;
    }

    // Convert the XML response from Cart Service to ProductsXML object
    private static ProductsXML cartxmlToProducts(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ProductsXML.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (ProductsXML) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This function checks the availability of products
    private static boolean checkAvailability(String xml) {
        // Assuming the availability response contains a status element that says "available" or "unavailable"
        return xml.contains("<status>available</status>");
    }

    // Other cart actions (add to cart, remove from cart, etc.) can be implemented similarly
}
