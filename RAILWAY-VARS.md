# Railway Environment Variables - Configuração Correta

## ⚠️ IMPORTANTE: Siga EXATAMENTE estes passos

### Passo 1: Obter a Connection String do PostgreSQL

1. No painel do Railway
2. Clique no serviço **PostgreSQL**
3. Vá para aba **"Connect"**
4. Copie a **CONNECTION STRING** (deve começar com `postgresql://`)
5. Guarde esse valor

### Passo 2: Configurar Variáveis no Spring Boot

No serviço Spring Boot, vá para **"Variables"** e configure:

```
SPRING_DATASOURCE_URL=<COLE_A_CONNECTION_STRING_AQUI>
JWT_SECRET=secret
JWT_EXPIRATION=14400000
APP_CORS_ALLOWED_ORIGINS=https://travelapp-frontend.vercel.app

UNSPLASH_ENABLED=true
UNSPLASH_ACCESS_KEY=8lzA8R-rI4fIbZ9hegBRNBxhA72WuFsUHy-EnfLe-0k

GEOAPIFY_ENABLED=true
GEOAPIFY_API_KEY=e28ce015f7d04bf4b9a9dd9d1472195d

AMADEUS_ENABLED=true
AMADEUS_CLIENT_ID=AaHwjmeOOPziqOvkzLwkiL0fxhxdsIJA
AMADEUS_CLIENT_SECRET=XvyHw6RSzhVj016Z
AMADEUS_BASE_URL=https://test.api.amadeus.com
```

### Exemplo de CONNECTION STRING:
```
postgresql://postgres:senha123@containers-us-west-123.railway.app:7890/railway
```

**Não use `jdbc:postgresql://` - apenas `postgresql://`**

### Passo 3: Redeploy

1. Salve as variáveis
2. Clique em "Redeploy" no serviço Spring Boot
3. Aguarde 5-10 minutos
4. Teste: `https://travelapp-spring-jwt-production.up.railway.app/api/health`

### Se ainda crashar:

Verifique os **Logs** do deployment (aba "Deployments" > "View Logs") e procure por:
- `Unable to connect to database`
- `Connection refused`
- `No suitable driver found`

Se encontrar algum desses, me compartilhe o erro completo!

