# Coding-Regeln

> Kurz und prüfbar. Jede Regel sagt, **wer** sie prüft. Ziel: möglichst viele Regeln in „automatisch“.

| # | Regel | Geprüft durch |
|---|---|---|
| R1 | Code-Format ist einheitlich | automatisch: ktlint, Prettier (PostToolUse-Hook + CI, aktiv seit Modul 4) |
| R2 | Keine Warnungen von detekt / ESLint | automatisch: Linter (Hook + CI) |
| R3 | `domain/` importiert nichts aus Spring, JPA, `infrastructure/`, `web/` | automatisch: ArchUnit-Test |
| R4 | Fachliche Regeln stehen in `domain/` (bei echten Invarianten als Aggregat oder Value Object), nie in Controllern oder React-Komponenten. Kein Muster ohne Regel, die es schützt | ArchUnit (teilweise) + Review |
| R5 | Begriffe im Code = Begriffe im Glossar (`docs/glossar.md`) | Review / Skill |
| R6 | Jeder Endpoint hat eine Berechtigung (`@PreAuthorize`) | automatisch: ArchUnit-Test |
| R7 | Jede fachliche Abfrage filtert nach `familie_id` aus der Session | Test (Mandantentrennung) + Review |
| R8 | Neuer Code entsteht testgetrieben: zuerst ein fehlschlagender Test | Review (Commit-Reihenfolge) |
| R9 | Angewendete Migrationen werden nie geändert | automatisch: PreToolUse-Hook (aktiv) + Flyway-Validierung (ab Modul 9) |
| R10 | Keine Secrets im Repo | automatisch: PreToolUse-Hook (aktiv) + `.gitignore` + GitHub Secret Scanning |
| R11 | TypeScript im `strict`-Modus, kein `any` | automatisch: tsconfig + ESLint |
| R12 | Kotlin: kein `!!` | automatisch: detekt |
| R13 | Keine personenbezogenen Daten in Logs oder Analytics | Review + Skill |
| R14 | Commit-Nachrichten nach Conventional Commits | automatisch: commitlint (optional) |
| R15 | Nie direkt auf `main` pushen | automatisch: PreToolUse-Hook (aktiv) + Branch Protection |
| R16 | Agent meldet „fertig“ erst bei grünen schnellen Tests | automatisch: Stop-Hook (aktiv seit Modul 4, nur schnelle Tests) |

## Status der Durchsetzung
- **Aktiv (Hooks):** R9, R10, R15 (PreToolUse), R1, R2, R11, R12 (PostToolUse), R16 (Stop). Alle mindestens einmal live ausgelöst (Modul 3 und 4).
- **Aktiv (CI):** R1, R2, R11, R12 über `.github/workflows/ci.yml` (ktlint, detekt, ESLint, Prettier, `tsc`, Tests).
- **Aktiv (CI, Skripte):** shellcheck für `scripts/*.sh` (Job „Skripte“ in `ci.yml`).
- **Hinweis zu R12:** Nur detekt mit Typauflösung findet `!!`. Der Task `detekt` hängt deshalb an `detektMain` und `detektTest`.
- **Noch offen:** R9 gegen echte Migrationen (Hook per Handtest geprüft, Flyway-Validierung ab Modul 9), R14 (commitlint optional).
- **Kommt mit Modul 6/7:** ArchUnit-Tests (R3, R4, R6).
