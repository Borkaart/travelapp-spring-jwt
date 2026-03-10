# Railway Environment Variables

Configure estas variáveis EXATAMENTE no painel do Railway:

## Option 1: Use DATABASE_URL (Recomendado)

Se o Railway fornece `DATABASE_URL`, configure APENAS:

```
SPRING_DATASOURCE_URL=postgresql://user:password@host:port/database
```

Converta de `postgres://` para `postgresql://` se necessário.

## Option 2: Manual JDBC URL (Se DATABASE_URL não funcionar)

Configure explicitamente:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://PGHOST:5432/PGDATABASE
SPRING_DATASOURCE_USERNAME=PGUSER
SPRING_DATASOURCE_PASSWORD=PGPASSWORD
```

Substitua PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD pelos valores reais do Railway.

## Todas as Variáveis Necessárias

```
SPRING_DATASOURCE_URL=<seu_database_url_aqui>
JWT_SECRET=secret
JWT_EXPIRATION=14400000
APP_CORS_ALLOWED_ORIGINS=https://travelapp.x10.network

UNSPLASH_ENABLED=true
UNSPLASH_ACCESS_KEY=8lzA8R-rI4fIbZ9hegBRNBxhA72WuFsUHy-EnfLe-0k

GEOAPIFY_ENABLED=true
GEOAPIFY_API_KEY=e28ce015f7d04bf4b9a9dd9d1472195d

AMADEUS_ENABLED=true
AMADEUS_CLIENT_ID=AaHwjmeOOPziqOvkzLwkiL0fxhxdsIJA
AMADEUS_CLIENT_SECRET=XvyHw6RSzhVj016Z
AMADEUS_BASE_URL=https://test.api.amadeus.com
```

## Passos:

1. Vá ao painel do Railway
2. Abra o serviço do PostgreSQL
3. Copie a CONNECTION STRING completa
4. No serviço do Spring Boot, vá para "Variables"
5. Adicione `SPRING_DATASOURCE_URL=<connection_string_aqui>`
6. Adicione as outras variáveis
7. Faça um redeploy manual

