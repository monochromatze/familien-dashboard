#!/usr/bin/env bash
# PostToolUse-Hook: prüft die gerade geänderte Datei mit Formatter/Linter.
# Läuft NACH Write/Edit. Exit 2 = Fehler gehen an den Agenten, er muss korrigieren.
# Regeln: R1 (Format), R2 (Linter). Nur die geänderte Datei, damit es schnell bleibt.
# Solange es noch kein Backend/Frontend gibt (Modul 4), macht der Hook nichts.

set -uo pipefail

input=$(cat)
file=$(echo "$input" | jq -r '.tool_input.file_path // empty')
[ -z "$file" ] && exit 0
[ -f "$file" ] || exit 0

root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
fail() { echo "LINT-FEHLER in $file:" >&2; echo "$1" >&2; exit 2; }

case "$file" in
  *.ts|*.tsx)
    cd "$root/frontend" 2>/dev/null || exit 0       # noch kein Frontend: überspringen
    [ -x node_modules/.bin/prettier ] || exit 0     # Werkzeuge noch nicht installiert
    npx --no-install prettier --write "$file" >/dev/null 2>&1
    out=$(npx --no-install eslint "$file" 2>&1) || fail "$out"
    ;;
  *.kt|*.kts)
    cd "$root/backend" 2>/dev/null || exit 0        # noch kein Backend: überspringen
    [ -x ./gradlew ] || exit 0
    out=$(./gradlew --quiet ktlintCheck detekt 2>&1) || fail "$out"
    ;;
esac

exit 0
