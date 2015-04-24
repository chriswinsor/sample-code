#! /opt/rh/python33/root/usr/bin/python

import re

"""Formatter takes in a list of lines with formatting instructions and outputs a formatted list
according the formatting instructions placed in the list"""

class Formatter:
	
	def __init__(self, l):
		self.out = []#holds the output
		self.mgn = 0#holds the current margin
		self.new_mgn = 0#used to hold the new margin when ?mrgn is called in the middle of a paragraph
		self.width = 132#holds the width (defalt is 132)
		self.align = "<"#holds the justifacation simbol
		self.upper = 0#switch for turning on or off uppercase
		self.fmt = 0#switch format on and off
		self.last = 0#used for aligning in the center
		self.curr_line = []#holds a list of words for the current line
		self.curr_len = 0#holds the length of the current line

		for line in l:#loops through each line of the file
			matchobj = re.match("(\?(width|mrgn|fmt|align|upper)) ((\S*)\s(\d\d)?)", line)
			if(matchobj):#checks if the line is a command
				self.process_command(matchobj.group(1),matchobj.group(4),matchobj.group(5))#sends the parts of the command and processes it
			elif self.fmt == 0:#format off case
				self.out.append(line[:-1])#adds the whole line
			elif self.fmt == 1:#format on case
				if line == "\n":#line is a new line char
					if self.curr_line != []: self.addline()#adds the current formated line
					self.out.append("")#adds a new line to the output
					self.curr_line = []#resets curr_line
					self.curr_len = 0#resets curr_len
				else:#not a new line char
					line = line.split()#puts each word in line in a list
					for i in range(0,len(line)):
						if self.curr_len+len(line[i]) <= self.width-self.mgn:#adds the word to curr_line
							self.curr_line.append(line[i])
							self.curr_len += len(line[i])+1
						else:#curr_line is full case
							self.addline()#adds the current formated line
							if self.mgn != self.new_mgn: self.mgn = self.new_mgn#changes mgn now
							self.curr_line = [line[i]]
							self.curr_len = len(line[i])+1
		if self.curr_line != [] and self.fmt == 1:#adds the last line in the file if format is on
			self.addline()
	
	#returns the formatted output
	def get_lines(self):
		return self.out
	
	#takes in a command and an opperation and processes that command
	def process_command(self,cmd,opperation,opp2):
		try:
			if cmd == "?mrgn":#margin case
				try:
					if "+" not in opperation and "-" not in opperation:#hard sets mgn if - or + is not present
						self.mgn = int(opperation)
						self.new_mgn = int(opperation)
					elif self.mgn + int(opperation) <= 0:#negitive margin case
						if self.curr_line == []: self.mgn = 0#hard sets margin if curr_line is empty
						self.new_mgn = 0
					elif self.mgn + int(opperation) < self.width-20:#changes margin if it is legal
						if self.curr_line == []: self.mgn += int(opperation)#hard sets margin if curr_line is empty
						self.new_mgn += int(opperation)
					elif self.mgn + int(opperation) >= self.width-20:#handles margin greater then width-20
						if self.curr_line == []: self.mgn = self.width-20#hard sets margin if curr_line is empty
						self.new_mgn = self.width-20
				except:
					raise illegalCommandError(cmd)
			elif cmd == "?width":#width command case
				try:
					if int(opperation) < 0: raise
					self.width = int(opperation)
				except:
					raise illegalCommandError(cmd)
				self.fmt = 1
			elif cmd == "?align":#align command case
				self.mgn = 0
				self.new_mgn = 0
				try:
					if opp2 != None and self.width <= int(opp2): self.last = int(opp2)
				except:
					raise illegalCommandError(cmd)
				if opperation == "left":
					if opp2 == None: self.last = 0
					self.align = "<"#left
				elif opperation == "center":
					if opp2 == None: self.last = self.width
					self.align = "^"#center
				elif opperation == "right":
					if opp2 == None: self.last = self.width
					self.align = ">"#right
				else: raise illegalCommandError(cmd)
			elif cmd == "?upper":#upper case command
				if opperation == "on":
					self.upper = 1
				elif opperation == "off":
					self.upper = 0	
				else: raise illegalCommandError(cmd)
			elif cmd == "?fmt" and opperation == "off":#format command case
				if opperation == "on":
					self.fmt = 1
				elif opperation == "off":
					self.fmt = 0
					self.curr_line = []
					self.curr_len = 0
				else: raise illegalCommandError(cmd)
					
		except illegalCommandError as err:
			print("illegal use of " + err.cmd)
			quit()
	#adds the current formatted line to the output
	def addline(self):
		if self.upper == 1:
			self.out.append(" "*self.mgn + "{0:{align}{last}}".format(" ".join(self.curr_line),align=self.align,last=self.last).upper())
		else:
			self.out.append(" "*self.mgn + "{0:{align}{last}}".format(" ".join(self.curr_line),align=self.align,last=self.last))
class illegalCommandError(Exception):
	"""illegalCommandError is when a command is called with an illegal argument"""
	def __init__(self, cmd):
		self.cmd = cmd 
