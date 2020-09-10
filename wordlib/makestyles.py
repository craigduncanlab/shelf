# (c) Craig Duncan 2019-2020
# make a list of Word styles that can wrap around text, using the StylesTemplate
# Styles Template is also the pro form docx document in which all text is inserted.
# write the file out so it can be read in by any program.   e.g. in Java.
# In theory the output is ok until StylesTemplate is updated.

import ooxmlutil
import sys # for processing command line args
import csv # library for simple csv reading
import sqlite3 # to store our test text in a database

def scanStyles():
    stylepath="StylesTemplate.docx"
    stylestring=ooxmlutil.getDocxContent(stylepath)
    updateDocxStyles(stylestring)

# update global styles dictionary from styles in customised docx styles template
def updateDocxStyles(stylestring):
    global blockdict
    paralist=ooxmlutil.getParasInclusive(stylestring)
    blockdict=identifyStyles(paralist)
    writeDictAsMD(blockdict)

#write dict.md file
def writeDictAsMD(blockdict):
	content=""
	prefix="# "
	EOL="\r\n" 
	for key,value in blockdict.items():
		content=content+prefix+key+EOL+value+EOL

	f = open("styledict.md", "w")
	f.write(content)
	f.close()

# at the end of this function, we have a list with style names and pro forma paragraph text to use
#returns list as dictionary with key,value
def identifyStyles(paralist):
    blocklist=[]

    for thispara in paralist:
        # print("In findInserts.  Finding a user tag ref in a para.")
        contigtext=makeContiguous(thispara)
        print("contig text:"+contigtext)
        if len(contigtext)<1:
            print("no contig text in style template line")
            exit()
        else:
            # put in standard 'code' text to be replaced later
            # do not just replace contigtext if the text is same as actual style name in docx document
            tblock=">"+contigtext+"<" # avoids replacing OpenXML pstyle text if it has same name
            newtext=thispara.replace(tblock,">XXX<")
            if ("XXX" not in newtext):
                print("Problem with Style Template.  Name not contiguous")
                print(contigtext)
                print(newtext)
                exit()
            #hardcode page break.  Template file just needs "PageBreak" words and font
            if (contigtext=="PageBreak"):
                #pbcode=xmlutil.returnPageBreak()
               # a whole run with a break in it.
                pbcode='<w:r><w:br w:type="page"/></w:r>'
                dummytag="<w:r>" # This is now in the newtext para
                if (dummytag not in newtext):
                    print("Cannot setup page break properly. No run tag <w:r>")
                    exit()
                exttext=pbcode+dummytag # pbreak inserted before next run tag.
                #print(newtext)
                newtext=newtext.replace(dummytag,exttext)
                if (pbcode not in newtext):
                    print("Page break not inserted properly.")
                    print(pbcode)
                    print(newtext)
                    exit()
            blocklist.append((contigtext,newtext))  # store this paragraph for replacement with library ref
                # print("Appended insert blockref: "+str(blockref))
    #convert list to dictionary
    print("===============================",blocklist)
    styledict=dict(blocklist) # convert list to dict
    return styledict

# Take string input equivalent to a Word OpenXML <w:p> paragraph block
# This is only a temporary transformation of para data obtained using the xmlutil utility
# It is never working with the OpenXML file directly.
# Locates Word text tags <w:t>, and makes text from those tags contiguous
# Use: For text-based searches/replacements that require plain, readable text.
# nb do not rely on <w:t> tag: use <w:t for opening as there are variations in those tags.
# nb this is one of them: <w:t xml:space="preserve">

def makeContiguous(thispara):
    # print ("Trying to make this contiguous:")
    # print(thispara)
    # make para contiguous text
    output="" 
    stop = len(thispara)
    # print("in makeC, length:"+str(stop))
    newstart=0
    starttag="<w:t"
    starttagend=">" # to allow for insertion of attributes (Word). 
    endtag="</w:t>"
    while newstart<=stop:
        sindex=thispara.find(starttag,newstart,stop) 
        test=thispara[sindex+len(starttag):sindex+len(starttag)+1]
        if test==starttagend or test==" ":
            sindexend=thispara.find(starttagend,sindex,stop) 
            gap=sindexend-sindex
            findex=thispara.find(endtag,newstart,stop)
            # whenever we find end tag we capture internal w:t tags text
            if (findex!=-1):
                thistext=thispara[sindex+gap+1:findex]
                output=output+thistext
                newstart=findex+len(endtag)
            else:
                newstart=newstart+1
        else:
            newstart=newstart+1
    #print("new contig output:"+output)
    return output

# START HERE
# Can be directly invoked with:  python3 docmaker.py fname

# global var for the styles
blockdict=dict()
outputfolder="output/"
db = sqlite3.connect(':memory:')   # this is RAM only.  Specify file to read from disk
cursor=db.cursor() # for command entry
 # this is from a file (create or open)
# db = sqlite3.connet('data/mydb')

# we are able to see how this code was invoked, and if called by another program, take no immediate action
# some prefer this technique for checking if this is the top-level or stand-alone source code run from command line:
# if __name__ == '__main__':

args=len(sys.argv)
#alternative way of checking how this is invoked
#progname=sys.argv[0]
#if (progname!="docmaker.py"):
#    print(progname)
#    exit()
if (args==2 and __name__ == '__main__'):
    nbname=sys.argv[1]
    if len(nbname)!=0:
        # remainder is optional for now:
        #
        nameonly,suffix=nbname.split('.')
        if (suffix=="txt" or suffix=="md" or suffix=="lmd" or suffix=="pml"):    # or suffix=="odt":
            print("Name is "+nbname)
            customFromFile(nbname)
        else:
            print("This program requires .txt .md or .lmd or  .pml extensions for the input (PML) file")
    else:
        doError()
if (args==3 and __name__ == '__main__'):
    outputfolder=sys.argv[1]
    nbname=sys.argv[2]
    if len(nbname)!=0:
        # remainder is optional for now:
        #
        nameonly,suffix=nbname.split('.')
        if (suffix=="md"):    # or suffix=="odt":
            print("Name is "+nbname)
            customFromFile(nbname)
        else:
            print("This program requires .md extension for the input file")
    else:
        doError()

elif (args==1 and __name__ == '__main__'):
    scanStyles()