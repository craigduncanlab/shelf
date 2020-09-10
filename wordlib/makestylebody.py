import ooxmlutil
import sys # for processing command line args
import csv # library for simple csv reading
import sqlite3 # to store our test text in a database

def extractStyleBody():
	stylepath="StylesTemplate.docx"
	stylestring=ooxmlutil.getDocxContent(stylepath)
	newbody=docBodySwap(stylestring)
	print(newbody)  # ultimately, this will be the contents of document.xml
	writeOut(newbody)

def extractStyleBodyNoChange():
	stylepath="StylesTemplate.docx"
	newbody=ooxmlutil.getDocxContent(stylepath)
	#newbody=docBodySwap(stylestring)
	print(newbody)  # ultimately, this will be the contents of document.xml
	writeOut(newbody)

# function for a merge by swapping out the body of the old doc and the assembled <body> from script.  TO DO: unit tests
def docBodySwap(templatestring):
	opentag="<w:body>"
	closetag="</w:body>"
	newstring=opentag+"XXXXXX"+closetag  # actual doc body can consist of series of paragraphs
	libbody=ooxmlutil.getTagListInclusive(templatestring,opentag,closetag)
	oldbodystring=libbody[0] # 1 entry
	# replace the whole of the body part of olddocxstring with body part of rpstring
	result = templatestring.replace(oldbodystring,newstring)
	return result

def writeOut(bodystring):
	f = open("stylebody.xml", "w")
	f.write(bodystring)
	f.close()

args=len(sys.argv)
if (args==1 and __name__ == '__main__'):
    extractStyleBodyNoChange()