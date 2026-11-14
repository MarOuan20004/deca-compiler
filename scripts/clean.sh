#!/usr/bin/env bash
# Clean build artifacts and temporary files
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=== Cleaning build artifacts ==="
mvn clean

echo "=== Cleaning temporary and generated assembly files ==="
find . -type f -name "*.ass" -delete
find . -type f -name "*.res" -delete
find . -type f -name "*.trace" -delete
find . -type f -name "*~" -delete
rm -rf tmp/ target/

echo "Clean completed."
