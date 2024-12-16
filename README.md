# Version 4: Microservices Architecture with Docker and Kubernetes

## Overview

Version 4 introduces a **Microservices Architecture** to the web application. The system is broken into **five microservices**: **Frontend**, **Cart**, **Payment**, **Order**, and **Search**. Each service operates independently with its own database, improving scalability, flexibility, and maintainability. Additionally, **Docker** is used for containerization and **Kubernetes** for orchestration, ensuring easy deployment, scaling, and management of the microservices.

## Microservices Breakdown

1. **Frontend Microservice**:
   - Manages user login/signup via **JWT** authentication.
   - Communicates with the **Cart**, **Order**, **Payment**, and **Search** services to retrieve and display relevant data.

2. **Cart Microservice**:
   - Handles user cart actions, like adding or removing items.
   - Communicates with the **Order** and **Payment** services to manage the cart contents.

3. **Payment Microservice**:
   - Processes payments and manages payment information.
   - Integrates with **Frontend** and **Order** services to ensure successful payment processing.

4. **Order Microservice**:
   - Manages the order lifecycle, from cart finalization to order completion.
   - Works with the **Cart** and **Payment** services to finalize orders.

5. **Search Microservice**:
   - Provides search capabilities for products.
   - Interfaces with the **Frontend** to display search results based on user queries.

## Docker Configuration

### Dockerfiles

Each microservice and its respective database are containerized using **Docker**. There are two Dockerfiles for each service: one for the **web application** and one for the **database**.

1. **Web Application Dockerfile**:
   Each service (e.g., frontend, cart, payment) has its own Dockerfile to build a container image for the application.

2. **Database Dockerfile**:
   Each service also has a dedicated database (e.g., MySQL) with its own Dockerfile, configuring the database container for each service.

### Docker Compose

The `docker-compose.yml` file is used to define and run multi-container Docker applications. It allows all microservices and their databases to run in isolated containers but still communicate with each other via internal networks. Below is the updated `docker-compose.yml`:

### `docker-compose.yml`

```yaml
version: "3.8"

services:
  # Frontend Application Service
  frontend-app:
    build:
      context: .
      dockerfile: Dockerfile-FrontendWebApp  # Dockerfile for the frontend web application
    ports:
      - "8080:8080"  # Expose Tomcat port to localhost for the frontend app
    environment:
      DB_URL: frontend-db  # The hostname 'frontend-db' points to the frontend-db service
      DB_PORT: 3306
      cartService: cart-webapp:8080  # Internal hostname and port for the cart service
      orderService: order-webapp:8080 # Internal hostname and port for the order service
      paymentService: payment-webapp:8080 # Internal hostname and port for the payment service
      searchService: search-webapp:8080 # Internal hostname and port for the search service
    networks:
      - app-network  # Connect to the app-network
    depends_on:
      - frontend-db  # Ensure the frontend database starts first
      - cart-webapp  # Ensure the cart service starts before the frontend app
      - order-webapp  # Ensure the order service starts before the frontend app
      - payment-webapp  # Ensure the payment service starts before the frontend app
      - search-webapp  # Ensure the search service starts before the frontend app

    deploy:
      resources:
        limits:
          cpus: "0.5"
          memory: 512M
        reservations:
          cpus: "0.2"
          memory: 256M

  # Frontend Database Service (MySQL)
  frontend-db:
    image: stutipatel8/learn:frontenddb  # Custom MySQL image for frontend database
    platform: linux/amd64
    build:
      context: .
      dockerfile: Dockerfile-FrontendDB  # Dockerfile for the frontend DB
    environment:
      MYSQL_ROOT_PASSWORD: root1234  # Set root password for MySQL
      MYSQL_DATABASE: Frontend_MKU  # Name of the database for frontend
    ports:
      - "3307:3306"  # Expose MySQL port for the frontend DB (localhost:3307 -> container:3306)
    networks:
      - app-network
    volumes:
      - frontend-db-data:/var/lib/mysql  # Persist MySQL data for the frontend DB

  # Cart Application Service
  cart-webapp:
    build:
      context: .
      dockerfile: Dockerfile-CartWebApp  # Dockerfile for the cart web application
    ports:
      - "8081:8080"  # Expose Cart webapp on a different port (localhost:8081 -> container:8080)
    environment:
      DB_URL: cart-db  # The hostname 'cart-db' points to the cart-db service
      DB_PORT: 3306
    networks:
      - app-network  # Connect to the app-network
    depends_on:
      - cart-db  # Ensure the cart database starts before the web app

  # Cart Database Service (MySQL)
  cart-db:
    image: stutipatel8/learn:cartdb  # Custom MySQL image for cart database
    platform: linux/amd64
    build:
      context: .
      dockerfile: Dockerfile-CartDB  # Dockerfile for the cart DB
    environment:
      MYSQL_ROOT_PASSWORD: root1234  # Set root password for MySQL
      MYSQL_DATABASE: Cart_MKU  # Name of the database for the cart
    ports:
      - "3308:3306"  # Expose MySQL port for the cart DB (localhost:3308 -> container:3306)
    networks:
      - app-network
    volumes:
      - cart-db-data:/var/lib/mysql  # Persist MySQL data for the cart DB

networks:
  app-network:
    driver: bridge

volumes:
  frontend-db-data:
  cart-db-data:
```

### Kubernetes Configuration

For Kubernetes, each microservice has its own **YAML** configuration file for deployment. Environment variables are set for inter-service communication, and Persistent Volume Claims (PVCs) are used for data storage.

#### Example Kubernetes Deployment YAML for Frontend Service

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-app
  namespace: microservices
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend-app
  template:
    metadata:
      labels:
        app: frontend-app
    spec:
      containers:
        - name: frontend-app
          image: stutipatel8/learn:frontendWebApp
          ports:
            - containerPort: 8080
          env:
            - name: DB_URL
              value: "frontend-db"
            - name: DB_PORT
              valueFrom:
                configMapKeyRef:
                  name: app-config
                  key: DB_PORT
            .
            .
            .
            .
            .
```
#### Frontend Using `getenv` to Access Cart's API
```java 
String cartService = System.getenv("cartService");
private final String BASE_CART_URL = "http://" + cartService + "/api/carts/";
```
#### Example Persistent Volume Claim (PVC) for Frontend Database

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: frontend-db-pvc
  namespace: microservices
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
```

### Environment Variables and API Configuration

Each microservice utilizes **environment variables** to configure connections to databases and other services. For example, the **Frontend** service sets the `DB_URL`, `DB_PORT`, and other service URLs (such as for **Cart**, **Order**, **Payment**, and **Search**) via environment variables to make the necessary API calls.

#### Example: Retrieving Database Connection with Environment Variables

```java
private static Connection getConnection() throws SQLException {
    String dbUrl = System.getenv("DB_URL");
    String dbPort = System.getenv("DB_PORT");
    
    if (dbUrl == null || dbUrl.isEmpty() || dbPort == null || dbPort.isEmpty()) {
        throw new IllegalStateException("DB_URL or DB_PORT environment variable is not set");
    }

    String DB_URL = "jdbc:mysql://" + dbUrl + ":" + dbPort + "/Frontend_MKU";
    
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("MySQL Driver not found", e);
    }

    return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
}
```
### Conclusion

In **Version 4**, the project is restructured into **Microservices** to provide better scalability and maintainability. Docker and Kubernetes are used to containerize, deploy, and manage the application, with each microservice running in its own container and communicating with others through well-defined APIs. This version simplifies scaling, deployment, and management of individual services, while enabling easy communication between the frontend and backend components.