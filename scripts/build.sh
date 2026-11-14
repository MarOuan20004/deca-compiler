#!/usr/bin/env bash
# Build script for Deca Compiler (decac)
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=== Building Deca Compiler (Maven packaging) ==="
mvn clean package -DskipTests

echo "=== Updating executable distribution ==="
cp -f target/Deca-0.0.1-SNAPSHOT-jar-with-dependencies.jar bin/
chmod +x bin/decac

echo "Build completed successfully."
echo "Binary available at: bin/decac"
