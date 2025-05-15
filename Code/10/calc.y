/* calc.y - YACC with %union for floating-point */

%{
#include <stdio.h>
#include <stdlib.h>

void yyerror(const char *s);
int yylex(void);
%}

/* Semantic value union */
%union {
    double val;
}

/* Token & nonterminal types */
%token <val> NUMBER
%type  <val> expr

/* Operator precedences */
%left '+' '-'
%left '*' '/'
%right UMINUS

%%

input:
    expr '\n'     { printf("Result = %lf\n", $1); }
  | expr          { printf("Result = %lf\n", $1); }
  ;

expr:
      expr '+' expr   { $$ = $1 + $3; }
    | expr '-' expr   { $$ = $1 - $3; }
    | expr '*' expr   { $$ = $1 * $3; }
    | expr '/' expr   {
                          if ($3 == 0.0) {
                            yyerror("division by zero");
                            YYABORT;
                          }
                          $$ = $1 / $3;
                        }
    | '-' expr %prec UMINUS { $$ = -$2; }
    | '(' expr ')'     { $$ = $2; }
    | NUMBER           { $$ = $1; }
  ;
%%

#include <ctype.h>

/* Simple lexer */
int yylex(void) {
    int c;
    while ((c = getchar()) == ' ' || c == '\t');
    if (c == EOF) return 0;
    if (isdigit(c) || c == '.') {
        ungetc(c, stdin);
        double d;
        if (scanf("%lf", &d) != 1) return 0;
        yylval.val = d;
        return NUMBER;
    }
    return c;
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter expression followed by newline:\n");
    return yyparse();
}
