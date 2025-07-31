# Restaurant Management System

A RESTful API application built with Spring Boot to manage restaurant operations, including order management, table bookings, and user management.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Setup & Run](#setup--run)
- [Testing](#testing)
- [Logging](#logging)
- [Project Structure](#project-structure)


---

## Project Overview

This project provides a backend API for managing restaurant operations such as:

- Creating, retrieving, and updating customer orders.
- Booking and managing tables.
- User registration and authentication.

With robust validation, exception handling, and logging for key operations.

---

## Architecture

- **Spring Boot** REST API framework
- **Spring Data JPA** for relational database interaction
- **Custom Exceptions** for error reporting
- **SLF4J + Logback** for logging
- **JUnit 5 + Mockito** for comprehensive service and web layer tests

---

## Key Features

- **Order Management:**
    - Create new orders with items, retrieve orders, update order status.

- **Table Booking:**
    - Book tables for future times, retrieve/update/cancel bookings, validation for date/time.

- **User Management:**
    - Register users with unique email, fetch users, authenticate via credentials.

- **Validation & Exception Handling:**
    - All inputs are validated; application returns clear errors for invalid requests.

- **Logging:**
    - Services and controllers log key actions & errors.

- **Testing:**
    - Unit and API tests ensure correctness, high coverage.

---

## Getting Started

### Prerequisites

- Java 17+ (e.g., Amazon Corretto 17)
- Maven 3.6+
- PostgreSQL database (or use H2 for demo/unit tests)
- IDE (IntelliJ IDEA recommended)

### Setup & Run

1. **Clone the repository**
    ```
    git clone https://github.com/YOUR_USERNAME/RestaurantManagement.git
    cd RestaurantManagement
    ```

2. **Configure database connection** in `src/main/resources/application.properties`
    ```
    spring.datasource.url=jdbc:postgresql://localhost:5432/restaurantdb
    spring.datasource.username=your_db_user
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update
    ```

   _For H2 in-memory for demo/testing:_
    ```
    spring.datasource.url=jdbc:h2:mem:testdb
    spring.datasource.driverClassName=org.h2.Driver
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.h2.console.enabled=true
    ```

3. **Build the project and run tests**
    ```
    mvn clean install
    ```

4. **Run the application**
    ```
    mvn spring-boot:run
    ```
   Or run `RestaurantManagementApplication` from your IDE.

5. **Access the API**  
   By default at: [http://localhost:8080](http://localhost:8080)

---

## Project Structure
````
src/main/java/org/zeta/RestaurantManagement/
├── RestaurantManagementApplication.java
├── entity/
│ ├── Order.java
│ ├── TableBooking.java
│ └── User.java
├── repository/
│ ├── OrderRepository.java
│ ├── TableBookingRepository.java
│ └── UserRepository.java
├── service/
│ ├── OrderService.java
│ ├── TableBookingService.java
│ └── UserService.java
├── controller/
│ ├── OrderController.java
│ ├── TableBookingController.java
│ └── UserController.java
└── exception/
├── BadRequestException.java
├── OrderNotFoundException.java
├── ResourceNotFoundException.java
├── UserAlreadyExistsException.java
├── UserNotFoundException.java
└── InvalidCredentialsException.java
````


## GitHub Repository

> [https://github.com/keerthitalakoti/RestaurantManagement](https://github.com/YOUR_USERNAME/RestaurantManagement)

---



# rms-spring
# rms-spring
