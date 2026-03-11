# 🏟️ LaLiga Manager

Full-stack football management simulation app built with **Spring Boot 3** + **Next.js** + **TailwindCSS**.

Manage your own LaLiga team: set tactics, buy players, simulate matches, and compete for the championship!

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (JDK)
- **Maven 3.8+**
- **Node.js 18+** and **npm**

### 1. Start Backend
```bash
cd football-manager-api
mvn spring-boot:run
```
Backend runs at `http://localhost:8080`. API docs at `http://localhost:8080/swagger-ui.html`.

### 2. Start Frontend
```bash
cd football-manager-client
npm install
npm run dev
```
Frontend runs at `http://localhost:3000`.

### 3. Play!
1. Open `http://localhost:3000`
2. Click **"Inicializar Nueva Liga"** to seed data
3. Select your team
4. Simulate matchdays and manage your squad

---

## 🗄️ Database Configuration

### Development (H2 - Default)
No setup required. H2 file-based database is created automatically at `football-manager-api/data/football_manager.mv.db`.

Profile: `dev` (active by default)

Access H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/football_manager`
- User: `sa` / Password: `password`

### Production (PostgreSQL)

1. Install PostgreSQL 15+
2. Create database and user:
```sql
CREATE DATABASE laliga_manager;
CREATE USER laliga_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE laliga_manager TO laliga_user;
```

3. Edit `football-manager-api/src/main/resources/application-prod.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/laliga_manager
    username: laliga_user
    password: your_password
```

4. Run with production profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Migration Scripts
```bash
cd football-manager-api/db/migration
psql -U laliga_user -d laliga_manager -f V1__initial_schema.sql
psql -U laliga_user -d laliga_manager -f V2__seed_laliga.sql
```

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/league/status` | League status (matchday, started, finished) |
| POST | `/api/league/reset` | Reset and re-seed all data |
| POST | `/api/league/select-team/{id}` | Select managed team |
| GET | `/api/teams` | List all teams |
| GET | `/api/teams/{teamId}` | Team details + players |
| PUT | `/api/teams/{teamId}/tactics` | Update formation/mentality |
| GET | `/api/standings` | League standings table |
| GET | `/api/matches` | All matches (optional `?matchday=N`) |
| GET | `/api/matches/{id}` | Match details + events |
| POST | `/api/simulate/matchday` | Simulate next matchday |
| POST | `/api/simulate/season` | Simulate entire remaining season |
| GET | `/api/stats/top-scorers` | Top 20 goal scorers |
| GET | `/api/stats/top-assists` | Top 20 assist providers |
| GET | `/api/stats/top-rated` | Top 20 rated players |
| GET | `/api/transfers/market` | Players on transfer market |
| POST | `/api/transfers/buy` | Buy a player |
| POST | `/api/transfers/sell` | Sell a player |
| GET | `/api/seasons` | Historical season results |
| POST | `/api/seasons/new` | Start new season |

Full API documentation: `http://localhost:8080/swagger-ui.html`

---

## ⚙️ Simulation Features

- **Poisson-based scoring**: Realistic goal distribution based on team rating differentials
- **Home advantage**: +5 rating for home teams
- **Player form**: Win/loss streaks affect player form (1-10 scale)
- **Injuries**: 3% chance per player per match, duration 1-5 matchdays
- **Cards**: Yellow cards (0-4 per team), red cards (~5% chance)
- **Assists**: 70% of goals generate an assist event
- **Advanced stats**: Possession %, shots on target per match

---

## 🐛 Troubleshooting

### Dashboard doesn't load
```bash
# 1. Verify backend is running
curl http://localhost:8080/api/league/status

# 2. Check for data
curl http://localhost:8080/api/teams

# 3. Reset if needed
curl -X POST http://localhost:8080/api/league/reset

# 4. Clean rebuild
cd football-manager-api
del data\football_manager.mv.db    # Windows
# rm data/football_manager.mv.db  # Linux/Mac
mvn clean spring-boot:run
```

### CORS errors
- Backend allows all origins via `@CrossOrigin(origins = "*")` and `SecurityConfig`
- Frontend connects to `http://127.0.0.1:8080/api` (configured in `lib/api.ts`)

### Duplicate key errors on reset
This was a known bug where H2 didn't flush deletes before reinserting. Fixed by using `EntityManager.flush()` + `clear()` in `DataLoadService.resetLeagueData()`.

---

## 🎨 Code Standards

### Java (Backend)
- Spring Boot conventions with Lombok (`@Slf4j`, `@Builder`, `@Data`)
- DTOs for all API responses (never expose entities directly)
- `@ControllerAdvice` global exception handler
- Javadoc on public service/controller methods

### TypeScript/React (Frontend)
- Functional components with hooks
- TypeScript interfaces for all API types
- One component per file
- Descriptive names: `LeagueTable.tsx`, `SimulationControl.tsx`

### Commits
Format: `type: description`
- `fix: corregir carga de liga en dashboard`
- `feat: añadir sistema de temporadas`
- `refactor: separar configuración por perfil`

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security |
| Database | H2 (dev), PostgreSQL (prod) |
| Frontend | Next.js, React 19, TailwindCSS, Shadcn UI |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Auth | JWT (json web tokens) |
| Build | Maven (backend), npm (frontend) |
