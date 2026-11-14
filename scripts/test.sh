#!/usr/bin/env bash
# Test execution script for Deca Compiler
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=== Running Deca Compiler Validation Suite ==="
# Run Maven tests with headless non-blocking input for interactive scripts
mvn test < /dev/null

echo "=== Running Custom Example Smoke Tests ==="
for src in examples/deca/0*.deca; do
    echo "Testing compilation: $src"
    ./bin/decac "$src"
    asm="${src%.deca}.ass"
    if [ -f "$asm" ]; then
        rm -f "$asm"
    fi
done

echo "All tests passed successfully."
