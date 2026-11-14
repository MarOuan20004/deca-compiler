#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:"$PATH"
for src in ./src/test/deca/context/invalid/provided/*.deca; do
    output=$(test_context "$src" 2>&1); status=$?
    if [ "$status" -eq 0 ] && ! printf '%s' "$output" | grep -q -e "$src:[0-9][0-9]*:"; then
        echo "Succes inattendu sur $src"
        echo "$output"
        exit 1
    fi
done
echo "OK"
