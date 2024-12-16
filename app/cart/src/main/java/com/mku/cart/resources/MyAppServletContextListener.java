package com.mku.cart.resources;


import com.mku.cart.business.CartMessaging;
import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
public class MyAppServletContextListener 
               implements ServletContextListener{
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("ServletContextListener destroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
     Runnable r = new Runnable() {
         public void run() {
            
             try {
                CartMessaging.receiveOrderPlacedEvent();
             } catch (SSLException ex) {
                 Logger.getLogger(MyAppServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
             } catch (ServerAddressNotSuppliedException ex) {
                 Logger.getLogger(MyAppServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
             }
         }    
     };
     
     new Thread(r).start();
    }
}
//
//
//package com.mku.order.resources;
//
//import com.mku.order.business.OrderMessaging;
//import io.kubemq.sdk.basic.ServerAddressNotSuppliedException;
//
//import javax.net.ssl.SSLException;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class MyAppServletContextListener implements ServletContextListener {
//
//    private static final Logger logger = Logger.getLogger(MyAppServletContextListener.class.getName());
//    private Thread eventReceiverThread;
//
//    @Override
//    public void contextDestroyed(ServletContextEvent event) {
//        logger.info("ServletContextListener destroyed. Shutting down resources.");
//        if (eventReceiverThread != null && eventReceiverThread.isAlive()) {
//            eventReceiverThread.interrupt();
//            logger.info("Event receiver thread interrupted.");
//        }
//    }
//
//    @Override
//    public void contextInitialized(ServletContextEvent event) {
//        logger.info("ServletContextListener initialized. Starting event receiver.");
//
//        eventReceiverThread = new Thread(() -> {
//            try {
//                logger.info("Initializing OrderMessaging event receiver...");
//                OrderMessaging.receivePaymentVerifiedEvent();
//                logger.info("OrderMessaging event receiver initialized successfully.");
//            } catch (SSLException | ServerAddressNotSuppliedException ex) {
//                logger.log(Level.SEVERE, "Error initializing OrderMessaging event receiver", ex);
//            }
//        });
//
//        eventReceiverThread.setName("OrderMessaging-EventReceiver");
//        eventReceiverThread.start();
//    }
//}
