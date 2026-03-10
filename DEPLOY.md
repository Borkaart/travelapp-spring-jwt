# Deploy Backend

## Recommended Host

Use a Java host such as:

- Render
- Railway
- Fly.io

Do not host the Spring Boot API on x10Hosting.
Do not deploy this Spring Boot API directly on Vercel in its current form.

## Production API URL

- `https://api.travelapp.x10.network`

## Required Environment Variables

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
SERVER_PORT=8080
JWT_SECRET=<strong-random-secret>
JWT_EXPIRATION=14400000
APP_CORS_ALLOWED_ORIGINS=https://travelapp.x10.network

UNSPLASH_ENABLED=true
UNSPLASH_ACCESS_KEY=<unsplash-access-key>

GEOAPIFY_ENABLED=true
GEOAPIFY_API_KEY=<geoapify-api-key>

AMADEUS_ENABLED=true
AMADEUS_CLIENT_ID=<amadeus-client-id>
AMADEUS_CLIENT_SECRET=<amadeus-client-secret>
AMADEUS_BASE_URL=https://test.api.amadeus.com
```

## Domain

Create a DNS record for:

- `api.travelapp.x10.network`

Point it to the backend provider and configure HTTPS there.

## Frontend Connection

The frontend must use:

```env
VITE_API_URL=https://api.travelapp.x10.network/api
```
