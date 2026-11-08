# Rejectr API

Anonymous job-rejection tracker. A **shred** records only the category of a rejection
(`stage`, `outcome`, optional `reason`) — the story text is never stored.

## Tech

- Java 27, Spring Boot 4
- Spring Web MVC, Spring Data JPA, Bean Validation
- PostgreSQL + Flyway migrations
- springdoc-openapi (Swagger UI)

## Run

Requires a running PostgreSQL and a `.env` at the project root:

```properties
DB_NAME=rejectr
DB_USER=rejectr
DB_PASSWORD=your-password
```

```bash
./mvnw spring-boot:run
```

The app runs at http://localhost:8080 and applies Flyway migrations on startup.

## API docs

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Endpoints

| Method | Path      | Description                                             |
|--------|-----------|---------------------------------------------------------|
| POST   | `/shreds` | Create a shred (category only; text is never persisted) |

### Example

```bash
curl -i -X POST http://localhost:8080/shreds \
  -H 'Content-Type: application/json' \
  -d '{"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED"}'
```

Returns `201 Created` with a `Location` header. Validation failures return `400` with a
consistent body:

```json
{"status":400,"message":"Validation failed","errors":{"stage":"stage is required"}}
```
