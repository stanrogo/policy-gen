grammar SimpleBoolean;

rule_set : target EOF;

AND : 'AND' ;
OR  : 'OR' ;

GT : '>' ;
GE : '>=' ;
LT : '<' ;
LE : '<=' ;
EQ : '=' ;

LPAREN : '(' ;
RPAREN : ')' ;

STRING : '\''[a-zA-Z_0-9\- ][a-zA-Z_0-9\- ]*'\'' ;
DECIMAL : '-'?[0-9]+('.'[0-9]+)? ;
IDENTIFIER : [a-zA-Z_][a-zA-Z_0-9]* ;

DOT : '.' ;

WS : [ \r\t\u000C\n]+ -> skip;

qualifiedIdentifier : IDENTIFIER DOT IDENTIFIER;

target: targetMatch;
targetMatch: anyOf (AND targetMatch)* | LPAREN targetMatch RPAREN;
anyOf: anyOfMatch;
anyOfMatch: allOf (OR anyOfMatch)* | LPAREN anyOfMatch RPAREN;
allOf: allOfMatch;
allOfMatch: predicate (AND allOfMatch)* | LPAREN allOfMatch RPAREN;
predicate : variable comparator attribute | LPAREN predicate RPAREN;
attribute : IDENTIFIER | qualifiedIdentifier ;
variable : DECIMAL | STRING | IDENTIFIER | qualifiedIdentifier ;

comparator : GT | GE | LT | LE | EQ ;
