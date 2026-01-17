#! /bin/sh

# Script to run all objet-related tests
# Syntax tests, Context tests, and Codegen tests

cd "$(dirname "$0")"/../../.. || exit 1

echo "=========================================="
echo "Running all OBJET tests"
echo "=========================================="

tests_failed=0

echo ""
echo "--- Syntax Valid Tests (objet) ---"
if ./src/test/script/syntax-valid-objet.sh; then
    echo "✓ Syntax valid objet tests passed"
else
    echo "✗ Syntax valid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "--- Syntax Invalid Tests (objet) ---"
if ./src/test/script/syntax-invalid-objet.sh; then
    echo "✓ Syntax invalid objet tests passed"
else
    echo "✗ Syntax invalid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "--- Context Valid Tests (objet) ---"
if ./src/test/script/context-valid-objet.sh; then
    echo "✓ Context valid objet tests passed"
else
    echo "✗ Context valid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "--- Context Invalid Tests (objet) ---"
if ./src/test/script/context-invalid-objet.sh; then
    echo "✓ Context invalid objet tests passed"
else
    echo "✗ Context invalid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "--- Codegen Valid Tests (objet) ---"
if ./src/test/script/codegen-valid-objet.sh; then
    echo "✓ Codegen valid objet tests passed"
else
    echo "✗ Codegen valid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "--- Codegen Invalid Tests (objet) ---"
if ./src/test/script/codegen-invalid-objet.sh; then
    echo "✓ Codegen invalid objet tests passed"
else
    echo "✗ Codegen invalid objet tests FAILED"
    tests_failed=$((tests_failed + 1))
fi

echo ""
echo "=========================================="
if [ "$tests_failed" -eq 0 ]; then
    echo "All OBJET tests PASSED ✓"
    exit 0
else
    echo "Some OBJET tests FAILED (count: $tests_failed) ✗"
    exit 1
fi
