# HMCTS Caseworker Task Manager

A full-stack task management system for HMCTS caseworkers to create, view, update, and delete tasks.

## Architecture

| Component | Technology |
|-----------|-----------|
| Backend API | Java 21, Spring Boot 3.5, Spring Data JPA, H2 (in-memory) |
| Frontend | Node 20, Express, TypeScript, Nunjucks, GOV.UK Design System |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

## Getting Started

### Prerequisites

- Java 21+
- Node 20+
- Yarn 3

### Backend

```bash
cd backend
./gradlew bootRun
```

The API starts on **http://localhost:4000**.

Swagger UI is available at **http://localhost:4000/swagger-ui/index.html**.

### Frontend

```bash
cd frontend
yarn install
yarn build
yarn start:dev
```

The app starts on **https://localhost:3100**.

> The backend must be running before starting the frontend.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/tasks` | Create a new task |
| `GET` | `/tasks` | Retrieve all tasks |
| `GET` | `/tasks/{id}` | Retrieve a task by ID |
| `PATCH` | `/tasks/{id}/status` | Update task status |
| `DELETE` | `/tasks/{id}` | Delete a task |

### Task fields

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `title` | String | Yes | Non-blank |
| `description` | String | No | Optional |
| `status` | Enum | Yes | `TODO`, `IN_PROGRESS`, `DONE` |
| `dueDateTime` | ISO-8601 datetime | Yes | e.g. `2026-12-31T17:00:00` |

### Example: Create a task

```bash
curl -X POST http://localhost:4000/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Review case files",
    "description": "Review all case files for the upcoming hearing",
    "status": "TODO",
    "dueDateTime": "2026-12-31T17:00:00"
  }'
```

## Running Tests

### Backend

```bash
cd backend
./gradlew test          # unit tests
./gradlew integration   # integration tests
```

### Frontend

```bash
cd frontend
yarn test:unit
yarn test:routes
```

## Frontend Features

- **Task list** — view all tasks with status tags and due dates
- **Create task** — form with validation for title, description, status, and due date
- **View task** — detailed view with inline status update
- **Delete task** — remove a task directly from the detail view
- GOV.UK Design System components throughout (tags, tables, summary lists, error messages)

## Design Decisions

- **H2 in-memory database** — zero-config setup ideal for the dev test; swap for PostgreSQL in production by updating the `datasource` block in `backend/src/main/resources/application.yaml`
- **Server-side rendering** — the Express frontend calls the Spring Boot API server-side (no CORS needed, no tokens exposed to the browser)
- **POST for delete/status** — HTML forms only support GET/POST, so status updates and deletes use POST routes in the Express layer which translate to PATCH/DELETE API calls
- **Validation at both layers** — Spring Bean Validation on the API, HTML5 `required` attributes on the frontend, with server errors surfaced back in the form
