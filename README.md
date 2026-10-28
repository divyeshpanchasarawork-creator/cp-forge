# CP Forge

CP Forge is a lightweight personal competitive programming dashboard. Backend is Spring Boot (Java 21), frontend is React (Vite) in `frontend/`. Database: H2 in-memory for local testing, PostgreSQL (Supabase) for production.

## Tech stack
- Backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway, RestClient, Maven
- Frontend: React 18, Vite (plain JS/JSX, no TypeScript, no Tailwind/Bootstrap)
- DB: H2 (local test), PostgreSQL/Supabase (prod)

## Local development

### Terminal 1 - Backend (H2 in-memory)
```bash
cd /Users/divyesh/Dev/projects/cp-forge
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
Runs at http://localhost:8080. CORS allows localhost origins. Uses H2 in-memory schema `cp_forge` via Flyway.

### Terminal 2 - Frontend (React + Vite)
```bash
cd /Users/divyesh/Dev/projects/cp-forge/frontend
npm install
npm run dev
```
Runs at http://localhost:3000. Vite proxies `/api` to `http://localhost:8080`, so no CORS issues locally.

Add a Codeforces handle in the UI to fetch real data. Data is lost when backend stops (H2 in-memory).

## Production deployment

### Backend on Render (Docker)
- Use this repo. Render can build with the included `Dockerfile`.
- Set environment variables:
  - `SPRING_DATASOURCE_URL` (e.g. `jdbc:postgresql://...:5432/postgres?sslmode=require`)
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- Expose port 8080. The app uses `application.properties` by default (or rely on env vars). CORS allows `*.vercel.app`.

### Frontend on Vercel
- Import repo, set **Root Directory** to `frontend/`
- Build Command: `npm run build`
- Output Directory: `dist`
- Environment variable: `VITE_API_BASE_URL` = `https://your-render-app.onrender.com` (if not using same-origin; for cross-origin call directly)
- Deploy. Frontend calls the backend API directly in production.

## Tests
```bash
./mvnw test
```
