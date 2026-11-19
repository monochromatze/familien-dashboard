# Fortschritt

> **Für Claude:** Lies diese Datei zu Beginn jeder Sitzung. Setze beim nächsten offenen Schritt an. Aktualisiere sie am Ende jeder Sitzung (Checkboxen, „Aktueller Stand“, Lernjournal) und committe sie.

## Aktueller Stand
- **Aktuelles Modul:** 5 – QA & Prod + CD abgeschlossen (Plan `P-002`, PR #18 bis #23 und PR E). Als Nächstes Modul 6 (DDD-Lite).
- **Nächster Schritt:** Modul 6: Event Storming (gern mit der Familie), Glossar `docs/glossar.md`, Kontext-Karte, dann `specs/S-001` zum Review. Kein Code vor Spec und Plan.
- **Offene Entscheidungen:** Basismessung für den Zielwert 80 % (vier Wochen Aufgaben notieren?), Bestätigung bei verspäteter Bestätigung, PIN-Login für Kinder. Technisch: detekt läuft als Alpha (`2.0.0-alpha.6`), restliche Actions in `ci.yml` per Commit-SHA pinnen (neue Workflows sind schon gepinnt), Migrations-Hook braucht eine echte Migration (Modul 9), **Fork-PR-Freigabe** auf `all_external_contributors` setzen (Befehl in `docs/betrieb.md`, noch nicht gesetzt), Prod im WLAN/iPhone erreichbar machen (`APP_BIND=0.0.0.0`), Runner später als Dauerdienst, alte Images aufräumen.
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
| 2026-11-16 | `intent/I-001` akzeptiert. Hauptmetrik: Anteil rechtzeitig erledigter Aufgaben, Ziel 80 % nach 3 Monaten. Frist ist Pflicht, keine Fristverschiebung im MVP (Messung später), Rolle Gast entfällt. Zugriff nur im Heim-WLAN. |
| 2026-11-16 | Repo `monochromatze/familien-dashboard` öffentlich. Branch Protection auf `main`: PR Pflicht, gilt auch für Admins, kein Force-Push, kein Löschen, 0 Freigaben (Einzelperson). CI-Check kommt in Modul 4. ADR-0001 (Tech-Stack) akzeptiert. |
| 2026-11-17 | Branch-Namen nennen die Phase: `intent/`, `spec/`, `plan/`, `adr/`, `feature/` (nicht `feat/`), `fix/`, `chore/`, `docs/` nur für sonstige Dokumentation. Commit-Nachrichten bleiben bei `feat:`, `docs:` usw. |
| 2026-11-17 | Nummerierung mit Typ-Präfix, je Typ eigener Zähler: `I-001` (Intent), `S-001` (Spec), `P-001` (Plan), `ADR-0001`. Grund: gleiche Nummern bei Intent und Plan suggerierten ein Paar. Die Beziehung steht im Feld „Basiert auf“. |
| 2026-11-17 | Walking Skeleton: TypeScript bleibt auf 6.0 und ESLint (statt TS 7/oxlint aus dem Vite-Template), weil `typescript-eslint` TS 7 noch nicht kann. detekt 2.0.0-alpha.6 mit Kotlin 2.4.10, weil der stabile detekt neue Kotlin-Versionen nicht kennt. Ein Image für alles (Multi-Stage), Spring liefert das Frontend aus. |
| 2026-11-17 | Branch Protection auf `main` um die Pflicht-Checks Backend, Frontend und Docker ergänzt (erst nach dem ersten grünen CI-Lauf). Rest unverändert. |
| 2026-11-17 | PR-Titel und Commit-Typen bleiben bei Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`), auch wenn der Branch `feature/…` heißt. Zwei Ebenen: Branch-Name = Etikett für Menschen, Commit-Typ = Schlüsselwort für Werkzeuge. |
| 2026-11-18 | Plan `P-002` akzeptiert: Image nur lokal im Docker (keine Registry), Versionen `v0.1.0` ff. per Tag von Hand, fünf PRs, Prod vorerst nur auf dem Mac (`127.0.0.1`), iPhone später. Repo bleibt öffentlich, weil Branch Protection und Environment-Freigabe sonst kostenpflichtig wären. |
| 2026-11-18 | Deploy: Runner nur von Hand gestartet (kein Dauerdienst), Workflows nur bei Push auf `main` bzw. Tag `v*`, Zugangsdaten in `~/familien-dashboard-secrets/` außerhalb des Repos, Prod baut nie neu (Image = Git-SHA vom QA-Deploy). |

## Module
- [x] **0 – Setup:** OrbStack · Node/npm · Java 21 · IntelliJ/VS Code · `gh auth login` · Output-Style „Explanatory“
- [x] **1 – Discovery & Wettbewerb:** Interviews · Jobs-to-be-Done · Wettbewerbsanalyse (3 Fakten geprüft) · `intent/I-001` · North Star · MVP
- [x] **2 – Git & GitHub:** `git init` · Repo erstellt · Branch Protection · Issues (#3–#7) · ADR-0001 · Merge-Konflikt geübt
- [x] **3 – Agent-Grundausstattung:** CLAUDE.md ausgebaut · Hooks (PreToolUse, PostToolUse, Stop, SessionStart live belegt; Migrations-Hook nur per Handtest, echter Test ab Modul 9) · `docs/coding-regeln.md` · Linter (ktlint, detekt, ESLint, Prettier) konfiguriert
- [x] **4 – Walking Skeleton:** `/api/health` mit Test · Postgres · React-Seite · Dockerfile · compose.yml · CI grün · Branch Protection mit Pflicht-Checks (Plan `P-001`, PR #13 bis #16)
- [x] **5 – QA & Prod + CD:** Self-hosted Runner · `fd-qa` / `fd-prod` · Prod-Freigabe · Rollback geübt (Plan `P-002`, PR #18 bis #23 und PR E)
- [ ] **6 – DDD-Lite:** Event Storming · Glossar · Kontext-Karte · `specs/S-001` reviewt
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
- Gelernt: Ein **Intent** beschreibt Problem und messbares Ergebnis, ein Epic/Feature dagegen eine Lösung. Kette: Intent → Spec → Plan → Code. `intent/I-001` ist der Produkt-Intent, kleinere Intents folgen pro Auslöser.
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

### 2026-11-17 – Modul 4 Walking Skeleton
- Gelernt: Ein **Walking Skeleton** ist die dünnste durchgehende Scheibe durch alle Schichten, hier `/api/health` von der Datenbank bis zur React-Seite. **Port und Adapter:** `HealthService` kennt nur das Interface `DatabaseCheck`, der Adapter `JdbcDatabaseCheck` kam in PR B dazu, ohne dass Service oder Controller angefasst wurden.
- Gelernt: **Zwei Teststufen**. `test` ist schnell (Stop-Hook), `integrationTest` startet echtes Postgres per Testcontainers (CI). **Multi-Stage-Dockerfile**: Bauwerkzeuge landen nicht im Endimage, läuft als Nicht-Root. `docker compose down` behält Daten, `down -v` löscht sie. Pflicht-Checks erst nach dem ersten grünen Lauf setzen.
- Hooks live belegt: PostToolUse (ktlint, `!!`, `any`, Prettier-Auto-Format), Stop (roter Test blockiert das Fertigmelden), PreToolUse (ein Befehl mit `.env`-Pfaden wurde blockiert und nicht umgangen), SessionStart.
- Agent lag falsch oder musste nachbessern: Der einfache `detekt`-Task sieht `!!` nicht, erst `detektMain`/`detektTest` mit Typauflösung. Kotlin-Version musste zu detekt passen. Das Vite-Template brachte TS 7 und oxlint mit, das passte nicht zu unseren Regeln. Ein Test, der nicht kompiliert, wird vom Hook beanstandet, deshalb zuerst ein Stub. PR-Titel von PR A mit `feature:` statt `feat:` (Branch-Präfix und Commit-Typ verwechselt).
- Für CLAUDE.md: Abschnitt „Befehle“ und „Stolpersteine“ eingetragen. Noch offen: Migrations-Hook mit echter Migration (Modul 9), Actions per SHA pinnen, detekt-Alpha beobachten.
- Gelernt (Nachtrag): **Branch-Name und Commit-Typ sind zwei Ebenen.** Bei Squash-Merge wird der PR-Titel zur Commit-Nachricht auf `main`. Deshalb `feat:` im Titel, auch wenn der Branch `feature/…` heißt. PR #13 hat fälschlich `feature:` im Titel, das lässt sich auf dem geschützten `main` nicht korrigieren. Idee für später: Hook oder CI-Check, der PR-Titel ohne erlaubten Typ ablehnt (commitlint, R14).
- Arbeitsweise bewährt: Pro PR erst Plan-Abschnitt, dann roter Test, dann Code, dann manuelle Prüfung. Du gibst Plan und Merges frei, Hooks und CI fangen die Fehler ab.

### 2026-11-18 – Modul 5 QA & Prod + CD
- Gelernt: **Build once, deploy many.** Das Image wird beim Merge einmal gebaut (Tag = Git-SHA), QA und Prod führen dieselbe Image-ID aus, nur Port, Datenbank, Volume und Zugangsdaten unterscheiden sie. Das Frontend erfährt die Stufe vom Backend (`/api/env`), weil es überall dasselbe Build ist.
- Gelernt: **Freigabe ist Konfiguration.** Im Workflow steht nur `environment: prod`, dass der Job auf dich wartet, bestimmt die Einstellung in GitHub. Der Runner ist ein Fremdzugang zum Mac: Trigger nur Push auf `main` und Tag, Tag muss auf `main` liegen, Actions per SHA gepinnt, kein Dauerdienst.
- **Rollback = Deploy mit alter SHA.** Geübt auf Prod: kaputte Version nach 72 s erkannt (Exit 1, Prod in der Zeit nicht erreichbar), Rollback in 17 s. Grenze: Der Rollback tauscht nur die App, nicht die Datenbank (Migrationen abwärtskompatibel, Modul 9).
- Agent lag falsch oder musste nachbessern: Der `sed`-Befehl in meiner Anleitung nutzte einen relativen Pfad und erzeugte außerhalb des Repos eine leere `qa.env`, der erste QA-Deploy scheiterte. Behoben mit absolutem Pfad und einer Prüfung in `deploy.sh`, die leere oder falsche env-Dateien früh meldet. Außerdem: Das Löschen mit `$S/*.env` blockierte die Sicherheitsprüfung, richtig ist `"${S:?}"/*.env`.
- Für CLAUDE.md: Abschnitt „Deploy“ eingetragen. Anleitungen für Befehle des Nutzers immer mit absoluten Pfaden und einer Kontrolle der Ausgabe (z. B. `wc -c`) schreiben.
