#!/usr/bin/env bash
# Rollt ein vorhandenes Image auf eine Stufe aus und prüft es danach per Smoke-Test.
#
# Aufruf (vom Hauptordner des Repos): scripts/deploy.sh <qa|prod> <tag>
#   <tag> ist die Git-SHA, mit der das Image gebaut wurde (Image: familien-dashboard:<tag>).
#   Rollback = derselbe Befehl mit dem Tag der vorherigen Version.
# Das Skript baut nie ein Image und löscht nie Daten (kein "down -v").
# Mit ENV_FILE=<pfad> lässt sich eine andere env-Datei nehmen (für Tests).
set -euo pipefail

stage="${1:-}"
tag="${2:-}"
case "$stage" in
  qa | prod) ;;
  *)
    echo "Aufruf: $0 <qa|prod> <tag>" >&2
    exit 2
    ;;
esac
if [[ -z "$tag" ]]; then
  echo "Aufruf: $0 <qa|prod> <tag>" >&2
  exit 2
fi

env_file="${ENV_FILE:-deploy/${stage}.env}"
if [[ ! -f "$env_file" ]]; then
  echo "FEHLER: ${env_file} fehlt. Vorlage: deploy/${stage}.env.example" >&2
  exit 2
fi

# Eine leere oder unvollständige env-Datei früh und verständlich melden, nicht erst tief in Compose.
for var in APP_ENV APP_PORT POSTGRES_DB POSTGRES_USER POSTGRES_PASSWORD; do
  if ! grep -Eq "^${var}=.+" "$env_file"; then
    echo "FEHLER: ${var} fehlt oder ist leer in ${env_file}. Vorlage: deploy/${stage}.env.example" >&2
    exit 2
  fi
done
# Schutz vor Verwechslung: Die Datei der Stufe muss auch zu dieser Stufe gehören.
if ! grep -Eq "^APP_ENV=${stage}$" "$env_file"; then
  echo "FEHLER: ${env_file} gehört nicht zur Stufe ${stage} (APP_ENV passt nicht)." >&2
  exit 2
fi

image="familien-dashboard:${tag}"
if ! docker image inspect "$image" >/dev/null 2>&1; then
  echo "FEHLER: Image ${image} gibt es nicht. Erst bauen, dieses Skript baut nicht." >&2
  exit 2
fi

compose=(docker compose -p "fd-${stage}" --env-file "$env_file" -f deploy/compose.yml)
export APP_TAG="$tag"

echo "Rolle ${image} auf ${stage} aus ..."
# --no-build: genau das vorhandene Image benutzen. --wait: warten, bis die Healthchecks grün sind.
if ! "${compose[@]}" up -d --no-build --wait; then
  echo "DEPLOY FEHLGESCHLAGEN (${stage}): Stack wurde nicht gesund. Letzte Logs:" >&2
  "${compose[@]}" logs --no-color --tail 30 >&2 || true
  exit 1
fi

ENV_FILE="$env_file" "$(dirname "$0")/smoke-test.sh" "$stage"
echo "Deploy ok: ${stage} läuft mit ${image}"
