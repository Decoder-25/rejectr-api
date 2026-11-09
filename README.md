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
DEVICE_TOKEN_SECRET=your-long-random-secret
```

`DEVICE_TOKEN_SECRET` is used to HMAC-SHA256 device tokens before storage. A local
development fallback exists, but production must set this. Generate one with
`openssl rand -hex 32`.

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
  -H 'X-Device-Token: 3f2c1a9b4d6e8f0a1b2c3d4e5f6a7b8c' \
  -d '{"stage":"APPLIED_NEVER_HEARD_BACK","outcome":"GHOSTED"}'
```

The optional `X-Device-Token` header lets the app recognise a returning browser. The
client generates a random token once and stores it; the server only stores its
HMAC-SHA256 hash (`device_hash`), never the raw token. Tokens must be 16–128 characters.

Returns `201 Created` with a `Location` header. Validation failures return `400` with a
consistent body:

```json
{"status":400,"message":"Validation failed","errors":{"stage":"stage is required"}}
```
