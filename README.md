# LaLiga Manager - Full Stack Application

## 📋 Overview
A comprehensive **LaLiga Manager** simulation built with **Spring Boot** (Backend) and **Next.js** (Frontend).
The application allows you to browse LaLiga teams, view player statistics, check standings, and simulate matchdays/seasons using a custom probability-based engine.

## 🚀 Features
- **Real League Structure**: 20 LaLiga teams with realistic ratings (Real Madrid, Barcelona, Atletico, etc.).
- **Simulation Engine**: Probabilistic match simulation taking into account team strength and home advantage.
- **Dynamic Standings**: Real-time league table updates as matches are simulated.
- **Detailed Views**:
  - **Dashboard**: Quick overview of next matches and top teams.
  - **Calendar**: View fixtures and results for all 38 matchdays.
  - **Team/Player Profiles**: Detailed stats (Goals, Assists, Cards, Overall Rating).

## 🛠️ Tech Stack
- **Backend**: Java 17, Spring Boot 3, H2 Database.
- **Frontend**: Next.js 16, React 19, TailwindCSS, Shadcn UI.
- **Data**: JSON-based initial seeding with auto-generated scheduling.

## 🚀 Quick Start
1. **Backend**:
   ```bash
   cd football-manager-api
   mvn spring-boot:run
   ```
2. **Frontend**:
   ```bash
   cd football-manager-client
   npm run dev
   ```
3. Open http://localhost:3000

## 📂 Project Structure
- `football-manager-api`: REST API handling logic, simulation, and data persistence.
- `football-manager-client`: Modern web interface for managing the league.
- `tools/migrate_data.py`: Utility to generate/reset the `laliga_db.json` data.

## ⚠️ Troubleshooting
- **Data Missing?** If `laliga_db.json` is missing, run `python tools/migrate_data.py` to regenerate it.
- **Port Conflicts?** Ensure ports 8080 (API) and 3000 (Client) are free.

## 🚀 Quick Start
Double-click `start_all.bat` to launch both services.

- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080/api

## 🛠️ Requirements
- **Java 17+**
- **Maven** (Required for Backend)
- **Node.js 18+** (Required for Frontend)

## 📂 Project Structure
- `football-manager-api`: Java Spring Boot Backend.
- `football-manager-client`: Next.js Frontend with Shadcn UI.
- `start_all.bat`: Startup script.

## ⚠️ Troubleshooting
- **Backend won't start?** Ensure `mvn` is in your system PATH.
- **Frontend 404?** Ensure you are accessing `localhost:3000` and the backend is running.
