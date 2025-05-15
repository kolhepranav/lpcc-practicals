/* taskC.y – Evaluate sqrt and strlen calls */
%{
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>   /* for strcmp, strlen */

void yyerror(const char *s);
int yylex(void);
%}

/* Semantic values: double for numbers, char* for identifiers/strings */
%union {
    double dval;
    char *sval;
}

/* Token declarations */
%token <sval> ID
%token <dval> NUMBER
%token <sval> STRING
%token EQ
%token SQRT STRLEN

/* Nonterminals */
%type <dval> input func_call expr

%%

input:
    ID EQ func_call   {
        if (strcmp($1, "u") == 0)
            printf("u = %lf\n", $3);
        else
            printf("%s = %lf\n", $1, $3);
        free($1);
    }
  ;

func_call:
      SQRT '(' expr ')'       { $$ = sqrt($3); }
    | STRLEN '(' STRING ')'   { $$ = (double)strlen($3); free($3); }
  ;

expr:
    NUMBER                   { $$ = $1; }
  ;

%%

#include <ctype.h>

int yylex(void) {
    int c;
    /* skip whitespace */
    while ((c = getchar()) == ' ' || c == '\t' || c == '\n');
    if (c == EOF) return 0;

    /* Identifier or keyword */
    if (isalpha(c)) {
        char buf[64]; int i = 0;
        buf[i++] = c;
        while ((c = getchar()) != EOF && (isalnum(c) || c == '_')) {
            if (i < sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        if (c != EOF) ungetc(c, stdin);

        if (strcmp(buf, "sqrt") == 0) return SQRT;
        if (strcmp(buf, "strlen") == 0) return STRLEN;
        /* else it's an ID */
        yylval.sval = strdup(buf);
        return ID;
    }

    /* Assignment operator */
    if (c == '=') return EQ;

    /* Number */
    if (isdigit(c) || c == '.') {
        ungetc(c, stdin);
        double d;
        if (scanf("%lf", &d) != 1) return 0;
        yylval.dval = d;
        return NUMBER;
    }

    /* String literal */
    if (c == '"') {
        char buf[128]; int i = 0;
        while ((c = getchar()) != '"' && c != EOF) {
            if (i < sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        yylval.sval = strdup(buf);
        return STRING;
    }

    /* Parentheses */
    return c;
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter e.g. u= sqrt(36) or v= strlen(\"pune\")\n");
    return yyparse();
}

