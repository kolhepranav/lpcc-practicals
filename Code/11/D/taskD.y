/* taskD.y – Evaluate sin/cos expressions, consuming the trailing newline */

%{
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
void yyerror(const char *s);
int yylex(void);
%}

/* Semantic-value union */
%union {
    double dval;
    char *sval;
}

/* Tokens */
%token <sval> ID
%token <dval> NUMBER
%token SIN COS EQ
%type  <dval> input expr func_call

/* Allow + in expressions */
%left '+'

%%

/* Now we consume the newline at the end */
input:
    ID EQ expr '\n'     {
                          printf("%s = %lf\n", $1, $3);
                          free($1);
                        }
  ;

expr:
      expr '+' expr    { $$ = $1 + $3; }
    | func_call        { $$ = $1; }
    | NUMBER           { $$ = $1; }
  ;

func_call:
      SIN '(' expr ')' { $$ = sin($3); }
    | COS '(' expr ')' { $$ = cos($3); }
  ;

%%

#include <ctype.h>

int yylex(void) {
    int c;

    /* skip only spaces and tabs */
    while ((c = getchar()) == ' ' || c == '\t');

    if (c == EOF) return 0;
    if (c == '\n') return '\n';   /* return newline! */

    /* Identifiers or keywords */
    if (isalpha(c)) {
        char buf[64];
        int i = 0;
        buf[i++] = c;
        while ((c = getchar()) != EOF && isalpha(c)) {
            if (i < (int)sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        if (c != EOF) ungetc(c, stdin);

        if (strcmp(buf, "sin") == 0) return SIN;
        if (strcmp(buf, "cos") == 0) return COS;

        yylval.sval = strdup(buf);
        return ID;
    }

    /* Assignment operator */
    if (c == '=') return EQ;

    /* Number literal */
    if (isdigit(c) || c == '.') {
        ungetc(c, stdin);
        double d;
        if (scanf("%lf", &d) != 1) return 0;
        yylval.dval = d;
        return NUMBER;
    }

    /* Plus or parentheses */
    if (c == '+' || c == '(' || c == ')') return c;

    yyerror("Unexpected character");
    exit(1);
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter e.g. u = sin(12)+cos(12) and press Enter\n");
    return yyparse();
}
