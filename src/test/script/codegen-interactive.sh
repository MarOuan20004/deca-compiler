#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
for src in ./src/test/deca/codegen/interactive/*.deca; do
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
    case "$src" in
        *age_echo.deca) input="25" ;;
        *grade_validator.deca) input="14.5" ;;
        *precision_inspector.deca) input="3.14" ;;
        *satisfaction_survey.deca) input="4" ;;
        *star_rating.deca) input="4" ;;
        *temperature_echo.deca) input="21.0" ;;
        *voltage_check.deca) input="3.3" ;;
        *) input="1" ;;
    esac
    if [ -t 0 ]; then
        if ! ima "$asm"; then
            echo "Echec ima sur $src"
            rm -f "$asm"
            exit 1
        fi
    else
        if ! printf "%s\n" "$input" | ima "$asm"; then
            echo "Echec ima sur $src"
            rm -f "$asm"
            exit 1
        fi
    fi
    rm -f "$asm"
done
echo "OK"
