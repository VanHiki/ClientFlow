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

## Demo Data

To seed a complete demo business on an empty database:

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Demo accounts:

- Owner: `owner@clientflow.local` / `DemoOwner@123`
- Staff: `staff@clientflow.local` / `DemoStaff@123`
- Public booking slug: `clientflow-demo`

The `demo` profile is opt-in and skips seeding when that slug already exists. Override credentials with `DEMO_OWNER_EMAIL`, `DEMO_OWNER_PASSWORD`, `DEMO_STAFF_EMAIL`, and `DEMO_STAFF_PASSWORD`.
