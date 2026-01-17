#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
for src in ./src/test/deca/codegen/invalid/objet/*.deca; do
    asm="${src%.deca}.ass"
    rm -f "$asm"
    if decac "$src" 2>/dev/null; then
        echo "Echec sur $src (devrait generer une erreur d'execution)"
        exit 1
    fi
    rm -f "$asm"
done
echo "OK"
