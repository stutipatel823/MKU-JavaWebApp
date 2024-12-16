package com.mku.cart.business;

import com.mku.cart.persistence.Cart_CRUD;
import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.subscription.EventsStoreType;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.tools.Converter;
import io.kubemq.sdk.event.Subscriber;
import javax.net.ssl.SSLException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartMessaging {

    // Method to listen for the Order Placed event and clear the cart
    public static void receiveOrderPlacedEvent() throws SSLException, ServerAddressNotSuppliedException { // Clear cart on order placed
        String channelName = "order_cart_channel";  // Channel to subscribe to for order placed events
        String clientID = "cart-service-subscriber"; // Unique client ID for Cart Service
        String kubeMQAddress = System.getenv("kubeMQAddress");

        Subscriber subscriber = new Subscriber(kubeMQAddress);
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setChannel(channelName);
        subscribeRequest.setClientID(clientID);
        subscribeRequest.setSubscribeType(SubscribeType.EventsStore);
        subscribeRequest.setEventsStoreType(EventsStoreType.StartAtSequence);
        subscribeRequest.setEventsStoreTypeValue(1);

        subscriber.SubscribeToEvents(subscribeRequest, new StreamObserver<EventReceive>() {
            @Override
            public void onNext(EventReceive value) {
                try {
                    String message = (String) Converter.FromByteArray(value.getBody());
                    System.out.println("Received order placed event: " + message);

                    // Process order placed event and clear cart
                    String[] messageParts = message.split(":");
                    if (messageParts.length == 2 && messageParts[0].equals("ORDER_PLACED")) {
                        int userId = Integer.parseInt(messageParts[1]);
                        System.out.println("Clearing cart for user: " + userId);
                        clearCart(userId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.getLogger(CartMessaging.class.getName()).log(Level.SEVERE, "Error processing order placed event", e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error in event subscription: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Subscription completed.");
            }
        });
    }

    // Simulate clearing the cart for the user
    private static void clearCart(int userId) throws SQLException {
        // Logic to clear the cart (e.g., remove items from database or cache)
        Cart_CRUD.emptyCart(userId);
        System.out.println("Cleared the cart for user: " + userId);
        
    }
}
