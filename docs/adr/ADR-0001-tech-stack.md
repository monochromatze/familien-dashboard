# ADR-0001: Tech-Stack für das Familien-Dashboard

- **Datum:** 2026-11-16 · **Status:** Akzeptiert

## Kontext
Das Familien-Dashboard ist ein Lernprojekt eines Product Managers. Der Agent (Claude Code) schreibt den Code, der Nutzer liefert die Eingaben (Intent, Spec, Regeln) und prüft das Ergebnis. Randbedingungen:

- Alles läuft lokal auf einem Mac und kostet kein Geld.
- Die Familie nutzt die App im Heim-WLAN (`intent/I-001`).
- Der Stack soll dem ähneln, was der Nutzer bei der Arbeit sieht (Kotlin-Backend, React-Frontend).
- Der Code muss für einen Agenten zuverlässig erzeugbar und per Test, Linter und Architektur-Regeln prüfbar sein.
- Das GitHub-Repo ist öffentlich (kostenlose Branch Protection). Familiendaten liegen nur in der lokalen Datenbank.

## Optionen

### Backend
1. **Kotlin + Spring Boot 3 (Gradle)**: Standard bei Kotlin-Backends in Firmen, passt gut zu DDD, große Auswahl an Bibliotheken (Security, Sessions, Actuator). Nachteil: viel „Magie“ (Annotationen), größerer Startaufwand.
2. **Kotlin + Ktor**: schlanker und expliziter. Nachteil: seltener im Firmenumfeld, für Login und Rollen müsste mehr selbst gebaut werden.

### Frontend
1. **React + TypeScript + Vite (npm)**: am weitesten verbreitet, Agenten erzeugen dafür am zuverlässigsten Code, wird bei der Arbeit genutzt. Nachteil: wenig vorgegebene Struktur, viele Wege zum selben Ziel.
2. **Svelte**: am leichtesten lesbar. Nachteil: kleineres Ökosystem, weniger Praxisbezug zur Arbeit.
3. **Angular**: mehr Struktur und Konventionen. Nachteil: schwergewichtiger, weniger Praxisbezug.

### Datenbank
1. **PostgreSQL 16 + Flyway**: robust, Standard, Migrationen als lesbare SQL-Dateien.
2. SQLite: einfacher, aber weniger realistisch für Mehrbenutzerbetrieb und Betriebsübungen (Backup, Restore).

### Betrieb
1. **Docker Compose (OrbStack) auf dem Mac**: QA und Prod als zwei Stacks, Deploy über einen Self-hosted GitHub Runner. Kostenlos.
2. Cloud (AWS, Azure, GCP): Kreditkarte nötig, Kostenrisiko. Nur als optionales Bonus-Modul B1.

## Entscheidung
Wir wählen:

- **Backend:** Kotlin + Spring Boot 3 mit Gradle (Kotlin DSL)
- **Frontend:** React + TypeScript + Vite, Pakete über npm. React, weil es am verbreitetsten ist, Agenten dafür am zuverlässigsten schreiben und es bei der Arbeit genutzt wird. Svelte und Angular wurden verworfen.
- **Datenbank:** PostgreSQL 16 mit Flyway
- **Betrieb:** Docker Compose über OrbStack, zwei Stacks (`fd-qa`, `fd-prod`), CI/CD mit GitHub Actions und einem Self-hosted Runner
- **Alles lokal und kostenlos.** Keine kostenpflichtigen Dienste.

## Konsequenzen
- **Leichter:** Der Agent hat für beide Seiten reichlich Trainingsmaterial. Der Stack entspricht der Praxis, das Gelernte ist übertragbar.
- **Schwerer:** React gibt wenig Struktur vor, deshalb brauchen wir Coding-Regeln, ESLint und Review (Modul 3 und 7). Spring Boot bringt viel Konfiguration mit, die der Nutzer nicht selbst schreibt, sondern prüfen muss.
- **Sicherheit:** Das Repo ist öffentlich. Self-hosted Runner dürfen Deploy-Jobs nur bei Push auf `main` oder bei Tags ausführen, nie bei PRs von Fremden. Geheimnisse liegen nur in `.env`-Dateien außerhalb von Git.
- **Zu beobachten:** Läuft der Mac nicht, ist die App im WLAN nicht erreichbar (akzeptiert für ein Lernprojekt). Eine spätere Cloud-Variante ist über Bonus-Modul B1 möglich, da dasselbe Docker-Image läuft.
