# ClientFlow

ClientFlow is a service booking and customer management SaaS backend built with Spring Boot and MySQL.

## Local Development

Start MySQL:

```bash
docker compose up -d mysql
```

Start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

API documentation:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Use the Swagger `Authorize` button with the JWT returned by `/api/auth/login`.
