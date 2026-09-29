# TicketDesk

![CI](https://github.com/Dhayalramesh/ticketdesk/actions/workflows/ci.yml/badge.svg)

A small ticket-management REST API built with Spring Boot to practice real
application design, database schema ownership, and test coverage in Java —
the areas plain scripting doesn't touch.

## Features

- Create, read, update, and delete support tickets
- Filter and paginate tickets by status
- Status lifecycle enforced by a transition rule, not left to the client:
  `OPEN → IN_PROGRESS → RESOLVED → CLOSED`, with `RESOLVED` reopenable back
  to `IN_PROGRESS` and `CLOSED` treated as terminal
- Request validation (blank titles, oversized fields) returns structured
  `400` errors instead of stack traces
- OpenAPI/Swagger docs generated from the code, not hand-maintained
- Schema owned by version-controlled Flyway migrations, not
  `hibernate.ddl-auto=update`

## Tech stack

Java 21 · Spring Boot 3 (Web, Data JPA, Validation, Actuator) · PostgreSQL ·
Flyway · springdoc-openapi · JUnit 5 · Mockito · H2 (tests) · JaCoCo · Docker

## Running it

```bash
docker compose up --build
```

This starts Postgres and the app together, with Flyway applying the schema
migration on boot. The API is then at `http://localhost:8080/api/tickets`,
and interactive docs at `http://localhost:8080/swagger-ui.html`.

To run without Docker, point `application.yml`'s datasource at a local
Postgres instance and run:

```bash
mvn spring-boot:run
```

## Running the tests

```bash
mvn clean verify
```

Tests run against an in-memory H2 database (`test` profile) so they need no
external services. Coverage report lands at `target/site/jacoco/index.html`.

Test layers:

| Layer | Tool | What it checks |
|---|---|---|
| `TicketServiceImplTest` | JUnit 5 + Mockito | status-transition business rules, not-found handling |
| `TicketControllerTest` | `@WebMvcTest` + MockMvc | HTTP status codes, request validation, JSON shape |
| `TicketRepositoryTest` | `@DataJpaTest` + H2 | query methods actually hit the database correctly |

Every push and pull request to `main` runs this full suite on a clean
GitHub Actions runner — see the badge above, or the
[Actions tab](https://github.com/Dhayalramesh/ticketdesk/actions) for the
latest run.

## API

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/tickets` | Create a ticket |
| `GET` | `/api/tickets/{id}` | Get one ticket |
| `GET` | `/api/tickets?status=OPEN` | List tickets, optional status filter, paginated |
| `PUT` | `/api/tickets/{id}` | Update a ticket's details |
| `PATCH` | `/api/tickets/{id}/status` | Transition a ticket's status |
| `DELETE` | `/api/tickets/{id}` | Delete a ticket |

## Design notes

- **DTOs instead of exposing the entity** (`TicketRequest`/`TicketResponse`):
  keeps the public API contract stable even if the schema changes, and
  means a caller can never set server-owned fields like `id` or `status`.
- **Status transitions live in the service, not the entity or controller**:
  `TicketServiceImpl` holds an explicit allowed-transitions map, so the rule
  is in one place and unit-testable without Spring or a database.
- **Flyway over `ddl-auto: update`**: the schema is a versioned SQL migration
  (`V1__create_tickets_table.sql`) checked into source control, the same way
  the app's own code is.
