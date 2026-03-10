# TravelApp API
REST API for managing trips, itineraries, activities, expenses, budgets, and destinations with real-time budget tracking and third-party integrations.
This project was built as a full-stack portfolio application focusing on:
* Clean architecture principles
* Domain-driven organization
* JWT authentication
* Financial consistency (budget health calculation)
* Real world relational modeling
* External API integrations for enhanced functionality
---
## Tech Stack
* Java 17
* Spring Boot
* Spring Security + JWT
* Spring Data JPA (Hibernate)
* PostgreSQL
* Maven
* Integrations: Amadeus (hotels), Geoapify (places), Unsplash (images)
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
* Auto-planning features
### Activities
* Categorized activities
* Linked to itinerary days
* Reordering support
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
### Destinations
* Destination lookup and resolution
* Place search via Geoapify
* Hotel search and booking via Amadeus
* Image fetching via Unsplash
* Country and city data via RestCountries
---
## Domain Concepts
The system models a real travel planning workflow:
User ? Trip ? ItineraryDay ? Activity
User ? Trip ? Expense ? BudgetHealth
Trip ? Destination ? Places/Hotels/Images
Budget health is calculated dynamically from total expenses vs budget limit.
---
## Running the project
### 1 — Database
Create a PostgreSQL database:
`
travelapp
`
Update pplication.yml:
`
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/travelapp
    username: YOUR_USER
    password: YOUR_PASSWORD
  jpa:
    hibernate:
      ddl-auto: update
`
### 2 — Configure Integrations (Optional)
Add to pplication.yml:
`
app:
  integrations:
    unsplash:
      enabled: true
      access-key: YOUR_UNSPLASH_KEY
    geoapify:
      enabled: true
      api-key: YOUR_GEOAPIFY_KEY
    amadeus:
      enabled: true
      client-id: YOUR_AMADEUS_ID
      client-secret: YOUR_AMADEUS_SECRET
      base-url: https://test.api.amadeus.com
`
### 3 — Run the API
`
./mvnw spring-boot:run
`
Server will start at:
`
http://localhost:8080
`
---
## Authentication
All protected endpoints require:
`
Authorization: Bearer <token>
`
Get token via:
`
POST /auth/login
`
---
## Main Endpoints
### Auth
- POST /auth/register
- POST /auth/login
### Trips
- GET /trips
- POST /trips
- GET /trips/{id}/summary
### Budget
- PUT /trips/{id}/budget
### Expenses
- GET /trips/{id}/expenses
- POST /expenses
- PUT /expenses/{id}
- DELETE /expenses/{id}
### Itinerary
- GET /trips/{id}/itinerary
- POST /itinerary/days
- PUT /itinerary/days/{id}
### Activities
- GET /itinerary/days/{id}/activities
- POST /activities
- PUT /activities/{id}
- DELETE /activities/{id}
- PUT /activities/reorder
### Destinations
- GET /destinations
- POST /destinations
- GET /destinations/{id}/places
- GET /destinations/{id}/hotels
- POST /destinations/{id}/hotels/book
---
## Project Structure
`
src/main/java/com/travelapp/
  activity/       ? activity management
  budget/         ? budget and health
  config/         ? Spring configurations
  controller/     ? HTTP endpoints
  destination/    ? destinations and integrations
  dto/            ? data transfer objects
  entity/         ? JPA entities
  exception/      ? error handling
  expense/        ? expense management
  itinerary/      ? itinerary planning
  repository/     ? data access
  security/       ? JWT and auth
  service/        ? business logic
  trip/           ? trip management
`
---
## Deployment
For production deployment, see [DEPLOY-RAILWAY.md](DEPLOY-RAILWAY.md) for Railway setup with PostgreSQL.
Recommended stack:
- Backend: Railway
- Database: Railway PostgreSQL
- Frontend: Vercel
---
## Design Decisions
The project intentionally separates domain logic from controllers.
Business rules such as budget health are not implemented in the frontend
to guarantee consistency across clients.
Integrations are optional and configurable for flexibility.
---
## Future Improvements
* Multi currency support
* Trip sharing between users
* Export to PDF
* Notifications
* Caching layer (Redis)
* Docker containerization
* Payment integration for bookings
---
## Author
Paulo Henrique dos Anjos
