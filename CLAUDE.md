# CLAUDE.md – Familien-Dashboard (Lernprojekt)

## Kontext
Lernprojekt eines Product Managers, der noch nie selbst eine WebApp gebaut hat. Der Kursplan steht in `KURS.md`, der Stand in `FORTSCHRITT.md`.
**Der Agent schreibt den Code.** Der Nutzer liefert die Eingaben (intent, spec, Regeln) und prüft das Ergebnis. Er will verstehen, nicht tippen.

## Arbeitsweise
- **Zu Beginn jeder Sitzung:** `FORTSCHRITT.md` lesen und kurz sagen, wo wir stehen und was der nächste Schritt ist.
- **Bei „Sitzung beenden“:** `FORTSCHRITT.md` aktualisieren (Checkboxen, Aktueller Stand, Lernjournal) und committen.
- Vor jedem Bauschritt das Konzept in wenigen Sätzen erklären (PM-verständlich, ohne Jargon oder mit erklärtem Jargon).
- Nach jedem Bauschritt sagen, **woran der Nutzer erkennt**, ob das Ergebnis gut ist, mit Verweis auf `vorlagen/review-checkliste.md`.
- Befehle, die der Nutzer selbst ausführen soll (Installation, git, gh), als einzelne Codeblöcke angeben und erklären.
- Sprache: Deutsch.

## Stack
Kotlin + Spring Boot (Gradle) · TypeScript + React + Vite (npm) · PostgreSQL 16 + Flyway · Docker Compose (OrbStack) · GitHub Actions + Self-hosted Runner. Alles lokal und kostenlos. Keine kostenpflichtigen Dienste vorschlagen.

## Regeln
- SDLC-Artefakte: `intent/` → `specs/` → `plans/` → Code + Tests → PR. Kein Code ohne akzeptierten Plan.
- Nummerierung: Jeder Artefakttyp hat ein Präfix und zählt für sich: `I-001` (Intent), `S-001` (Spec), `P-001` (Plan), `ADR-0001` (ADR, vierstellig). Dateiname z. B. `plans/P-001-walking-skeleton.md`. Die Nummer sagt nichts über Zusammengehörigkeit. Die Beziehung steht im Feld „Basiert auf“ im Dokument.
- Branch-Namen nennen die Phase: `intent/…`, `spec/…`, `plan/…`, `adr/…` für die Artefakte (z. B. `plan/p-001-walking-skeleton`), `feature/…` für Funktionen (nicht `feat/`), `fix/…` für Fehlerbehebungen, `chore/…` für Aufräumarbeiten, `docs/…` nur für sonstige Dokumentation (Fortschritt, Interviews, Wettbewerb). Commit-Nachrichten bleiben bei Conventional Commits (`feat:`, `docs:` …), das ist eine andere Ebene.
- TDD: zuerst ein fehlschlagender Test, dann der Code. Tests nie abschwächen, um grün zu werden.
- Nie direkt auf `main` pushen. Immer über Branch und PR.
- Issues: Ein Issue enthält nie Regeln, nur Ziel und Link auf die Quelle (Intent, Spec, Plan). Regeln stehen genau einmal. Arbeits-Issues entstehen aus Spec oder Plan, Ideen (`idee`) und offene Fragen (`offene-frage`) dürfen früher angelegt werden. Vorlagen in `.github/ISSUE_TEMPLATE/`. Jeder PR nennt `Closes #N`, wenn er ein Issue erledigt.
- Coding-Regeln: `docs/coding-regeln.md`. Jede Regel dort nennt, wer sie prüft. Vor Code-Entscheidungen zu `domain/`, Begriffen (R3 bis R5) und Berechtigungen (R6, R7) dort nachlesen. Der Ansatz ist DDD-Lite (Begründung in `KURS.md`, Modul 6).
- Hooks in `.claude/hooks/` (Konfiguration `.claude/settings.json`) setzen Regeln deterministisch durch. Wird eine Aktion blockiert (Exit 2), den Hinweis befolgen, **nie umgehen** (kein Umweg über Bash, kein `--no-verify`). Bei Zweifel den Nutzer fragen.
- `.env*` und bestehende Dateien in `db/migration/` nie ändern. Neue Migration statt Änderung.
- Behauptungen aus Konkurrenz- oder Fremdquellen an der Primärquelle prüfen.
- Neue `.gitignore`-Einträge vor dem ersten Commit, das Repo ist öffentlich.

## Befehle
Alle Befehle laufen vom Hauptordner des Repos aus. Die Hooks und die CI nutzen dieselben.

**Backend** (`cd backend`, Gradle Wrapper, JDK 21):
- Lint (Format + Linter mit Typauflösung): `./gradlew ktlintCheck detekt`. Format automatisch korrigieren: `./gradlew ktlintFormat`
- Schnelle Tests (Stop-Hook): `./gradlew test`
- Integrationstests mit echtem Postgres (brauchen Docker): `./gradlew integrationTest`
- Alles wie in der CI: `./gradlew ktlintCheck detekt test integrationTest`
- Lokal starten (Datenbank muss laufen): `DB_PASSWORD=<aus deploy/dev.env> ./gradlew bootRun`

**Frontend** (`cd frontend`, npm, Node 24):
- `npm run lint` (ESLint) · `npm run format:check` / `npm run format` (Prettier) · `npx tsc -b` (Typen)
- Tests: `npm test -- --run` (einmalig, ohne `--run` im Watch-Modus)
- Entwicklungsserver: `npm run dev` (Port 5173, `/api` geht per Proxy an Port 8080) · Build: `npm run build`

**Docker** (Hauptordner, Datei `deploy/dev.env` aus `deploy/dev.env.example` kopieren):
- Alles starten (Image bauen): `docker compose --env-file deploy/dev.env -f deploy/compose.yml up -d --build --wait`, danach `http://localhost:8080`
- Nur Datenbank für lokale Entwicklung: `docker compose --env-file deploy/dev.env -f deploy/compose.yml -f deploy/compose.dev.yml up -d db`
- Stoppen, Daten bleiben: `docker compose -f deploy/compose.yml down` · Stoppen und Daten löschen: `... down -v`

**Deploy** (Hauptordner, Zugangsdaten liegen in `~/familien-dashboard-secrets/`, Details in `docs/betrieb.md`):
- QA rollt automatisch bei jedem Merge auf `main` aus (Workflow `deploy-qa.yml`, Self-hosted Runner muss laufen: `~/actions-runner/run.sh`).
- Prod: Tag setzen und pushen (`git tag v0.1.1 && git push origin v0.1.1`), dann in GitHub „Approve“ (Workflow `deploy-prod.yml`).
- Von Hand oder Rollback: `ENV_FILE=~/familien-dashboard-secrets/<qa|prod>.env scripts/deploy.sh <qa|prod> <git-sha>` (baut nie, löscht nie Daten). Rauchtest allein: `scripts/smoke-test.sh <qa|prod>`.

**Stolpersteine:**
- Der einfache `detekt`-Task hängt an `detektMain`/`detektTest`, weil nur mit Typauflösung `!!` gefunden wird. detekt 2.0.0-alpha.6 verlangt genau Kotlin 2.4.10.
- TypeScript bleibt auf 6.0.x, bis `typescript-eslint` Version 7 unterstützt. Neue Vite-Vorlagen bringen oxlint und TS 7 mit, beides bei Bedarf ersetzen.
- Tests, die nicht kompilieren, beanstandet der Hook. Für „rot“ erst einen Stub anlegen, der kompiliert, aber das Falsche tut.

## Dinge, die Claude falsch macht
*(ergänzen, sobald derselbe Fehler zweimal passiert)*
