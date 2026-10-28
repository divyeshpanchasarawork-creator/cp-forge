# CP Forge

CP Forge is a lightweight personal competitive programming dashboard. Backend is Spring Boot (Java 21), frontend is React (Vite) in `frontend/`. Database: H2 in-memory by default for local dev; override with env vars for PostgreSQL/Supabase in production.

## Tech stack
- Backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway, RestClient, Maven
- Frontend: React 18, Vite (JS/JSX only)
- DB: H2 (default local), PostgreSQL/Supabase (prod)

## Local development

### Terminal 1 - Backend
```bash
cd /Users/divyesh/Dev/projects/cp-forge
./mvnw spring-boot:run
```
Runs at http://localhost:8080 with H2 in-memory by default. No profiles/config needed.

### Terminal 2 - Frontend
```bash
cd /Users/divyesh/Dev/projects/cp-forge/frontend
npm install
npm run dev
```
Runs at http://localhost:3000. Vite proxies `/api` to `http://localhost:8080`.

Add a Codeforces handle in the UI. Data is in-memory H2 (cleared on restart).

## Production deployment

### Backend on Render (Docker)
- Use this repo with the included `Dockerfile`.
- Set environment variables:
  - `SPRING_DATASOURCE_URL` (e.g. `jdbc:postgresql://...:5432/postgres?sslmode=require`)
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SPRING_DATASOURCE_DRIVER` (optional, defaults to `org.postgresql.Driver` if needed)
- CORS allows `*.vercel.app`. App runs on port 8080.

### Frontend on Vercel
- Import repo, Root Directory: `frontend/`
- Build: `npm run build`, Output: `dist`
- Env var: `VITE_API_BASE_URL=https://your-render-app.onrender.com`
- Deploy.

## Tests
```bash
./mvnw test
```
