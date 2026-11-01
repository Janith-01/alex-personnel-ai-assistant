# Alex - Personal AI Assistant

A locally-run, voice-driven personal AI assistant for Windows, built incrementally in phases.

## Status: Phase 1 - Project Foundation

- Spring Boot 4.1.2 / Java 21 backend scaffolded in `backend/`
- Basic REST endpoint: `GET /api/status`

## Project Structure

- `backend/` - Spring Boot application (Java 21, Maven wrapper)
- `voice-engine/` - speech-to-text / text-to-speech services (Phase 2-3)
- `android-app/` - Android companion app (Phase 8)
- `database/` - SQLite/Postgres schema and data (Phase 6)
- `documentation/` - design notes and docs
- `scripts/` - setup and automation scripts

## Running the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Then check: http://localhost:8080/api/status
