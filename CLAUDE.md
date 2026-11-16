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
- TDD: zuerst ein fehlschlagender Test, dann der Code. Tests nie abschwächen, um grün zu werden.
- DDD-Schichten: `domain/` ist frei von Spring und JPA. Begriffe aus `docs/glossar.md` verwenden.
- Nie direkt auf `main` pushen. Immer über Branch und PR.
- Coding-Regeln: `docs/coding-regeln.md` (wird in Modul 3 angelegt, Vorlage in `vorlagen/`).

## Befehle
*(werden in Modul 4 ergänzt: Build, Test, Lint, Start)*

## Dinge, die Claude falsch macht
*(ergänzen, sobald derselbe Fehler zweimal passiert)*
