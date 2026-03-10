# Deploy Backend on Railway

## Recommendation

Use Railway for the Spring Boot API and PostgreSQL.

Why this stack:

- Railway has an official Spring Boot deploy guide
- Railway provides PostgreSQL in the same project
- Railway supports public domains, custom domains and automatic SSL

## 1. Create the Railway project

1. Create a new project in Railway
2. Add the backend repository or connect the GitHub repo
3. Add a PostgreSQL service to the same project

Official docs:

- Spring Boot: https://docs.railway.com/guides/spring-boot
- PostgreSQL: https://docs.railway.com/databases/postgresql
- Public networking: https://docs.railway.com/networking/public-networking

## 2. Backend service variables

Set these variables in the Spring Boot service:

```env
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
JWT_SECRET=change-this-in-production
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

Notes:

- `PORT` is provided by Railway automatically
- this app now supports `PORT` directly
- if Railway does not expose `DATABASE_URL` in JDBC format for your service setup, use:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

## 3. Generate the backend domain

In the backend service:

1. Open `Settings`
2. Go to `Networking`
3. Click `Generate Domain`

Railway gives you a `*.railway.app` domain first.

## 4. Attach your custom API domain

Create:

- `api.travelapp.x10.network`

Point it to the Railway domain with the DNS record requested by Railway.

## 5. Frontend production env

Create `travelapp-web/.env.production`:

```env
VITE_API_URL=https://api.travelapp.x10.network/api
```

Then build:

```bash
npm run build
```

Upload the contents of `travelapp-web/dist/` to `travelapp.x10.network`.

## 6. Smoke test

After deploy:

1. Open `https://api.travelapp.x10.network/v3/api-docs`
2. Open `https://travelapp.x10.network`
3. Test login
4. Open one trip
5. Test places and hotels

## Security

Rotate these credentials before production because they were exposed in chat:

- Unsplash access key
- Geoapify API key
- Amadeus client id
- Amadeus client secret
