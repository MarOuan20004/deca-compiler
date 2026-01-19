#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

# On parcourt les fichiers interactifs
for src in ./src/test/deca/codegen/interactive/*.deca; do
    asm="${src%.deca}.ass"
    rm -f "$asm"

    # 1. Compilation
    if ! decac "$src"; then
        echo "❌ Echec compilation (decac) sur $src"
        exit 1
    fi

    if [ ! -f "$asm" ]; then
        echo "❌ Fichier .ass manquant pour $src"
        exit 1
    fi

    # 2. Execution
    # J'ajoute un header pour que tu saches quel test tourne
    echo "=================================================="
    echo "Testing : $src"
    echo "=================================================="

    # J'ai ENLEVÉ ">/dev/null 2>&1" pour que tu voies le texte et puisses taper
    if ! ima "$asm"; then
        echo "❌ Echec execution (ima) sur $src"
        rm -f "$asm"
        exit 1
    fi

    echo "" # Un saut de ligne pour la propreté
    rm -f "$asm"
done

echo "✅ Tous les tests interactifs sont passés."