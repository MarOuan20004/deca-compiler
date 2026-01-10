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
FLOAT_LITERAL : NUM '.' NUM  ;
STRING : '"' ( '\\' . | ~('\\'|'"') )* '"';


fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT :'0' .. '9';
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




