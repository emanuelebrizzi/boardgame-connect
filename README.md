# Boardgame Connect

Boardgame Connect is a full-stack web application designed to connect board game players with local associations.

## 🚀 Technologies

### Frontend

- **Framework:** [Angular 21](https://angular.io/)
- **State Management:** Angular Signals

### Backend

- **Framework:** [Spring Boot 3](https://spring.io/projects/spring-boot)
- **Language:** Java 21
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA (Hibernate)
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Build Tool:** Maven

### Deployment

- **Nginx**
- **Docker Compose**

---

## 🛠️ Architecture

The application is built as a distributed system using Docker containers, following modern software engineering principles.

### 1. Infrastructure Architecture

Nginx acts as the single entry point (Reverse Proxy), routing traffic to the appropriate service based on the URL. This ensures a clean separation between the client and server while solving CORS issues in production.

| URL Path  | Service      | Internal Port | Description                                       |
| :-------- | :----------- | :------------ | :------------------------------------------------ |
| `/`       | **Frontend** | 80            | Serves the Angular static files (SPA)             |
| `/api/**` | **Backend**  | 8080          | Proxies API requests to the Spring Boot container |

### 2. Application Architecture

#### 🔌 RESTful API

The system follows a **RESTful architecture**. The backend exposes resources (Users, Games, Associations) via standard HTTP methods (`GET`, `POST`, `PUT`, `DELETE`), ensuring a stateless and predictable interface.

#### 🎨 Frontend: Component-Service-Model

The Angular client is structured using the **Component-Service-Model** pattern to ensure separation of concerns:

- **Models:** TypeScript interfaces defining the shape of data.
- **Services:** Injectable classes handling business logic, state management (Signals), and API communication.
- **Components:** Standalone UI units responsible solely for presenting data and capturing user events.

#### ⚙️ Backend: MVC

The Spring Boot application adapts the classic **Model-View-Controller (MVC)** pattern for REST APIs:

- **Controller:** Handles incoming HTTP requests and maps them to service methods.
- **Model:** Represented by JPA Entities and DTOs (Data Transfer Objects) that manage data and business rules.
- **View:** Instead of rendering HTML server-side, the "View" is the JSON representation of the data returned to the client.

---

## 📦 Prerequisites

Docker and Docker Compose are required to run the full application.

---

## ⚡ Quick Start

The easiest way to run the application is using Docker Compose. This will set up the database, backend, and frontend automatically.

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/emanuelebrizzi/game-connect.git
    cd boardgame-connect
    ```

2.  **Build and Run:**

    ```bash
    docker-compose up --build --force-recreate -d
    ```

3.  **Access the Application:**
    Open your browser and navigate to:

    > **http://localhost**

4.  **Stop the Application:**
    Press `Ctrl+C` in the terminal or run:
    ```bash
    docker-compose down
    ```
