# Review-Checkliste für Pull Requests (für PMs, die Code beurteilen, aber nicht schreiben)

> Ziel: In etwa 10 Minuten entscheiden, ob ein PR **gut**, **fragwürdig** oder **schlecht** ist.
> Ergänze diese Liste, wann immer du etwas Neues gelernt hast.

## 1. Automatik zuerst (wenn das rot ist, gar nicht weiter lesen)
- [ ] CI ist grün (`gh pr checks`)
- [ ] Linter / Formatter ohne neue Warnungen
- [ ] Architektur-Tests (ArchUnit) sind grün

## 2. Passt der PR zum Auftrag?
- [ ] Er verweist auf ein Issue bzw. auf spec/plan
- [ ] Es wurde nur das gebaut, was im Plan steht (nichts Zusätzliches „nebenbei“)
- [ ] Bei Abweichungen wurde `plans/NNN.md` im selben PR angepasst
- [ ] Größe: unter etwa 400 geänderten Zeilen. Sonst aufteilen lassen.

## 3. Tests lesen (das Wichtigste)
- [ ] Jedes Akzeptanzkriterium aus der Spec hat einen Test
- [ ] Die Testnamen lesen sich wie fachliche Sätze
- [ ] Es gibt Tests für **Fehlerfälle** und **verbotene Aktionen** (403/404), nicht nur für den Erfolgsfall
- [ ] Bestehende Tests wurden **nicht** ohne Begründung geändert, abgeschwächt oder gelöscht

## 4. Warnzeichen im Diff
- [ ] Regeln werden ausgehebelt: `@Suppress`, `eslint-disable`, `any`, `!!`, `@Disabled`, `.skip`
- [ ] Fachlogik steckt im Controller oder in einer React-Komponente statt in `domain/`
- [ ] Imports von Spring oder JPA in `domain/`
- [ ] Datenbankabfrage ohne `familie_id`, oder `familie_id` wird aus URL oder Request-Body übernommen
- [ ] Neue Endpoints ohne `@PreAuthorize`
- [ ] Copy-Paste-Blöcke, die fast identisch sind
- [ ] Neue Abhängigkeiten (`build.gradle.kts`, `package.json`), die niemand bestellt hat
- [ ] Secrets, Passwörter oder Tokens im Code
- [ ] Geänderte, bereits angewendete Flyway-Migration
- [ ] Personenbezogene Daten in Logs oder Analytics-Events
- [ ] Auskommentierter Code, `TODO` ohne Issue

## 5. Fragen an den Agenten (frischer Kontext / Reviewer-Subagent)
1. „Erkläre diesen PR in 5 Sätzen für einen PM.“
2. „Welche Akzeptanzkriterien sind nicht durch Tests abgedeckt?“
3. „Was ist die riskanteste Änderung, und was könnte in Prod schiefgehen?“
4. „Welche Alternative hast du verworfen und warum?“
5. „Verletzt etwas `docs/coding-regeln.md`?“

## Urteil
- **Gut:** Abschnitte 1–3 sind komplett erfüllt, in Abschnitt 4 gibt es nichts → mergen
- **Fragwürdig:** Ein Punkt aus 4 ist offen, oder Fragen aus 5 werden ausweichend beantwortet → zurück an den Agenten, mit konkretem Punkt
- **Schlecht:** Tests fehlen oder wurden abgeschwächt, oder die Mandantentrennung fehlt → ablehnen, Plan überarbeiten
