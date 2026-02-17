# TravelApp API

REST API for managing trips, itineraries, activities and expenses with real-time budget tracking.

This project was built as a full-stack portfolio application focusing on:

* Clean architecture principles
* Domain-driven organization
* JWT authentication
* Financial consistency (budget health calculation)
* Real world relational modeling

---

## Tech Stack

* Java 17
* Spring Boot
* Spring Security + JWT
* Spring Data JPA (Hibernate)
* PostgreSQL
* Maven

---

## Features

### Authentication

* User registration
* Login with JWT
* Token validation
* Stateless security

### Trips

* Create and manage trips
* Date range calculation
* Trip summary aggregation

### Itinerary

* Day planning
* Activity scheduling

### Activities

* Categorized activities
* Linked to itinerary days

### Expenses

* Create / update / delete expenses
* Expense categories
* Automatic trip total calculation

### Budget

* Budget limit per trip
* Real-time budget health:

    * healthy (< 70%)
    * warning (< 90%)
    * danger (< 100%)
    * exceeded (> 100%)

---

## Domain Concepts

The system models a real travel planning workflow:

User → Trip → ItineraryDay → Activity
User → Trip → Expense → BudgetHealth

Budget health is calculated dynamically from total expenses vs budget limit.

---

## Running the project

### 1 — Database

Create a PostgreSQL database:

```
travelapp
```

Update `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/travelapp
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

---

### 2 — Run the API

```
./mvnw spring-boot:run
```

Server will start at:

```
http://localhost:8080
```

---

## Authentication

All protected endpoints require:

```
Authorization: Bearer <token>
```

Get token via:

```
POST /auth/login
```

---

## Main Endpoints

### Auth

POST /auth/register
POST /auth/login

### Trips

GET /trips
POST /trips
GET /trips/{id}/summary

### Budget

PUT /trips/{id}/budget

### Expenses

GET /trips/{id}/expenses
POST /expenses
PUT /expenses/{id}
DELETE /expenses/{id}

---

## Project Structure

```
domain        → business rules and calculations
service       → use cases
repository    → persistence
controller    → HTTP layer
security      → authentication and filters
```

---

## Design Decisions

The project intentionally separates domain logic from controllers.
Business rules such as budget health are not implemented in the frontend
to guarantee consistency across clients.

---

## Future Improvements

* Multi currency support
* Trip sharing between users
* Export to PDF
* Notifications
* Caching layer (Redis)
* Docker containerization

---

## Author

Paulo Henrique dos Anjos
