# Spring Boot JWT Authentication and Authorization

This project is an API developed with Spring Boot that provides user authentication and authorization using JSON Web Tokens (JWT) and Spring Security. The API handles different user roles with various permissions.

## Features
* User registration and login with JWT authentication
* Password encryption using BCrypt
* Role-based authorization with Spring Security
* Customized access denied handling
* Logout mechanism
* Refresh token

## Technologies

- Java 21
- Spring Boot 3.x
- Spring Security
- JWT (JSON Web Tokens)
- Maven

## Requirements

- JDK 21 or higher
- Maven 3.x
- Database (PostgreSQL)

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/abakudev/auth-service.git
    ```
2. Navigate to the project directory:
    ```bash
    cd auth-service
    ```
3. Configure the database in the `application.yml` file:
    ```yml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/your_database
        username: your_username
        password: your_password
      jpa:
        hibernate:
          ddl-auto: update
    ```
4. Build and run the application:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
-> The application will be available at http://localhost:8080.


---

Thank you for using our API! If you have any questions or issues, feel free to open an issue.
