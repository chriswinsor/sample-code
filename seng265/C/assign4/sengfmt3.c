/*
 * UVic SENG 265, Summer 2014, A#4
 *
 * This will contain a solution to sengfmt3. In order to complete the
 * task of formatting a file, it must open the file and pass the result
 * to a routine in formatter.c.
 */

#include <stdio.h>
#include <stdlib.h>
#include "formatter.h"

int main(int argc, char *argv[]) {
	char **result;

	FILE *f;
	if(argc < 2){
		f = stdin;
		if(f == NULL){
			return(1);
		}
	}else{
		f = fopen(argv[1], "r");
		if(f == NULL){
			return(1);
		}
	}
	char **line;

	result = format_file(f);

	if (result == NULL) {
		printf("It appears that there was no input\n");
		exit(1);
	}

	for (line = result; *line != NULL; line++) {
		printf ("%s\n", *line);
	}
	exit(0);
}
