/*
 * UVic SENG 265, Summer 2014,  A#4
 *
 * This will contain the bulk of the work for the fourth assignment. It
 * provide similar functionality to the class written in Python for
 * assignment #3.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "formatter.h"

int width;/*to hold the maximum length of a line*/
int prev_width;/*holder for when the width needs to be reinstated*/
int mgn = 0;/*holds the margin size*/
int prev_mgn = 0;/*holder for when mgn needs to be reinstated*/
int new_mgn = 0;
int fmt = 0;/*1 is on and 0 is off*/
int curr_len = 0;/*used to keep track of the number of chars in the current line*/
char **result;
int cur_line = 0;
int nl = 0;

char *print_mgn();
void fmt_line(char line[]);
void add_line();
void add_word(char *word);
void do_cmd(char *line);
void initalize_result();

char **format_file(FILE *infile) {
	/* allocate buffer for input */
	char line_buffer[1000];
	/* initialize memory for string array */
	char **lines = calloc(1,sizeof(char*));
	/* initialize line count to 0 */
	int num_lines = 0;
	
	if (lines == NULL){
		fprintf(stderr, "ERROR: Memory Allocation Failed\n");
		exit(1);
	}

	while (fgets(line_buffer, 1000, infile)){
		lines = (char**)realloc(lines, sizeof(char*) * (num_lines + 1));

		if (lines == NULL){
			fprintf(stderr, "ERROR: Memory Allocation Failed\n");
			exit(1);
		}
		lines[num_lines] = calloc((strlen(line_buffer) + 1), sizeof(char));

		if (lines[num_lines] == NULL){
			fprintf(stderr, "ERROR: Memory Allocation Failed\n");
			exit(1);
		}

		strncpy(lines[num_lines], line_buffer, strlen(line_buffer) + 1);
		num_lines++;
		
	}
	lines[num_lines] = NULL;
	char **holder = format_lines(lines, num_lines);
	return holder;
}

char **format_lines(char **lines, int num_lines) {
	initalize_result();
	
	int i = 0;
	for(i = 0;i<num_lines;i++){
		if(strncmp(lines[i],"?",1) == 0){
			do_cmd(lines[i]);
		}else{
			fmt_line(lines[i]);
		}
	}
	if(fmt == 0){
		result[cur_line-1] = NULL;
	}
	result[cur_line] = NULL;

	return result;
}

/*tested and works*/
void do_cmd(char *line){
	char *copy = strdup(line);
	char *pointer = strtok(copy, " \t\n");/*points to the current token*/
	if(strcmp(pointer,"?width") == 0){/*retrieves the desired width and sets the width*/
		pointer = strtok(NULL, " \t\n");
		width = (int) strtol(pointer,(char **)NULL,10);
		prev_width = width;
		fmt = 1;
	}else if(strcmp(pointer,"?mrgn") == 0){/*retrieves the desired margin value*/
		pointer = strtok(NULL, " \t\n");
		int temp = (int) strtol(pointer,(char **)NULL,10);
		if(strncmp(pointer,"-",1) == 0 || strncmp(pointer,"+",1) == 0){
			if(mgn + temp <= 0){
				new_mgn = 0;
			}
			else if(mgn + temp >= width - 20){
				new_mgn = width - 20;
			}
			else{
				new_mgn += temp;
			}
		}
		else{
			mgn = temp;
			new_mgn = temp;
		}
		
	}else if(strcmp(pointer,"?fmt") == 0){/*processes the format case*/
		pointer = strtok(NULL, " \t\n"); 
		if(strcmp(pointer,"off")==0){
			mgn = 0;
			//new_mgn = 0;
			width = 0;
			fmt = 0;
		}else if(strcmp(pointer,"on")==0){
			fmt = 1;
			mgn = prev_mgn;
			//new_mgn = prev_mgn;
			width = prev_width;
		}
	}
}

/*tested and works*/
void fmt_line(char *line){
	if(fmt == 0){
		nl = 0;
		char *temp = strdup(line);
		temp = strtok(temp, "\n");
		add_word(temp);
		add_line();
	}else{
		if(strcmp(line,"\n") == 0){
			add_line();/*adds a new line to the output*/
			if(nl == 0){
				result[cur_line-1] = "";
				add_line();
			}
			curr_len = 0;/*resets curr_len*/
			nl = 1;
		}else{
			nl = 0;
			char *temp = strdup(line);
			char *pointer = (char *)malloc(sizeof(char) * (strlen(temp)+1));/*points to the current token*/
			for(pointer = strtok(temp, " \t\n");pointer != NULL;pointer = strtok(NULL, " \t\n")){/*loops through the current line token by token*/
				if((int)strlen(pointer)+curr_len <= width-mgn){/*prints the token if it is less then the current width*/
					if(curr_len !=  0){
						curr_len += 1;
						add_word(" ");
					}
					curr_len += strlen(pointer);/*incriments curr_len by the length of the token*/
					add_word(pointer);
				}else{/*prints the current token on a new line if it was to long to go on the previous line*/
					add_line();
					curr_len = strlen(pointer);/*resets curr_len for the new line*/
					add_word(pointer);
				}
			}
		}
	}
}

/*tested and works*/
/*prints mgn number of spaces*/
char *print_mgn(){
	mgn = new_mgn;
	char *temp = (char *)calloc(mgn,sizeof(char));
	int i;
	for(i = 0; i<mgn; i++){
		strcat(temp," ");
	}
	return temp;
}

/*tested and works*/
void add_line(){
	result = (char **)realloc(result, sizeof(char *)*(cur_line+1));
	if (result == NULL) {
		printf("an error occured when reallocating result in add_line\n");
		exit(1);
	}
	if(cur_line != 0){
		result[cur_line] = (char *)calloc(1,sizeof(char));
		if (result[cur_line] == NULL) {
			printf("an error occured when callocating result in add_line\n");
			exit(1);
		}
	}
	curr_len = 0;
	cur_line++;
}

/*tested and works*/
void add_word(char *word){
	result[cur_line-1] = (char *)realloc(result[cur_line-1], strlen(result[cur_line-1])+strlen(word)+mgn+1);
	if (result[cur_line-1] == NULL) {
		printf("an error occured when reallocating result in add_word\n");
		exit(1);
	}
	if(strcmp(result[cur_line-1], "") == 0){
		strcat(result[cur_line-1], print_mgn(mgn));
	}
	strcat(result[cur_line-1], word);
}

/*tested and works*/
void initalize_result(){
	result = (char **)malloc(sizeof(char *));
	if (result == NULL) {
		printf("an error occured when mallocating result in initalize_result\n");
		exit(1);
	}
	result[0] = (char *)calloc(1,sizeof(char));
	if (result[0] == NULL) {
		printf("an error occured when callocating result in initalize_result\n");
		exit(1);
	}
	cur_line++;
}
