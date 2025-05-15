%{
#include <stdio.h>
%}

/* Token patterns */
KEYWORD     (int|char|float|printf|return|void)
ID          [A-Za-z_][A-Za-z0-9_]*
NUMBER      [0-9]+(\.[0-9]+)?
STRING      \"([^\\\"]|\\.)*\"
OP          [\+\-\*\/=]
DELIM       [,;]
BRACE       [\{\}\(\)]

%%

{KEYWORD}   { printf("<KEYWORD,   \"%s\">\n", yytext); }
{ID}        { printf("<IDENTIFIER, \"%s\">\n", yytext); }
{NUMBER}    { printf("<NUMBER,     \"%s\">\n", yytext); }
{STRING}    { printf("<STRING,     %s>\n", yytext); }
{OP}        { printf("<OPERATOR,   \"%s\">\n", yytext); }
{DELIM}     { printf("<DELIMITER,  \"%s\">\n", yytext); }
{BRACE}     { printf("<BRACE,      \"%s\">\n", yytext); }

[ \t\r\n]+  /* skip whitespace */
"//".*      /* skip single-line comments */
"/*"[^*]*"*"+"/"     /* skip multi-line comments */

.           { printf("<UNKNOWN,    \"%s\">\n", yytext); }

%%

int main(int argc, char **argv) {
    yylex();
    return 0;
}

int yywrap(void) {
    return 1;
}
