// A class to hold Markdown file information for Projects
//The mdBlock object is the upper class container in this md Data Model, within mdFile.
//cf Book which is the class container for the GUI

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class mdFile {
	String docString; //main text
	ArrayList<mdLineObject> fileLineData = new ArrayList<mdLineObject>();
	ArrayList<mdBlock> blocklist = new ArrayList<mdBlock>();
	ArrayList<Book> booklist = new ArrayList<Book>();
	String fileExt;

//constructor
public mdFile(){
	String docString;
	//ArrayList
}

public void openMD(File file){
      setDocString(getFileText(file));
      checkExtensions(file);
      if (getExtension().equals("rmd")) {
      	makeBlocklistRMD();
      }
      if (getExtension().equals("md")) {
      	makeBlocklist();
      }
      //makeBooksFromBlocklist(); //handled externally
      //process internal contents of books to distinguish notes etc.  Use LinesIndex.
}

public Boolean checkExtensions(File file){
    String thefilename=file.getName();
    String ext1=thefilename.substring(thefilename.length() - 3);
    String ext2=thefilename.substring(thefilename.length() - 4);
    String ext3=thefilename.substring(thefilename.length() - 5);
    
    if (ext1.equals(".md")==true) {
      setExtension("md");
      return true;
    }

    else if (ext2.equals(".rmd")==true || ext2.equals(".Rmd")==true){
      setExtension("rmd");
      return true;
    }

     else if (ext3.equals(".docx")==true){
      setExtension("docx");
      return true;
    }
    else {
      return false;
    }
}

public void setExtension(String input){
	this.fileExt=input;
}

public String getExtension(){
	return this.fileExt;
}

//Simple utility to return contents of file as String
public String getFileText(File myFile) {
  StringBuffer myText = new StringBuffer(); //mutable String
  String endOfLine="\n"; //to do - operating system independent
  try {
    Scanner scanner1 = new Scanner(myFile);
    if (scanner1==null) {
      System.out.println("No text/html content");
      return null;
    }
    int nl=0;
    while (scanner1.hasNextLine()) {
      nl++;
      String thisRow=scanner1.nextLine();
      System.out.println(thisRow);
      myText.append(thisRow);
      myText.append(endOfLine);
    }
    scanner1.close();
  }
  catch (Throwable t)
  {
    t.printStackTrace();
    //System.exit(0);
    return null;
  }
  //System.out.println(myText);
  //System.exit(0);
  return myText.toString();
}

//File line information (part of the DOM)
private void setFileLineData(ArrayList<mdLineObject>input){
  this.fileLineData=input;
}
public ArrayList<mdLineObject>getFileLineData(){
  return this.fileLineData;
}

// -- Unzipped Strings

public void setDocString(String input) {
	this.docString = input;
}

public String getDocString() {
	return this.docString;
}

// - BLOCKS/BOOKS API

public void setBlocklist(ArrayList<mdBlock> input) {
    this.blocklist = input;
}

public ArrayList<mdBlock> getBlocklist() {
    return this.blocklist;
}

public void setBooklist(ArrayList<Book> input) {
    this.booklist = input;
}

public ArrayList<Book> getBooklist() {
    return this.booklist;
}

// --- line processing for main API
//The setMDlinecode() is '0' or '1' - it doesn't code for comments.

public void codeMDLines(ArrayList<mdLineObject>myLines) {
	for (mdLineObject lineItem : myLines) {
		lineItem.setMDLineCode();
	} 
	//return fileindex;
}

//blocks were originally just an array of Strings split on # 
//old method name: splitMDfile
//TO DO: create ArrayList to hold the 'blocks' into which this file has been partitioned.

/*
public void makeBlocklist() {
	ArrayList<String>myLines = getFileLines(getDocString());
	ArrayList<Integer> fileindex = codeMDLinesForNotes(myLines);
	//System.out.println(fileindex.toString());
	fileindex = testprocess(fileindex);
	Parser myParser=new Parser();
	//System.out.println(fileindex.toString());
	//System.exit(0);
	ArrayList<String> newblocks = myParser.packageBlocksFromLines (myLines, fileindex);
	System.out.println(newblocks.toString());
	System.out.println("Block count in mdFile:"+newblocks.size());
	setBlocklist(newblocks);
}
*/

public void makeBlocklist() {
	this.fileLineData = getFileLines(getDocString());
	this.fileLineData = codeMDLinesForNotes(this.fileLineData); //updates contents of line objects (codes)?
	Parser myParser=new Parser();
	ArrayList<mdBlock> newblocks = packageBlocksFromLineObjects();
	setBlocklist(newblocks);
}

/*
Parsing algo:
1. read file
2. split into heading blocks (make note of whether #- which is 'not visible state')
3. split each block further into an individual Book which has:
(a) the label from the same row as the # heading
(b) main markdown section (up to next block).  This inclues single line markdown.
(c) any multi-line notes (using ``` code or /* comments markup)
4. Store Blocks in an array and return...

old method name:splitRMDfile

*/

public void makeBlocklistRMD() {
	 this.fileLineData = getFileLines(getDocString());
  this.fileLineData = codeMDLinesForNotes(this.fileLineData); //updates contents of line objects (codes)?
  Parser myParser=new Parser();
  ArrayList<mdBlock> newblocks = packageBlocksFromLineObjects();
  setBlocklist(newblocks);
}


/*
Test whether we can subdivide blocks further for notes etc
Input is index filled with 1,0,3,6,3 codes for each line.
This function toggles a '1' code, appearing after end of notes, as '0' (new block start)
*/
public ArrayList<Integer> testprocess(ArrayList<Integer> myIndex){
	for (int x=0;x<myIndex.size();x++) {
		Integer item = myIndex.get(x);
		Integer item2;
		if (x==0 && item==1){
			myIndex.set(x,0); //set it to 0 if first line
		}
		//if there is a non-H1 line after notes, create new block for the '3' item
		if ((x+1)<myIndex.size()) {
			item2=myIndex.get(x+1);
			if (item==3 && item2==1) {
				myIndex.set(x+1,0); 
			}
		}

	}
	return myIndex;
}

/* 
Function below returns the lines in a markdown file as Array of line objects.
*/

public ArrayList<mdLineObject> getFileLines(String input){
	ArrayList<mdLineObject>myLines = new ArrayList<mdLineObject>();
	Scanner scanner1 = new Scanner(input); //default delimiter is EOL?
	int linecount=0;
	try {
			
			if (scanner1==null) {
				System.out.println("No MD block content");
				return null;
			}

	while (scanner1.hasNextLine()) {
				//Array currentNotes[] = new Array(2);
				String thisRow=scanner1.nextLine();
				mdLineObject myObject=new mdLineObject();
				myObject.setLineText(thisRow);
				myObject.setLineIndex(linecount);
				myObject.setMDLineCode(); //basic 0 or 1 test for now
				myLines.add(myObject);
				linecount++;
		} 
	}//end try
	catch (Throwable t)
			{
				t.printStackTrace();
				//System.exit(0);
				return null;
			}
			scanner1.close();
	return myLines;	
}

/* 

This function helps divide up line index codes in RMD file so that they are 0 for main dividers, and then 5 or 6 for notes sections.
In this way, the file can be divided up into sections (based on 0 codes), but retains the 'notes' information for future internal structure.


A complication of rmd files is that a # inside ``` is a comment, not a markdown tag!
To do: map as FSM and see if the logic can be coded that way.

*/
public ArrayList<mdLineObject> codeMDLinesForNotes(ArrayList<mdLineObject> myLines) {
	//codeMDLines(myLines);
	int header = 0;
	int ntest=0;
	
	int linecount=0;
	int blocktype=1;

	//fileindex=codeMDLines(myLines); //simple 0 for headings, otherwise 1. (some will be inside notes) 

	for (mdLineObject lineItem : myLines) {
    //blocktype=1;
    //lineItem.setLineCode(blocktype);
		String linePrefix="";
		String lineSuffix="";
		String thisRow = lineItem.getLineText();
		//classify as 0 or 1 based on 'heading', for 'split'
		blocktype = lineItem.getLineCode(); //Sets linecode. default is 1 unless '# '
		//System.out.println(linecount+")"+thisRow+"["+bcode+"]");  
		//distinguish notes sections first, then check on the # outside this
        String codetest="```";
        //int oldcode=fileindex.get(linecount);
        //System.out.println(linecount+","+oldcode);
       int rowlength=thisRow.length();
       if (rowlength>=3) {
			linePrefix = thisRow.substring(0,3);
			if (rowlength>linePrefix.length()) {
				lineSuffix = thisRow.substring(thisRow.length()-3,thisRow.length());
			}
		} 
		else {linePrefix=thisRow;lineSuffix="";}
    //Overrule basic block codes (0,1) if there is a notes section
		//System.out.println("pre:"+linePrefix+", suf:"+lineSuffix);
        ntest = getRMDblockstate(rowlength,linePrefix,lineSuffix,ntest,codetest,codetest);  
        switch (ntest) {
        	case 0:
        		//blocktype=0; //just reset.  No action.
        		break; //default . no change to blockcodes from MD
        	case 1:
        		blocktype=3; //don't capture (later: capture after the prefix on firstline).
        		break;
        	case 2:
        		blocktype=6; //capture the inside line
        		break;
        	case 3:
        		blocktype=3;  //don't capture. end of code
        		break; 	
        	case 5:
        		blocktype=5;  //Same line, so reset it here
        		break;
        	default:
        		//no change
        		break;
        }  
        if (ntest>0){ //blocktype==3 || blocktype == 5 || blocktype==6
        	lineItem.setLineCode(blocktype);
        	//fileindex.set(linecount,blocktype);
        	//System.out.println(linecount+")"+thisRow+"["+blocktype+"]");  
        }
        //linecount should equal lineItem.getLineIndex()
        System.out.println(lineItem.getLineIndex()+")"+thisRow+" ntest:"+ntest+" blocktype:"+blocktype);  
		
       
		linecount++;
	}
	//TO DO: Extend this idea to headers, with the '---' as the search code, and case 2 : blocktype = 4
	
	return myLines;
}

/*
Stream processor: iteratively processes current line plus 'state' of a testcode (e.g. "---")
[model as FSM, or equivalent to regex]
Cycles through states (#) 0-->1(w) -->2 ::::> (#)2-->3(w)-->0
This could possibly be generalised to detect first/second tag for any open/close pair

it works with case statements only if you have hardcoded test cases. 
*/

//START HERE TO DO: ensure it doesn't capture ``` line and then captures inside as 6
//CUrrently capturing '6' i.e. out=4 for the first line?
 public int getRMDblockstate(int rowlength,String prefixString, String suffixString,int hc, String testcode, String endcode) {
        //for R markdown, we check any time we find dashes, but only deal with first line
        int out=hc;
        if (prefixString.equals(testcode) && hc==0) {
			out=1;
		}	//detected "---" start of coded section	
		if (prefixString.equals(endcode) && hc==2) {
			out=3;
		}	 //detected "---" start of line, but end of coded section
		if (suffixString.equals(endcode) && out==1) {
			out=5; //detected "---" endcode on same line;
		}
        if (hc==1) {
        	out=2; //detect string/words (w) shift to inside coded section
        }
        if (hc==3 || hc==5) {
        	out=0; //detect string/words (w) shift back to default.  
        }

    return out;
}

/*
public void makeBooksFromBlocklist(){
      ArrayList<Book> myBookList = new ArrayList<Book>();
      ArrayList<mdBlock> blocks = getBlocklist();
      int length = blocks.size();  // number of blocks
      Parser myParser=new Parser();
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int rowcount=0;
      if (length>0) {
        Iterator<mdBlock> iter = blocks.iterator(); 
          while (iter.hasNext()) {
          	 //simple book creation
          	//default String in constructor sets markdown, ooxml text
          	mdBlock myBlock = iter.next();
            Book newBook =new Book(myBlock); //TO DO:add Event Handlers separately
            
            //add book to list
      		myBookList.add(newBook);
      		rowcount++;
         } //end while
      } //end if       	
      else {
        System.out.println("Nothing returned from parser");
      }
    setBooklist(myBookList);
    //System.out.println("Make books done with content");
    //System.exit(0);
    }
*/

public ArrayList<mdBlock>packageBlocksFromLineObjects() {
	ArrayList<mdBlock> output = new ArrayList<mdBlock>();
	mdBlock currentblock = new mdBlock();
 // in any case start a new block, or start first
  currentblock.setHeaderText("First Page");
	//System.out.println(myLines.getLineCode()+") "+my)
	String headertext="";
	int cl=0;
	for (mdLineObject lineItem: this.fileLineData) {
		int linecode=lineItem.getLineCode();
    int testnum=0;
		//if this row is a heading it will have [0] next to it
		System.out.println(cl+") ["+linecode+"] "+lineItem.getLineText());

		//SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES
		if (linecode==0) { //if we encounter start of next block (#)
			//first line
				if (currentblock.getStoredLines()>0) {
					output.add(currentblock); //add the current block to newBlocks array
					currentblock = new mdBlock(); //start again with a new block
          currentblock.addLineObject(lineItem); // in any case start a new block, or start first
          currentblock.setHeaderText(lineItem.getHeaderText());
				}
        else {
          currentblock.addLineObject(lineItem); // in any case start a new block, or start first
          currentblock.setHeaderText(lineItem.getHeaderText());
        }
				
        testnum++;
		}
		//for linecode > 0, add it (could ignore lines with other codes, or pre-process, but don't do it for now)
		if (linecode>0) { 
				currentblock.addLineObject(lineItem);
		}
		//do not store header for .rmd in blocks, but capture for later
		if (linecode==4) { //for rmd at present
			headertext=headertext+lineItem.getLineText()+System.getProperty("line.separator"); 
    	}
		cl++; //increase line count for index	
	}   //end loop

	//add last block or we drop 1 each time
	if (currentblock.getStoredLines()>0) {
		output.add(currentblock); //add the current block to newBlocks array
	}

	if (headertext.length()>0) {
		//projectCopy.setHeader(headertext); //store for later;
	}
	//System.exit(0);
	return output;
}
}

/*
public void makeBooksFromStringBlocklist(){
      ArrayList<Book> myBookList = new ArrayList<Book>();
      ArrayList<String> blocks = getBlocklist();
      Parser myParser=new Parser();
      int headingcount=0;
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int length = this.blocklist.size();  // number of blocks
      System.out.println(length); //each of these numbered blocks is a string.
      int rowcount=0;
      if (length>0) {
        Iterator<String> iter = blocks.iterator(); 
          while (iter.hasNext()) {
          	 //simple book creation
             Book newBook =new Book(iter.next()); //TO DO:add Event Handlers separately
      myBookList.add(newBook);
      rowcount++;
         } //end while
      } //end if       	
      else {
        System.out.println("Nothing returned from parser");
      }
    setBooklist(myBookList);
    }
*/    

