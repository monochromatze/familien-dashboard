# Event Storming (Modul 6)

> Arbeitsdatei. Hier sammelst du die Ereignisse, danach werden daraus Glossar (`docs/glossar.md`) und Kontext-Karte (`docs/kontext-karte.md`).

## Was ist ein Ereignis?

Ein Ereignis ist etwas, das **in der Familie fachlich passiert ist und das jemanden interessiert**. Es steht in der **Vergangenheitsform**, denn es ist schon geschehen und nicht mehr änderbar.

Drei Prüffragen. Ein Ereignis erfüllt alle drei:

1. **Ist es schon passiert?** („Aufgabe wurde angelegt", nicht „Aufgabe anlegen")
2. **Würde ein Familienmitglied es so sagen?** Also kein Technikwort („Datensatz gespeichert") und kein Bildschirm („Button geklickt").
3. **Ändert es etwas oder löst es etwas aus?** („Frist ist abgelaufen" löst z. B. eine Erinnerung oder einen Zähler aus.)

| Ereignis ✅ | Kein Ereignis ❌ | Warum nicht |
|---|---|---|
| Aufgabe wurde als erledigt gemeldet | Aufgabe erledigen | Gegenwart, das ist eine Handlung (Command) |
| Frist ist abgelaufen | Aufgabenliste anzeigen | Anzeigen verändert nichts |
| Einladung wurde angenommen | Eintrag in Tabelle `einladung` | Technik, kein Familienwort |
| Aufgabe wurde nicht bestätigt | Elternteil | Das ist ein Akteur, kein Ereignis |

## Meine Ereignisse (Vergangenheitsform, ein Ereignis pro Zeile, grob in zeitlicher Reihenfolge)

Nicht sortieren und nicht bewerten. Auch Unschönes und Ausnahmen aufschreiben.

- Familie wurde erstellt
- Familienmitglieder wurden hinzugefügt (Eltern, Kinder,...)
- Einladung wurde versendet
- Einladung wurde angenommen
- Einladung wurde nicht angenomme
- Aufgabe wurde erstellt (wer macht was bis wann)
- Aufgabe wurde versehentlich angelegt
- Aufgabe enthält falsche Angaben (oder man hat sich einfach verschrieben)
- Aufgabe wurde bewusst ohne Termin angelegt
- Aufgabe wurde als erledigt gemeldet
- Aufgabe wurde als erledigt bestätigt
- Aufgabe ist überfällig
- Aufgabe wurde erledigt aber nicht bestätigt
- Produkt wurde auf die Einkaufsliste gesetzt (wieviel und welche sorte bzw. welcher hersteller)
- Produkt wurde einem Laden zugeordnet (Aldi, Edeka, Marktkauf)
- Laden wurde angelegt
- Einkaufsliste wurde angelegt
- Einkaufsliste wurde eine Info hinzugefügt wann man einkaufen geht und optional auch welcher Laden
- Abfrage an die Familie ob noch etwas eingekauft werden muss wurde an die Familie gesendet
- Einkauf wurde abgeschlossen
- Nicht gekaufte Produkte wurden nicht gelöscht

## Bereinigte Ereignisse für den MVP (Stand 2026-11-19)

Entscheidungen, die hier eingeflossen sind: Mitglieder werden angelegt und sind sofort nutzbar, die Einladung folgt danach · Aufgaben sind bearbeitbar, die Frist nicht · Aufgaben nur mit Termin · nicht bestätigt = nicht erledigt · Einkauf ist nicht im MVP. Die Gründe stehen im Intent I-001 (Abschnitt „Entschieden").

**Bereich Familie & Zugang**
1. Familie wurde erstellt
2. Mitglied wurde angelegt (mit Rolle Elternteil oder Kind)
3. Einladung wurde versendet
4. Einladung wurde angenommen
5. Einladung ist abgelaufen *(statt „nicht angenommen": Etwas, das nicht passiert, ist kein Ereignis)*

**Bereich Aufgaben**
6. Aufgabe wurde erstellt (Person, Beschreibung, Frist)
7. Aufgabe wurde korrigiert (Person oder Beschreibung, nie die Frist) *(ersetzt „versehentlich angelegt" und „falsche Angaben")*
8. Aufgabe wurde zurückgezogen
9. Aufgabe wurde als erledigt gemeldet
10. Aufgabe wurde bestätigt
11. Aufgabe ist überfällig *(zeitgesteuert: Die Frist ist verstrichen, ohne dass bestätigt wurde)*

**Kein Ereignis, sondern ein Zustand:** „erledigt, aber nicht bestätigt". Das ist die Lücke zwischen 9 und 10. Offene Frage: Issue #27.

**Nicht im MVP:** alle Ereignisse zum Einkauf (Issue #6) und „Aufgabe ohne Termin" (Issue #26).

## Commands: Wer löst welches Ereignis aus?

Ein Command ist die Handlung davor, in der Gegenwart. Sie kann scheitern, wenn eine Regel verletzt wird. Ein Ereignis ist das Ergebnis, wenn sie gelingt.

| Command | Akteur | Ergebnis (Ereignis) |
|---|---|---|
| Familie erstellen (auf dem Startscreen) | erste Person, wird automatisch Elternteil | 1 |
| Mitglied anlegen | Elternteil | 2 |
| Einladung versenden | Elternteil | 3 |
| Einladung annehmen | eingeladene Person | 4 |
| *(Zeit vergeht, 7 Tage)* | System | 5 |
| Aufgabe erstellen | Elternteil (für alle) oder Kind (nur für sich und für Elternteile) | 6 |
| Aufgabe korrigieren (nur vor der Meldung) | Person, die die Aufgabe angelegt hat | 7 |
| Aufgabe zurückziehen (nur vor der Meldung) | Person, die die Aufgabe angelegt hat | 8 |
| Aufgabe als erledigt melden | zugewiesene Person | 9 |
| Aufgabe bestätigen | Elternteil | 10 |
| *(Zeit vergeht, Frist verstreicht)* | System | 11 |

Mit ❓ markierte Stellen sind Annahmen von mir und müssen von dir bestätigt oder geändert werden.

## Regeln (Invarianten)

Eine Regel sagt, was **immer** gelten muss. Hier entscheidet sich später, welche Teile des Codes echte Aggregate brauchen. Spalte „Herkunft": *Intent* = steht im Intent, *Entschieden* = von dir in dieser Sitzung, *Annahme* = von mir ergänzt, bitte prüfen.

| # | Regel | Herkunft |
|---|---|---|
| A1 | Jede Aufgabe hat genau eine verantwortliche Person und eine Frist. | Intent |
| A2 | Die Frist einer Aufgabe ändert sich nach dem Anlegen nie. | Entschieden |
| A3 | Nur die zugewiesene Person meldet eine Aufgabe als erledigt. | Annahme |
| A4 | Nur ein Elternteil bestätigt, und nur eine als erledigt gemeldete Aufgabe. *(Für den MVP so. Wer bei Aufgaben für ein Elternteil bestätigt, ist offen: Issue #28.)* | Intent, Entschieden |
| A5 | Nicht bestätigt gilt als nicht erledigt. | Entschieden |
| A6 | Zurückgezogene Aufgaben zählen in keiner Auswertung. | Annahme |
| A7 | Sobald eine Aufgabe als erledigt gemeldet ist, ist sie nicht mehr bearbeitbar und nicht mehr zurückziehbar. Eine Meldung lässt sich nicht zurücknehmen. | Entschieden |
| A8 | Ein Kind legt Aufgaben nur für sich selbst und für Elternteile an. | Entschieden |
| A9 | Elternteile sehen alle Aufgaben der Familie (eigene, die der Kinder, die der Eltern). Kinder sehen nur ihre eigenen Aufgaben, und die, die sie selbst angelegt haben. ❓ | Entschieden + Annahme |
| A10 | Nur wer eine Aufgabe angelegt hat, darf sie vor der Meldung korrigieren oder zurückziehen. | Entschieden |
| F1 | Eine Familie hat immer mindestens ein Elternteil. Wer die Familie anlegt, ist ein Elternteil. | Entschieden |
| F2 | Eine Einladung ist 7 Tage gültig und nur einmal verwendbar. | Entschieden |

## Geklärt am 2026-11-19

- Wer die Familie anlegt, ist ein Elternteil. Das Anlegen geschieht auf dem Startscreen.
- Kinder dürfen Aufgaben für sich und für Elternteile anlegen.
- Im MVP sieht jede Person nur ihre eigenen Aufgaben. Tauschen unter Familienmitgliedern ist eine spätere Idee (Issue #29).
- Nach der Meldung ist eine Aufgabe nicht mehr bearbeitbar.
- Korrigieren und Zurückziehen darf, wer die Aufgabe angelegt hat (vor der Meldung).
- Elternteile sehen alle Aufgaben der Familie, Kinder nur ihre eigenen.
- Eine versehentliche Meldung lässt sich im MVP nicht zurücknehmen. Das Elternteil bestätigt in dem Fall einfach nicht.
- Idee für später: Ein Kind weist eine Aufgabe „den Eltern" zu, ohne eine bestimmte Person zu wählen (Issue #30).

## Offene Fragen

1. **Aufgabe für ein Elternteil: Wer bestätigt?** Issue #28.
2. **Gemeldet, aber nie bestätigt:** Issue #27.
3. **Aufgaben, die ein Kind für ein Elternteil angelegt hat (A9):** Das Kind darf sie korrigieren und zurückziehen (A10), also muss es sie auch sehen können. Ist das so gewollt? Ich habe es als Annahme eingetragen.

## Beobachtungen aus Gesprächen mit der Familie

Aussagen wie „Das bestätige ich nie sofort" sind oft verborgene Regeln. Wörtlich notieren.

-
