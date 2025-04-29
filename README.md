# CliQshop Backend

## 📌 Project Overview

CliQshop Backend is a robust Spring Boot-based RESTful API that powers the CliQshop e-commerce platform. It provides comprehensive backend services for product management, order processing, user authentication, and more. This server-side application works in conjunction with the [CliQshop Frontend](https://github.com/hrishabh-16/cliqshop-frontend) to deliver a complete e-commerce solution.

## 🔧 Components

The backend architecture is organized into the following key components:

### Controllers
- **AddressController**: Handles user address management
- **AdminController**: Provides admin-specific operations
- **AuthController**: Manages authentication and authorization
- **CartController**: Processes shopping cart operations
- **CategoryController**: Manages product categories
- **HealthController**: System health monitoring endpoints
- **InventoryController**: Manages product inventory
- **OrderController**: Processes customer orders
- **PaymentController**: Handles payment processing with Stripe
- **ProductController**: Manages product information
- **ReportController**: Generates business reports
- **UserController**: Handles user account operations
- **WebhookController**: Processes Stripe webhook notifications

### Services
- **Address Service**: User address CRUD operations
- **Cart Service**: Shopping cart functionality
- **Category Service**: Product categorization
- **Inventory Service**: Stock management
- **Order Service**: Order processing and management
- **Product Service**: Product catalog management
- **User Service**: User account management
- **Authentication Service**: Security and user authentication

### Repositories
- JPA repositories for data access and persistence

## ✨ Features

### Core Features
- **RESTful API Design**: Well-structured endpoints following REST principles
- **Secure Authentication**: JWT-based authentication with role-based authorization
- **Database Integration**: MySQL database with JPA/Hibernate ORM
- **Payment Integration**: Stripe payment processing with webhook support
- **Data Validation**: Request validation using Bean Validation
- **Error Handling**: Comprehensive exception handling with custom error responses

### Security Features
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access Control**: Admin and user role separation
- **Password Encryption**: Secure password storage
- **CORS Configuration**: Configured for secure cross-origin requests

### Data Management
- **JPA/Hibernate ORM**: No manual table creation required - tables automatically generated from entity classes
- **Transaction Management**: ACID-compliant data operations
- **Data Transfer Objects**: Clean separation between API and domain models

## 🏗️ Project Structure

```
cliqshop-backend/
├── src/
│   ├── main/
│   │   ├── java/com/cliqshop/
│   │   │   ├── config/                # Application configurations
│   │   │   ├── controller/            # REST API controllers
│   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   ├── entity/                # JPA Entity classes
│   │   │   ├── exception/             # Custom exceptions
│   │   │   ├── payment/               # Stripe payment integration
│   │   │   ├── repository/            # JPA repositories
│   │   │   ├── security/              # JWT authentication
│   │   │   ├── service/               # Business logic services
│   │   │   └── websocket/             # WebSocket components (future)
│   │   └── resources/
│   │       └── application.properties # Application configuration
│   └── test/                          # Unit and integration tests
├── pom.xml                            # Maven dependencies
├── Dockerfile                         # Instructions for Docker image creation
├── docker-compose.yml                 # Docker Compose configuration
└── README.md
```

## 📦 Modules

- **Core Application**: Main Spring Boot application setup
- **Security**: JWT authentication and authorization
- **Data Access**: JPA/Hibernate repositories and entities
- **Service Layer**: Business logic implementation
- **Web Layer**: REST controllers and DTOs
- **Payment Processing**: Stripe integration
- **Configuration**: Application and infrastructure setup

## 🛠️ Technology Stack

- **Java 17**: Core programming language
- **Spring Boot 3.4.4**: Application framework
- **Spring Data JPA**: Data persistence
- **Spring Security**: Authentication and authorization
- **Hibernate ORM**: Object-relational mapping
- **MySQL**: Relational database
- **JWT**: JSON Web Token for stateless authentication
- **Stripe API**: Payment processing
- **Maven**: Dependency management and build tool
- **SLF4J**: Logging framework
- **Docker**: Containerization platform
- **Docker Compose**: Multi-container Docker applications

## 🔧 Dependencies Overview

- **spring-boot-starter-data-jpa**: JPA with Hibernate for database operations
- **spring-boot-starter-security**: Security framework for authentication and authorization
- **spring-boot-starter-web**: RESTful API support
- **spring-boot-starter-validation**: Request validation
- **stripe-java**: Stripe payment gateway integration
- **jsonwebtoken**: JWT token generation and validation
- **mysql-connector-j**: MySQL database connector
- **modelmapper**: Object mapping for DTO conversions

## 🚀 Setup Instructions

### Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8+
- Stripe account (for payment processing)
- Docker and Docker Compose (for containerized deployment)

### Option 1: Standard Setup

#### Database Setup
1. Install MySQL if not already installed
   ```bash
   # For Ubuntu
   sudo apt install mysql-server
   
   # For Windows
   # Download and install MySQL from https://dev.mysql.com/downloads/installer/
   ```

2. Create a database for the application
   ```sql
   CREATE DATABASE cliqshop;
   ```

3. No manual table creation is required - JPA/Hibernate will automatically create tables based on entity classes when the application starts with `spring.jpa.hibernate.ddl-auto=update`.

#### Stripe Integration Setup
1. Create a Stripe account at [stripe.com](https://stripe.com)
2. Get your API keys from the Stripe dashboard
3. Add the keys to your `application.properties`:
   ```properties
   stripe.api.secretKey=sk_test_your_secret_key
   stripe.api.publishableKey=pk_test_your_publishable_key
   ```

4. Set up Stripe Webhook:
   - Install Stripe CLI as detailed in the [Frontend README](https://github.com/hrishabh-16/cliqshop-frontend)
   - Configure your webhook to forward to your local backend:
     ```bash
     stripe listen --forward-to http://localhost:9000/api/webhook
     ```
   - Add the webhook secret to your `application.properties`:
     ```properties
     stripe.webhook.secret=whsec_your_webhook_secret
     ```

#### JWT Configuration
1. Generate a secure random string for your JWT secret key
2. Configure JWT in `application.properties`:
   ```properties
   jwt.secret-key=your_secure_jwt_secret_key
   jwt.expiration-time=86400000  # 24 hours in milliseconds
   ```

#### Running the Application (Standard)
1. **Clone the repository**
   ```bash
   git clone https://github.com/hrishabh-16/cliqshop-backend.git
   cd cliqshop-backend
   ```

2. **Configure application.properties**
   
   Create the file `src/main/resources/application.properties` with the following settings:
   ```properties
   # Database
   spring.datasource.url=jdbc:mysql://localhost:3306/cliqshop?useSSL=false
   spring.datasource.username=root
   spring.datasource.password=your_password
   
   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   
   # JWT
   jwt.secret-key=your_secure_jwt_secret_key
   jwt.expiration-time=86400000
   
   # Server port
   server.port=9000
   
   # For Stripe integration
   stripe.api.secretKey=sk_test_your_secret_key
   stripe.api.publishableKey=pk_test_your_publishable_key
   stripe.webhook.secret=whsec_your_webhook_secret
   ```

3. **Build the application**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Verify the application is running**
   - Access the health endpoint: http://localhost:9000/api/health
   - API will be available at http://localhost:9000/api

### Option 2: Docker Deployment

#### Prerequisites
- Docker 20.10+ and Docker Compose V2+
- Git

#### Running with Docker Compose

1. **Clone the repository**
   ```bash
   git clone https://github.com/hrishabh-16/cliqshop-backend.git
   cd cliqshop-backend
   ```

2. **Build the backend Docker image**
   ```bash
   docker build -t cliqshop-backend .
   ```

3. **Build the frontend Docker image**
   ```bash
   # For the frontend application, please refer to the frontend repository:
   # https://github.com/hrishabh-16/cliqshop-frontend

4. **Start the entire application stack**
   ```bash
   docker-compose up -d
   ```

5. **Verify the application is running**
   - Backend: http://localhost:9000/api/health
   - Frontend: http://localhost:4200

#### Docker Configuration Details

The Docker setup includes:

1. **Database container**:
   - MySQL 8.0 with automatic database creation
   - Persistent volume for data storage
   - Accessible on port 3307 (mapped from 3306)

2. **Backend container**:
   - Java 17 with Spring Boot
   - Built using Maven
   - Configured to wait for database readiness
   - Accessible on port 9000

3. **Frontend container**:
   - Angular application
   - Accessible on port 4200

#### Docker Environment Variables

You can customize the Docker environment by modifying the `docker-compose.yml` file:

- Database settings:
  ```yaml
  MYSQL_ROOT_PASSWORD: root
  MYSQL_DATABASE: clickshop
  MYSQL_USER: cliqshop
  MYSQL_PASSWORD: cliqshop
  ```

- Backend settings:
  ```yaml
  SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/clickshop?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
  SPRING_DATASOURCE_USERNAME: cliqshop
  SPRING_DATASOURCE_PASSWORD: cliqshop
  ```

#### Docker Commands

- **Start the application**:
  ```bash
  docker-compose up -d
  ```

- **View logs**:
  ```bash
  docker-compose logs -f
  ```

- **Stop the application**:
  ```bash
  docker-compose down
  ```

- **Stop and remove volumes**:
  ```bash
  docker-compose down -v
  ```

## 🔮 Future Implementations

- **WebSockets for Live Updates**: Implementation of WebSocket functionality to provide real-time product updates, live inventory updates, and order status changes to the frontend.

## **Contact**

For questions or support, reach out via [hrishabhgautam480@gmail.com] or raise an issue on the repository.
