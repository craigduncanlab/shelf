// A class to hold unzipped docXML information for Projects

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class docXML {
String docString;
String docStylesString;
String summaryStylesString;
String docNumberingString;
ArrayList<String> docxStyles;
ArrayList<xmlPara> paraList;
ArrayList<String> stylesList;
ArrayList<String> lvl0StyleNames;
ArrayList<String> headingTextList;
ArrayList<xmlBlock> blocklist;
ArrayList<Book>  booklist;	
xmlStyles myStyles = new xmlStyles();
ZipUtil originalZip = new ZipUtil();

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

//nb: this Zip object holds the original docx information, whilst it persists.

public int openDocx(File file){
      originalZip.OpenDocX(file);
      System.out.println(file.getName());
      setDocString(this.originalZip.getDocument());
      if (getDocString().length()==0) {
        System.out.println("No document.xml string returned in docXML");
        return -1;
      }
      else {
          myStyles.setStylesXML(this.originalZip.getStyles()); //sets string and populates internal list/array with xstyle objects.
          setStylesString(myStyles.getStylesXML()); //update the document version of styles.xml
          setSummaryStylesString(myStyles.getSummaryStylesString()); //just for display
          setDocNumberingString(this.originalZip.getNumbering());
          //populate blocklist for future use
          setInitialParalist();
          setInitialBlocklist();
          //makeBooksFromBlocklist(); //handle this externally
          return 0;
      }
}

//--blocklist for main API

public void setInitialParalist() {
    ArrayList<xmlPara>myLines=extractParas(); //lines are coded as they are added
    setParaList(myLines);
}

public void setParaList(ArrayList <xmlPara> input) {
    this.paraList=input;
}

public ArrayList<xmlPara>getParaList() {
    return this.paraList;
}

public void setInitialBlocklist() {
    ArrayList<xmlPara>myLines=getParaList(); 
    setBlocklist(makeBlocksSplitOnOutlineLevel(myLines)); //lines are coded as they are added
}

//TO DO: block for first page
//numLevels should match number in xmlBlock 'makeBlockText' function
private ArrayList<xmlBlock> makeBlocksSplitOnOutlineLevel(ArrayList<xmlPara> myLines) {
    int numLevels=1;
    ArrayList<xmlBlock>newBlocks = new ArrayList<xmlBlock>();
    xmlBlock currentblock = new xmlBlock();
    
    String headertext="";
    int cl=0;
    for (xmlPara thisPara: myLines) {
        int linecode=thisPara.getOutlineLevel();//.get(cl);  cf getLineCode
        //if this row is a heading
        System.out.println(cl+") ["+linecode+"] "+thisPara.getParaString());

        //SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES

        //check each level in turn and make a new block if it is detected in this paragraph.
        for (int testlevel=0;testlevel<numLevels;testlevel++) {
        if (linecode==testlevel) { //if we encounter start of next block (#)
            //first line
                if (currentblock.getStoredLines()>0) {
                    newBlocks.add(currentblock); //add the current block to newBlocks array
                    currentblock=new xmlBlock(); //reset it to a new pointer
                    //currentblock.setSplitType("OutlineLvl0"); //default
                }
                //Adds xmlPara to Block, then makes text property from it.
                currentblock.addLineObject(thisPara); 
            }
        }
        //for linecode > 0, add it (could ignore lines with other codes, or pre-process, but don't do it for now)
        if (linecode>(numLevels-1)) { 
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

public ArrayList<xmlBlock> getBookmarkBlocklist(){
    ArrayList<xmlBlock> output = makeBlocksSplitOnBookmarks();
    return output;
}

private ArrayList<xmlBlock> makeBlocksSplitOnBookmarks() {
     ArrayList<xmlPara>myLines=getParaList(); 
    int numLevels=1;
    ArrayList<xmlBlock>newBlocks = new ArrayList<xmlBlock>();
    xmlBlock currentblock = new xmlBlock();
    currentblock.setSplitType("Bookmark");
    
    String headertext="";
    int cl=0;
    for (xmlPara thisPara: myLines) {
        String bmcode=thisPara.getBookmarkId();//.get(cl);  cf getLineCode
        //if this row is a heading
        System.out.println(cl+") ["+bmcode+"] "+thisPara.getParaString());

        //SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES

        //check each level in turn and make a new block if it is detected in this paragraph.
        if (!bmcode.equals("")) { //if non-empty bookmark
            //first line
                if (currentblock.getStoredLines()>0) {
                    newBlocks.add(currentblock); //add the current block to newBlocks array
                    currentblock=new xmlBlock(); //reset it to a new pointer
                    currentblock.setSplitType("Bookmark");
                }
                //Adds xmlPara to Block, then makes text property from it.
                currentblock.addLineObject(thisPara); 
            }
        //for linecode > 0, add it (could ignore lines with other codes, or pre-process, but don't do it for now)
        if (bmcode.equals("")) { 
                currentblock.addLineObject(thisPara);
        }
        cl++; //increase line count for index   
    }   //end loop

    //add last block or we drop 1 each time
    if (currentblock.getStoredLines()>0) {
        newBlocks.add(currentblock); //add the current block to newBlocks array
    }
    /*
    System.out.println(newBlocks.toString());
    System.exit(0);
    */
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

public xmlPara setmyParaOutlineCode(xmlPara thisPara) {
        int code=99;
        //System.out.println("Row:"+thisRow);
        String paraStyle = thisPara.getpStyle(); //the styleId in para
        //no style found to check against
        if (paraStyle.length()==0) {
            //return code;
            thisPara.setLineCode(code);
        }
        //make reference to all the styles
        xmlStyles stylesObject = getStylesObject(); //(xmlStyles).  
        ArrayList<xstyle> stylesList=stylesObject.getOutlineStyles(); //all the Outline styles
        for (xstyle item : stylesList ) {
            thisPara.setLineCode(code); //for custom block definition
            String styleId=item.getId(); //this is styleid in the xstyle
            //match on the style with this paragraph
            if (paraStyle.equals(styleId)) {
                //thisPara.setLineCode(0);
                thisPara.setOutlineLevel(item.getOutlineLevel()); //para stores same outline level as its style
                thisPara.setLineCode(item.getOutlineLevel()); //TO DO: can be an indepenent category
                thisPara.setStyleXML(item.getStyleXML()); //this is full xml
        }
    }
    return thisPara; //need to do this to update the object 
 }

public String getListBookmarks(){
    String output = "";
    ArrayList<xmlPara> myParas = getParaList();
    for (xmlPara item: myParas) {
        //add only if specific entry
        if (item.getBookmarkId().length()>0) {
            String entry = "["+item.getBookmarkId()+"]"+item.getBookmarkName()+System.getProperty("line.separator");
            output=output+entry;
        }
    }
    return output;
} 

// -- METADATA FOR DOCX PROJECTS

/*
public void setdocxStyles(ArrayList<String> input){
    this.docxStyles=input;
}

public ArrayList<String> getdocxStyles(){
    return this.docxStyles;
}
*/

/*
private void setdocxStyles(xmlStyles input){
    this.myOpenXMLStyle=input;
}


public xmlStyles getdocxStyles(){
    return this.myOpenXMLStyle;
}
*/

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

private void setStylesString(String input) {
    this.docStylesString=input;;
}

private String getStylesString() {
	return this.docStylesString;
}

private void setSummaryStylesString(String input) {
    this.summaryStylesString=input;
}

private String getSummaryStylesString() {
    return this.summaryStylesString;
}

public xmlStyles getStylesObject(){
    return this.myStyles;
}

public void setStylesObject(xmlStyles input){
    this.myStyles=input;
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
                currentPara=setmyParaOutlineCode(currentPara); //update para with style information
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
/*
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
              Book newBook =new Book(myBlock);  //constructor handles xmlBlock or mdBlock differently (polymorphism!)
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
*/
public void saveDocxWithNewStylesOnly(File inputFile){
    String myStyles = getStylesObject().getStylesXML();
    originalZip.readAndReplaceStyles(myStyles,inputFile);
}

}
