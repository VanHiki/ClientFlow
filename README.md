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

## Postman

The complete API collection is available at `postman/ClientFlow.postman_collection.json`.

1. Start MySQL and the backend.
2. Import the collection into Postman.
3. Run the folders from top to bottom, creating the business data before testing dependent APIs.
4. Run `Cleanup - Run Last` only after finishing the other requests.

The collection uses `http://localhost:8080` by default. Login and create requests automatically save JWTs, resource IDs, booking codes, and generated booking dates as collection variables. If demo data is enabled, use the provided owner and staff credentials; otherwise run `Register Owner` before `Login Owner`.

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

## Continuous Integration

`.github/workflows/backend-ci.yml` runs the backend test suite with Java 21 and the H2 test profile for every backend push and pull request.
