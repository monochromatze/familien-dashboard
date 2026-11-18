# Plan P-002: QA und Prod lokal mit Continuous Deployment

- **Basiert auf:** `intent/I-001` (Rahmen: Familie nutzt die App im Heim-WLAN), `docs/adr/ADR-0001-tech-stack.md` (Betrieb, Sicherheitsregel für den Runner), `plans/P-001-walking-skeleton.md`, `KURS.md` Modul 5. Es gibt keine Spec, weil der Plan nur Betrieb und Auslieferung betrifft, keine Fachlogik.
- **Freigegeben von:** monochromatze am 2026-11-18
- **Status:** Akzeptiert

## Ziel
Zwei getrennte Stacks laufen auf deinem Mac aus **demselben** Image: `fd-qa` (Port 8081, gelbes „QA“-Banner) und `fd-prod` (Port 8080, vorerst nur auf diesem Mac erreichbar). Ein Merge auf `main` baut das Image (Tag = Git-SHA), rollt es auf QA aus und prüft per Smoke-Test. Ein Tag `v*` rollt **dasselbe** Image nach deiner Freigabe in GitHub auf Prod aus. Ein Rollback auf den vorherigen Tag ist geübt.

## Nicht Teil dieses Plans
Fachlogik, Flyway und Backups (Modul 9), Login (Modul 10), Monitoring (Modul 12), Zugriff im WLAN (iPhone und andere Geräte, kommt später), Zugriff von außerhalb des WLANs, Cloud (Bonus B1), automatische Rollbacks.

## Sicherheitsregeln (aus ADR-0001, hier konkret)
- Das Repo ist öffentlich, der Runner läuft auf deinem Mac. Der Deploy-Workflow startet **nur** bei `push` auf `main` und bei `push` von Tags `v*`. Nie bei `pull_request`. Die Prüfung der PRs bleibt auf GitHub-Runnern (`ci.yml`, unverändert).
- Ein Tag `v*` darf nur auf einen Commit zeigen, der auf `main` liegt (Job prüft das, sonst Abbruch). Tags legst nur du an.
- Der Self-hosted Runner läuft unter deinem normalen Benutzer, nicht als Admin. In den Repo-Einstellungen: Fork-PR-Workflows brauchen Freigabe („Require approval for all outside collaborators“).
- Secrets liegen nur in `deploy/qa.env` und `deploy/prod.env` auf dem Mac, außerhalb von Git (`.gitignore` deckt `*.env` bereits ab). Der Workflow liest sie lokal, GitHub kennt sie nicht.
- Actions in den neuen Workflows werden per Commit-SHA gepinnt (offener Punkt aus Modul 4).

## Dateien, die sich ändern
| Datei | Änderung |
|---|---|
| `deploy/qa.env.example`, `deploy/prod.env.example` | neu: Vorlagen (Port, DB-Name, Platzhalter-Passwort, `APP_ENV`, `APP_BIND`) |
| `deploy/compose.yml` | `APP_ENV` an die App durchreichen, Image-Name/Tag aus `APP_TAG`. Sonst unverändert: eine Datei für alle Stufen |
| `backend/…/web/EnvController.kt` (o. ä.) | neu: `GET /api/env` liefert `{"env":"qa"}`. Quelle: Umgebungsvariable `APP_ENV` |
| `frontend/src/App.tsx`, `App.test.tsx` | gelbes Banner „QA“, wenn `env` = `qa`. Prod zeigt kein Banner |
| `scripts/deploy.sh` | neu: `deploy.sh <qa\|prod> <tag>`: Stack starten, auf gesund warten, Smoke-Test, bei Fehler Abbruch mit Exit ≠ 0 |
| `scripts/smoke-test.sh` | neu: prüft `/api/health` (`"db":"ok"`) und die Startseite auf Port der Stufe |
| `.github/workflows/deploy-qa.yml` | neu: bei Push auf `main`, `runs-on: self-hosted`: Image bauen (Tag = SHA), QA ausrollen |
| `.github/workflows/deploy-prod.yml` | neu: bei Tag `v*`, GitHub Environment `prod` mit Pflicht-Freigabe, **kein Neubau**, nur Image mit der SHA des Tags ausrollen |
| `docs/betrieb.md` | neu: Runner einrichten, Deploy, Rollback, Fehlersuche |
| `CLAUDE.md`, `FORTSCHRITT.md`, `docs/coding-regeln.md` | Befehle, Modul 5, Durchsetzungsstatus |
| Repo-Einstellungen (GitHub, von dir) | Environment `prod` mit dir als Reviewer, Fork-PR-Freigabe, Runner-Registrierung |

## Reihenfolge der Arbeit
Jeder Abschnitt ist ein eigener Branch und PR. Wo Code entsteht, gilt: zuerst ein fehlschlagender Test, dann der Code.

**PR A – Stufen unterscheidbar machen** (`feature/stage-banner`)
1. **Roter Test:** Backend: `GET /api/env` liefert den Wert von `APP_ENV`. Frontend: bei `qa` erscheint „QA“, bei `prod` nicht.
2. Endpunkt, Banner, `qa.env.example` und `prod.env.example`, `compose.yml` reicht `APP_ENV` durch.
3. Manuell: zwei Stacks lokal parallel (`-p fd-qa` Port 8081, `-p fd-prod` Port 8080) mit je eigenem Volume. Daten der einen Stufe sind in der anderen nicht sichtbar.

**PR B – Deploy-Skripte** (`feature/deploy-scripts`)
1. `smoke-test.sh` und `deploy.sh`. Test per Hand: Gesunde Version → Exit 0. Absichtlich kaputte Version (falsche DB-Zugangsdaten) → Exit ≠ 0 und klare Meldung.
2. `shellcheck` im CI-Lauf, damit die Skripte geprüft werden.

**PR C – Runner und QA-Deploy** (`feature/cd-qa`)
1. Du registrierst den Runner (Schritte in `docs/betrieb.md`, Befehle einzeln, ich erkläre jeden).
2. `deploy-qa.yml`: Image bauen, Tag = SHA, `deploy.sh qa <sha>`. Beweis: Merge dieses PR löst den ersten QA-Deploy aus.

**PR D – Prod mit Freigabe** (`feature/cd-prod`)
1. Du legst das Environment `prod` mit Pflicht-Reviewer an.
2. `deploy-prod.yml`: Tag `v*` → Prüfung „liegt auf `main`“ → wartet auf deine Freigabe → `deploy.sh prod <sha>` mit dem **vorhandenen** Image.
3. Erster Release `v0.1.0`. Prod zeigt das Skeleton ohne Banner im WLAN.

**PR E – Rollback üben und Doku** (`docs/rollback-uebung`)
1. Absichtlich kaputte Version (`v0.1.1`) ausrollen, Smoke-Test schlägt fehl, Rollback auf `v0.1.0`-Image per `deploy.sh prod <alte-sha>`. Ablauf und Zeitbedarf in `docs/betrieb.md` festhalten.
2. `CLAUDE.md`, `coding-regeln.md`, `FORTSCHRITT.md` aktualisieren.

## Risiken
- **Was könnte brechen?** Der Runner läuft nur, wenn der Mac an und wach ist. Ein Deploy bei schlafendem Mac bleibt in der Warteschlange und läuft später. Für Prod heißt das: Der Mac muss im WLAN erreichbar bleiben (Energiespar-Einstellung prüfen).
- **Riskantester Schritt:** der Runner. Er ist ein Fremdzugang zu deinem Rechner. Darum nur die zwei Trigger, Fork-Freigabe, kein Admin-Benutzer, SHA-gepinnte Actions.
- **Datenverlust:** `docker compose down -v` löscht das Prod-Volume. `deploy.sh` benutzt nie `-v`. Ein echtes Backup kommt erst in Modul 9, bis dahin enthält Prod nur Testdaten.
- **Port 8080 doppelt belegt:** Dev-Stack und Prod nutzen beide 8080. Dev-Stack vor dem Prod-Test stoppen. Steht in `docs/betrieb.md`.
- **Tag auf falschem Commit:** Der Prod-Job bricht ab, wenn der Tag nicht auf `main` liegt.
- **Bewusst verworfene Alternativen:**
  - Image bei Prod neu bauen: widerspricht „Build once, deploy many“, QA-Test wäre wertlos.
  - Image-Registry (GitHub Container Registry): kostenlos, aber das Image enthält dann öffentlich lesbaren Code-Stand und braucht Token. Lokal im Docker des Macs genügt, weil beide Stufen auf demselben Rechner laufen. Ändert sich, wenn Prod auf einen anderen Rechner zieht.
  - Deploy per Hand (`docker compose up`): kein Lerneffekt zu CD, keine Freigabe-Spur.
  - Ein gemeinsamer Stack mit zwei Datenbanken: keine echte Trennung von Volume und Secrets.

## Beweis (woran wir erkennen, dass es funktioniert)
- **Tests:** `./gradlew test` und `npm test -- --run` grün (Env-Endpunkt, Banner), Commit-Reihenfolge Test vor Code.
- **Zwei Stufen:** `http://localhost:8081` zeigt gelbes „QA“, `http://localhost:8080` nicht. Ein in QA angelegter Datensatz (später) erscheint nicht in Prod.
- **CD QA:** Merge auf `main` startet automatisch den Deploy. `docker ps` zeigt `fd-qa` mit dem Image-Tag = der Merge-SHA.
- **CD Prod:** Tag `v0.1.0` → Workflow wartet auf dein „Approve“ → danach läuft `fd-prod` mit **derselben** SHA wie in QA (`docker inspect` zeigt dieselbe Image-ID).
- **Sicherheit:** Ein Test-PR löst **keinen** Deploy-Job auf dem Runner aus.
- **Rollback:** Kaputte Version erkannt, alter Tag wieder aktiv, `/api/health` ok.
- **Nur lokal:** `prod.env` setzt `APP_BIND=127.0.0.1`. Ein anderes Gerät im WLAN erreicht Prod noch nicht (bewusst, kommt später).
- **Metrik/Event:** nicht zutreffend.

## Entschieden am 2026-11-18
1. **Image-Ablage:** nur lokal im Docker des Macs, keine Registry.
2. **Versionsschema:** `v0.1.0`, `v0.1.1` …, Tag legst du selbst mit `git tag` und `git push --tags`.
3. **PR-Zuschnitt:** fünf kleine PRs A bis E.
4. **WLAN:** vorerst nur Test auf dem Mac (`APP_BIND=127.0.0.1`). Zugriff vom iPhone kommt später, dann `APP_BIND=0.0.0.0` und `<mac>.local`.
