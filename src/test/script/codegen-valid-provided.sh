#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
for src in ./src/test/deca/codegen/valid/provided/*.deca; do
    asm="${src%.deca}.ass"
    rm -f "$asm"
    if ! decac "$src"; then
        echo "Echec decac sur $src"
        exit 1
    fi
    if [ ! -f "$asm" ]; then
        echo "Fichier $asm manquant"
        exit 1
    fi
    if ! ima "$asm" >/dev/null 2>&1; then
        echo "Echec ima sur $src"
        rm -f "$asm"
        exit 1
    fi
    rm -f "$asm"
done
echo "OK"
