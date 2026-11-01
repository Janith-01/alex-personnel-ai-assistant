# Alex - Personal AI Assistant

A locally-run, voice-driven personal AI assistant for Windows, built incrementally in phases.

## Status: Phase 2 - Voice Input

- Spring Boot 4.1.2 / Java 21 backend scaffolded in `backend/`
- Basic REST endpoint: `GET /api/status`
- Microphone capture + local speech-to-text via whisper.cpp: `POST /api/voice/listen`

## Project Structure

- `backend/` - Spring Boot application (Java 21, Maven wrapper)
- `voice-engine/` - whisper.cpp binaries and models (not checked into git, see setup below)
- `android-app/` - Android companion app (Phase 8)
- `database/` - SQLite/Postgres schema and data (Phase 6)
- `documentation/` - design notes and docs
- `scripts/` - setup and automation scripts

## First-time setup

Download the whisper.cpp CPU binaries and speech model (one-time, ~150 MB):

```powershell
powershell -ExecutionPolicy Bypass -File scripts/setup-voice-engine.ps1
```

## Running the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Then check: http://localhost:8080/api/status

### Voice input

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/voice/listen?seconds=5" -Method POST
```

Speaks into the default microphone for `seconds` (default 5), transcribes locally with whisper.cpp
(`base.en` model), and returns `{"text": "..."}`.
