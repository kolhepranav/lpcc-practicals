/* taskB.y – Toggle case of a single word */

%{
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
void yyerror(const char *s);
int yylex(void);
%}

/* We’ll pass the word as a C‑string */
%union {
    char *sval;
}

/* Token with string value */
%token <sval> WORD
%type  <sval> input

%%

input:
    WORD       {
                  /* Toggle case in-place */
                  for (char *p = $1; *p; ++p) {
                    if (islower((unsigned char)*p))      *p = toupper((unsigned char)*p);
                    else if (isupper((unsigned char)*p)) *p = tolower((unsigned char)*p);
                  }
                  printf("Toggled: %s\n", $1);
                  free($1);
                }
  ;

%%

#include <ctype.h>
#include <string.h>

/* Build the word in a buffer, strdup it into yylval.sval */
int yylex(void) {
    int c;
    /* skip leading whitespace */
    do {
        c = getchar();
        if (c == EOF) return 0;
    } while (c == ' ' || c == '\t' || c == '\n');

    if (isalpha(c)) {
        char buf[128];
        int i = 0;
        buf[i++] = c;
        while ((c = getchar()) != EOF && isalpha(c)) {
            if (i < (int)sizeof(buf)-1) buf[i++] = c;
        }
        buf[i] = '\0';
        if (c != EOF) ungetc(c, stdin);

        yylval.sval = strdup(buf);
        return WORD;
    }

    /* If we hit anything else, stop */
    return 0;
}

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter one word to toggle case:\n");
    return yyparse();
}

