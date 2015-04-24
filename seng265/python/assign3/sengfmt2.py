#! /opt/rh/python33/root/usr/bin/python 

import sys
import fileinput
from formatter import Formatter
'''sengfmt2 takes in a text file, and places each line of the file into a list.
It then sends the list into formatter.py to get formatted. After, it prints out 
the formatted file to stdout.'''
def main():
	lines = []
	for line in fileinput.input():
		lines.append(line)
	f = Formatter(lines)
	lines = f.get_lines()

	for l in lines:
		print (l)

if __name__ == "__main__":#calls main
	main()