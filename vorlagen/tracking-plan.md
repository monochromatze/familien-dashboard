# Tracking-Plan

> Grundregel: erst die Frage, dann das Event. Keine personenbezogenen Daten (Namen, E-Mails, Freitext) in den Properties.

## Fragen, die wir beantworten wollen
| # | Frage | Metrik | Entscheidung, die davon abhängt |
|---|---|---|---|
| F1 | Nutzen Familien das Produkt regelmäßig? | North Star: bestätigte Aufgaben / aktive Familie / Woche | Weiterbauen oder pivotieren |
| F2 | Kommen neue Familien in Gang? | Aktivierungsrate (7 Tage) | Onboarding verbessern |

## Events
| Event | Auslöser (Domain Event) | Properties | Beantwortet |
|---|---|---|---|
| `familie_angelegt` | FamilieAngelegt | familie_id, stage | F2 |
| `mitglied_beigetreten` | EinladungAngenommen | familie_id, rolle | F2 |
| `aufgabe_erledigt_gemeldet` | AufgabeErledigtGemeldet | familie_id, rolle, punkte | F1 |
| `aufgabe_bestaetigt` | AufgabeBestätigt | familie_id, punkte, dauer_bis_bestaetigung_h | F1 |

## Metrik-Definitionen (eindeutig!)
- **Aktive Familie:** mindestens 1 Event `aufgabe_*` in den letzten 7 Tagen
- **Aktiviert:** mindestens 2 Mitglieder UND mindestens 1 `aufgabe_bestaetigt` innerhalb von 7 Tagen nach `familie_angelegt`
