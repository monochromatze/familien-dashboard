# Übung: „Familien-Dashboard“: eine WebApp von der Idee bis Prod

> Ein Lernpfad für PMs. **Der Agent (Claude Code) schreibt den Code.** Du lernst, was du hineingeben musst, wie du das Ergebnis prüfst und wie du mit Regeln, Hooks und Tests dafür sorgst, dass der Code gut wird.
> Er folgt dem **AI-native SDLC** (Plan → Design → Build → Test → Deploy → Maintain). **Alles läuft lokal und kostet kein Geld.**
>
> Den aktuellen Stand findest du in [FORTSCHRITT.md](FORTSCHRITT.md).

---

## 0. Grundidee

### Deine Rolle: Product Owner + Reviewer, nicht Programmierer
Im AI-native SDLC schreiben Menschen kaum noch Code. Sie **steuern** und **prüfen**. Deine drei Fähigkeiten in diesem Kurs:

| Fähigkeit | Was das konkret heißt |
|---|---|
| **1. Gute Eingaben machen** | `intent.md`, `spec.md` mit Akzeptanzkriterien, Coding-Regeln, `CLAUDE.md`. Je präziser die Eingabe, desto besser der Code. |
| **2. Leitplanken bauen** | Linter, Formatter, Architektur-Tests, **Hooks**, CI. Sie prüfen automatisch, damit du nicht jede Zeile lesen musst. |
| **3. Ergebnisse beurteilen** | Tests lesen (sie sind die ausführbare Spec), Diffs überfliegen, Warnzeichen erkennen, die richtigen Rückfragen stellen ([Review-Checkliste](vorlagen/review-checkliste.md)). |

**Die wichtigste Einsicht:** Du musst Kotlin nicht schreiben können. Du musst aber **Tests lesen** können, denn sie sagen in fast normaler Sprache, was der Code tun soll. Wenn die Tests deine Akzeptanzkriterien abbilden und grün sind, weißt du, dass der Code das Richtige tut. Ob er es auch *gut* tut, prüfen Linter, Architektur-Tests und ein Review-Agent.

### Fünf Regeln, wie ein Profi das aufbaut
1. **Zuerst ein „Walking Skeleton“.** In Woche 1 läuft eine App, die nur `Hallo` sagt, schon durch die ganze Pipeline: Git → Tests → Docker → CI → QA → Prod. Danach kommen Features nur noch in kleinen Schritten.
2. **Vertikale Slices.** Ein Feature wird komplett gebaut, von der Datenbank bis zur Oberfläche, inklusive Test, Tracking und Deploy.
3. **Echte Nutzer.** Deine Familie nutzt die Prod-Version über das WLAN.
4. **Jede Phase hinterlässt ein Artefakt in Git.** Dadurch kann jeder neue Chat den Stand herleiten.
5. **Leitplanken vor Autonomie.** Erst wenn Regeln, Hooks und Tests stehen, bekommt der Agent mehr Freiheit.

### Ergänzungen zu deiner Lernliste
Product Discovery & Wettbewerbsanalyse · **Agent-Hooks** · **Linter & Coding-Regeln** · **Architektur-Tests** · **Code-Review lernen** · DB-Migrationen · Secrets pro Stage · Mandantentrennung · OWASP-Grundlagen · Observability · Tracking-Plan & Metriken · Feature Flags · DSGVO (Kinderdaten) · Backup & Restore · ADRs (Architecture Decision Records)

---

## Tech-Stack: an deine Arbeit angelehnt, 100 % kostenlos

| Bereich | Wahl | Warum / Alternative |
|---|---|---|
| Backend | **Kotlin + Spring Boot 3** | Der Standard bei Kotlin-Backends in Firmen, ideal für DDD. *Alternative:* Ktor (schlanker, seltener). |
| Build & Pakete Backend | **Gradle** (Kotlin DSL) | Gradle ist für Kotlin, was npm für Node ist. |
| Frontend | **React + TypeScript + Vite** | Am weitesten verbreitet, und Agenten erzeugen dafür den zuverlässigsten Code. **Entschieden am 2026-11-14.** Verworfen wurden Svelte (am leichtesten zu lesen) und Angular (mehr Struktur). Die Begründung kommt ins ADR-0001. |
| Pakete Frontend | **npm** | Siehe unten |
| Datenbank | **PostgreSQL 16** im Docker-Container | |
| Migrationen | **Flyway** | Reine SQL-Dateien, gut lesbar |
| Tests Backend | JUnit 5, MockK, **Testcontainers** (echtes Postgres im Test), **ArchUnit** (Architektur-Regeln als Test) | |
| Tests Frontend | Vitest + Testing Library, **Playwright** (E2E) | |
| Linter / Formatter | **ktlint + detekt** (Kotlin), **ESLint + Prettier** (TypeScript) | |
| Auth | **Spring Security** (Sessions, Rollen). Später optional **Keycloak** als lokaler Container (Login wie in Firmen, mit OIDC) | |
| Container | Docker + Docker Compose (über **OrbStack**, kostenlos für private Nutzung) | |
| CI/CD | **GitHub Actions**: CI auf GitHub-Runnern, Deploy über einen **Self-hosted Runner auf deinem Mac** | |
| QA & Prod | **Zwei Docker-Compose-Stacks auf deinem Mac**, jeweils mit eigener Datenbank und eigenem Port | |
| Monitoring & Metriken | Spring Actuator, **Metabase** (Produkt-Dashboard), **Uptime Kuma**. Alle laufen als lokale Container. | |
| Agent | Claude Code (dein Abo). Kein API-Key nötig. | |

### Kurz beantwortet: deine Fragen
**Ist npm ein Paketmanager?** Ja. npm ist der Paketmanager von Node.js und wird mit Node mitgeliefert. `package.json` listet die Abhängigkeiten, `npm install` lädt sie, `npm run test` führt Skripte aus. Alternativen sind `pnpm` (schneller, spart Speicher) und `yarn`. Im Kotlin-Backend übernimmt **Gradle** diese Rolle (`build.gradle.kts`, `./gradlew test`).

**Gibt es kostenlose Test-Accounts bei AWS, Azure oder Google?** Ja, alle drei haben ein Gratis-Angebot. Typisch sind Startguthaben für einige Wochen oder Monate plus dauerhaft kostenlose Kleinst-Ressourcen. **Aber:** Alle verlangen eine Kreditkarte, und wer vergisst, etwas abzuschalten, bekommt eine Rechnung. Die genauen Konditionen ändern sich oft, prüfe sie direkt beim Anbieter. **Meine Empfehlung:** Bleib für den Kurs lokal. Das optionale Bonus-Modul B1 bringt Prod später einmal in die Cloud, dann **mit Budget-Alarm** als erstem Schritt.

### Wichtig: GitHub kostenlos nutzen
- Im **Free-Plan** gibt es einige Funktionen nur für **öffentliche** Repos: Branch Protection und Freigabe-Gates für Environments. Prüfe den aktuellen Stand in den GitHub-Docs. Deshalb empfehle ich ein **öffentliches Repo**. Der Code ist ohnehin harmlos. **Familiendaten bleiben lokal** in deiner Datenbank und landen nie in Git.
- **Self-hosted Runner und öffentliche Repos:** Deploy-Jobs laufen **nur** bei Push auf `main` oder bei Tags, **nie** bei Pull Requests von Fremden. Setze außerdem *Settings → Actions → „Require approval for all outside collaborators“*. Sonst könnte fremder Code auf deinem Mac laufen.
- Die **Claude Code GitHub Action** (automatisches PR-Review in der Cloud) braucht einen kostenpflichtigen API-Key. Wir machen das Review deshalb **lokal mit Claude Code** und deinem Abo, bevor der PR rausgeht.

---

## Wie Wissen und Stand über viele Chats erhalten bleiben

Du machst nicht alles in einem Chat. Damit jeder neue Chat weiß, wo du stehst, gibt es drei Mechanismen:

| Datei / Mechanismus | Zweck |
|---|---|
| [`FORTSCHRITT.md`](FORTSCHRITT.md) | **Checkliste aller Module**, aktuelles Modul, nächster Schritt, Lernjournal. Claude aktualisiert sie am Ende jeder Sitzung. |
| [`CLAUDE.md`](CLAUDE.md) | Wird von Claude Code **in jedem Chat automatisch gelesen**. Sie sagt: „Lies zuerst FORTSCHRITT.md. Du bist Tutor. Erkläre, bevor du baust.“ |
| `SessionStart`-Hook (Modul 3) | Zeigt dem Agenten den Stand aus `FORTSCHRITT.md` beim Start jeder Sitzung automatisch an. Das ist dein erster echter Hook. |
| Git-Historie + Artefakte | `intent/`, `specs/`, `plans/`, `docs/adr/` sind die eigentliche Wahrheit. FORTSCHRITT.md verweist nur darauf. |
| GitHub Issues (ab Modul 2) | Ein Issue pro Feature. Der PR schließt das Issue. |

**Start eines neuen Chats:** Schreib einfach *„Weiter mit dem Kurs“*. Claude liest CLAUDE.md und FORTSCHRITT.md und macht beim nächsten offenen Schritt weiter.
**Ende eines Chats:** Schreib *„Sitzung beenden“*. Claude aktualisiert FORTSCHRITT.md (erledigt, gelernt, nächster Schritt) und committet die Datei.

---

## Kursübersicht (etwa 10–12 Wochen bei 5–8 h pro Woche)

| # | Modul | SDLC-Stage | Lernziele | Ergebnis |
|---|---|---|---|---|
| 0 | Setup | – | – | Werkzeuge laufen |
| 1 | Discovery & Wettbewerbsanalyse | **Plan** | PM | `intent/I-001`, Wettbewerbsanalyse |
| 2 | Git & GitHub | – | 6 | Repo, Branch Protection, Issues |
| 3 | **Agent-Grundausstattung: CLAUDE.md, Hooks, Coding-Regeln, Linter** | Build | 7, Hooks, Linter | Agent arbeitet mit Leitplanken |
| 4 | Walking Skeleton: Docker, Postgres, CI | Build/Test | 1, 2, 5 | Grüne CI, App im Container |
| 5 | QA & Prod lokal + CD | **Deploy** | 5, 8 | `Hallo` auf QA und Prod, Prod nur nach Freigabe |
| 6 | Domäne modellieren (DDD-Lite) | **Design** | 4 | Glossar, Kontext-Karte, `spec.md` |
| 7 | Code beurteilen lernen | Test/Review | Review | Review-Routine, Architektur-Tests |
| 8 | Feature 1 mit TDD | **Build/Test** | 3, 4 | Aufgaben: melden, bestätigen |
| 9 | Postgres in der Tiefe | Build | 2 | Migrationen, Constraints, Backup |
| 10 | Anmeldung & Berechtigungen | Build/Test | 9 | Login, Einladung, Rollen, Mandantentrennung |
| 11 | Agent-Ausbau: Plan Mode, Skills, Verifier, Review-Agent | alle | 7 | Feature 2 fast ohne Eingriff |
| 12 | Metriken, Observability, Feature Flags | **Maintain** | PM | Tracking-Plan, Dashboard, Alarme |
| 13 | Abschluss: der volle Loop + Retro | alle | alle | Feature 3 komplett agentisch |
| B1 | *Bonus:* Prod in die Cloud (mit Budget-Alarm) | Deploy | – | optional |
| B2 | *Bonus:* Login mit Keycloak (OIDC/SSO) | Build | 9 | optional |
| B3 | *Später:* Ein-Befehl-Setup für neue Mitentwickler (mise) | Build | – | `mise.toml`, `docs/onboarding.md` |

Jedes Modul folgt demselben Muster:
**📥 Du gibst rein** → **🤖 Agent macht** → **🔍 Du prüfst** → **✅ Fertig, wenn**

---

## Modul 0 – Setup (½ Tag)

- 📥 Installiere:
  ```bash
  brew install --cask orbstack
  ```
  ```bash
  brew install node
  ```
  ```bash
  brew install --cask temurin@21
  ```
  OrbStack liefert Docker, `node` bringt npm mit, Temurin ist ein Java-Runtime für Kotlin und Gradle. Dazu kommen VS Code oder IntelliJ IDEA Community (kostenlos und sehr gut für Kotlin). Dann:
  ```bash
  gh auth login
  ```
- 🔍 Prüfe: `docker run hello-world`, `node -v`, `npm -v`, `java -version`, `gh auth status`
- 💡 Stelle den Output-Style von Claude Code auf **„Explanatory“**. Dann erklärt Claude, *warum* es etwas so baut.

---

## Modul 1 – Discovery & Wettbewerbsanalyse (SDLC: **Plan**)

Agentische PM-Arbeit: Der Agent recherchiert und strukturiert, du urteilst.

1. **Interviews:** 3–5 kurze Gespräche mit der Familie. Frage nach der Vergangenheit („Wann hat zuletzt etwas nicht geklappt?“), nicht nach Wünschen. Claude clustert die Notizen zu **Jobs-to-be-Done**.
2. **Wettbewerbsanalyse:** Claude recherchiert z. B. Cozi, FamilyWall, OurHome, Skylight, Google Family Link, Notion-Vorlagen und den echten Hauptkonkurrenten: Google-Kalender plus WhatsApp-Gruppe. Pro Anbieter: Zielgruppe, Jobs, Preismodell, Stärken und Schwächen, EU-Datenschutz, Muster in App-Store-Bewertungen. Dazu eine Positionierungs-Matrix und 3 Marktlücken.
   🔍 **Prüfe 3 Behauptungen selbst an der Quelle.** Notiere, wo der Agent falsch lag. Das ist die Kernfähigkeit im agentischen PM.
3. **intent.md** nach Playbook: Du erzählst, Claude stellt Rückfragen und schreibt [Vorlage intent](vorlagen/intent.md). Du korrigierst.
4. **North Star:** „Erledigte Aufgaben pro aktiver Familie und Woche“. **MVP (angepasst am 2026-11-16 und 2026-11-19, siehe `intent/I-001`):** Familie, Mitglieder anlegen und einladen, Rollen (Elternteil, Kind), Aufgaben mit Person und Pflicht-Frist (erledigt melden → Elternteil bestätigt), Übersicht „Heute / Diese Woche“. **Kein** vollständiger Kalender, **keine** Punkte/Belohnungen, **keine** Rolle Gast, **keine** Fristverschiebung, **keine** Einkaufsliste (Issue #6, wird Feature 2 in Modul 11). Hauptmetrik: Anteil rechtzeitig erledigter Aufgaben (Ziel 80 %).
   > Folgen für spätere Module: In Modul 6, 8 und 10 sind Punkte, Einkauf und die Rolle Gast am 2026-11-19 bereinigt. **Noch offen:** In Modul 12 setzt das Experiment „Punkte-Rangliste“ (Feature Flag) Punkte voraus. Beim Durcharbeiten ein anderes Experiment wählen oder Punkte vorher per eigenem Intent einführen.

✅ `intent/I-001-familien-dashboard.md`, `docs/wettbewerb.md`, `docs/interviews.md`

---

## Modul 2 – Git & GitHub (Lernziel 6)

**Konzepte:** Repository, Commit, Branch, Remote, Pull Request, Merge, Konflikt, Tag. **`main` ist immer deploybar.** Auch der Agent pusht nie direkt auf `main`.

```bash
cd ~/Documents/WebAppTraining
```
```bash
git init -b main && git add . && git commit -m "docs: Kursplan, Vorlagen, Fortschritt"
```
```bash
gh repo create familien-dashboard --public --source=. --push
```
Danach in GitHub: **Branch Protection** für `main` (PR nötig, CI muss grün sein), Issues pro MVP-Feature, erstes **ADR** `docs/adr/ADR-0001-tech-stack.md` ([Vorlage](vorlagen/adr.md)).

### Spickzettel
| Zweck | Befehl |
|---|---|
| Stand / Änderungen | `git status` · `git diff` · `git diff --staged` |
| Vormerken + Commit | `git add -p` · `git commit -m "feat: …"` ([Conventional Commits](https://www.conventionalcommits.org)) |
| Verlauf | `git log --oneline --graph` |
| Branch anlegen / wechseln | `git switch -c feat/aufgaben` · `git switch main` |
| Holen / Hochladen | `git pull --rebase` · `git push -u origin feat/aufgaben` |
| PR | `gh pr create --fill` · `gh pr checks` · `gh pr diff` · `gh pr merge --squash --delete-branch` |
| Rückgängig (sicher) | `git revert <commit>` · `git restore <datei>` |
| Zwischenspeichern | `git stash` · `git stash pop` |
| Release | `git tag v0.1.0 && git push --tags` |
| Parallel arbeiten | `git worktree add ../fd-x feat/x` |

**Übe einen Merge-Konflikt** bewusst und lass dir von Claude erklären, wie man ihn löst.

✅ Der PR mit dem ADR ist gemergt, und ein direkter Push auf `main` wird abgelehnt.

---

## Modul 3 – Agent-Grundausstattung: CLAUDE.md, Hooks, Coding-Regeln, Linter

**Warum so früh?** Weil der Agent ab jetzt Code schreibt. Leitplanken baut man, **bevor** man Autonomie gibt.

### 3a. Drei Ebenen, auf denen Regeln durchgesetzt werden
| Ebene | Wann greift sie? | Beispiel | Verbindlich? |
|---|---|---|---|
| **CLAUDE.md, Skills** | Während der Agent denkt | „Domain-Klassen dürfen kein Spring importieren“ | ❌ nur Empfehlung, der Agent kann es vergessen |
| **Claude-Code-Hooks** | Während der Agent handelt (vor/nach jedem Werkzeug-Aufruf) | Nach jeder Dateiänderung den Linter laufen lassen, Fehler zurückmelden | ✅ deterministisch, lokal |
| **Git-Hooks** (pre-commit) | Beim Commit | Formatter, Secret-Scan | ✅ lokal, umgehbar mit `--no-verify` |
| **CI (GitHub Actions)** | Beim PR | Alle Tests, Linter, Architektur-Tests | ✅ **das finale Tor**, nicht umgehbar |

**Faustregel aus dem Playbook:** Alles, was *immer* gelten muss, braucht eine deterministische Absicherung (Hook oder CI). CLAUDE.md und Skills sind nur Hinweise.

### 3b. Claude-Code-Hooks verstehen
Ein Hook ist ein **Shell-Befehl, den Claude Code automatisch bei einem Ereignis ausführt**. Konfiguriert wird er in `.claude/settings.json`, die im Repo liegt und damit für alle gilt.

| Ereignis | Wann | Typische Nutzung |
|---|---|---|
| `SessionStart` | Sitzung beginnt | FORTSCHRITT.md in den Kontext laden |
| `UserPromptSubmit` | Du schickst eine Nachricht | Kontext ergänzen, Prompts prüfen |
| `PreToolUse` | **Bevor** der Agent etwas tut (Datei ändern, Befehl ausführen) | **Blockieren:** `.env`-Dateien, bereits angewendete Migrationen, `git push origin main` |
| `PostToolUse` | **Nachdem** der Agent etwas getan hat | Formatter und Linter auf die geänderte Datei, Fehler gehen an den Agenten zurück |
| `Stop` | Der Agent will fertig melden | „Erst fertig, wenn die Tests grün sind“: Wenn sie rot sind, muss er weitermachen |

**Mechanik:** Der Hook bekommt per JSON (stdin) mitgeteilt, was der Agent gerade tut, zum Beispiel welche Datei. Endet er mit **Exit-Code 0**, geht es weiter. Mit **Exit-Code 2** wird die Aktion **blockiert**, und die Fehlermeldung geht an den Agenten, der sich dann korrigiert.

**Übung (der Agent baut, du verstehst):**
1. `SessionStart`: Fortschritt anzeigen
2. `PreToolUse`: Änderungen an `.env*` und an `db/migration/` (bereits angewendete Dateien) blockieren
3. `PostToolUse`: ktlint bzw. Prettier auf die geänderte Datei
4. `Stop`: Schnelle Unit-Tests müssen grün sein
5. 🔍 **Teste jeden Hook absichtlich:** Bitte den Agenten, `.env` zu ändern, und schau zu, wie er blockiert wird. Lies jedes Hook-Skript und lass es dir Zeile für Zeile erklären.
6. Hooks müssen **schnell** sein (Sekunden) und sich auf die geänderte Datei beschränken. Die volle Testsuite gehört in CI.

### 3c. Coding-Regeln & Linter
- **Linter** finden problematische Muster (ungenutzte Variablen, zu lange Funktionen, gefährliche Konstrukte). **Formatter** vereinheitlichen nur das Aussehen. Kotlin: ktlint (Format) + detekt (Qualität). TypeScript: Prettier (Format) + ESLint (Qualität, mit `strict`-TypeScript).
- **Architektur-Regeln als Tests (ArchUnit):** „Klassen in `domain` importieren nichts aus `infrastructure` oder Spring.“ Das macht DDD **prüfbar** statt nur gewünscht.
- **`docs/coding-regeln.md`** ([Vorlage](vorlagen/coding-regeln.md)): kurze, prüfbare Regeln. Jede Regel steht in einer von drei Spalten: *automatisch geprüft durch …* / *Review-Checkliste* / *nur Hinweis*. Ziel: möglichst viel in die erste Spalte.

✅ `.claude/settings.json` mit 4 Hooks, die du alle absichtlich ausgelöst hast. CLAUDE.md auf einer Seite. `docs/coding-regeln.md`.

---

## Modul 4 – Walking Skeleton: Docker, Postgres, CI

**Ziel:** `GET /api/health` liefert `{"status":"ok","db":"ok"}`, und die React-Seite zeigt diesen Status an.

### Struktur (Monorepo)
```
backend/                 Kotlin + Spring Boot (Gradle)
  src/main/kotlin/…/
    domain/              reine Fachlogik: kein Spring, keine DB
    application/         Use Cases
    infrastructure/      DB, Repositories
    web/                 REST-Controller
frontend/                React + TypeScript (npm, Vite)
deploy/
  compose.yml            App + Postgres
  qa.env.example  prod.env.example
Dockerfile               baut Frontend + Backend zu EINEM Image
.github/workflows/       ci.yml, deploy.yml
.claude/                 settings.json (Hooks), skills/, agents/
```

- 📥 Du gibst die Struktur oben, die Coding-Regeln und die Anweisung „TDD: zuerst ein fehlschlagender Test für /api/health“.
- 🤖 Agent: Test → Endpoint → Dockerfile (Multi-Stage: Node baut das Frontend, Gradle baut das Backend, schlankes Java-Runtime-Image, Nicht-Root-User) → `compose.yml` → `ci.yml` (Lint, Tests mit Postgres, Docker-Build).
- 🔍 Du prüfst:
  - Lass dir das Dockerfile Abschnitt für Abschnitt erklären: Image, Layer, Multi-Stage, Port, Volume.
  - Liegen Passwörter nur in `.env`-Dateien, die nicht in Git landen?
  - Ist der Test vor dem Code entstanden? Das siehst du in der Commit-Reihenfolge.
  - Probiere `docker compose down` gegen `docker compose down -v`: Was passiert mit den Daten?

**Docker-Spickzettel:** `docker compose up -d --build` · `docker compose logs -f app` · `docker compose ps` · `docker compose exec db psql -U app` · `docker compose down` · `docker system prune`

✅ CI ist grün, die App läuft lokal im Container.

---

## Modul 5 – QA & Prod lokal + CD (SDLC: **Deploy**, Lernziele 5 + 8)

### Prinzip „Build once, deploy many“: alles auf deinem Mac
```
PR ─► CI auf GitHub-Runnern (Lint, Tests, Build)                     kostenlos
merge auf main ─► Self-hosted Runner (dein Mac):
     Image bauen, Tag = Git-SHA ─► Stack "fd-qa" (Port 8081, DB qa) ─► Smoke-Test
Tag v* ─► Freigabe durch dich (Environment "prod") ─►
     DASSELBE Image ─► Stack "fd-prod" (Port 8080, DB prod) ─► Smoke-Test
```
- Zwei Compose-Projekte aus **derselben** `compose.yml`, getrennt nur durch die Konfiguration:
  `docker compose -p fd-qa --env-file deploy/qa.env up -d` bzw. `-p fd-prod --env-file deploy/prod.env`
- Jede Stage hat **eigenes Volume, eigene Datenbank und eigene Secrets**. QA bekommt Testdaten, Prod echte Daten.
- **Die Familie nutzt Prod** im WLAN: `http://<dein-mac>.local:8080`. QA zeigt ein gelbes „QA“-Banner.
- **Rollback üben:** Deploye absichtlich eine kaputte Version und gehe zurück zum vorherigen Image-Tag.

✅ Zwei laufende Stages. Prod wurde nur nach deinem „Approve“ in GitHub ausgerollt.

---

## Modul 6 – Domäne modellieren mit DDD-Lite (SDLC: **Design**, Lernziel 4)

**Warum „Lite“?** Im Agentic Workflow ist der **strategische** Teil von DDD der wertvollste: Der Agent kennt nur, was in Intent, Spec und Glossar steht, und ein fester Wortschatz hält Specs und Code zusammen. Die **taktischen** Muster (Aggregat, Value Object, Repository) erzeugt der Agent fast kostenlos, aber **du** musst sie lesen und prüfen. Deine Aufmerksamkeit ist der Engpass, und die App ist klein. Deshalb gilt:

| Teil | Umfang in diesem Kurs |
|---|---|
| **Immer:** Event Storming, Glossar, Bounded Contexts, Kontext-Karte | voll |
| **Immer:** `domain/` frei von Spring und JPA (per ArchUnit prüfbar) | voll |
| **Nur bei echter Regel:** Aggregat, Value Object | nur im Kontext „Aufgaben“ (`Aufgabe`, siehe Regeln A1 bis A10 in `docs/event-storming.md`) |
| **Weglassen:** Domain Events als Code, CQRS, Repository-Interface pro Tabelle | einfaches CRUD (z. B. Mitglieder anlegen) bleibt schlicht |

*Faustregel:* Ein taktisches Muster kommt erst ins Spiel, wenn es eine Regel gibt, die es schützt (Invariante), und nicht schon, weil das Muster existiert. *Alternativen, falls DDD-Lite nicht reicht oder zu viel ist:* Vertical Slices (Code pro Feature), Hexagonal (nur Trennung Fachlogik/Technik), Functional Core + Imperative Shell, Transaction Script.

**Konzepte:** Ubiquitous Language (Glossar) · Bounded Context · Aggregat (schützt Regeln, nur wo es Regeln gibt) · Value Object (nur wo es Regeln gibt).

**Event Storming** mit Post-its oder Miro (Free-Plan), gern mit der Familie: Domain Events (orange, Vergangenheitsform) → Commands (blau) → Akteure (gelb) → Regeln (lila) → Grenzen ziehen.

| Bounded Context | Aggregate | Beispiel-Invarianten |
|---|---|---|
| **Familie & Zugang** (unterstützend, einfache Prüfungen reichen) | `Familie`, `Mitglied`, `Einladung` | Mindestens ein Elternteil. Eine Einladung ist 7 Tage gültig und nur einmal verwendbar. |
| **Aufgaben** (Kern-Domäne) | `Aufgabe` | Person und Frist sind Pflicht, die Frist ändert sich nie. Nur die zugewiesene Person meldet „erledigt“, nur ein Elternteil bestätigt. Nach der Meldung ist die Aufgabe nicht mehr bearbeitbar. |

*Der Bereich „Einkauf“ und das `Punktekonto` entfallen im MVP (siehe `intent/I-001`). Sie kommen frühestens mit einem späteren Intent zurück.*

- 📥 intent.md, Glossar, Kontext-Karte
- 🤖 Claude schreibt `specs/S-001-aemtchen.md` ([Vorlage](vorlagen/spec.md)) mit **Akzeptanzkriterien im Format Gegeben/Wenn/Dann**
- 🔍 **Du reviewst die Spec.** Hier liegt der größte Hebel: Was hier unklar ist, wird im Code falsch.

✅ `docs/glossar.md`, `docs/kontext-karte.md`, `specs/S-001-aemtchen.md`, ADR „Modularer Monolith und DDD-Lite“ (hält fest, wo taktische Muster gelten und wo nicht)

---

## Modul 7 – Code beurteilen lernen (ohne ihn selbst zu schreiben)

**Ziel:** Du erkennst in 10 Minuten, ob ein PR gut, fragwürdig oder schlecht ist.

1. **Tests zuerst lesen.** Ein guter Testname liest sich wie ein Satz aus der Spec: `kind kann fremde Aufgabe nicht als erledigt melden`. Prüfe: Steckt jedes Akzeptanzkriterium in einem Test? Gibt es Tests für den **Fehlerfall**, nicht nur für den Erfolgsfall?
2. **Den Diff überfliegen, nicht lesen.** Achte auf Größe (> 400 Zeilen heißt: zu groß, aufteilen lassen), auf Dateien in falschen Ordnern und auf geänderte Tests ohne Grund.
3. **Warnzeichen** (die volle Liste steht in der [Review-Checkliste](vorlagen/review-checkliste.md)):
   - Tests wurden abgeschwächt oder gelöscht, damit sie grün werden
   - `@Suppress`, `eslint-disable`, `any`, `!!`: Regeln werden ausgehebelt
   - Fachlogik steckt im Controller oder in der React-Komponente statt in `domain/`
   - Kopierte, fast identische Code-Blöcke
   - Abfragen ohne `familie_id`
   - Neue Abhängigkeiten, die niemand bestellt hat
4. **Die richtigen Fragen an den Agenten** (auch als Prompt-Vorlage):
   - „Erkläre diesen PR in 5 Sätzen für einen PM.“
   - „Welche Akzeptanzkriterien sind **nicht** durch Tests abgedeckt?“
   - „Was ist die riskanteste Änderung? Was könnte in Prod schiefgehen?“
   - „Welche Alternative hast du verworfen und warum?“
   - „Zeige mir eine Stelle, an der eine Coding-Regel verletzt sein könnte.“
5. **Zweite Meinung mit frischem Kontext:** Ein Review-Subagent prüft, ohne den Bau-Chat zu kennen.
6. **Übung „Fehler finden“:** Lass den Agenten absichtlich einen PR mit 3 versteckten Fehlern bauen (z. B. fehlender Mandantenfilter, abgeschwächter Test, Logik im Controller). Findest du alle drei mit der Checkliste?

✅ Du hast den Fehler-PR durchgearbeitet, und die Checkliste ist an deine Erkenntnisse angepasst.

---

## Modul 8 – Feature 1 mit TDD (SDLC: **Build + Test**, Lernziele 3 + 4)

**TDD:** Rot (Test schlägt fehl) → Grün (minimaler Code) → Refactor. **Deine Rolle:** Du lieferst die **Testliste** (aus den Akzeptanzkriterien) und prüfst die Tests, **bevor** der Agent implementiert.

**Testpyramide:** viele Unit-Tests für `domain/` (Millisekunden, ohne DB) · einige Integrationstests (Repository gegen Postgres, REST-API) · 1–2 Playwright-E2E-Tests (Happy Path im Browser).

**Woher die Testliste kommt:** Sie entsteht aus den **Akzeptanzkriterien (ACs) in `specs/S-001`**, nicht direkt aus dem Event Storming. Die Kette ist: Event Storming findet die Regeln (`docs/event-storming.md`, A1 bis A10) → die Spec macht daraus prüfbare ACs (Gegeben/Wenn/Dann) → jeder AC wird mindestens ein Test. Die Spec enthält dazu die Liste „Abgedeckte Regeln" ([Vorlage](vorlagen/spec.md)): Jede Regel aus dem Event Storming steht dort bei mindestens einem AC, sonst ist die Spec unvollständig. Ein eigener Test pro Regel kommt nur dazu, wenn ein AC mehrere Regeln bündelt und ein Fehler sonst unklar bliebe.

**Testliste für das Aggregat `Aufgabe`** (entsteht, sobald `S-001` freigegeben ist. Der Agent arbeitet sie in dieser Reihenfolge ab, ein Commit pro grünem Test). *Beispiel für die Form, kein verbindlicher Inhalt:*
1. AC „Gegeben eine offene Aufgabe für Anna, wenn Ben sie als erledigt melden will, dann wird das abgelehnt." → Test `andere_person_kann_nicht_melden` (Regel A3)
2. AC „Gegeben eine gemeldete Aufgabe, wenn ein Kind sie bestätigen will, dann wird das abgelehnt." → Test `kind_kann_nicht_bestaetigen` (Regel A4)

**Arbeitsweise mit dem Agenten:** „Schreibe **nur** den Test für Punkt 1 und zeige ihn mir.“ → Du prüfst: Beschreibt der Test genau das Akzeptanzkriterium? → „Jetzt implementieren.“ Ab dem fünften Punkt darf der Agent mehrere Punkte am Stück machen.

✅ Ein Kind hakt auf QA ab, ein Elternteil bestätigt. Die Abdeckung von `domain/` liegt bei mindestens 90 % (Jacoco-Report).

---

## Modul 9 – PostgreSQL in der Tiefe (Lernziel 2)

- **Flyway-Migrationen** (`V1__aufgaben.sql`): Eine angewendete Migration wird **nie geändert**, nur ergänzt. Dein Hook aus Modul 3 erzwingt das.
- **Constraints:** `NOT NULL`, `FOREIGN KEY`, `CHECK (punkte >= 0)`, `UNIQUE`. Die Domäne prüft, die DB garantiert.
- **`familie_id` + Index** in jeder fachlichen Tabelle
- **Transaktionen:** Ein Use Case entspricht einer Transaktion. Lass dir zeigen, was bei einem Fehler in der Mitte passiert.
- **Expand → Migrate → Contract:** Spalten werden nie in einem Schritt umbenannt.
- **psql üben:** `\dt`, `\d aufgabe`, `EXPLAIN ANALYZE` mit und ohne Index
- **Backup:** `pg_dump` von Prod und **Restore nach QA**. Miss die Dauer.

---

## Modul 10 – Anmeldung & Berechtigungen (Lernziel 9)

**Authentifizierung** heißt *Wer bist du?*, **Autorisierung** heißt *Was darfst du?*

### So melden sich Nutzer an (Spring Security)
1. **Registrierung** des ersten Elternteils. Das Passwort wird nur als Hash gespeichert (BCrypt/Argon2), nie im Klartext.
2. **Login:** Nach der Prüfung bekommt der Nutzer ein **Session-Cookie** (`HttpOnly`, `Secure`, `SameSite`). Die Session liegt in Postgres (Spring Session JDBC). Halte in einem ADR fest, warum Sessions und nicht JWT: Bei einer App mit eigenem Frontend auf derselben Domain sind Sessions einfacher und sicherer.
3. **Einladung:** Ein Elternteil legt zuerst das Mitglied an (sofort in der App verfügbar), danach folgt die Einladung zum eigenen Zugang. Ein zufälliger Token, 7 Tage gültig, einmal verwendbar, die Rolle ist durch das angelegte Mitglied vorgegeben. Für Kinder optional ein PIN-Login auf dem Familien-Tablet.
4. Logout, Passwort-Reset (lokal mit **Mailpit**-Container als Fake-Mailserver, kostenlos), Rate-Limiting, CSRF-Schutz.
5. **In Firmen** übernimmt das meist ein Identity-Provider (Keycloak, Entra ID, Okta) über OIDC. Den zeigt Bonus-Modul B2, lokal mit Keycloak.

### Zugriff nur auf ausgewählte Aktionen (RBAC)
Rollen bündeln **Berechtigungen**. Der Code prüft immer die Berechtigung, nie den Rollennamen, z. B. `@PreAuthorize("hasAuthority('aufgabe:bestaetigen')")`. In React werden Buttons nur ausgeblendet. **Die echte Prüfung passiert immer im Backend.**

| Berechtigung | Elternteil | Kind |
|---|:-:|:-:|
| `aufgabe:anlegen` (Kind nur für sich und Elternteile) | ✅ | ✅ |
| `aufgabe:erledigt_melden` (eigene) | ✅ | ✅ |
| `aufgabe:bestaetigen` | ✅ | ❌ |
| `aufgabe:alle_ansehen` (Kinder sehen nur eigene) | ✅ | ❌ |
| `mitglied:anlegen`, `mitglied:einladen` | ✅ | ❌ |
| `dashboard:ansehen` (eigene Sicht) | ✅ | ✅ |

*Rolle Gast und Einkauf entfallen im MVP. „Korrigieren und Zurückziehen nur durch den Ersteller“ ist eine Domänenregel (A10), keine Berechtigung.*

**Drei Prüf-Ebenen:** Endpoint (Berechtigung) · Mandant (`familie_id` aus der Session, **nie** aus der URL) · Domäne (z. B. „nur die eigene Aufgabe“).

**Tests:** Die Matrix oben wird zu einem **parametrisierten Test** (Rolle × Aktion → 200 oder 403). Dazu kommt ein Test für die Mandantentrennung: Ein Zugriff auf eine Aufgabe aus Familie B liefert **404**. Danach einmal die OWASP Top 10 abhaken.

---

## Modul 11 – Agent-Ausbau (Lernziel 7, Playbook komplett)

| Artefakt | Zweck |
|---|---|
| **Plan Mode** + `plans/P-NNN.md` ([Vorlage](vorlagen/plan.md)) | Kein Code ohne akzeptierten Plan. Frage: „Was könnte brechen? Riskantester Schritt? Verworfene Alternativen?“ |
| Skill `ddd-konventionen` | Wo Code hingehört, welche Begriffe das Glossar vorgibt und wann ein Aggregat sinnvoll ist (nur bei echter Regel) |
| Skill `sichere-route` | Jeder neue Endpoint braucht Berechtigung, Mandantenfilter und einen 403/404-Test |
| Subagent `verifier` | Prüft am Ende mit frischem Kontext: Diff gegen Plan, Tests, Rechte-Matrix |
| Subagent `reviewer` + `REVIEW.md` | Lokales Review in 3 Durchgängen: Bugs, Security, DDD. **Du gibst den Merge frei.** |
| Stufenweise Autonomie | dev: frei · QA: Deploy automatisch · Prod: nur nach deiner Freigabe |

**Übung:** Feature 2 (**Einkaufsliste**) durchläuft den ganzen Ablauf: intent → spec → Plan Mode → plan.md → Auto-Mode im Worktree → Verifier → Reviewer → PR → du mergst → QA → Prod.
**Miss den Ablauf:** Zeit von intent bis Merge, Anzahl der Nacharbeits-Runden, „Passt der Diff zum Plan?“, Anteil der CI-Läufe, die beim ersten Mal grün sind.

---

## Modul 12 – Metriken, Observability, Feature Flags (SDLC: **Maintain**)

**Technik (alles lokal):** strukturierte JSON-Logs mit `requestId`, `familieId` und `stage` · Spring Actuator `/health` · **Uptime Kuma** überwacht QA und Prod · DORA-Metriken aus GitHub Actions (Deploy-Häufigkeit, Lead Time, Change Failure Rate, Time to Restore).

**Produkt-Analytics (dein PM-Kern):**
1. **Tracking-Plan zuerst** ([Vorlage](vorlagen/tracking-plan.md)): Frage → Event → Properties. Keine personenbezogenen Daten.
2. Die fachlichen Ereignisse aus dem Event Storming (z. B. „Aufgabe bestätigt“) werden an den entsprechenden Stellen in eine Tabelle `analytics_event` geschrieben. Dein Event Storming liefert so direkt dein Tracking. *(Hier reicht ein einfacher Schreibaufruf, kein Domain-Event-Mechanismus.)*
3. **Metriken:** North Star · Aktivierung (2 Mitglieder + 1 bestätigte Aufgabe in 7 Tagen) · Retention als Kohorten · Anteil der Kinder, die selbst abhaken · Guardrail: Anteil der Zurückweisungen
4. **Dashboard in Metabase** (Container, verbunden mit der Prod-DB, nur lesend)
5. **Feature Flag:** „Punkte-Rangliste“ nur für eine Person aktivieren. Du lernst die Mechanik eines Experiments.
6. **DSGVO:** Einwilligung, Datensparsamkeit bei Kindern, „Familie löschen“, Datenexport

**Den Loop schließen:** `bands.yaml` legt Schwellenwerte fest (z. B. Fehlerrate > 2 %, North Star sinkt 2 Wochen in Folge). Wird einer gerissen, schreibt Claude eine Diagnose als **neue intent.md**, und der Loop beginnt von vorn.

---

## Modul 13 – Abschluss

Feature 3, z. B. eine Wochenansicht für das Küchen-Tablet, kommt aus deinen Metriken oder Interviews und durchläuft komplett agentisch alle Stages. Danach schreibst du `docs/retro.md`: Wo war der Agent stark? Wo musstest du eingreifen? Welche Leitplanken haben am meisten gebracht? Welche Metrik hat eine Entscheidung verändert?

---

## Bonus
- **B1 – Prod in die Cloud:** AWS, Azure oder GCP Free Tier. **Schritt 1 ist immer ein Budget-Alarm von 1 €.** Dasselbe Image läuft auf einer kleinen VM, danach wird alles wieder gelöscht.
- **B2 – Keycloak:** Login über OIDC wie in Firmen, Rollen kommen aus Keycloak, lokal als Container.
- **B3 – Ein-Befehl-Setup (mise):** Idee aus Modul 0: Wer neu ins Projekt kommt, soll nicht alles von Hand installieren (OrbStack, Node, Java …). Stattdessen legt eine `mise.toml` im Repo die exakten Versionen von Node und Java fest, und `mise install` holt alles. Dazu Tasks wie `mise run setup`, `mise run test`, `mise run dev` (Postgres per Docker Compose starten, Frontend- und Backend-Abhängigkeiten laden). Ergebnis: `docs/onboarding.md` und ein Test, ob ein frischer Klon mit einem Befehl läuft (z. B. in einem sauberen Container oder auf einem zweiten Mac). Sinnvoll nach Modul 4, wenn es wirklich Build-Befehle gibt, und passt zum Abschnitt „Befehle“ in `CLAUDE.md`. **Hinweis:** OrbStack/Docker selbst und `gh` kann mise nicht ersetzen, die bleiben ein Schritt im Onboarding.

---

## Lernziel-Checkliste
- [ ] (1) Docker · (2) Postgres · (3) TDD · (4) DDD · (5) CI/CD · (6) Git/GitHub · (7) Agentic SDLC · (8) QA + Prod · (9) Login + Rollen
- [ ] Hooks · Linter & Coding-Regeln · Code-Review · PM: Discovery, Wettbewerb, North Star, Tracking, Dashboard
