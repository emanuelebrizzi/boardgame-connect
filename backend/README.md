# Boardgame Connect - Backend API

The backend REST API for the Boardgame Connect platform, designed to manage boardgame associations, players, and reservation systems. Built with modern Java and Spring Boot.

## 🛠 Tech Stack

* **Java 21**
* **Spring Boot 3**
* **Spring Security**
* **Spring Data JPA** 
* **PostgreSQL**
* **Docker Compose**
* **Testcontainers**

## 📦 Project Structure

* src/main/java: Source code.
* src/test/java: Unit tests.
* src/it/java: Integration tests.

## 🔐 Authentication

The API is secured using JWT (JSON Web Tokens).

* Public Endpoints: Login and Registration.
* Protected Endpoints: Require a valid Bearer Token in the Authorization header.

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

* **Java Development Kit (JDK) 21**
* **Docker Desktop** (Required for the database and integration tests)

## 🚀 Getting Started

This project uses **Spring Boot Docker Compose** support. When you start the application, it will automatically spin up a PostgreSQL container defined in `docker-compose.yml`, so you don't need to manually configure the database connection for local development.

### Running the Application

To start the backend server, open your terminal in the project root and run:

```bash
./mvnw spring-boot:run
```

The server will start on port 8080 (by default).

### Running Tests

The project includes a suite of Unit and Integration tests. Integration tests use Testcontainers to spin up ephemeral databases, ensuring a clean state for every test run.

To execute the full test suite, run:

```bash
./mvnw clean verify
```
