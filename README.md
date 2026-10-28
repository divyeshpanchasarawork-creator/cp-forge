# CP Forge

A lightweight personal competitive programming and DSA training dashboard built with Spring Boot, plain HTML/CSS/JS.

## Features (Milestone 1 - Codeforces)
- Add and track Codeforces profiles
- View current rating, peak rating, rank
- View rating history as a simple SVG chart
- Manual refresh of profile data
- Data persisted in PostgreSQL/H2

## Tech Stack
- Backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway, RestClient
- Database: H2 (in-memory for local testing), PostgreSQL/Supabase (production)
- Frontend: Plain HTML, CSS, Vanilla JavaScript (separate `frontend/` folder for Vercel deployment)

## Prerequisites
- Java 21
- Maven (or use included mvnw)

## Running Locally (Testing with in-memory H2 DB)

No external DB needed. Run with local profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The app uses H2 in-memory with Flyway creating schema `cp_forge` and running migrations. Runs on `http://localhost:8080`.

### Frontend
```bash
cd frontend
echo "window.CP_FORGE_API_BASE = 'http://localhost:8080';" > config.js
python3 -m http.server 3000
```
Open `http://localhost:3000`. CORS allows localhost.

## Running with Production DB
Set env vars:
```bash
SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
SPRING_DATASOURCE_USERNAME="..." \
SPRING_DATASOURCE_PASSWORD="..." \
./mvnw spring-boot:run
```

## Tests
```bash
./mvnw test
```

## API Endpoints
- `POST /api/codeforces/profiles` - Add/refresh a profile (body: `{"handle": "username"}`)
- `GET /api/codeforces/profiles` - List all saved profiles
- `GET /api/codeforces/profiles/{handle}` - Get a specific profile
- `POST /api/codeforces/profiles/{handle}/refresh` - Refresh a profile

## Deploying as separate frontend/backend (Vercel + Render)

### Backend (Render)
1. Create Web Service from repo. Build: `./mvnw clean package -DskipTests` Start: `java -jar target/cp-forge-0.0.1-SNAPSHOT.jar`
2. Set env vars: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

### Frontend (Vercel)
1. Import repo, Root Directory: `frontend/` (static, no build). Set `frontend/config.js` to point to Render URL.
