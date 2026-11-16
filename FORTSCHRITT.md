# Fortschritt

> **Für Claude:** Lies diese Datei zu Beginn jeder Sitzung. Setze beim nächsten offenen Schritt an. Aktualisiere sie am Ende jeder Sitzung (Checkboxen, „Aktueller Stand“, Lernjournal) und committe sie.

## Aktueller Stand
- **Aktuelles Modul:** 3 – Agent-Grundausstattung (fast fertig, Linter-Konfiguration offen)
- **Nächster Schritt:** PR für `feat/modul-3-agent-leitplanken` mergen. In neuer Sitzung prüfen, ob der SessionStart-Hook den Stand anzeigt, und Migrations-Hook live testen. Dann Modul 4. Dort nachholen: Linter-Konfiguration (ktlint, detekt, Prettier, ESLint), PostToolUse- und Stop-Hook mit echtem Code testen, Branch Protection um den CI-Check ergänzen.
- **Offene Entscheidungen:** Basismessung für den Zielwert 80 % (vier Wochen Aufgaben notieren?), Bestätigung bei verspäteter Bestätigung, PIN-Login für Kinder.
- **Blocker:** –

## Entscheidungen (Kurzfassung, Details in docs/adr/)
| Datum | Entscheidung |
|---|---|
| 2026-11-14 | Stack: Kotlin + Spring Boot, TypeScript + React + Vite, Postgres, alles lokal, keine Kosten |
| 2026-11-14 | Frontend: React, weil es am verbreitetsten ist, Agenten dafür am zuverlässigsten schreiben und es bei der Arbeit genutzt wird. Svelte (lesbarer) und Angular (mehr Struktur) wurden verworfen. |
| 2026-11-14 | QA und Prod als zwei Docker-Compose-Stacks auf dem Mac, Deploy über einen Self-hosted GitHub Runner |
| 2026-11-14 | Der Agent schreibt den Code. Der Nutzer liefert die Eingaben und prüft (Rolle PM/Reviewer). |
| 2026-11-14 | GitHub-Repo öffentlich (kostenlose Branch Protection und Environments). Familiendaten bleiben lokal. |
| 2026-11-16 | MVP: kein vollständiger Kalender, nur Übersicht „Heute“ und „Diese Woche“. Punkte und Belohnungen **nicht** im MVP (Aufgaben: erledigt melden → Elternteil bestätigt). |
| 2026-11-16 | Neuer Job 5 aufgenommen (Essensauswahl nach Vorlieben aller Teilnehmenden), als Hypothese ohne Beleg aus Interviews. **Nach dem MVP** (Folgeversion). |
| 2026-11-16 | `intent/001` akzeptiert. Hauptmetrik: Anteil rechtzeitig erledigter Aufgaben, Ziel 80 % nach 3 Monaten. Frist ist Pflicht, keine Fristverschiebung im MVP (Messung später), Rolle Gast entfällt. Zugriff nur im Heim-WLAN. |
| 2026-11-16 | Repo `monochromatze/familien-dashboard` öffentlich. Branch Protection auf `main`: PR Pflicht, gilt auch für Admins, kein Force-Push, kein Löschen, 0 Freigaben (Einzelperson). CI-Check kommt in Modul 4. ADR 0001 (Tech-Stack) akzeptiert. |

## Module
- [x] **0 – Setup:** OrbStack · Node/npm · Java 21 · IntelliJ/VS Code · `gh auth login` · Output-Style „Explanatory“
- [x] **1 – Discovery & Wettbewerb:** Interviews · Jobs-to-be-Done · Wettbewerbsanalyse (3 Fakten geprüft) · `intent/001` · North Star · MVP
- [x] **2 – Git & GitHub:** `git init` · Repo erstellt · Branch Protection · Issues (#3–#7) · ADR 0001 · Merge-Konflikt geübt
- [~] **3 – Agent-Grundausstattung:** [x] CLAUDE.md ausgebaut · [~] Hooks (PreToolUse live ausgelöst; SessionStart, PostToolUse, Stop noch ohne Live-Beleg) · [x] `docs/coding-regeln.md` · [ ] Linter (kommt in Modul 4)
- [ ] **4 – Walking Skeleton:** `/api/health` mit Test · Dockerfile · compose.yml · CI grün
- [ ] **5 – QA & Prod + CD:** Self-hosted Runner · `fd-qa` / `fd-prod` · Prod-Freigabe · Rollback geübt
- [ ] **6 – DDD:** Event Storming · Glossar · Kontext-Karte · `specs/001` reviewt
- [ ] **7 – Code beurteilen:** Review-Checkliste angewendet · Fehler-PR (3 von 3 Fehlern gefunden?)
- [ ] **8 – Feature 1 (TDD):** Testliste 1–8 · Abdeckung von `domain/` ≥ 90 % · läuft auf QA und Prod
- [ ] **9 – Postgres:** Flyway · Constraints · Index + `EXPLAIN` · Backup-Restore-Test
- [ ] **10 – Auth & Rollen:** Login · Einladung · RBAC-Matrix als Test · Mandantentrennung-Test · OWASP abgehakt
- [ ] **11 – Agent-Ausbau:** Plan Mode · 2 Skills · Verifier · Reviewer · Feature 2 über den vollen Ablauf
- [ ] **12 – Metriken:** Tracking-Plan · Events-Tabelle · Metabase-Dashboard · Uptime Kuma · Feature Flag · bands.yaml
- [ ] **13 – Abschluss:** Feature 3 komplett agentisch · `docs/retro.md`
- [ ] *B1 – Cloud (optional)* · *B2 – Keycloak (optional)*
- [ ] *B3 – Ein-Befehl-Setup mit mise (später, nach Modul 4 sinnvoll):* `mise.toml` · `mise run setup` · `docs/onboarding.md` · frischer Klon getestet

## Lernjournal
> Pro Sitzung 2–4 Stichpunkte: Was habe ich verstanden? Was hat der Agent falsch gemacht? Was kommt in die Checkliste oder in CLAUDE.md?

### 2026-11-14 – Kursplanung
- Kursplan erstellt und auf Kotlin/TS/lokal angepasst
- Gelernt: npm = Paketmanager für Node. Gradle = Build- und Paketwerkzeug für Kotlin.
- Gelernt: Regeln werden auf drei Ebenen durchgesetzt: CLAUDE.md/Skills (Hinweis) → Hooks (lokal, deterministisch) → CI (finales Tor)

### 2026-11-14 – Modul 0 Setup
- Installiert und geprüft: OrbStack (Docker, `hello-world` läuft), Node 26 / npm 11, Java 21 (Temurin), `gh` angemeldet
- Gelernt: Nach der Installation muss OrbStack einmal gestartet werden, erst dann gibt es den `docker`-Befehl (und ein neues Terminal öffnen)
- Idee für später: Ein-Befehl-Setup für neue Mitentwickler mit mise, als Modul B3 eingeplant
- Output-Style „Explanatory“ in den App-Einstellungen gesetzt (gilt ab der nächsten Sitzung)

### 2026-11-16 – Modul 1 Discovery & Wettbewerb
- Gelernt: Ein **Intent** beschreibt Problem und messbares Ergebnis, ein Epic/Feature dagegen eine Lösung. Kette: Intent → Spec → Plan → Code. `intent/001` ist der Produkt-Intent, kleinere Intents folgen pro Auslöser.
- Gelernt: **Jobs-to-be-Done** („Wenn …, will ich …, damit …“) entstehen aus Geschichten. Stärke = in wie vielen Interviews ein Job unabhängig vorkommt. Eine Familie ist keine unabhängige Stichprobe.
- Gelernt: Eine Metrik im Intent erzwingt Produktentscheidungen (rechtzeitig erledigt → Frist ist Pflicht) und lässt sich umgehen (Fristverschiebung, später messen).
- Agent lag falsch: Bei OurHome hatte Claude „2022 zuletzt aktualisiert, vermutlich aufgegeben“ geschrieben, die Quelle war ein Konkurrent. Tatsächlich Update am 02.10.2026. Cozi-Preise und Skylight-Plus-Belohnungen stimmten.
- Für Checkliste/CLAUDE.md: Behauptungen aus Konkurrenzseiten an der Primärquelle prüfen. Wettbewerbs-Lücken (EU-Datenschutz pro Anbieter, App-Store-Bewertungsmuster) sind nicht recherchiert.
- Entscheidungen: Frist Pflicht, keine Fristverschiebung im MVP, kein Gast, keine Punkte, nur „Heute/Diese Woche“, Essen/Rezepte (Job 5) nach dem MVP. `docs/vision.md` bewusst nicht genutzt (Entwurf liegt noch im Ordner).

### 2026-11-16 – Modul 2 Git & GitHub
- Gelernt: Ablauf **Branch → Commit → Push → PR → Merge → Pull**. Lokal und GitHub sind zwei Kopien: Ein Merge über `gh` ändert zuerst nur GitHub.
- Gelernt: Branch Protection ist das Schloss, CLAUDE.md nur ein Hinweis. Der Test-Push auf `main` wurde mit `GH006` abgelehnt. Die CI-Pflicht fehlt noch, weil es noch keine CI gibt (Modul 4).
- Gelernt: Löschen entfernt eine Datei nur aus dem aktuellen Stand, nicht aus dem Verlauf (`docs/vision.md` ist im öffentlichen Verlauf noch lesbar, harmlos). Bei Geheimnissen müsste man das Geheimnis ändern.
- Gelernt: Ein Merge-Konflikt entsteht bei verschiedenen Änderungen an derselben Zeile. Die Markierungen `<<<<<<<`, `=======`, `>>>>>>>` entfernen, `git add`, `git commit`. Git öffnet dabei `vi`: `Esc`, dann `:wq` (oder `:q!` zum Abbrechen).
- Hinweis: Die E-Mail-Adresse im Commit-Verlauf eines öffentlichen Repos ist für alle sichtbar. Dafür gibt es die GitHub-noreply-Adresse.
- Für CLAUDE.md/Checkliste: Neue `.gitignore` vor dem ersten Commit anlegen (`.env*` ausschließen), weil das Repo öffentlich ist.

### 2026-11-16 – Modul 3 Agent-Grundausstattung
- Gelernt: Ein Hook ist ein Skript, das Claude Code selbst startet. Exit 0 = weiter, Exit 2 = blockiert, stderr geht als Erklärung an den Agenten. So wird aus dem Hinweis in CLAUDE.md ein Schloss.
- Gelernt: Der Stop-Hook braucht die Prüfung `stop_hook_active`, sonst droht eine Endlosschleife bei dauerhaft roten Tests.
- Live blockiert: `cat .env` und `git push origin main`. Nur per Handtest geprüft: Migrations-Regel. PostToolUse und Stop überspringen ohne Code still, echter Test in Modul 4.
- Für CLAUDE.md: Blockaden nie umgehen, `.env*` und bestehende Migrationen nicht ändern (bereits eingetragen).
