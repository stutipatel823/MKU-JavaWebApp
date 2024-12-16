package com.mku.payment.business;

import com.mku.payment.helper.PaymentInfo;
import com.mku.payment.persistence.Payment_CRUD;
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
import java.io.IOException;
import java.sql.SQLException;

public class PaymentMessaging {

    // Receive Order Initiated event and process payment verification
    public static void receiveOrderInitiatedEvent() throws SSLException, ServerAddressNotSuppliedException {
        String channelName = "order_payment_channel"; // Payment service channel
        String clientID = "payment-service-subscriber"; // Subscriber ID for Payment Service
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
                    System.out.println("Received order initiated event: " + message);

                    // Process payment (e.g., check if payment exists)
                    String[] messageParts = message.split(":");
                    if (messageParts.length == 2 && messageParts[0].equals("ORDER_INITIATED")) {
                        String userId = messageParts[1];

                        // Check if payment exists and is valid
                        boolean paymentValid = checkPaymentStatus(userId);  // Assuming this checks the Payment table

                        // Send Payment Verified event to Order Service
                        sendPaymentVerifiedEvent(userId, paymentValid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error in subscription: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Subscription completed.");
            }

            // Simulate checking payment status (this would query your database)
            private boolean checkPaymentStatus(String userId) {
                // Placeholder logic: assuming payment is always successful
                
                if(userId == null || userId.isEmpty()){
                    System.out.println("Invalid user ID.");
                    return false;
                }
                try{
                    int userIdInt = Integer.parseInt(userId);
                    PaymentInfo paymentInfo = Payment_CRUD.getPaymentMethodByUserId(userIdInt);
                    
                    if(paymentInfo == null){
                        System.out.println("No payment method found for user ID: " + userId);
                        return false;
                    }
                     // If all checks pass, the payment method is considered valid
                    System.out.println("Payment method is valid for user ID: " + userId);
                    return true;
                } catch (NumberFormatException e) {
                    // Handle the case where userId is not a valid integer
                    System.out.println("Invalid user ID format: " + userId);
                    return false;
                } catch (SQLException e) {
                    // Handle database errors
                    System.out.println("Error while checking payment status for user ID: " + userId);
                    e.printStackTrace();
                    return false;
                }
                
            }

            // Send the payment verified event to Order Service
            private void sendPaymentVerifiedEvent(String userId, boolean paymentValid) throws IOException {
                String channelName = "payment_order_channel";  // Channel to communicate with Order Service
                String clientID = "payment-service-sender"; // Unique client ID for Payment Service
                String kubeMQAddress = System.getenv("kubeMQAddress");

                io.kubemq.sdk.event.Channel channel = new io.kubemq.sdk.event.Channel(channelName, clientID, false, kubeMQAddress);
                channel.setStore(true);

                Event event = new Event();
                String message = "PAYMENT_VERIFIED:" + userId + ":" + paymentValid;
                event.setBody(Converter.ToByteArray(message));
                event.setEventId("payment-verified-" + System.currentTimeMillis());

                try {
                    channel.SendEvent(event);
                    System.out.println("Payment verified event sent: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
