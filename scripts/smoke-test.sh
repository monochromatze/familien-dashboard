#!/usr/bin/env bash
# Rauchtest für eine laufende Stufe: Antwortet die App, ist die Datenbank erreichbar,
# und ist es wirklich diese Stufe?
#
# Aufruf (vom Hauptordner des Repos): scripts/smoke-test.sh <qa|prod>
# Der Port kommt aus deploy/<stufe>.env (APP_PORT). Mit ENV_FILE=<pfad> lässt sich eine andere Datei nehmen.
set -euo pipefail

stage="${1:-}"
case "$stage" in
  qa | prod) ;;
  *)
    echo "Aufruf: $0 <qa|prod>" >&2
    exit 2
    ;;
esac

env_file="${ENV_FILE:-deploy/${stage}.env}"
if [[ ! -f "$env_file" ]]; then
  echo "FEHLER: ${env_file} fehlt. Vorlage: deploy/${stage}.env.example" >&2
  exit 2
fi

# Wert einer Zeile "NAME=wert" lesen, ohne die Datei als Skript auszuführen.
env_value() {
  grep -E "^$1=" "$env_file" | tail -n 1 | cut -d= -f2-
}

port="$(env_value APP_PORT)"
base="http://127.0.0.1:${port:-8080}"

fail() {
  echo "SMOKE-TEST FEHLGESCHLAGEN (${stage}): $1" >&2
  exit 1
}

# Die App braucht nach dem Start einen Moment: bis zu 30 Sekunden erneut versuchen.
health=""
for _ in $(seq 1 15); do
  if health="$(curl --silent --show-error --fail --max-time 3 "${base}/api/health" 2>/dev/null)"; then
    break
  fi
  health=""
  sleep 2
done
[[ -n "$health" ]] || fail "${base}/api/health antwortet nicht mit 200"
grep -q '"db":"ok"' <<<"$health" || fail "Datenbank nicht ok: ${health}"

env_json="$(curl --silent --show-error --fail --max-time 3 "${base}/api/env")" || fail "${base}/api/env antwortet nicht"
grep -q "\"env\":\"${stage}\"" <<<"$env_json" || fail "Falsche Stufe, erwartet ${stage}, bekommen: ${env_json}"

curl --silent --show-error --fail --max-time 3 "${base}/" | grep -q "<title>Familien-Dashboard</title>" ||
  fail "Startseite fehlt oder hat den falschen Titel"

echo "Smoke-Test ok (${stage}, ${base})"
