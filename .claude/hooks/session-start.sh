#!/usr/bin/env bash
# SessionStart-Hook: zeigt dem Agenten beim Start den Abschnitt "Aktueller Stand".
# Alles, was dieses Skript auf stdout ausgibt, landet im Kontext des Agenten.
# Exit 0 = weiter (SessionStart kann nichts blockieren).

set -euo pipefail

cd "${CLAUDE_PROJECT_DIR:-.}"

if [ ! -f FORTSCHRITT.md ]; then
  echo "Hinweis: FORTSCHRITT.md fehlt."
  exit 0
fi

echo "=== Stand aus FORTSCHRITT.md (automatisch geladen) ==="
# Nur den Abschnitt "Aktueller Stand" ausgeben: von seiner Überschrift bis zur nächsten.
awk '/^## Aktueller Stand/{p=1; next} /^## /{p=0} p' FORTSCHRITT.md
echo "======================================================"
