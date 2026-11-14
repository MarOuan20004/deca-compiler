#!/usr/bin/env bash
# Execution helper for Deca Compiler
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BIN="$ROOT_DIR/bin/decac"

if [ ! -f "$BIN" ]; then
    echo "Error: decac binary not found at $BIN. Please run scripts/build.sh first."
    exit 1
fi

exec "$BIN" "$@"
