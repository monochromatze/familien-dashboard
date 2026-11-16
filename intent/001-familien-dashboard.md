# Intent 001: Familien-Dashboard

- **Autor:** PM (Nutzer), Entwurf mit Claude · **Datum:** 2026-11-16 · **Status:** Akzeptiert (2026-11-16)
- **Auslöser:** Interview (5 Interviews) + eigene Erfahrung

## Problem
In Familien mit Schulkindern und Teenagern ist oft unklar, **wer welche Aufgabe bis wann macht**. Zuständigkeiten werden nebenbei im Familienchat oder mündlich verteilt, gehen unter oder werden unterschiedlich verstanden. Die Folge: Aufgaben bleiben liegen, es wird mehrfach nachgefragt, und das Nachfragen selbst nervt alle.

Belege aus den Interviews (Details in `docs/interviews.md`):
- Müll: Zwei Eltern sehen die Zuständigkeit verschieden, die Tonne bleibt stehen (I1).
- „Ich hatte gar nicht gesagt, wer backt.“ Spülmaschine dreimal erfragt, dann selbst gemacht und sauer (I2).
- „Bis abends“ ohne Uhrzeit führt zum Streit; die Müllnachricht ging in der stummgeschalteten Gruppe unter (I3).
- Tausch unter Geschwistern bleibt unsichtbar, später heißt es, einer habe nichts gemacht (I4).

Begleitende Probleme aus denselben Interviews, die das Dashboard mit abdecken soll: kurzfristige Termine/Abholzeiten stehen im Chat statt im Kalender (Job 2), Einkaufswünsche ohne Sorte und Menge (Job 3).

> Stichprobe: alle Interviews stammen aus einer Familie.

## Gewünschtes Ergebnis
Jede Aufgabe hat eine **verantwortliche Person und eine Frist**, ist für alle sichtbar und wird **erledigt gemeldet und von einem Elternteil bestätigt**. Niemand muss nachfragen, wer dran ist.

**Erfolgsmaß (Hauptmetrik):** Anteil der **rechtzeitig erledigten Aufgaben** an allen Aufgaben mit Frist. Eine Aufgabe zählt als rechtzeitig, wenn sie bis zur Frist als erledigt gemeldet **und** bestätigt wurde.
**Zielwert nach 3 Monaten Nutzung:** mindestens **80 %**. *(Vorläufig: Es gibt noch keine Basismessung. Der Wert wird nach den ersten 4 Wochen Nutzung überprüft.)*

Die North Star aus dem Kurs („Erledigte Aufgaben pro aktiver Familie und Woche“) bleibt als Begleitmetrik für Aktivität.

## Betroffene Nutzer und Systeme
- **Elternteil:** legt Aufgaben mit Person und Frist an, bestätigt Erledigtes, lädt Mitglieder ein.
- **Kind / Teenager:** sieht eigene Aufgaben, meldet sie als erledigt.
- **Geräte:** Smartphones, ein gemeinsames Tablet (auch für Kinder ohne Handy), Laptop/PC. Zugriff **nur im Heim-WLAN**.
- **Bounded Contexts (Vorarbeit für Modul 6):** Familie & Zugang · Aufgaben · Einkauf (einfach).

## Im Umfang des MVP
1. Familie anlegen, Mitglieder einladen, Rollen (Elternteil, Kind)
2. Aufgaben mit **Person und Frist (Pflicht)**, erledigt melden, Elternteil bestätigt
3. Einkaufsliste (mit Sorte und Menge)
4. Übersicht **„Heute“ und „Diese Woche“** (kein vollständiger Kalender)

## Einschränkungen
- **Datenschutz:** Alles läuft lokal, keine Cloud, keine Weitergabe. Kein Tracking durch Dritte. **Kinderdaten minimal** (Vorname oder Spitzname, kein Geburtsdatum, kein Foto, kein Standort). Eine Familie ist **vollständig löschbar** (und exportierbar).
- **Kosten:** Keine kostenpflichtigen Dienste.
- **Zeit:** 5–8 Stunden pro Woche, ca. 12 Wochen bis zur ersten Version auf Prod.
- **Ausdrücklich NICHT dabei:**
  - vollständiger Kalender
  - Punkte und Belohnungen
  - Rezepte und Essensplanung (Job 5, nach dem MVP, siehe `docs/interviews.md`)
  - Zugriff von außerhalb des Heimnetzes
  - **Fristverschiebung:** Im MVP kann eine Frist nach dem Anlegen nicht verschoben werden. Wie oft Fristen verschoben werden, messen wir erst später (Folgeversion).
  - Rolle **Gast**

## Offene Fragen
- **80 %** ist ein Schätzwert ohne Basismessung. Wie messen wir den heutigen Stand (z. B. vier Wochen Aufgaben notieren)?
- Was passiert mit einer Aufgabe, die **nicht bestätigt** wird, obwohl das Kind sie gemeldet hat? (Zählt sie als rechtzeitig, wenn die Meldung rechtzeitig war?)
- Kinder ohne Smartphone: Reicht ein Tablet im Heimnetz, oder braucht es einen PIN-Login pro Kind?

## Entschieden (2026-11-16)
- Keine Fristverschiebung im MVP, Messung später.
- Rolle Gast entfällt im MVP.
