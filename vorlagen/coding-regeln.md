# Coding-Regeln

> Kurz und prüfbar. Jede Regel sagt, **wer** sie prüft. Ziel: möglichst viele Regeln in „automatisch“.

| # | Regel | Geprüft durch |
|---|---|---|
| R1 | Code-Format ist einheitlich | automatisch: ktlint, Prettier (Hook + CI) |
| R2 | Keine Warnungen von detekt / ESLint | automatisch: Linter (Hook + CI) |
| R3 | `domain/` importiert nichts aus Spring, JPA, `infrastructure/`, `web/` | automatisch: ArchUnit-Test |
| R4 | Fachliche Regeln stehen in Aggregaten, nicht in Controllern | ArchUnit (teilweise) + Review |
| R5 | Begriffe im Code = Begriffe im Glossar (`docs/glossar.md`) | Review / Skill |
| R6 | Jeder Endpoint hat eine Berechtigung (`@PreAuthorize`) | automatisch: ArchUnit-Test |
| R7 | Jede fachliche Abfrage filtert nach `familie_id` aus der Session | Test (Mandantentrennung) + Review |
| R8 | Neuer Code entsteht testgetrieben: zuerst ein fehlschlagender Test | Review (Commit-Reihenfolge) |
| R9 | Angewendete Migrationen werden nie geändert | automatisch: PreToolUse-Hook + Flyway-Validierung |
| R10 | Keine Secrets im Repo | automatisch: PreToolUse-Hook + GitHub Secret Scanning |
| R11 | TypeScript im `strict`-Modus, kein `any` | automatisch: tsconfig + ESLint |
| R12 | Kotlin: kein `!!` | automatisch: detekt |
| R13 | Keine personenbezogenen Daten in Logs oder Analytics | Review + Skill |
| R14 | Commit-Nachrichten nach Conventional Commits | automatisch: commitlint (optional) |
