# Betrieb: QA und Prod auf dem Mac

Stand: Plan `P-002`. Dieses Dokument wächst mit den PRs (Prod und Rollback kommen in PR D und E).

## Überblick
```
PR ─► CI auf GitHub-Runnern (Lint, Tests, Build)
merge auf main ─► Self-hosted Runner (dein Mac): Image bauen, Tag = Git-SHA ─► Stack fd-qa (Port 8081) ─► Smoke-Test
```
- Zwei Stacks aus **derselben** `deploy/compose.yml`: `fd-qa` (8081, gelbes Banner) und `fd-prod` (8080).
- Jede Stufe hat eigene Datenbank, eigenes Volume und eigene Zugangsdaten.
- Das Image heißt `familien-dashboard:<git-sha>` und wird nur einmal gebaut.

## Zugangsdaten (außerhalb von Git)
Die echten env-Dateien liegen in `~/familien-dashboard-secrets/`, nicht im Repo. Der Runner checkt den Code in einen frischen Ordner aus, dort gäbe es sie sonst nicht.

```bash
mkdir -p ~/familien-dashboard-secrets && chmod 700 ~/familien-dashboard-secrets
sed "s/bitte-aendern/$(openssl rand -hex 16)/" deploy/qa.env.example > ~/familien-dashboard-secrets/qa.env
chmod 600 ~/familien-dashboard-secrets/qa.env
```
Für Prod genauso mit `prod.env` (ein anderes Passwort als in QA).

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
