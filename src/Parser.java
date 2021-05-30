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

Project projectCopy;

//default constructor
public Parser(){
	this.projectCopy=new Project();
}

//constructor with myProject object passed in
public Parser(Project inputProject){
	this.projectCopy=inputProject;
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

//Try and remove this - encapsulate in upper class for each input filetype.
//Aim to produce blocks as an interface to Books objects

public ArrayList<mdBlock>packageBlocksFromLineObjects(ArrayList<mdLineObject> myLines) {
	ArrayList<mdBlock> output = new ArrayList<mdBlock>();
	mdBlock currentblock = new mdBlock();
	
	String headertext="";
	int cl=0;
	for (mdLineObject lineItem: myLines) {
		int linecode=lineItem.getLineCode();
		//if this row is a heading
		//System.out.println(cl+") ["+fileindex.get(cl)+"] "+thisLine);

		//SPLIT INTO SMALLER BLOCKS BASED ON 0 CODES
		if (linecode==0) { //if we encounter start of next block (#)
			//first line
				if (currentblock.getStoredLines()>0) {
					output.add(currentblock); //add the current block to newBlocks array
					currentblock = new mdBlock(); //start again with a new block
				}
				currentblock.addLineObject(lineItem); // in any case start a new block, or start first
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
		projectCopy.setHeader(headertext); //store for later;
	}
	
	return output;
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

} //end class
