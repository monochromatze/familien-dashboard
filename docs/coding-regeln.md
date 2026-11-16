# Coding-Regeln

> Kurz und prüfbar. Jede Regel sagt, **wer** sie prüft. Ziel: möglichst viele Regeln in „automatisch“.

| # | Regel | Geprüft durch |
|---|---|---|
| R1 | Code-Format ist einheitlich | automatisch: ktlint, Prettier (PostToolUse-Hook vorbereitet, aktiv ab Modul 4, CI ab Modul 4) |
| R2 | Keine Warnungen von detekt / ESLint | automatisch: Linter (Hook + CI) |
| R3 | `domain/` importiert nichts aus Spring, JPA, `infrastructure/`, `web/` | automatisch: ArchUnit-Test |
| R4 | Fachliche Regeln stehen in Aggregaten, nicht in Controllern | ArchUnit (teilweise) + Review |
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
| R16 | Agent meldet „fertig“ erst bei grünen schnellen Tests | automatisch: Stop-Hook (vorbereitet, aktiv ab Modul 4) |

## Status der Durchsetzung
- **Aktiv:** R9, R10, R15 (Hooks). Es gibt noch keinen Code, daher greifen R1, R2, R11, R12, R16 erst ab Modul 4.
- **Kommt mit Modul 4 (Walking Skeleton):** Linter-Konfiguration (ktlint, detekt, Prettier, ESLint) und CI.
- **Kommt mit Modul 6/7:** ArchUnit-Tests (R3, R4, R6).
