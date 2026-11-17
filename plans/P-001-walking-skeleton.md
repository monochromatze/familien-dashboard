# Plan P-001: Walking Skeleton (Health-Check durch alle Schichten)

- **Basiert auf:** `intent/I-001` (Rahmen), `docs/adr/ADR-0001-tech-stack.md`, `KURS.md` Modul 4. Es gibt keine Spec, weil das Skeleton keine Fachlogik enthält. Es ist ein technischer Unterbau.
- **Freigegeben von:** monochromatze am 2026-11-17
- **Status:** Akzeptiert

## Ziel
`GET /api/health` liefert `{"status":"ok","db":"ok"}`. Die React-Seite zeigt diesen Status an. Alles läuft als **ein** Docker-Image und mit Postgres per `docker compose`. Die CI ist grün. Die Linter laufen, und die Hooks PostToolUse und Stop greifen zum ersten Mal an echtem Code.

## Nicht Teil dieses Plans
Fachlogik, Login, Flyway-Migrationen und Fachtabellen (Modul 9), QA/Prod-Stacks und Deploy (Modul 5), ArchUnit (Modul 6/7). Es entsteht deshalb noch kein Ordner `db/migration/`.

## Dateien, die sich ändern
| Datei | Änderung |
|---|---|
| `backend/settings.gradle.kts`, `build.gradle.kts`, `gradle/`, `gradlew` | neu: Spring Boot 3, Kotlin, Gradle (Kotlin DSL), JDK 21, ktlint, detekt |
| `backend/src/main/kotlin/de/familiendashboard/Application.kt` | neu: Startpunkt |
| `…/domain/` | neu, bewusst leer (nur `package-info`-Platzhalter), bleibt frei von Spring und JPA |
| `…/application/HealthService.kt` | neu: Use Case „Gesundheit prüfen“, hängt von einem Port `DatabaseCheck` ab |
| `…/infrastructure/JdbcDatabaseCheck.kt` | neu: Adapter, führt `SELECT 1` aus |
| `…/web/HealthController.kt` | neu: `GET /api/health` |
| `backend/src/test/kotlin/…/HealthControllerTest.kt`, `HealthIntegrationTest.kt` | neu: Tests (siehe Reihenfolge) |
| `backend/src/main/resources/application.yml` | neu: DB-Verbindung nur über Umgebungsvariablen |
| `frontend/` (Vite, React, TypeScript `strict`, Vitest, ESLint, Prettier) | neu |
| `frontend/src/App.tsx`, `App.test.tsx` | neu: zeigt den Health-Status |
| `Dockerfile` | neu: Multi-Stage (Node baut Frontend, Gradle baut Backend, schlankes JRE-Image, Nicht-Root-User) |
| `deploy/compose.yml`, `deploy/dev.env.example` | neu: App + Postgres 16, Passwort nur aus `.env`-Datei |
| `.github/workflows/ci.yml` | neu: Lint, Tests mit Postgres, Docker-Build |
| `.gitignore` | ergänzen vor dem ersten Commit: `deploy/*.env` (Beispiele `*.env.example` bleiben erlaubt) |
| `CLAUDE.md` | Abschnitt „Befehle“ ausfüllen (Build, Test, Lint, Start) |
| `docs/coding-regeln.md` | Status der Durchsetzung aktualisieren (R1, R2, R11, R12, R16 jetzt aktiv) |
| `FORTSCHRITT.md` | Modul 4 abhaken, Modul 3 abschließen |

## Reihenfolge der Arbeit
Jeder Abschnitt ist ein eigener Branch und PR. So bleibt jeder Schritt einzeln prüfbar. Pro Schritt gilt: zuerst ein fehlschlagender Test, dann der Code. In der Commit-Reihenfolge muss der Test vor dem Code stehen.

**PR A – Backend-Skeleton ohne Datenbank** (`feature/backend-health`)
1. Gradle-Projekt anlegen, `.gitignore` zuerst ergänzen.
2. **Roter Test:** `HealthControllerTest` (MockMvc, `DatabaseCheck` als Fake): Ist die DB erreichbar, kommt `{"status":"ok","db":"ok"}`. Ist sie es nicht, kommt `{"status":"degraded","db":"down"}` mit HTTP 503.
3. `HealthService`, `HealthController` bauen, bis der Test grün ist.
4. ktlint und detekt konfigurieren (kein `!!`, R12). Dabei läuft der PostToolUse-Hook das erste Mal an echtem `.kt`-Code.
5. Stop-Hook: Er läuft jetzt echte Tests. Wir provozieren einmal einen roten Test und prüfen, dass er blockiert.

**PR B – Postgres anbinden** (`feature/db-health`)
1. **Roter Test:** `HealthIntegrationTest` mit Testcontainers (echtes Postgres 16). Erwartet `db: ok`.
2. `JdbcDatabaseCheck` und `application.yml` (Zugangsdaten aus Umgebungsvariablen).
3. `deploy/compose.yml` mit Postgres und Healthcheck, `dev.env.example`. Das echte `.env` bleibt außerhalb von Git.

**PR C – Frontend** (`feature/frontend-health`)
1. Vite, React, TypeScript `strict`, Vitest, ESLint (kein `any`, R11), Prettier.
2. **Roter Test:** `App.test.tsx`: Bei Antwort `ok` steht „Backend: ok · Datenbank: ok“ auf der Seite. Bei Fehler steht dort „Backend nicht erreichbar“.
3. `App.tsx` mit `fetch('/api/health')`. Vite leitet `/api` im Entwicklungsmodus an das Backend weiter.

**PR D – Image und CI** (`feature/docker-ci`)
1. `Dockerfile` (Multi-Stage). Spring liefert das gebaute Frontend aus, daher gibt es **ein** Image mit **einem** Port.
2. `docker compose up -d --build` und manuelle Prüfung (siehe Beweis).
3. `ci.yml`: ktlint, detekt, ESLint, Prettier-Check, Backend-Tests, Frontend-Tests, Docker-Build.
4. Nach dem ersten grünen Lauf den CI-Check in die **Branch Protection** aufnehmen (Nachholpunkt aus Modul 2).
5. `CLAUDE.md` („Befehle“), `coding-regeln.md` und `FORTSCHRITT.md` aktualisieren.

## Risiken
- **Was könnte brechen?** Testcontainers braucht Docker. OrbStack stellt das bereit, in GitHub Actions ist Docker vorinstalliert. Falls es lokal nicht klappt, ist der Ausweg ein Postgres-Service-Container in der CI, und der lokale Test nutzt `compose`.
- **Riskantester Schritt:** das Multi-Stage-Dockerfile (Frontend ins Backend einbinden, Nicht-Root-User, Dateirechte). Deshalb steht es in einem eigenen PR und wird Abschnitt für Abschnitt erklärt.
- **CI-Pflicht in der Branch Protection:** Wird sie gesetzt, bevor der Check einmal gelaufen ist, kann man keinen PR mehr mergen. Darum erst nach dem ersten grünen Lauf.
- **Öffentliches Repo:** Passwörter und `.env` dürfen nie committet werden. Das fangen `.gitignore` und der PreToolUse-Hook ab. Beispiele heißen `*.env.example` und enthalten nur Platzhalter.
- **Bewusst verworfene Alternativen:**
  - Spring Actuator `/actuator/health`: Das Format entspricht nicht dem Zielformat, und das Lernziel (Schichten, Port und Adapter) entfiele.
  - Zwei getrennte Images (Frontend und Backend): mehr Betriebsaufwand für eine Familien-App. Ein Image passt auch zu „Build once, deploy many“ in Modul 5.
  - H2 statt Postgres in den Tests: Fehler, die erst auf echtem Postgres auftreten, blieben unentdeckt.
  - Flyway schon jetzt: Es gibt noch keine Tabellen. Es kommt in Modul 9.

## Beweis (woran wir erkennen, dass es funktioniert)
- **Tests:** `./gradlew test` und `npm test -- --run` sind grün. Die Commit-Reihenfolge zeigt jeweils zuerst den Test, dann den Code. Ein absichtlich roter Test blockiert den Stop-Hook.
- **Manuelle Prüfung lokal:** `docker compose up -d --build`, dann `curl localhost:8080/api/health` liefert `{"status":"ok","db":"ok"}`, und die Seite im Browser zeigt „Backend: ok · Datenbank: ok“. Nach `docker compose stop db` zeigt die Seite den Fehlerzustand. Nach `docker compose down` bleiben die Daten erhalten, nach `docker compose down -v` nicht.
- **CI:** grüner Haken auf dem PR, danach als Pflicht-Check in der Branch Protection.
- **Hooks:** Der PostToolUse-Hook meldet absichtlich schlecht formatierten Code zurück, und der Stop-Hook blockiert bei rotem Test. Beides ist mit einem Live-Beleg im Lernjournal notiert.
- **Metrik/Event:** nicht zutreffend (kein Produktverhalten).

## Entschieden am 2026-11-17 (Vorschläge übernommen)
1. **Paketname:** `de.familiendashboard` (Vorschlag). Wenn du eine andere Kennung willst, sag es jetzt, später ist das Umbenennen mühsam.
2. **Port:** App lokal auf `8080`, Postgres nur im internen Compose-Netz (nicht nach außen). In Modul 5 wird daraus `8081` (QA) und `8080` (Prod).
3. **PR-Zuschnitt:** vier kleine PRs (A bis D), wie oben. Alternativ ein großer PR, der aber schwerer zu prüfen ist.
