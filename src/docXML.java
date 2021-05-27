// A class to hold unzipped docXML information for Projects

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class docXML {
String docString;
String docStylesString;
String docNumberingString;
ArrayList<String> docxStyles;
ArrayList<String> lvl0StyleNames;
ArrayList<String> headingTextList;
ArrayList<String> blocklist;
ArrayList<Book>  booklist;		

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
      setStylesString(myZip.getStyles()); //sets string and populates list
      setDocNumberingString(myZip.getNumbering());
      //populate blocklist for future use
      setInitialBlocklist();
      makeBooksFromBlocklist();
}

//--blocklist for main API

public void setInitialBlocklist() {
    ArrayList<String>myLines = getOOXMLParasInclusive(); 
    ArrayList<Integer> fileindex = codeOOXMLLines(myLines);//TO DO - Lines format should save headings too
    Parser myParser=new Parser();
    setBlocklist(myParser.packageBlocksFromLines(myLines,fileindex)); 
}

// -- METADATA FOR DOCX PROJECTS

public void setdocxStyles(ArrayList<String> input){
    this.docxStyles=input;
}

public ArrayList<String> getdocxStyles(){
    return this.docxStyles;
}

public void setLvl0Styles(ArrayList<String> input){
    this.lvl0StyleNames=input;
}


public ArrayList<String> getlvl0Styles(){
    return this.lvl0StyleNames;
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

public void setStylesString(String input) {
	docStylesString = input;
	//update other states that use styles
	ArrayList<String> myStyles=getStylesInclusive();
	setStyleLvlNames(myStyles);
	//setLvl0Styles();
}

public String getStylesString() {
	return docStylesString;
}

public void setDocNumberingString(String input) {
    this.docNumberingString = input;
}

public String getDocNumberingString() {
    return this.docNumberingString;
}

// - BLOCKS/BOOKS API

public void setBlocklist(ArrayList<String> input) {
    this.blocklist = input;
}

public ArrayList<String> getBlocklist() {
    return this.blocklist;
}

public void setBooklist(ArrayList<Book> input) {
    this.booklist = input;
}

public ArrayList<Book> getBooklist() {
    return this.booklist;
}


// --- Parsers

//get OOXML paras (modelled on my xmlutil.py)
/* input:
This is the entire document.xml (could be a smaller string, if needed)
output:
An array of String type with elements that inclusively match the requested tags
*/
public ArrayList<String> getOOXMLParasInclusive(){
	String contents = getDocString(); 
    String starttag="<w:p>";
    String endtag="</w:p>";
    ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    return result;
}

/*
input: styles.xml (String equivalent)

output: Array of each <w;style> tag as String
*/

public ArrayList<String> getStylesInclusive(){
	System.out.println("Preparing an array of styles from styles.xml");
	String contents = getStylesString(); 
    String starttag="<w:style ";
    String endtag="</w:style>";
    ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    /*
    for (String item: result) {
    	System.out.println(item);
    }
    System.exit(0);
    */
    return result;
}

/* 
get the styleId of the current style in styles.xml 
*/

public String getStyleId(String item){
	String parameter = "w:styleId";
	String output=getParameterValue(item,parameter);
	//System.out.println(parameter+","+item+"--->"+output);
	return output;
}

/* 
get the styleId of the current document.xml paragraph <w:p>
*/
//w:pStyle w:val
public String getParaStyleId(String item){
	String parameter = "pStyle w:val";
	String output=getParameterValue(item,parameter);
	//System.out.println(parameter+","+item+"--->"+output);
	return output;
}

/*
input: inclusive arraylist of styles tags from styles.xml (as full strings)

output: only a list of names of styles that contain outline lvl= 0
*/

public void setStyleLvlNames(ArrayList<String> input) {
	  System.out.println("Preparing a list of lvl 0 styles from styles list");
      ArrayList<String> output = new ArrayList<String>();
      int length = input.size();  // number of blocks
      if (length>0) {
        Iterator<String> iter = input.iterator(); 
          while (iter.hasNext()) {
              String item = iter.next();
              if (item.contains("<w:outlineLvl w:val=\"0\"/>")) {
              		String name=getStyleId(item);
              		System.out.println("Match in this para:"+item);
              		System.out.println(name);
                	output.add(name);
              }
              else {
                //do nothing
              }
         } //end while
      } //end if
      //list and exit for now
      System.out.println(output.toString());
      setLvl0Styles(output);
}

/*  

Input: a <w:p> tagged paragraph from OOXML document.xml
Also, linecount to see where we are in file.
 
Function: checks to see if the StyleId in this paragraph is same as the list of StyleIds that
have Outline lvl 0.  

Output: If  a match, returns 0 (as if H1 or # in markdown). Otherwise returns 1.
*/

public int getOOXMLLevel0code(String thisRow, int linecount) {
		int code=1;
		System.out.println("Row:"+thisRow);
		String styleID = getParaStyleId(thisRow);
		System.out.println("Detected style in this para:"+styleID);
		//no style found to check against
		if (styleID.length()==0) {
			return code;
		}
		
		ArrayList<String> stylesList = getlvl0Styles();
		for (String item : stylesList) {
			System.out.println("Checking this from list:"+item);
			//match
			if (item.contains(styleID)) {
				//if we haven't add heading text for first part of file
				if (linecount>0 && this.headingTextList.size()==0) {
					this.headingTextList.add("First page");
				}
				String headingText=getTextTags(thisRow);
				addHeadingText(headingText);
				//System.out.println("Heading Text:"+headingText);
				//System.exit(0);
				return 0;
			}
	}
     return code;
 }


/*
input: a paragraph string from document.xml

output: just the <w:t> tags section
*/

public String getTextTags(String input){
	String starttag="<w:t>";
	String endtag="</w:t>";
	ArrayList<String> result = getTagAttribInclusive(input,starttag,endtag); 
	ArrayList<String> result2 =  new ArrayList<String>();
	//remove ad hoc internal <w:t> tag
	for (String item: result) {
		item = item.replace("<w:t xml:space=\"preserve\">",""); 
		result2.add(item);
	}
	String output = removeTags(result2,starttag,endtag);
	//System.out.println(output);
	return output;
}

/*
Remove input tags from string
Input: a <w:t> set from <x:p> sourced from document.xml

*/

public String removeTags(ArrayList<String> inputList, String tag1, String tag2) {
	String empty="";
	String output="";
	for (String input: inputList) {
		//TO DO: move this to <w:t> text specific area
		String output1 = input.replace(tag1,empty);
		String output2 = output1.replace(tag2,empty);
		output=output+output2;
	}
	return output;
}

public ArrayList<Integer>codeOOXMLLines(ArrayList<String> myLines) {
	System.out.println("Starting OOXML code checks");
	System.out.println("Lines to process:"+myLines.size());
	//System.out.println("Lines :"+myLines.toString());
	ArrayList<Integer> fileindex = new ArrayList<Integer>();
	int linecount=1;
	for (String thisRow : myLines) {
		System.out.println("Current Row:\n"+thisRow);
		int bcode=getOOXMLLevel0code(thisRow,linecount);
		System.out.println(linecount+")"+bcode);
		//int bcode=setOOXMLLineCode(thisRow);
		fileindex.add(bcode);
		linecount++;
	} 
	//System.exit(0);
	return fileindex;
}

/*
# removes end of start tag so that included attributes section can be found
# e.g. <w:p> becomes <w:p and looks for > ahead.
# end tag is unaltered
# To do: use this to replace getTagListInclusive function
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

/*
input: name of parameter, where it is specified in OOXML tag.  Do not include = or ""

output: value of parameter

*/

public String getParameterValue(String thispara,String parameter) {
    String output="";
    int stop = thispara.length();
    int newstart=0;
    //String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    String starttag=parameter+"=\""; //starttag.substring(0,starttag.length()-1);// strip off closing >
    String endtag="\"";
    while (newstart<=stop) {
        int sindex=thispara.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        //find first index of end tag, but start past end of starttag
        int findex=thispara.indexOf(endtag,sindex+starttag.length()); 
        //String test=thispara.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (findex!=-1){
                String thistext=thispara.substring(sindex+starttag.length(),findex);
                return thistext;
            }
            else {
                newstart=newstart+1;
            }
    }
    return output;
}

public void makeBooksFromBlocklist(){
    ArrayList<Book> myBookList = new ArrayList<Book>();
      Parser myParser=new Parser();
      int headingcount=0;
      ArrayList<String> headList = getHeadingList();
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int length = this.blocklist.size();  // number of blocks
      System.out.println(length); //each of these numbered blocks is a string.
      int rowcount=0;
      if (length>0) {
        Iterator<String> iter = this.blocklist.iterator(); 
          while (iter.hasNext()) {
              //creates new Book, fills data from internal structure of String file, but this assumes markdown
              //TO DO: create new book with data.  Add GUI properties after
              //Book newBook=myParser.parseMDfile(MainStage.this,PressBox,DragBox,iter.next());
              //constructor: make new meta data with label of book
             //DO we need Event handlers for a data object? Add them to a purely GUI object?
              Book newBook =new Book(iter.next()); //TO DO:separate data model more
              //
              if (newBook!=null) {
                System.out.println("Starting iteration of block lines in MD");
                //set position as part of data model
                newBook.setRow(rowcount); //default col is 0.
                newBook.setCol(0);
                //add heading from docXML.  Needs to align to 'read' of headings and 0 codes.
                if (headList.size()>headingcount){
                  newBook.setLabel(headList.get(headingcount));
                  headingcount++;
                } 
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
}
