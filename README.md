# Student Achievement Tracker — Java Full Stack

A full rewrite of the original Python/Flask **Student Achievement Tracker** using a
**Java full-stack**: **Spring Boot + Spring Security (JWT) + Spring Data JPA + PostgreSQL**
on the backend, and **React (Vite)** on the frontend, with **STOMP/WebSocket** for
real-time notifications.

Three roles are supported, matching the original app:

- **Student** — uploads certificates, tracks approval status, views personal analytics
- **Staff (Class Teacher)** — reviews certificates from their own students (approve/reject with feedback)
- **HOD** — gives the final department-level approval and views branch-wide analytics

## What this demonstrates (resume bullets)

- Built a scalable full-stack application using **Spring Boot** and **React**, replacing manual/paper record keeping with a structured approval workflow.
- Implemented **RBAC with Spring Security & JWT** — every API route is locked to `STUDENT` / `STAFF` / `HOD` roles, with per-record ownership checks (a staff member can only review their own students; HOD is scoped to their branch).
- Designed a **PostgreSQL schema with Spring Data JPA** (`User`, `Student`, `Staff`, `Hod`, `Certificate`, `Notification`) and exposed optimized, purpose-built REST endpoints per dashboard instead of generic CRUD, cutting the number of round trips a client needs.
- Enabled **real-time tracking & notifications** via a JWT-authenticated STOMP/WebSocket channel — certificate status changes are pushed to the affected user's browser instantly instead of requiring a page refresh or polling.

## Project layout

```
student-achievement-tracker-java/
├── backend/                 Spring Boot application (Maven)
│   ├── pom.xml
│   └── src/main/java/com/sat/tracker/
│       ├── model/            JPA entities (User, Student, Staff, Hod, Certificate, Notification)
│       ├── repository/       Spring Data JPA repositories
│       ├── security/         JWT filter, UserDetailsService, UserPrincipal
│       ├── config/           SecurityConfig (RBAC), WebSocketConfig, DataSeeder
│       ├── service/          Business logic (upload, approvals, analytics, notifications)
│       ├── controller/       REST controllers
│       ├── dto/               Request/response DTOs
│       └── exception/         Centralized error handling
├── frontend/                 React app (Vite)
│   └── src/
│       ├── api/               axios client + WebSocket client
│       ├── context/           AuthContext (login/logout/session)
│       ├── components/        Navbar, tables, cards, notification bell
│       └── pages/              student/, staff/, hod/ route pages + Login
└── docker-compose.yml         PostgreSQL container for local development
```

## Prerequisites

- Java 17+
- Maven 3.9+ (or use your IDE's bundled Maven)
- Node.js 18+ and npm
- PostgreSQL 14+ (or Docker, see below)

## 1. Start PostgreSQL

Easiest option — Docker:

```bash
docker compose up -d
```

This starts Postgres on `localhost:5432` with database `achievement_tracker`,
user `postgres`, password `postgres` (matches `backend/src/main/resources/application.yml`).

If you'd rather use a local Postgres install, just create the database yourself:

```sql
CREATE DATABASE achievement_tracker;
```

and update `backend/src/main/resources/application.yml` with your own credentials if they differ.

## 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**. On first run (empty database), a
`DataSeeder` automatically creates demo accounts so you can log in immediately:

| Role    | Username    | Password    | Branch/Section     |
|---------|-------------|-------------|---------------------|
| HOD     | `hod_cse`   | `hod_cse`   | CSE                 |
| Staff   | `staff_cse` | `staff_cse` | CSE - A (class teacher) |
| Student | `student1`  | `student1`  | CSE - A, Year 2      |
| Student | `student2`  | `student2`  | CSE - A, Year 2      |

Uploaded certificate files are stored under `backend/storage/` (created automatically,
configurable via `app.file-storage.upload-dir` in `application.yml`).

You can also register additional accounts via:
- `POST /api/auth/register/student`
- `POST /api/auth/register/staff`
- `POST /api/auth/register/hod`

(These are open endpoints intended for seeding/demo purposes — see the note in `AuthController`.)

## 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on **http://localhost:5173** and talks to the backend at
`http://localhost:8080` by default (override with a `VITE_API_BASE_URL` env var
in a `.env` file inside `frontend/` if needed).

## How the approval workflow works

1. A **student** uploads a certificate (PDF/JPEG + title, event type, achievement type).
2. Their **class teacher (staff)** is notified in real time and can approve or reject it, with feedback.
3. On staff approval, the student's **HOD** is notified and gives the final decision.
4. The student is notified at every step, and their dashboard/analytics update immediately
   because the same certificate record carries both `staffStatus` and `hodStatus`.

## Notes on the JWT/RBAC design

- Passwords are stored as BCrypt hashes (`Spring Security` `BCryptPasswordEncoder`).
- JWTs carry `role` and `userId` claims and are validated on every request by `JwtAuthFilter`.
- Route-level RBAC is enforced in `SecurityConfig` (`/api/student/**` → `ROLE_STUDENT`, etc.).
- Object-level authorization is enforced in the service layer — e.g. a staff member can only
  review certificates belonging to students where they are the registered class teacher, and
  a HOD is scoped to certificates from students in their own branch.
- Certificate files are served through an authenticated endpoint
  (`GET /api/certificates/{id}/file`) with the same ownership checks, rather than being
  publicly reachable static assets.

## Production notes

This project is set up for local development/demo purposes (`ddl-auto: update`, an open JWT
secret in `application.yml`, open registration endpoints, a permissive CORS origin list). For a
real deployment you would want to: move secrets to environment variables, switch to a proper
migration tool (Flyway/Liquibase) instead of `ddl-auto: update`, lock down or remove the
registration endpoints, and tighten CORS to your actual frontend origin(s).
