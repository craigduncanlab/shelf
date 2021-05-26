//(c) Craig Duncan 2020
//A class to parse markdown files for this application (input and output)
import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*; //scanner, HashMap etc
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class Parser {
EventHandler PressBox;
EventHandler DragBox;
MainStage mainstage;
HashMap<String,String> myHM;
Project projectCopy;

//default constructor
public Parser(){
	this.projectCopy=new Project();
}

//constructor with myProject object passed in
public Parser(Project inputProject){
	this.projectCopy=inputProject;
}

//parse the Word styles markdown file (key line starts with #, then the paragraph)
public HashMap<String,String> readWordStyles() {
	System.out.println("RWS");
	HashMap<String,String> styleMap=new HashMap();
	String myKey="";
	String myValue="";
	File codeFile=new File ("wordlib/styledict.md");
	 try {
    Scanner scanner1 = new Scanner(codeFile);
    if (scanner1==null) {
      	System.out.println("No content");
     	 return null;
	}
    int nl=0;
    while (scanner1.hasNextLine()) {
			String line1 = scanner1.nextLine();
			String line2 = scanner1.nextLine();
			myKey=line1.substring(2,line1.length());
			myValue=line2;
			System.out.println("Key: "+myKey);
			System.out.println("Value: "+myValue);
			styleMap.put(myKey,myValue);
	      	//
		} //end while
	} //end try
	catch (Exception e) {
		//
	}
	//System.out.println(styleMap.toString());
	//System.exit(0);
	return styleMap;
}


/*
Context: Called externally by MainStage class instances.

Input:
The block of input text is always a split part of .md or .rmd file with no more than one hash (#) section 
(which should also be in first line)

Output: 
could return an Array of Books, not one.
A new Book object is used for data model and GUI.

TO DO: separate all GUI event handles from this (it is data only)
*/

public Book parseMDfile(MainStage myStage, EventHandler pb,EventHandler db,String contents) {
	this.PressBox=pb;
	this.DragBox=db;
	this.mainstage=myStage;
    //System.out.println("Begin parsing MD file");
    // for now, no processing of contents
    //Book newNode = new Book("Test",contents,"notes");
    Book newBook = parseMDblock(contents);
    //System.out.println("Finished parsing MD file");
    return newBook;
}

/*
Context: Called externally by MainStage class instances.

Input:
The block of input text is always a split part of .md or .rmd file with no more than one hash (#) section 
(which should also be in first line)

Output: 
could return an Array of Books, not one.
A new Book object is used for data model and GUI.

TO DO: avoid duplication with the other code related to data model; separate out the GUI differences
*/

public Book parseMDfileAsRow(MainStage myStage, EventHandler pb,EventHandler db,String contents) {
	this.PressBox=pb; //are these global eventhandlers needed here anymore?
	this.DragBox=db;
	this.mainstage=myStage;
    //System.out.println("Begin parsing MD file");
    // for now, no processing of contents
    //Book newNode = new Book("Test",contents,"notes");
    Book newBook = parseMDblock(contents);
    //set position to 0,0 so that it will be processed as if a file without coordinates nb 'positionBookOnStage' checks Y values only
    newBook.setXY(0,0);
    newBook.setRow(0);
    newBook.setCol(0);
    newBook.setLayer(1);//needed?  or take in required layer as input to function?
    //System.out.println("Finished parsing MD file as Row");
    return newBook;
}

/*
Parsing algo:
1. read file
2. split into heading blocks (make note of whether #- which is 'not visible state')
3. split each block further into an individual Book which has:
(a) the label from the same row as the # heading
(b) main markdown section (up to next block).  This inclues single line markdown.
(c) any multi-line notes (using ``` code or /* comments markup)
4. Store Books in an array and return...

*/

/*
Function to read in a Word docx, sliced as if it were a markdown file...
However, OOXML files do not have EOL characters,   The <w:p> tags are the principal dividor of text stream.
Why?  XML defines these 'objects' in the file stream with tags alone.

The input:
Text of the document.xml (or similar)
Output:
Blocks that are not 'lines' with EOL as they are in markdown files , but an array of the <w:p> enclosed tags
that appear in document.xml files.

*/

//called externally to split up an MD file into separate blocks
//blocks were originally just an array of Strings split on # 

public ArrayList<String> splitMDfile(String input) {
	ArrayList<String>myLines = getFileLines(input);
	ArrayList<Integer> fileindex = codeMDLines(myLines);
	ArrayList<String> newblocks = packageBlocksFromLines (myLines, fileindex);
	return newblocks;
}

public ArrayList<String> splitRMDfile(String input) {
	ArrayList<String>myLines = getFileLines(input);
	ArrayList<Integer> fileindex = codeRMDLines(myLines);
	ArrayList<String> newblocks = packageBlocksFromLines (myLines, fileindex);
	return newblocks;
}

/* 
Function below returns lines in a markdown file.

For OOXML there may not by end of lines like normal text files, 
so to 'partition' the file into lines, we would have to split it based on suitable OOXML tags
However, the goal of this s to be able to test individual units of text that might contain a heading.
So ultimately, we want to split it on <w: or <w:p or similar as I've done in python before
*/

public ArrayList<String> getFileLines(String input){
	ArrayList<String>myLines = new ArrayList<String>();
	Scanner scanner1 = new Scanner(input); //default delimiter is EOL?
	try {
			
			if (scanner1==null) {
				System.out.println("No MD block content");
				return null;
			}

	while (scanner1.hasNextLine()) {
				//Array currentNotes[] = new Array(2);
				String thisRow=scanner1.nextLine();
				myLines.add(thisRow);
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

public ArrayList<Integer> codeMDLines(ArrayList<String>myLines) {
	ArrayList<Integer> fileindex = new ArrayList<Integer>();
	
	for (String thisRow : myLines) {
		int bcode=setMDLineCode(thisRow);
		fileindex.add(bcode);
	} 
	return fileindex;
}

/* 

This function helps divide up line index codes in RMD file so that they are 0 for main dividers, and then 5 or 6 for notes sections.
In this way, the file can be divided up into sections (based on 0 codes), but retains the 'notes' information for future internal structure.


A complication of rmd files is that a # inside ``` is a comment, not a markdown tag!
To do: map as FSM and see if the logic can be coded that way.

*/
public ArrayList<Integer> codeRMDLines(ArrayList<String> myLines) {
	ArrayList<Integer> fileindex = codeMDLines(myLines);
	int header = 0;
	int ntest=0;
	
	int linecount=0;
	int blocktype=0;

	//fileindex=codeMDLines(myLines); //simple 0 for headings, otherwise 1. (some will be inside notes) 

	for (String thisRow : myLines) {
		String linePrefix="";
		String lineSuffix="";
		//classify as 0 or 1 based on 'heading', for 'split'
		int bcode = fileindex.get(linecount);
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
		//System.out.println("pre:"+linePrefix+", suf:"+lineSuffix);
        ntest = getRMDblockstate(rowlength,linePrefix,lineSuffix,ntest,codetest,codetest);  
        switch (ntest) {
        	case 0:
        		blocktype=0; //just reset.  No action.
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
        	fileindex.set(linecount,blocktype);
        	//System.out.println(linecount+")"+thisRow+"["+blocktype+"]");  
        }
        System.out.println(linecount+")"+thisRow+"["+fileindex.get(linecount)+"] ntest:"+ntest+" blocktype:"+blocktype);  
		
       
		linecount++;
	}
	//TO DO: Extend this idea to headers, with the '---' as the search code, and case 2 : blocktype = 4
	//System.exit(0);
	return fileindex;
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

 public int getRMDcode(String inputString, String suffixString,int hc, String testcode, String endcode) {
        //for R markdown, we check any time we find dashes, but only deal with first line
        int out=hc;
        if (inputString.equals(testcode) && hc==0) {
			out=1;
		}	//detected "---" start of coded section	
		if (suffixString.equals(endcode) && hc==0) {
			out=5; //detected "---" endcode on same line;
		}
		if (inputString.equals(endcode) && hc==2) {
			out=3;
		}	 //detected "---" end of coded section
        if (hc==1) {
        	out=2; //detect string/words (w) shift to inside coded section
        }
        if (hc==3) {
        	out=0; //detect string/words (w) shift back to default.  
        }

    return out;
}

//set a linecode for this line based on a few prefix characters
//line code distinguishes between division in stream (#) or RMD headers

public int setMDLineCode(String thisRow) {
		int blocktype=1; //default, even if row length small
		//if row is long enough to check for hash prefix; if row is not already inside a notes section
		if (thisRow.length()>=2) {
			if (thisRow.substring(0,2).equals("# ")) {
				return 0;
			}
		}
     return blocktype;
 }

/*

Set a linecode for this line based on relevant tag in OOXML
binary distinction here between H1 level and everything else.

The H1string refers to 'ilvl' tag, which is based on the 'indent level' 
Indent level is Word's way of encoding Heading Styles if one has been applied.
i.e so we used that convention as an analogue of the # division in markdown.
Note: ilvl may be a deprecated .doc convention.
The setting of a w:pStyle w:val="Heading1" with custom name may be only indication in document.xml
In latest (2021) versions, in order to determine if this is H1, we also need to inspect numbering.xml?

*/

public int setOOXMLLineCode(String thisRow) {
		int blocktype=1; //default, even if row length small
		//String H1string="<w:ilvl w:val=\"0\"/>";
		String H1string="<w:outlineLvl w:val=\"0\"/>"; //w:outlineLvl w:val="0"
		//if row is long enough to check for hash prefix; if row is not already inside a notes section
		if (thisRow.length()>=2) {
			if (thisRow.contains(H1string)) {
				return 0;
			}
			/*
			if (thisRow.substring(0,2).equals("# ")) {
				return 0;
			}
			*/
		}
     return blocktype;
 }

/* 
Function: creation of new array of block strings based on '0 in block index
(this is Outline Lvl 1 for Word, or # portions of original .md or .rmd files

TO DO:
This is not using the benefit of the prior scanning i.e. fileindex codes by line and array entries
In the 'myLines' array we have text.  
In fileindex array : '0's for # lines and '1's for other lines.
Also '3' for start of R markdown header and '4' for end of R markdown header
Also '5' and '6' for notes sections.

We could further code the stream (new index, or with further coding)
One or more passes to code sections with consecutive index codes that then become 'sub-blocks'
i.e. improve passing on of information to parseMDblock();
To do: store in a Book data object and refine later.
*/ 

public ArrayList<String> packageBlocksFromLines(ArrayList<String> myLines, ArrayList<Integer> fileindex) {
	ArrayList<String>newBlocks = new ArrayList<String>();
	StringBuffer currentblock = new StringBuffer();
	
	String headertext="";
	int cl=0;
	for (String thisLine: myLines) {
		int linecode=fileindex.get(cl);
		//if this row is a heading
		//System.out.println(cl+") ["+fileindex.get(cl)+"] "+thisLine);

		//SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES
		if (linecode==0) { //if we encounter start of next block (#)
			//first line
				if (currentblock.length()>0) {
					newBlocks.add(currentblock.toString()); //add the current block to newBlocks array
					currentblock.delete(0,currentblock.length()); //delete current string buffer contents
				}
				currentblock.append(thisLine); // in any case start a new block, or start first
				currentblock.append(System.getProperty("line.separator"));
		}
		//for linecode > 0, add it (could ignore lines with other codes, or pre-process, but don't do it for now)
		if (linecode>0) { 
				currentblock.append(thisLine);
				currentblock.append(System.getProperty("line.separator"));//EOL
		}
		//do not store header for .rmd in blocks, but capture for later
		if (linecode==4) { //for rmd at present
			headertext=headertext+thisLine+System.getProperty("line.separator"); 
    	}
		cl++; //increase line count for index	
	}   //end loop

	//add last block or we drop 1 each time
	if (currentblock.length()>0) {
		newBlocks.add(currentblock.toString()); //add the current block to newBlocks array
		currentblock.delete(0,currentblock.length()); //delete current string buffer contents
	}

	if (headertext.length()>0) {
		projectCopy.setHeader(headertext); //store for later;
	}
	
	return newBlocks;
}

/* 
Called by parseMDfile() to prepare new index of internal structure of String, line by line, 
Input : Takes an unprocessed String as input (one that has been constructed from those arrays).
The string has already been 'split' from a .md file into sections.
Output: creates a new 'Book' object (the one used for GUI etc) with internal structure from index.

TO DO:
This is currently inefficient because the splitting before input created its own index and arrays.
Past processing is not retained.  As a computing machine, energy is wasted.  *Software as energy paths*
Create an RMD parser that will separate out the notes from the fileindex codes.

Pass any GUI-specific information (i.e. not part of base RMD) as a separate step.
e.g X,Y coordinates etc.  This is part of 'symbolic' or 'structural' markdown.

*/

public Book parseMDblock(String input) {
	ArrayList<Integer> fileindex = makeMDfileindex(input);
	Book newNode = MDfileFilter(fileindex,input);
	return newNode;
}

//SECOND STAGE PARSING (OF EACH BLOCK)
//TO DO: STREAMLINE INTO INPUT PROCESS AS PER INITIAL BLOCKS CONSTRUCTION
//I.E DO SCANNER ONLY ONCE
//make an index (like an R vector) to determine nature of each line of markdown file

/*
In effect, this is looking for annotations that are additional to a normal markdown file.
Reading in a normal .Rmd or .md file and creating a simple index to define 'blocks' with heading does not require as many steps as here.
*/

public ArrayList<Integer> makeMDfileindex(String input) {
		ArrayList<Integer> fileindex = new ArrayList<Integer>();
		
		Boolean visibility=true; //TO DO: fix visibility state after index prepared?
		Boolean dateLine=false;
		Boolean layerLine=false;
		Boolean timeLine=false;
		Boolean codeLine=false;
		Boolean urlLine=false;
		Boolean filepathLine=false;
		Boolean imagepathLine=false; 
		Boolean singlecodeline=false;
		Boolean coord=false;
		Boolean coord3D=false;
		Boolean grid=false;
		Boolean grid3D=false;
		String label = "";
		int nl=0;
		try {
			Scanner scanner1 = new Scanner(input); //default delimiter is EOL?
			if (scanner1==null) {
				System.out.println("No MD block content");
				return null;
			}
			
			int brackets=0;
			while (scanner1.hasNextLine()) {
				codeLine=false;
				singlecodeline = false;
				dateLine=false;
				layerLine=false;
				timeLine=false;
				imagepathLine=false;
				filepathLine=false;
				urlLine=false;
				coord=false;
				grid=false;
				grid3D=false;
				coord3D=false;
				//Array currentNotes[] = new Array(2);
				String thisRow=scanner1.nextLine();
				//Scanner scanner2= new Scanner(thisRow).useDelimiter("#");
				
				System.out.println(nl+") "+thisRow);
				
				//short row
				if (thisRow.length()<2) {
					if (brackets==0) {
						fileindex.add(1);
					}
					else {
						fileindex.add(2); //notes
					}
				}
				//long row
				else {
					String firstpart = thisRow.substring(0,2);
					Boolean singlelinenote = thisRow.contains("*/");
					if (thisRow.length()>5) {
						String endpart = thisRow.substring(3,thisRow.length());
						singlecodeline = endpart.contains("```");
					}
					if (thisRow.length()>2) {
						String visFlag = thisRow.substring(0,3);
						if (visFlag.equals("# -")) {
		            		visibility=false;
		            	}
		            	if (visFlag.equals("```")) {
		            		codeLine=true;
		            	}
		            }
		            if (thisRow.length()>5) {
		            	String prefix=thisRow.substring(0,6);
		            	String check=thisRow.substring(0,5);
		            	System.out.println(thisRow);
		            	if (prefix.equals("[url](")){
		            		urlLine=true;
		            		System.out.println(prefix);
		            		
		            	} 
		            	if (prefix.equals("[img](")){
		            		imagepathLine=true;
		            		System.out.println(prefix);
		            		
		            	}
		            	else if (prefix.equals("[x,y](")) {
		            		coord=true;
		            		System.out.println(prefix);
		            		
		            	}
		            	else if (prefix.equals("[r,c](")) {
		            		grid=true;
		            		System.out.println(prefix);
		            		
		            	}
		            	
		            }
		            if (thisRow.length()>6) {
		            	String prefix=thisRow.substring(0,7);
		            	if (prefix.equals("[date](")){
		            		dateLine=true;
		            	} 
		            	if (prefix.equals("[time](")){
		            		timeLine=true;
		            	} 
		            }

		            if (thisRow.length()>7) {
		            	String prefix=thisRow.substring(0,8);
		            	if (prefix.equals("[layer](")){
		            		layerLine=true;
		            	} 
		            	//[r,c,l]
		            	else if (prefix.equals("[r,c,l](")) {
		            		grid3D=true;
		            		System.out.println(prefix);
		            		
		            	}
		            	else if (prefix.equals("[x,y,z](")) {
		            		coord3D=true;
		            		System.out.println(prefix);
		            		
		            	}
		            }
		            if (thisRow.length()>11) {
		            	String prefix=thisRow.substring(0,11);
		            	if (prefix.equals("[filepath](")){
		            		filepathLine=true;
		            	} 
		            }
					System.out.println(nl+") "+firstpart);
					//this should find first line in file
					switch (firstpart) {
	            		case "# ":  
		            		if (brackets==0) {
		            			fileindex.add(0);
		            			label=thisRow; //not actually used here.  see MDfilefilter
		            		}
		            		else if (brackets==1) {
			                    	fileindex.add(2); //notes
			                    }
		                    break;
	                    case "//":  
		                    if (brackets==0) {
		            			fileindex.add(1); //md
		                    }
		                    else {
		                    	fileindex.add(2); //notes
		                    }
		            		break;
	                    case "``":  
		                    if (brackets==0 && codeLine==true) {
		                    	brackets=1;
		                    	fileindex.add(4); //code notes
		                    }
		                    
	                        else if (brackets==1 && codeLine==true) {
		                    	brackets=0;
		                    	fileindex.add(5); //end code notes
		                    }

		                    else if (brackets==0 && singlecodeline==true) {
		                    	brackets=0;
		                    	fileindex.add(5); //end code notes
		                    }

		                    else if (codeLine==false) {
		                    	fileindex.add(1); //md
		                    }
		                    break;

		                case "/*":  
		                    if (brackets==0 && singlelinenote==false) {
		                    	brackets=1;
		                    }
		                    fileindex.add(2); //notes
		                    break;
	                    case "*/":  
		                    if (brackets==1) {
		                    	brackets=0;
		                    	fileindex.add(3); //notes
		                    }
		                    else {
		                    	fileindex.add(1); //md
		                    }
		                    break;
	                    default:  
	                    	if (brackets==1) {
	                    		fileindex.add(2); //notes
	                    	}
	                    	else if(filepathLine==true) {
	                    		fileindex.add(6); //6=filepath
	                    	}
	                    	else if(urlLine==true) {
	                    		fileindex.add(7); //7=url path
	                    	}
	                    	else if(imagepathLine==true) {
	                    		fileindex.add(10); //10=img path
	                    	}
	                    	else if(coord==true){
	                    		fileindex.add(8);//8=coordinates
	                    	}
	                    	else if(grid==true){
	                    		fileindex.add(9);//9=grid coord
	                    	}
	                    	else if(dateLine==true) {
	                    		fileindex.add(11); //11=date
	                    	}
	                    	else if(timeLine==true) {
	                    		fileindex.add(12); //12=time
	                    	}
	                    	else if(layerLine==true) {
	                    		fileindex.add(13); //13=layer
	                    	}
	                    	else if(grid3D==true) {
	                    		fileindex.add(14); //14=row,col,layer coords
	                    	}
	                    	else if(coord3D==true) {
	                    		fileindex.add(15); ///15=[x,y,z] coords
	                    	}
	                    	else {
	                    		if (thisRow.length()>0) {
	                    			fileindex.add(1); //md
	                    		} 
	                    		else {
	                    			fileindex.add(20); //nothing
	                    		}
	                    	}
	                    	break;
	                } //end switch
				} //end else 
				
				int max = fileindex.size();
				if (nl>max) {
					System.out.println("nl: "+nl+" max: "+max);
					System.exit(0);
				}
				nl++;
			} //end while
			scanner1.close();			
			} //end try
			catch (Throwable t)
			{
				t.printStackTrace();
				//System.exit(0);
				return null;
			}
			System.out.println(fileindex.toString());
			int max = fileindex.size();
			if (max!=nl) {
				System.out.println("Max : "+max+" nl: "+nl);
			}
			//System.exit(0);
			return fileindex;
		} // end class


//process/filter a markdown file using the prepared index of line content types

/* Input: 
a fileindex for the String, for itnernal structure.
The String itself

TO DO:
This is currently processing both regular markdown, as well as custom tags like [x,y] for the GUI.
Try and prepare separate steps.
The main benefit for markdown processing is to distinguish between notes and other text, 
which is already done in the earlier steps of preparing the file index.


*/

public Book MDfileFilter(ArrayList<Integer> fileindex,String input) {
		StringBuffer mdStream = new StringBuffer();
		StringBuffer notesStream = new StringBuffer();
		StringBuffer codenotesStream = new StringBuffer();
		Integer nl;
		String label="";
		String urlString="";
		String filepathString="";
		String imagepathString="";
		String dateString="";
		String timeString="";
		double x=0.0;
		double y=0.0;
		double z=0.0;
		Integer row=0;
		Integer col=0;
		Integer layer=1;
		try {
				Scanner scanner2 = new Scanner(input); //default delimiter is EOL?
				if (scanner2==null) {
					System.out.println("No MD block content");
					return null;
				}
			
			nl=0;
			//Integer max = 0; //this should be length of lines.
			while (scanner2.hasNextLine()) {
				String thisLine=scanner2.nextLine();
				System.out.println("Line: "+thisLine);
				System.out.println("File index:"+fileindex.get(nl));
				
				//heading or label. Do not include it in markdown
				if (fileindex.get(nl)==0){
					label=thisLine; //limit book label to first "# " + 10 characters
				}
				//markdown
				if (fileindex.get(nl)==1) {
					//all lines get a separator afterwards
						mdStream.append(thisLine);
						//mdStream.append("\n"); //EOL
						mdStream.append(System.getProperty("line.separator"));
					
				}
				//notes line
				if (fileindex.get(nl)==2 || fileindex.get(nl)==3) {
					String replacement = thisLine.replace("/*","");
					String replacement2 = replacement.replace("```","");
					String replacement3 = replacement2.replace("*/","");
					if (replacement3.length()>=0) {
						notesStream.append(replacement3);
						//notesStream.append("\n"); //EOL
						notesStream.append(System.getProperty("line.separator"));
					}
				}

				//code notes line
				if (fileindex.get(nl)==4 || fileindex.get(nl)==5) {
					String replacement2 = thisLine.replace("```","");
					if (replacement2.length()>=0) {
						codenotesStream.append(replacement2);
						//codenotesStream.append("\n"); //EOL
						codenotesStream.append(System.getProperty("line.separator"));
					}
				}
				if(fileindex.get(nl)==5) {

				}
				//file path line
				if(fileindex.get(nl)==6) {
					String suffix=thisLine.substring(11,thisLine.length());
		            filepathString=suffix.replace(")","");
		            System.out.println(filepathString);
				}
				//url index line
				if(fileindex.get(nl)==7) {
					String suffix=thisLine.substring(6,thisLine.length());
		            urlString=suffix.replace(")","");
		            System.out.println(urlString);
				}
				//coordinates line (old)
				if(fileindex.get(nl)==8) {
				  String restart=thisLine.replace("[x,y](","");
				  String restart2=restart.replace(")","");
				  String restart3=restart2.replace("]",",");
				  Scanner scanner3= new Scanner(restart3).useDelimiter(",");
				  String xcoord=scanner3.next();
				  String ycoord=scanner3.next();
				  System.out.println(xcoord+","+ycoord);
				  x = Double.parseDouble(xcoord);
				  y = Double.parseDouble(ycoord);
				  System.out.println(x+","+y);
				  if (x<0) {
				  	x=0.0;
				  }
				  if(y<0) {
				  	y=0;
				  }
				}
				
				//img line
				if(fileindex.get(nl)==10) {
				  String suffix=thisLine.substring(6,thisLine.length());
		          imagepathString=suffix.replace(")","");
		          System.out.println(imagepathString);
				}
				//grid coordinates line
				if(fileindex.get(nl)==9) {
				  String restart=thisLine.replace("[r,c](","");
				  String restart2=restart.replace(")","");
				  String restart3=restart2.replace("]",",");
				  Scanner scanner3= new Scanner(restart3).useDelimiter(",");
				  String rc=scanner3.next();
				  String cc=scanner3.next();
				  System.out.println(rc+","+cc);
				  row = (int)Double.parseDouble(rc);
				  col = (int)Double.parseDouble(cc);
				  System.out.println(row+","+col);
				  if (row<0) {
				  	row=0;
				  }
				  if(col<0) {
				  	col=0;
				  }
				}
				//grid3D refs
				if(fileindex.get(nl)==14) {
				  String restart=thisLine.replace("[r,c,l](","");
				  String restart2=restart.replace(")","");
				  String restart3=restart2.replace("]",",");
				  Scanner scanner3= new Scanner(restart3).useDelimiter(",");
				  String rc=scanner3.next();
				  String cc=scanner3.next();
				  String lc=scanner3.next();
				  System.out.println(rc+","+cc+","+lc);
				  row = (int)Double.parseDouble(rc);
				  col = (int)Double.parseDouble(cc);
				  layer=(int)Double.parseDouble(lc);
				  System.out.println(row+","+col+","+layer);
				  if (row<0) {
				  	row=0;
				  }
				  if(col<0) {
				  	col=0;
				  }
				  if(layer<0) {
				  	layer=0;
				  }
				}
				//coordinates line (3D)
				if(fileindex.get(nl)==15) {
				  String restart=thisLine.replace("[x,y,z](","");
				  String restart2=restart.replace(")","");
				  String restart3=restart2.replace("]",",");
				  Scanner scanner3= new Scanner(restart3).useDelimiter(",");
				  String xcoord=scanner3.next();
				  String ycoord=scanner3.next();
				  String zcoord=scanner3.next();
				  System.out.println(xcoord+","+ycoord);
				  x = Double.parseDouble(xcoord);
				  y = Double.parseDouble(ycoord);
				  z = Double.parseDouble(zcoord);
				  System.out.println(x+","+y+","+z);
				  if (x<0) {
				  	x=0.0;
				  }
				  if(y<0) {
				  	y=0;
				  }
				  if(z<0) {
				  	z=0;
				  }
				}
				//date line
				if (fileindex.get(nl)==11) {
				  String suffix=thisLine.substring(7,thisLine.length()); //8 = length [date]( + 1
		          dateString=suffix.replace(")","");
		          System.out.println(dateString);
				}
				//time line
				if (fileindex.get(nl)==12) {
				  String suffix=thisLine.substring(7,thisLine.length()); 
		          timeString=suffix.replace(")","");
		          System.out.println(timeString);
				}
				//layer line {REDUNDANT NOW THERE IS R,C,L coordinates}
				if (fileindex.get(nl)==13) {
				  String suffix=thisLine.substring(8,thisLine.length()); 
		          suffix=suffix.replace(")","");
		          //System.out.println(timeString);
		          layer = (int)Double.parseDouble(suffix);
				  System.out.println(row+","+col);
				  if (layer<0) {
				  	layer=0;
				  }
				}

				nl++;
				//advance if ok
				/*
				if (nl<max) {
					nl++;
				}
				*/
				} //end while
				scanner2.close();
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				//System.exit(0);
				return null;
			}
		//finish
		
		String notes = notesStream.toString();
		String contents = mdStream.toString();
		String codes = codenotesStream.toString();
		if (label.length()<1) {
			label="Default"; //for front of box.
		}
		
		String label1=label.replace("# -",""); //remove hash but leave minus sign?
		String label2=label1.replace("# ","");
		//omits in-line coded notes for now
		Book newNode=new Book(this.PressBox,this.DragBox,label2,contents,notes); //constructor: make new meta data with label of book
		newNode.seturlpath(urlString);
		newNode.setdocfilepath(filepathString);//filepath,urlpath,
		newNode.setimagefilepath(imagepathString);
		newNode.setdate(dateString);
		newNode.settime(timeString);
		newNode.setRow(row);
		newNode.setCol(col);
		newNode.setLayer(layer);
		newNode.setXY(x,y); //x,y  must be doubles	
		newNode.setXYZ(x,y,z);//x,y,z must be doubles
		this.mainstage.snapYtoShelf(newNode,y); //check y and set shelf number
		//At present visibility reflects the last markdown # code detected in file.
		newNode.setVisible(true);
		//convert contents to html for initial 'preview'
		newNode.updateHTML();
		//could do this in method that receives below but do it here for now
		return newNode;
		} //end method


//Convert the MD section of current Book to styled Word paragraphs	
public String getOOXMLfromContents(Book myBook) {
	String input = myBook.getMD();
	String label = myBook.getLabel();
	String dtstring=myBook.getdate();
	String tmstring=myBook.gettime();
	String headingstring = "";
	if (dtstring.length()>0 && tmstring.length()>0) {
		headingstring=dtstring+","+tmstring+" : "+label;
	}
	else if (dtstring.length()>0 && tmstring.length()==0) {
		headingstring=dtstring+" : "+label;
	}
	else if (dtstring.length()==0 && tmstring.length()>0) {
		headingstring=tmstring+" : "+label;
	}
	else if (dtstring.length()==0 && tmstring.length()==0) {
		headingstring=label;
	}
	if (this.myHM==null) {
		this.myHM = readWordStyles();
	}
	 //iterate and create rest of file
	Scanner scanner1 = new Scanner(input);
 	String h1_style=myHM.get("H1");
 	String h2_style=myHM.get("H2");
 	String p_style=myHM.get("Default");
 	/*
 	System.out.println(h1_style);
 	System.out.println(h2_style);
 	System.out.println(p_style);
 	System.exit(0);
 	*/
 	StringBuffer output = new StringBuffer();
 	String fix_h=fixEscapeChars(headingstring);
 	String h = h1_style.replace("XXX",fix_h); //heading1
 	output.append(h);
 	Boolean lasthead2=false; //track level 2 headings
	while (scanner1.hasNextLine()) {
		String wordline="";
	 	//just make paragraphs for now
	 	String thisLine=scanner1.nextLine();
		thisLine=fixEscapeChars(thisLine);
	 	if (thisLine.length()==0 && lasthead2==true) {
	 		lasthead2=false;
	 		//do nothing
	 	}
	 	else if (thisLine.length()>3) {
	 			String code = thisLine.substring(0,3);
	 			if (code.equals("## ")) {
			 		wordline=h2_style.replace("XXX",thisLine.substring(3,thisLine.length()));
			 		lasthead2=true;
	 			}
	 			else {
	 				if (thisLine.length()>0) {
	 					wordline=p_style.replace("XXX",thisLine);
	 					lasthead2=false;
	 				}
	 				else if(lasthead2=true) {
	 					lasthead2=false; //don't add another line
	 				}
	 		}
	 	}
	 	else if (thisLine.length()<=3) {
	 		wordline=p_style.replace("XXX",thisLine);
	 		lasthead2=false;
	 	}	
	 	output.append(wordline);
	 }
	String stringOut=output.toString();
	//System.out.println(stringOut);
	return stringOut;
}

//escape characters for the OOXML output
	 	/*
	 	' -> &apos;
		" -> &quot;
		> -> &gt;
		< -> &lt;
		& -> &amp;
		*/
public String fixEscapeChars(String thisLine){
	String rep1=thisLine.replace("&","&amp;");
 	String rep2=rep1.replace("<","&lt;");
 	String rep3=rep2.replace(">","&gt;");
 	String rep4=rep3.replace("\"","&quot;");
 	String output=rep4.replace("\'","&apos;");
 	return output;
}

//Convert the MD section of current Book to some HTML and update the HTML parameter	
/*	
public String getHTMLfromContents(Book myBook) {
	String input = myBook.getMD();
	String label = myBook.getLabel();
	String notes = myBook.getNotes();
	String logString="";
	//take out any existing headers?
	//String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
	int index =0; //
	//top row or heading
	if(index==0) {
	 	logString = "<html><head>";
	 	logString=logString+"<title>"+label+"</title>";
	 	logString=logString+"<script> border {border-style:dotted;}</script>"; //css
	 	logString=logString+"</head>"+"<body>";// use the label for the html page (if needed)
	 	//logString=logString+"<p><b>"+label+"</b></p>";
	 	logString=logString+"<H1>"+label+"</H1>";
	 }
	 //iterate and create rest of file
	Scanner scanner1 = new Scanner(input);
 	String prefix = "<p>";
 	String suffix="</p>";

	 while (scanner1.hasNextLine()) {
	 	//just make paragraphs for now
	 	String thisLine=scanner1.nextLine();
	 	logString=logString+prefix+thisLine+suffix;
	 }
	 //
	 Scanner scanner2 = new Scanner(notes);
	 String prefixdiv="<div id=\"border\">";
	 String suffixdiv="</div>";
	 logString=logString+prefixdiv;
	 while (scanner2.hasNextLine()) {
	 	String notesLine=scanner2.nextLine();
	 	logString=logString+prefix+notesLine+suffix;
	 	System.out.println(notesLine);
	 }
	 logString=logString+suffixdiv;
	 //
	 logString=logString+"</body></html>";
	 System.out.println(logString);
	 //System.exit(0);
	return logString;
	}
	*/

} //end class
