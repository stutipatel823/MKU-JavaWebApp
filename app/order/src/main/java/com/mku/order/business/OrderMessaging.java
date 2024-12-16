package com.mku.order.business;

import com.mku.order.helper.ProductsXML;
import com.mku.order.persistence.Order_CRUD;
import io.grpc.stub.StreamObserver;
import io.kubemq.sdk.event.Subscriber;
import io.kubemq.sdk.subscription.SubscribeRequest;
import io.kubemq.sdk.subscription.SubscribeType;
import io.kubemq.sdk.event.EventReceive;
import io.kubemq.sdk.tools.Converter;
import io.kubemq.sdk.event.Event;
import io.kubemq.sdk.subscription.EventsStoreType;
import javax.net.ssl.SSLException;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import io.kubemq.sdk.event.Channel;
import java.io.IOException;
import java.sql.SQLException;
public class OrderMessaging {

    private ProductsXML productsXML; 
    private double totalAmount;      
    private int orderId;  // Instance variable instead of static

    // Constructor to initialize instance variables
    public OrderMessaging(ProductsXML productsXML, double totalAmount) {
        this.productsXML = productsXML;
        this.totalAmount = totalAmount;
    }

    public OrderMessaging() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Getter for orderId (accessed after the order is placed)
    public int getOrderId() {
        return orderId;
    }

    public void initiateOrder(int userId) throws IOException, SSLException {
        sendOrderInitiatedEvent(userId);
    }

    private void sendOrderInitiatedEvent(int userId) throws IOException {
        String channelName = "order_payment_channel";
        String clientID = "order-service-sender";
        String kubeMQAddress = System.getenv("kubeMQAddress");

        Channel channel = new Channel(channelName, clientID, false, kubeMQAddress);
        channel.setStore(true);

        Event event = new Event();
        String message = "ORDER_INITIATED:" + userId;
        event.setBody(Converter.ToByteArray(message));
        event.setEventId("order-initiation-" + System.currentTimeMillis());

        try {
            channel.SendEvent(event);
            System.out.println("Order initiation event sent: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to send order initiation event.", e);
        }
    }

    public void receivePaymentVerifiedEvent() throws SSLException, ServerAddressNotSuppliedException {
        String channelName = "payment_order_channel";
        String clientID = "order-service-subscriber";
        String kubeMQAddress = System.getenv("kubeMQAddress");

        Subscriber subscriber = new Subscriber(kubeMQAddress);
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setChannel(channelName);
        subscribeRequest.setClientID(clientID);
        subscribeRequest.setSubscribeType(SubscribeType.EventsStore);
        subscribeRequest.setEventsStoreType(EventsStoreType.StartAtSequence);
        subscribeRequest.setEventsStoreTypeValue(1);

        subscriber.SubscribeToEvents(subscribeRequest, new PaymentVerifiedStreamObserver());
    }

    private class PaymentVerifiedStreamObserver implements StreamObserver<EventReceive> {
        @Override
        public void onNext(EventReceive value) {
            try {
                String message = (String) Converter.FromByteArray(value.getBody());
                System.out.println("Received payment verified event: " + message);

                String[] messageParts = message.split(":");
                if (messageParts.length == 3 && messageParts[0].equals("PAYMENT_VERIFIED")) {
                    int userId = Integer.parseInt(messageParts[1]);
                    boolean paymentValid = Boolean.parseBoolean(messageParts[2]);

                    if (paymentValid) {
                        // Payment verified, proceed with placing the order
                        placeOrder(userId);
                    } else {
                        // Handle payment failure
                        System.out.println("Payment verification failed. Order cannot be placed.");
                    }
                } else {
                    System.out.println("Invalid payment verification message format.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("Error in payment verification subscription: " + t.getMessage());
        }

        @Override
        public void onCompleted() {
            System.out.println("Subscription completed.");
        }

        private void placeOrder(int userId) throws IOException, SQLException {
            // Example: Generate cartId and paymentId from userId, modify logic as needed
            int cartId = userId;  
            int paymentId = userId;

            // Insert the order into the database
            orderId = Order_CRUD.insertOrder(userId, cartId, paymentId, totalAmount, productsXML.getProducts());

            if (orderId == -1) {
                throw new SQLException("Failed to place order for user: " + userId);
            }

            System.out.println("Order placed for user: " + userId);
            sendOrderPlacedEvent(userId);
        }

        private void sendOrderPlacedEvent(int userId) throws IOException {
            String channelName = "order_cart_channel";
            String clientID = "order-service-sender";
            String kubeMQAddress = System.getenv("kubeMQAddress");

            io.kubemq.sdk.event.Channel channel = new io.kubemq.sdk.event.Channel(channelName, clientID, false, kubeMQAddress);
            channel.setStore(true);

            Event event = new Event();
            String message = "ORDER_PLACED:" + userId;
            event.setBody(Converter.ToByteArray(message));
            event.setEventId("order-placed-" + System.currentTimeMillis());

            try {
                channel.SendEvent(event);
                System.out.println("Order placed event sent to clear cart: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

//public class OrderMessaging {
//
//    private static ProductsXML productsXML; // Static field to store ProductsXML for the order
//    private static double totalAmount;      // Static field to store totalAmount for the order
//    public static int orderId;      // Static field to store totalAmount for the order
//
//
//    // Static method to initiate order
//    public static void initiateOrder(int userId, ProductsXML cartData, double totalAmount) throws IOException, SSLException {
//        OrderMessaging.productsXML = cartData;    // Save ProductsXML for later use
//        OrderMessaging.totalAmount = totalAmount; // Save totalAmount for later use
//
//        sendOrderInitiatedEvent(userId);
//    }
//
//    // Static method to send only userId during order initiation
//    private static void sendOrderInitiatedEvent(int userId) throws IOException {
//        String channelName = "order_payment_channel";  // Payment service channel
//        String clientID = "order-service-sender"; // Unique client ID for Order Service
//        String kubeMQAddress = System.getenv("kubeMQAddress");
//
//        Channel channel = new Channel(channelName, clientID, false, kubeMQAddress);
//        channel.setStore(true);
//
//        Event event = new Event();
//        String message = "ORDER_INITIATED:" + userId; // Only send userId
//        event.setBody(Converter.ToByteArray(message));
//        event.setEventId("order-initiation-" + System.currentTimeMillis());
//
//        try {
//            channel.SendEvent(event);
//            System.out.println("Order initiation event sent: " + message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Static method to receive payment verified event and process the order
//    public static void receivePaymentVerifiedEvent() throws SSLException, ServerAddressNotSuppliedException {
//        String channelName = "payment_order_channel"; // Order channel for payment verification
//        String clientID = "order-service-subscriber"; // Unique subscriber ID for Order Service
//        String kubeMQAddress = System.getenv("kubeMQAddress");
//
//        Subscriber subscriber = new Subscriber(kubeMQAddress);
//        SubscribeRequest subscribeRequest = new SubscribeRequest();
//        subscribeRequest.setChannel(channelName);
//        subscribeRequest.setClientID(clientID);
//        subscribeRequest.setSubscribeType(SubscribeType.EventsStore);
//        subscribeRequest.setEventsStoreType(EventsStoreType.StartAtSequence);
//        subscribeRequest.setEventsStoreTypeValue(1);
//
//        subscriber.SubscribeToEvents(subscribeRequest, new PaymentVerifiedStreamObserver());
//    }
//
//    // Static nested class for the StreamObserver implementation
//    private static class PaymentVerifiedStreamObserver implements StreamObserver<EventReceive> {
//        @Override
//        public void onNext(EventReceive value) {
//            try {
//                String message = (String) Converter.FromByteArray(value.getBody());
//                System.out.println("Received payment verified event: " + message);
//
//                String[] messageParts = message.split(":");
//                if (messageParts.length == 2 && messageParts[0].equals("PAYMENT_VERIFIED")) {
//                    int userId = Integer.parseInt(messageParts[1]);
//                    boolean paymentValid = Boolean.parseBoolean(messageParts[2]);
//
//                    if (paymentValid) {
//                        // Payment verified, proceed with placing the order
//                        placeOrder(userId);
//                    } else {
//                        // Handle payment failure
//                        System.out.println("Payment verification failed. Order cannot be placed.");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onError(Throwable t) {
//            System.out.println("Error in payment verification subscription: " + t.getMessage());
//        }
//
//        @Override
//        public void onCompleted() {
//            System.out.println("Subscription completed.");
//        }
//
//        // Static method to place the order
//        private static void placeOrder(int userId) throws IOException, SQLException {
//            // Generate IDs for cart and payment (as an example)
//            int cartId = userId; // Example: Derive cartId from userId
//            int paymentId = userId; // Example: Derive paymentId from userId
//
//            // Insert the order into the database
//            orderId = Order_CRUD.insertOrder(userId, cartId, paymentId, OrderMessaging.totalAmount, OrderMessaging.productsXML.getProducts());
//
//            System.out.println("Order placed for user: " + userId);
//            // Send "Order Placed" event to clear the cart
//            sendOrderPlacedEvent(userId);
//        }
//
//        // Static method to send Order Placed event to clear the cart
//        private static void sendOrderPlacedEvent(int userId) throws IOException {
//            String channelName = "order_cart_channel";  // Cart service channel to clear the cart
//            String clientID = "order-service-sender"; // Unique client ID for Order Service
//            String kubeMQAddress = System.getenv("kubeMQAddress");
//
//            io.kubemq.sdk.event.Channel channel = new io.kubemq.sdk.event.Channel(channelName, clientID, false, kubeMQAddress);
//            channel.setStore(true);
//
//            Event event = new Event();
//            String message = "ORDER_PLACED:" + userId;
//            event.setBody(Converter.ToByteArray(message));
//            event.setEventId("order-placed-" + System.currentTimeMillis());
//
//            try {
//                channel.SendEvent(event);
//                System.out.println("Order placed event sent to clear cart: " + message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
