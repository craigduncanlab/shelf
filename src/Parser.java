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

//default constructor
public Parser(){

}

//read in an .md file and then process it
//This could return an Array of Books, not one.

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

public Book parseMDfileAsRow(MainStage myStage, EventHandler pb,EventHandler db,String contents) {
	this.PressBox=pb;
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

//called externally to split up an MD file into separate blocks

public ArrayList<String> splitMDfile(String input) {
	ArrayList<Integer> fileindex = new ArrayList<Integer>();
	StringBuffer currentblock = new StringBuffer();
	ArrayList<String>myLines = new ArrayList<String>();
	ArrayList<String>newBlocks = new ArrayList<String>();
	int filecount=0;
	int nl=0;
		try {
			Scanner scanner1 = new Scanner(input); //default delimiter is EOL?
			if (scanner1==null) {
				System.out.println("No MD block content");
				return null;
			}
			
			int brackets=0;
			while (scanner1.hasNextLine()) {
				//Array currentNotes[] = new Array(2);
				String thisRow=scanner1.nextLine();
				myLines.add(thisRow);
				//Scanner scanner2= new Scanner(thisRow).useDelimiter("#");
				
				//This is a short row
				System.out.println(nl+") "+thisRow);
				if (thisRow.length()<3) {
					fileindex.add(1);
				}
				else {
					String firstpart = thisRow.substring(0,2);
					String visFlag = thisRow.substring(0,3);
					System.out.println(nl+") "+firstpart);
					switch (firstpart) {
	            		case "# ":  
		            		fileindex.add(0);
		                	break;
	                    default:  
	                    	fileindex.add(1);
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

			try {
				Scanner scanner2 = new Scanner(input); //default delimiter is EOL?
				if (scanner2==null) {
					System.out.println("No MD block content");
					return null;
				}
			// at this point we have fileindex array with '0's for # lines and '1's for other lines.
			int cb=0;
			int cl=0;
			while (scanner2.hasNextLine()) {
				String thisLine=scanner2.nextLine();
				//if this row is a heading
				System.out.println(cl+") ["+fileindex.get(cl)+"] "+thisLine);
				if (fileindex.get(cl)==0) { //if we encounter start of next block
					if(cl>cb) {
						//currentblock.append("\n"); //EOL
						String thisBlock=currentblock.toString();
						newBlocks.add(thisBlock); //add the current block to newBlocks array
						currentblock.delete(0,currentblock.length()); //delete current block
						//currentblock.append("\n"); 
						cb=cl;
					}

				}
				cl++;
				if (thisLine.length()>=0) { //first line of new block at start of file
					currentblock.append(thisLine);
					currentblock.append(System.getProperty("line.separator"));
					//currentblock.append("\n"); //EOL needed;
				}
			}   //end while
			//add last block or we drop 1 each time
			String thisBlock=currentblock.toString();
			newBlocks.add(thisBlock); //add the current block to newBlocks array
			currentblock.delete(0,currentblock.length()); //delete current block
			//currentblock.append("\n"); 
		
			scanner2.close();
			} //end try
			catch (Throwable t)
			{
				t.printStackTrace();
				//System.exit(0);
				return null;
			}
			System.out.println(newBlocks);
			//System.exit(0);
			return newBlocks;
}

//called externally to work on contents of an individual Markdown block
public Book parseMDblock(String input) {
	ArrayList<Integer> fileindex = makeMDfileindex(input);
	Book newNode = MDfileFilter(fileindex,input);
	return newNode;
}

//make an index (like an R vector) to determine nature of each line of markdown file
public ArrayList<Integer> makeMDfileindex(String input) {
		ArrayList<Integer> fileindex = new ArrayList<Integer>();
		
		Boolean visibility=true; //TO DO: fix visibility state after index prepared?
		Boolean codeLine=false;
		Boolean urlLine=false;
		Boolean filepathLine=false;
		Boolean singlecodeline=false;
		Boolean coord=false;
		Boolean grid=false;
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
				filepathLine=false;
				urlLine=false;
				coord=false;
				grid=false;
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
		            	else if (prefix.equals("[x,y](")) {
		            		coord=true;
		            		System.out.println(prefix);
		            		
		            	}
		            	else if (prefix.equals("[r,c](")) {
		            		grid=true;
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
	                    	else if(coord==true){
	                    		fileindex.add(8);//8=coordinates
	                    	}
	                    	else if(grid==true){
	                    		fileindex.add(9);//9=grid coord
	                    	}
	                    	else {
	                    		if (thisRow.length()>0) {
	                    			fileindex.add(1); //md
	                    		} 
	                    		else {
	                    			fileindex.add(10); //nothing
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
public Book MDfileFilter(ArrayList<Integer> fileindex,String input) {
		StringBuffer mdStream = new StringBuffer();
		StringBuffer notesStream = new StringBuffer();
		StringBuffer codenotesStream = new StringBuffer();
		Integer nl;
		String label="";
		String urlString="";
		String filepathString="";
		double x=0.0;
		double y=0.0;
		Integer row=0;
		Integer col=0;
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
				//coordinates line
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
		newNode.setXY(x,y); //x,y  must be doubles		
		this.mainstage.snapYtoShelf(newNode,y); //check y and set shelf number
		//At present visibility reflects the last markdown # code detected in file.
		newNode.setVisible(true);
		//convert contents to html for initial 'preview'
		String myHTML=getHTMLfromContents(newNode);
		newNode.setHTML(myHTML); //could do this in method that receives below but do it here for now
		return newNode;
		} //end method

//Convert the MD section of current Book to some HTML and update the HTML parameter		
public String getHTMLfromContents(Book myBook) {
	String input = myBook.getMD();
	String label = myBook.getLabel();
	String logString="";
	//take out any existing headers?
	//String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
	int index =0; //
	//top row or heading
	if(index==0) {
	 	logString = "<html><head><title>"+label+"</title></head>"+"<body>";// use the label for the html page (if needed)
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
	 logString=logString+"</body></html>";
	 System.out.println(logString);
	return logString;
	}

} //end class
