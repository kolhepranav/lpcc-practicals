/* taskA.y – Validate a single C‑style identifier */

%{
#include <stdio.h>
#include <stdlib.h>
void yyerror(const char *s);
int yylex(void);
%}

/* We’ll pass each identifier as a C‑string */
%union {
    char *sval;
}

/* Token with a string value */
%token <sval> IDENT

/* The start symbol produces that string */
%type  <sval> input

%%

input:
    IDENT          { 
                      printf("Valid identifier: %s\n", $1);
                      free($1);
                    }
  ;

%%

#include <ctype.h>
#include <string.h>

/* Our lexer: builds the identifier in buf, then returns IDENT */
int yylex(void) {
    int c;
    /* skip whitespace */
    do {
        c = getchar();
        if (c == EOF) return 0;
    } while (c == ' ' || c == '\t' || c == '\n');

    if (isalpha(c)) {
        /* read rest of identifier */
        char buf[128];
        int i = 0;
        buf[i++] = c;
        while ( (c = getchar()) != EOF && (isalnum(c) || c == '_') ) {
            if (i < (int)sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        if (c != EOF) ungetc(c, stdin);

        /* allocate and pass it up */
        yylval.sval = strdup(buf);
        return IDENT;
    }

    /* anything else is an error */
    yyerror("Invalid input; expected identifier");
    exit(1);
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter one identifier:\n");
    return yyparse();
}

