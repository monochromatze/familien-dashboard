# Betrieb: QA und Prod auf dem Mac

Stand: Plan `P-002`, alle Teile umgesetzt.

## Überblick
```
PR ─► CI auf GitHub-Runnern (Lint, Tests, Build)
merge auf main ─► Self-hosted Runner (dein Mac): Image bauen, Tag = Git-SHA ─► Stack fd-qa (Port 8081) ─► Smoke-Test
Tag v* ─► Freigabe durch dich (Environment "prod") ─► DASSELBE Image ─► Stack fd-prod (Port 8080) ─► Smoke-Test
```
- Zwei Stacks aus **derselben** `deploy/compose.yml`: `fd-qa` (8081, gelbes Banner) und `fd-prod` (8080).
- Jede Stufe hat eigene Datenbank, eigenes Volume und eigene Zugangsdaten.
- Das Image heißt `familien-dashboard:<git-sha>` und wird nur einmal gebaut.

## Zugangsdaten (außerhalb von Git)
Die echten env-Dateien liegen in `~/familien-dashboard-secrets/`, nicht im Repo. Der Runner checkt den Code in einen frischen Ordner aus, dort gäbe es sie sonst nicht.

```bash
mkdir -p ~/familien-dashboard-secrets && chmod 700 ~/familien-dashboard-secrets
sed "s/bitte-aendern/$(openssl rand -hex 16)/" /Users/matzehome/Documents/WebAppTraining/deploy/qa.env.example > ~/familien-dashboard-secrets/qa.env
chmod 600 ~/familien-dashboard-secrets/qa.env
wc -c ~/familien-dashboard-secrets/qa.env   # muss rund 450 Bytes zeigen, nicht 0
```
Der Pfad zur Vorlage ist absolut, damit der Befehl von überall läuft. Mit einem relativen Pfad außerhalb des Repos entsteht sonst eine leere Datei.
Für Prod genauso, mit einem anderen Passwort als in QA:
```bash
sed "s/bitte-aendern/$(openssl rand -hex 16)/" /Users/matzehome/Documents/WebAppTraining/deploy/prod.env.example > ~/familien-dashboard-secrets/prod.env && chmod 600 ~/familien-dashboard-secrets/prod.env && wc -c ~/familien-dashboard-secrets/prod.env
```

## Self-hosted Runner einrichten (einmalig)
Der Runner ist ein Programm auf deinem Mac, das Aufträge von GitHub abholt. **Sicherheit:** Das Repo ist öffentlich. Deshalb startet nur `deploy-qa.yml` (Push auf `main`) und später `deploy-prod.yml` (Tag `v*`) auf dem Runner, nie ein PR. Der Runner läuft unter deinem normalen Benutzer, nicht als Admin, und wird von Hand in einem Terminal gestartet (`./run.sh`), nicht als Dauerdienst. Ist er aus, warten Deploys in der Warteschlange.

1. Ordner anlegen und Runner herunterladen (Version 2.338.2, macOS Apple Silicon):
   ```bash
   mkdir -p ~/actions-runner && cd ~/actions-runner
   curl -o actions-runner-osx-arm64-2.338.2.tar.gz -L https://github.com/actions/runner/releases/download/v2.338.2/actions-runner-osx-arm64-2.338.2.tar.gz
   ```
2. Prüfsumme kontrollieren (muss `OK` ausgeben):
   ```bash
   echo "25a065af409ffa317ab4d5a3b614dd15fc9c40f2172bc1b35a1b1258e919a388  actions-runner-osx-arm64-2.338.2.tar.gz" | shasum -a 256 -c
   ```
3. Entpacken:
   ```bash
   tar xzf ./actions-runner-osx-arm64-2.338.2.tar.gz
   ```
4. Einmaliges Registrierungs-Token holen (gilt rund eine Stunde, danach wertlos) und den Runner konfigurieren:
   ```bash
   ./config.sh --url https://github.com/monochromatze/familien-dashboard --token "$(gh api -X POST repos/monochromatze/familien-dashboard/actions/runners/registration-token --jq .token)" --name mac-runner --labels macos --unattended
   ```
5. Starten (das Terminal muss offen bleiben, Abbruch mit `Ctrl+C`):
   ```bash
   ./run.sh
   ```
6. In GitHub unter *Settings → Actions → Runners* steht `mac-runner` als „Idle“.

Entfernen: `./config.sh remove --token "$(gh api -X POST repos/monochromatze/familien-dashboard/actions/runners/remove-token --jq .token)"`.

## Fork-PRs nur nach Freigabe
Einmalig, damit PRs von Fremden nie ohne deine Freigabe Workflows starten:
```bash
gh api -X PUT repos/monochromatze/familien-dashboard/actions/permissions/fork-pr-contributor-approval -f approval_policy=all_external_contributors
```

## Prod freigeben und ausrollen
**Einmalig: Environment `prod` mit dir als Pflicht-Reviewer** (ohne diese Sperre würde Prod ohne Klick ausgerollt). `prevent_self_review` bleibt aus, du bist die einzige Person und musst dich selbst freigeben können:
```bash
gh api -X PUT repos/monochromatze/familien-dashboard/environments/prod -F "reviewers[][type]=User" -F "reviewers[][id]=$(gh api user --jq .id)"
```
**Ein Release auslösen** (Tag legst du selbst, der Tag muss auf einen Commit auf `main` zeigen):
```bash
git checkout main && git pull
git tag v0.1.0
git push origin v0.1.0
```
Dann in GitHub unter *Actions → Deploy Prod* auf **Review deployments → prod → Approve** klicken. Danach rollt der Mac das Image mit der SHA des Tags aus. Das Image muss existieren, weil der QA-Deploy es beim Merge gebaut hat. Fehlt es (Runner war aus), bricht der Deploy mit einer klaren Meldung ab.

## Rollback
Jedes Image bleibt unter seiner Git-SHA im Docker liegen. Ein Rollback ist deshalb derselbe Befehl wie ein Deploy, nur mit der SHA der vorherigen Version. Er baut nichts, und die Datenbank bleibt unberührt.

1. Die SHA der letzten guten Version ermitteln (Tag, wenn es ein Release war):
   ```bash
   git rev-parse v0.1.0
   ```
   Alle vorhandenen Images zeigt `docker images familien-dashboard`.
2. Auf Prod zurückgehen (die SHA aus Schritt 1 einsetzen):
   ```bash
   ENV_FILE=~/familien-dashboard-secrets/prod.env scripts/deploy.sh prod <sha>
   ```
3. Prüfen: `curl localhost:8080/api/health` meldet `{"status":"ok","db":"ok"}`.

**Geübt am 2026-11-18** (Prod mit einem Image, dessen App nie gesund wird): `deploy.sh` brach nach **72 Sekunden** mit Exit 1 ab (Prod war in dieser Zeit nicht erreichbar). Der Rollback auf `v0.1.0` brauchte **17 Sekunden**. Gesamte Ausfallzeit rund anderthalb Minuten. Der Healthcheck-Zeitraum (`start_period` 30 s plus Wiederholungen) bestimmt, wie schnell ein kaputter Deploy auffällt.

**Grenze:** Der Rollback tauscht nur die App. Hat die neue Version die Datenbank verändert (Flyway-Migration ab Modul 9), läuft die alte App womöglich nicht mehr auf dem neuen Schema. Migrationen müssen deshalb abwärtskompatibel sein (erst ergänzen, später entfernen). Das üben wir in Modul 9.

## Deploy von Hand (z. B. Fehlersuche)
Image bauen und ausrollen, wie es der Workflow tut:
```bash
docker build --tag "familien-dashboard:$(git rev-parse HEAD)" .
ENV_FILE=~/familien-dashboard-secrets/qa.env scripts/deploy.sh qa "$(git rev-parse HEAD)"
```
`deploy.sh` baut nie selbst und löscht nie Daten. Rollback ist derselbe Befehl mit der SHA der vorherigen Version.

## Stolpersteine
- **Port 8080 doppelt belegt:** Der Dev-Stack und Prod nutzen beide 8080. Den Dev-Stack vor dem Prod-Test stoppen.
- **Mac schläft:** Dann nimmt der Runner keine Aufträge an. Sie laufen, sobald er wieder wach ist.
- **`docker` nicht gefunden im Workflow:** Der Runner hat einen schlanken `PATH`. Die Workflows ergänzen ihn um die OrbStack-Pfade.
