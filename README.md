# CP Forge

A lightweight personal competitive programming and DSA training dashboard built with Spring Boot, plain HTML/CSS/JS.

## Features (Milestone 1 - Codeforces)
- Add and track Codeforces profiles
- View current rating, peak rating, rank
- View rating history as a simple SVG chart
- Manual refresh of profile data
- Data persisted in PostgreSQL (Supabase)

## Tech Stack
- Backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway, RestClient
- Database: PostgreSQL (Supabase)
- Frontend: Plain HTML, CSS, Vanilla JavaScript (no frameworks/build tools)

## Prerequisites
- Java 21
- Maven (or use included mvnw)

## Configuration
Set environment variables or update `application.properties`. For production, use environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `PORT`

The app is configured to use schema `cp_forge` and Flyway migrations from `classpath:db/migration`.

## Running Locally
```bash
./mvnw spring-boot:run
```

The app will start on `http://localhost:8080`. The dashboard is served from `static/index.html`.

## Building
```bash
./mvnw clean package
java -jar target/cp-forge-0.0.1-SNAPSHOT.jar
```

## Tests
```bash
./mvnw test
```

## Deployment (Render)
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -jar target/cp-forge-0.0.1-SNAPSHOT.jar`
- Set environment variables for database credentials
- Ensure the database allows connections from Render

## API Endpoints
- `POST /api/codeforces/profiles` - Add/refresh a profile (body: `{"handle": "username"}`)
- `GET /api/codeforces/profiles` - List all saved profiles
- `GET /api/codeforces/profiles/{handle}` - Get a specific profile
- `POST /api/codeforces/profiles/{handle}/refresh` - Refresh a profile

## Deploying as separate frontend/backend (Vercel + Render)

### Backend (Render)
1. Create a new Web Service from this GitHub repo.
2. Build Command: `./mvnw clean package -DskipTests`
3. Start Command: `java -jar target/cp-forge-0.0.1-SNAPSHOT.jar`
4. Environment Variables (set in Render):
   - `SPRING_DATASOURCE_URL` (e.g. `jdbc:postgresql://...:5432/postgres?sslmode=require`)
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `PORT` (usually auto-set by Render)
5. Deploy. Note your Render URL (e.g. `https://your-app.onrender.com`).

CORS is configured to allow `http://localhost:*` and `https://*.vercel.app`.

### Frontend (Vercel)
1. Import this repo to Vercel.
2. Set Root Directory to `frontend/`.
3. Framework Preset: `Other` (static site, no build).
4. Build Command: leave empty. Output Directory: `.` 
5. Edit `frontend/config.js` before deploy or after - set `window.CP_FORGE_API_BASE = "https://your-app.onrender.com"`.
6. Deploy. Your app will call the Render API directly.
