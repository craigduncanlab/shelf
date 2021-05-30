// A class to hold unzipped docXML information for Projects

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class docXML {
String docString;
String docStylesString;
String docNumberingString;
ArrayList<String> docxStyles;
ArrayList<String> stylesList;
ArrayList<String> lvl0StyleNames;
ArrayList<String> headingTextList;
ArrayList<xmlBlock> blocklist;
ArrayList<Book>  booklist;	
xmlStyles myStyles = new xmlStyles();

//constructor
public docXML() {
	lvl0StyleNames = new ArrayList<String>();
	headingTextList = new ArrayList<String>();
}

//-- LOADER

/* 
Since the .docx format is compressed and based on OOXML, it needs to be unzipped, then the contents extracted.
Those contents need to be interpreted to find out the 'intent' of paragraph properties like styles, numbering.
It is best to store them in-memory to work on them later
*/

public void openDocx(File file){
      ZipUtil myZip = new ZipUtil();
      myZip.OpenDocX(file);
      setDocString(myZip.getDocument());
      myStyles.setStylesString(myZip.getStyles()); //sets string and populates internal list/array with xstyle objects.
      setDocNumberingString(myZip.getNumbering());
      //populate blocklist for future use
      setInitialBlocklist();
      makeBooksFromBlocklist();
}

//--blocklist for main API

public void setInitialBlocklist() {
    ArrayList<xmlPara>myLines=extractParas(); //lines are coded as they are added
    setBlocklist(makeBlocksFromParas(myLines)); 
}

//TO DO: block for first page
public ArrayList<xmlBlock> makeBlocksFromParas(ArrayList<xmlPara> myLines) {
    ArrayList<xmlBlock>newBlocks = new ArrayList<xmlBlock>();
    xmlBlock currentblock = new xmlBlock();
    
    String headertext="";
    int cl=0;
    for (xmlPara thisPara: myLines) {
        int linecode=thisPara.getLineCode();//.get(cl);
        //if this row is a heading
        System.out.println(cl+") ["+linecode+"] "+thisPara.getParaString());

        //SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES
        if (linecode==0) { //if we encounter start of next block (#)
            //first line
                if (currentblock.getStoredLines()>0) {
                    newBlocks.add(currentblock); //add the current block to newBlocks array
                    currentblock=new xmlBlock(); //reset it to a new pointer
                }
                //Adds xmlPara to Block, then makes text property from it.
                currentblock.addLineObject(thisPara); 
        }
        //for linecode > 0, add it (could ignore lines with other codes, or pre-process, but don't do it for now)
        if (linecode>0) { 
                currentblock.addLineObject(thisPara);
        }
        cl++; //increase line count for index   
    }   //end loop

    //add last block or we drop 1 each time
    if (currentblock.getStoredLines()>0) {
        newBlocks.add(currentblock); //add the current block to newBlocks array
    }
    return newBlocks;
}

// -- CODING PARAGRAPHS BASED ON STYLES OR OTHER FEATURES

/*  

Input: a <w:p> tagged paragraph from OOXML document.xml
Also, linecount to see where we are in file.
 
Function: checks to see if the StyleId in this paragraph is same as the list of StyleIds that
have Outline lvl 0.  

Output: If  a match, returns 0 (as if H1 or # in markdown). Otherwise returns 1.
*/

public xmlPara setmyParaCode(xmlPara thisPara) {
        int code=1;
        //System.out.println("Row:"+thisRow);
        String paraStyle = thisPara.getpStyle(); 
        //no style found to check against
        if (paraStyle.length()==0) {
            //return code;
            thisPara.setLineCode(code);
        }
        //make reference to all the styles
        xmlStyles stylesObject = getStylesObject(); //(xmlStyles).  
        ArrayList<xstyle> stylesList=stylesObject.getOutline0Styles(); //just the Outline 0 styles
        for (xstyle item : stylesList) {
            String styleId=item.getId();
            //match
            if (paraStyle.equals(styleId)) {
                thisPara.setLineCode(0);
            }
            else {
                thisPara.setLineCode(code);
            }
    }
    return thisPara; //need to do this to update the object 
 }

// -- METADATA FOR DOCX PROJECTS

public void setdocxStyles(ArrayList<String> input){
    this.docxStyles=input;
}

public ArrayList<String> getdocxStyles(){
    return this.docxStyles;
}

/* Add another heading to the heading text list */

public void addHeadingText(String input){
	this.headingTextList.add(input);
}

public ArrayList<String> getHeadingList(){
	return this.headingTextList;
}

// -- Unzipped Strings

public void setDocString(String input) {
	docString = input;
}

public String getDocString() {
	return docString;
}

public String getStylesString() {
	return docStylesString;
}

public xmlStyles getStylesObject(){
    return this.myStyles;
}

public void setDocNumberingString(String input) {
    this.docNumberingString = input;
}

public String getDocNumberingString() {
    return this.docNumberingString;
}

// - BLOCKS/BOOKS API

public void setBlocklist(ArrayList<xmlBlock> input) {
    this.blocklist = input;
}

public ArrayList<xmlBlock> getBlocklist() {
    return this.blocklist;
}

public void setBooklist(ArrayList<Book> input) {
    this.booklist = input;
}

public ArrayList<Book> getBooklist() {
    return this.booklist;
}


// --- Parsers

//get OOXML <w:p> paras (modelled on my xmlutil.py)
/* input:
This is the entire document.xml (could be a smaller string, if needed)
output:
An array of String type with elements that inclusively match the requested tags
*/
public ArrayList<xmlPara> extractParas(){
	String contents = getDocString(); 
    String starttag="<w:p>";
    String endtag="</w:p>";
    //ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    ArrayList<xmlPara> result=getXMLparas(contents,starttag,endtag);
    return result;
}

/*
input: styles.xml (String equivalent)
Preparing an array of styles from styles.xml
output: Array of each <w;style> tag as String
*/

public ArrayList<String> getStylesInclusive(){
	
	String contents = getStylesString(); 
    String starttag="<w:style ";
    String endtag="</w:style>";
    ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    return result;
}

/*
# removes end of start tag so that included attributes section can be found
# e.g. <w:p> becomes <w:p and looks for > ahead.
# end tag is unaltered
# To do: use this to replace getTagListInclusive function
Also: <w:table> will appear in midst of <w:p> tags so need to allow for this in Block creation
*/
public ArrayList<String> getTagAttribInclusive(String thispara,String starttag, String endtag) {
    ArrayList<String> output= new ArrayList<String>();
    int stop = thispara.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        int sindex=thispara.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        int findex=thispara.indexOf(endtag,sindex); //find first index of end tag
        String test=thispara.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=thispara.substring(sindex,findex+endtag.length());
                // omit if this is a picture
                //testpict="<w:pict>"
                //if testpict not in thistext:
                output.add(thistext); // add to the array
                newstart=findex+endtag.length(); //len(endtag);
            }
            else {
                newstart=newstart+1;
            }
        }
        else {
            newstart=newstart+1;
        }
    }
    return output;
}

public ArrayList<xmlPara> getXMLparas(String input,String starttag, String endtag) {
    ArrayList<xmlPara> output= new ArrayList<xmlPara>();
    xmlPara currentPara = new xmlPara();
    int stop = input.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        currentPara = new xmlPara(); //to reset the reference.
        int sindex=input.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        int findex=input.indexOf(endtag,sindex); //find first index of end tag
        String test=input.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=input.substring(sindex,findex+endtag.length());
                // omit if this is a picture
                //testpict="<w:pict>"
                //if testpict not in thistext:
                currentPara.setParaString(thistext); //initialise the paragraph with text
                currentPara=setmyParaCode(currentPara); //update object
                output.add(currentPara); // add to the array
                newstart=findex+endtag.length(); //len(endtag);
            }
            else {
                newstart=newstart+1;
            }
        }
        else {
            newstart=newstart+1;
        }
    }
   
    return output;
}

/* 
Function to make Book (intermediate data object, for GUI) from xmlBlock objects
*/
public void makeBooksFromBlocklist(){
    ArrayList<Book> myBookList = new ArrayList<Book>();
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int length = this.blocklist.size();  // number of blocks
      System.out.println(length); //each of these numbered blocks is a string.
      int rowcount=0;
      if (length>0) {
        Iterator<xmlBlock> iter = this.blocklist.iterator(); 
          while (iter.hasNext()) {
              xmlBlock myBlock = iter.next();
              Book newBook =new Book(myBlock); 
              System.out.println("Book heading:"+newBook.getLabel());
              //
              if (newBook!=null) {
                //set default position for GUI?
                newBook.setRow(rowcount); //default col is 0.
                newBook.setCol(0);
              }
              else {
                System.out.println("Nothing returned from parser");
              }
              myBookList.add(newBook);
              rowcount++;
         } //end while
      } //end if
    setBooklist(myBookList);
    }

//write out Word just from notes in blocks, using some Standard styles
public void writeOutWordFromBooks(String filepath, ArrayList<Book> mySaveBooks) {
    System.out.println("Saving: "+filepath);
    Parser myP = new Parser();
    Iterator<Book> myIterator = mySaveBooks.iterator();
    StringBuffer myWordOutput=new StringBuffer();
         while (myIterator.hasNext()) {
            Book myNode=myIterator.next();
            //System.out.println(myNode.toString());
            
            //to preserve original word contents:
            //String myWordString = myNode.getOOXMLtext(); //or get markdown?

            //Convert Book text to OOXML.  To do: put in Book class?
            String myWordString=myP.getOOXMLfromContents(myNode); //this gets wordcodes every time
            
            myWordOutput.append(myWordString);
            myWordString="";
             //option: prepare string here, then write once.
        }
        //System.exit(0);
        WordWriter(myWordOutput.toString(),filepath);
}


//basic writer to create a de novo Word Doc from just the String (i.e. mdnotes)

public void WordWriter(String inputstring, String filename) {
    String myRefFile="wordlib/StylesTemplate.docx";
    //we need to take inputstring, insert it into document.xml and then zip it up with rest of docx
    File sourceFile = new File("wordlib/LittleDoc.xml");
    ZipUtil util = new ZipUtil();
    String myDocument = util.getFileText(sourceFile); //alternatively, extract from StylesTemplate
    String newDoc=myDocument.replace("XXXXXX",inputstring); //we now have a new document.xml
   
    util.readAndReplaceZip(myRefFile,newDoc,filename);
}

}
