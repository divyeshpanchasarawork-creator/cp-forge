# CP Forge

A lightweight personal competitive programming and DSA training dashboard built with Spring Boot, plain HTML/CSS/JS.

## Features (Milestone 1 - Codeforces)
- Add and track Codeforces profiles
- View current rating, peak rating, rank
- View rating history as a simple SVG chart
- Manual refresh of profile data
- Data persisted in PostgreSQL

## Tech Stack
- Backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway, RestClient
- Database: PostgreSQL (local for testing, Supabase/Postgres for production)
- Frontend: Plain HTML, CSS, Vanilla JavaScript (separate `frontend/` folder for Vercel deployment)

## Prerequisites
- Java 21
- Maven (or use included mvnw)
- PostgreSQL (for local testing) or use Supabase/Postgres

## Running Locally (Testing with local DB)

### 1) Start local PostgreSQL
Using Docker:
```bash
docker run -d --name cpforge-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=cp_forge_test \
  -p 5432:5432 \
  postgres:16
```

Or create a DB named `cp_forge_test` in your local Postgres instance.

### 2) Run backend with local profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The app will use `application-local.properties` (local DB) with Flyway creating schema `cp_forge`. Runs on `http://localhost:8080`.

### 3) Run frontend separately
```bash
cd frontend
echo "window.CP_FORGE_API_BASE = 'http://localhost:8080';" > config.js
python3 -m http.server 3000
```
Open `http://localhost:3000`. CORS is configured to allow localhost.

## Running with Production DB
Use environment variables (or default `application.properties`):
```bash
SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
SPRING_DATASOURCE_USERNAME="..." \
SPRING_DATASOURCE_PASSWORD="..." \
./mvnw spring-boot:run
```
Or just run without local profile to use the configured DB.

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
1. Create a new Web Service from this repo.
2. Build Command: `./mvnw clean package -DskipTests`
3. Start Command: `java -jar target/cp-forge-0.0.1-SNAPSHOT.jar`
4. Set environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (Render sets `PORT`).
5. Deploy.

### Frontend (Vercel)
1. Import repo to Vercel, set Root Directory to `frontend/`.
2. Framework: Other (static). Build command empty, output `.` 
3. Set `frontend/config.js` to point to your Render URL: `window.CP_FORGE_API_BASE = "https://your-app.onrender.com";`
4. Deploy.
