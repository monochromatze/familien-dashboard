#!/usr/bin/env bash
# Stop-Hook: "Erst fertig, wenn die schnellen Unit-Tests grün sind."
# Läuft, wenn der Agent fertig melden will. Exit 2 = er muss weitermachen.
# Nur schnelle Tests (Sekunden). Die volle Suite läuft in CI.

set -uo pipefail

input=$(cat)

# Schutz vor Endlosschleife: Wenn der Stop-Hook schon einmal zurückgeschickt hat,
# nicht nochmals blockieren (sonst käme der Agent nie zum Ende).
if [ "$(echo "$input" | jq -r '.stop_hook_active // false')" = "true" ]; then
  exit 0
fi

root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
failed=""

if [ -x "$root/backend/gradlew" ]; then
  out=$(cd "$root/backend" && ./gradlew --quiet test 2>&1) || failed+="Backend-Tests rot:\n$out\n"
fi

if [ -d "$root/frontend/node_modules" ]; then
  out=$(cd "$root/frontend" && npm test --silent -- --run 2>&1) || failed+="Frontend-Tests rot:\n$out\n"
fi

if [ -n "$failed" ]; then
  printf "BLOCKIERT: Nicht fertig, solange Tests rot sind (Tests nie abschwächen!).\n$failed" >&2
  exit 2
fi

exit 0
