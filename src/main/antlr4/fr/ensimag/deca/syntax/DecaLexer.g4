lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}
/* 
// Deca lexer rules.
DUMMY_TOKEN: .; // A FAIRE : Règle bidon qui reconnait tous les caractères.
                // A FAIRE : Il faut la supprimer et la remplacer par les vraies règles.
 */


//Mots-clés :
TRUE : 'true';
FALSE : 'false';
INT : 'int';
FLOAT :'float';
IF : 'if';
ELSE :'else';
WHILE : 'while';
READINT : 'readInt';
READFLOAT : 'readFloat';
BOOLEAN : 'boolean' ;
PRINTLN : 'println';
PRINT : 'print';
PRINTX : 'printx';
PRINTLNX :'printlnx';
RETURN : 'return';
ASM : 'asm';
CLASS : 'class';
EXTENDS : 'extends';
INSTANCEOF : 'instanceof';
NEW : 'new';
THIS : 'this';
NULL : 'null';
PROTECTED : 'protected';
VOID : 'void';
CAST : 'cast';


 
//Opérateurs  :
EQEQ: '==' ; 
NEQ : '!=' ;
AND : '&&';
GE : '>=';
LE : '<=';

PLUS : '+' ;
MINUS : '-' ;
TIMES : '*' ;
DIV : '/' ;
ASSIGN : '=';
LT : '<';
GT :'>';
OR: '||';
NOT : '!';
COMMA : ',';
DOT : '.';
MOD : '%';

//Les litteraux : 
fragment POSITIVE_DIGIT : '1' .. '9' ;
INT_LITERAL : '0' | POSITIVE_DIGIT DIGIT*  ;
fragment NUM : DIGIT+;

fragment DIGIT     : [0-9];
fragment SIGN      : [+-]?;
fragment EXP       : [Ee] SIGN DIGIT+;
fragment P_EXP     : [Pp] SIGN DIGIT+;
fragment DIGITHEX  : [0-9a-fA-F];

FLOAT_LITERAL
    : DIGIT+ '.' DIGIT+ EXP? [Ff]?
    | '0' [xX] DIGITHEX+ '.' DIGITHEX+ P_EXP [Ff]?
    ;

fragment STRING_CAR: ~('"' | '\\' | '\n' | '\r' ) ;
STRING : '"' ( STRING_CAR | '\\"' | '\\\\' )* '"' ;
MULTI_LINE_STRING :  '"' ( STRING_CAR | '\n' | '\r' | '\\"' | '\\\\' )* '"' ;


fragment LETTER : 'a'..'z' | 'A'..'Z';

//Identificateur 
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

// Ignore spaces, tabs, newlines and whitespaces
WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {
              skip(); // avoid producing a token
          }
    ;

COMMENT : '/*' ( COMMENT | . )*? '*/' { skip(); } ;
LINE_COMMENT : '//' ~[\r\n]* { skip(); };



OBRACE :'{' ;

OPARENT: '(';
CPARENT: ')';
SEMI : ';';
CBRACE :'}';

// inclusion de fichiers
fragment FILENAME : (LETTER | DIGIT | '.' | '_' | '-')+ ;
INCLUDE : '#include' (' ')* '"' FILENAME '"' ;




