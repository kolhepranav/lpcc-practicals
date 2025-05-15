/* taskE.y – Evaluate pow and log expressions with division */

%{
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

void yyerror(const char *s);
int yylex(void);
%}

/* Semantic values: double for numbers/exprs, char* for IDs */
%union {
    double dval;
    char   *sval;
}

/* Tokens */
%token <sval> ID
%token <dval> NUMBER
%token POW LOG EQ
%type  <dval> input expr func_call

/* Division operator */
%left '/'

%%

input:
    ID EQ expr '\n'        {
                              printf("%s = %lf\n", $1, $3);
                              free($1);
                            }
  ;

expr:
      expr '/' expr       { $$ = $1 / $3; }
    | func_call           { $$ = $1; }
    | NUMBER              { $$ = $1; }
  ;

func_call:
      POW '(' NUMBER ',' NUMBER ')'  { $$ = pow($3, $5); }
    | LOG '(' expr ')'               { $$ = log($3); }
  ;

%%

#include <ctype.h>

int yylex(void) {
    int c;
    /* skip spaces & tabs only */
    while ((c = getchar()) == ' ' || c == '\t');

    if (c == EOF) return 0;
    if (c == '\n') return '\n';

    /* Identifiers & keywords */
    if (isalpha(c)) {
        char buf[64];
        int i = 0;
        buf[i++] = c;
        while ((c = getchar()) != EOF && isalpha(c)) {
            if (i < (int)sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        if (c != EOF) ungetc(c, stdin);

        if (strcmp(buf, "pow") == 0)   return POW;
        if (strcmp(buf, "log") == 0)   return LOG;

        yylval.sval = strdup(buf);
        return ID;
    }

    /* Assignment */
    if (c == '=') return EQ;

    /* Number literal */
    if (isdigit(c) || c == '.') {
        ungetc(c, stdin);
        double d;
        if (scanf("%lf", &d) != 1) return 0;
        yylval.dval = d;
        return NUMBER;
    }

    /* Division or parentheses or comma */
    if (c == '/' || c == '(' || c == ')' || c == ',') {
        return c;
    }

    yyerror("Unexpected character in input");
    exit(1);
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter e.g. p = pow(3,2)/log(24) and press Enter\n");
    return yyparse();
}

