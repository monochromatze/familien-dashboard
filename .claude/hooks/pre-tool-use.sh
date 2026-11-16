#!/usr/bin/env bash
# PreToolUse-Hook: blockiert gefährliche Aktionen, BEVOR sie passieren.
# Eingabe: JSON auf stdin (Werkzeugname + Parameter).
# Exit 0 = erlaubt. Exit 2 = blockiert, stderr geht als Erklärung an den Agenten.
# Regeln: R9 (Migrationen nie ändern), R10 (keine Secrets), "nie auf main pushen".

set -uo pipefail

input=$(cat)
tool=$(echo "$input" | jq -r '.tool_name // empty')

block() {
  echo "BLOCKIERT: $1" >&2
  exit 2
}

case "$tool" in
  Write|Edit|NotebookEdit)
    file=$(echo "$input" | jq -r '.tool_input.file_path // .tool_input.notebook_path // empty')
    base=$(basename "$file")

    # R10: .env-Dateien (außer Beispieldateien *.env.example) nie anfassen
    case "$base" in
      *.env.example) ;;
      .env|.env.*) block "'$base' enthält Geheimnisse und darf vom Agenten nicht geändert werden (R10). Bitte den Nutzer, die Datei selbst zu bearbeiten." ;;
    esac

    # R9: Flyway-Migrationen. Neue Dateien sind erlaubt, bestehende nie ändern.
    case "$file" in
      */db/migration/*)
        if [ -e "$file" ]; then
          block "'$base' ist eine bestehende Migration und darf nie geändert werden (R9). Lege stattdessen eine neue Migration mit höherer Versionsnummer an."
        fi
        ;;
    esac
    ;;

  Bash)
    cmd=$(echo "$input" | jq -r '.tool_input.command // empty')

    # Nie direkt auf main pushen (auch nicht per HEAD:main oder --force)
    if echo "$cmd" | grep -Eq 'git[[:space:]]+push([^|;&]*)[[:space:]](origin[[:space:]]+)?(HEAD:)?main([[:space:]]|$)'; then
      block "Direkter Push auf main ist verboten. Arbeite auf einem Branch und öffne einen PR."
    fi

    # Shell-Umgehung: .env per Befehl lesen/ändern
    if echo "$cmd" | grep -Eq '(^|[[:space:]/])\.env([[:space:]]|$|\.)' && ! echo "$cmd" | grep -q '\.env\.example'; then
      block "Befehle, die .env-Dateien berühren, sind blockiert (R10)."
    fi
    ;;
esac

exit 0
