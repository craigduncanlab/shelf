//(c) Craig Duncan 2020
//A class to parse markdown files for this application (input and output)
import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class Parser {

//default constructor
public Parser(){

}

//read in an .md file and then process it
//This could return an Array of ClauseContainers, not one.

public ClauseContainer parseMDfile(String contents) {
    System.out.println("Begin parsing MD file");
    // for now, no processing of contents
    //ClauseContainer newNode = new ClauseContainer("Test",contents,"notes");
    ClauseContainer newNode = parseMDblock(contents);
    System.out.println("Finished parsing MD file");
    return newNode;
}

/*
Parsing algo:
1. read file
2. split into heading blocks (make note of whether #- which is 'not visible state')
3. split each block further into an individual ClauseContainer which has:
(a) the label from the same row as the # heading
(b) main markdown section (up to next block).  This inclues single line markdown.
(c) any multi-line notes (using ``` code or /* comments markup)
4. Store ClauseContainers in an array and return...

*/

public ArrayList<String> splitMDfile(String input) {
	ArrayList<Integer> fileindex = new ArrayList<Integer>();
	StringBuffer currentblock = new StringBuffer();
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
				//Scanner scanner2= new Scanner(thisRow).useDelimiter("#");
				
				System.out.println(nl+") "+thisRow);
				if (thisRow.length()<2) {
					fileindex.add(1);
				}
				else {
					String firstpart = thisRow.substring(0,2);
					System.out.println(nl+") "+firstpart);
					switch (firstpart) {
	            		case "# ":  
		            		fileindex.add(0);
		                	break;
	                    case "#-":  
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
			
			int cb=0;
			int cl=0;
			while (scanner2.hasNextLine()) {
				String thisLine=scanner2.nextLine();

				if (fileindex.get(cl)==0) {
					if(cl>cb) {
						currentblock.append("\n"); //EOL
						String thisBlock=currentblock.toString();
						newBlocks.add(thisBlock);
						currentblock.delete(0,currentblock.length());
						currentblock.append(thisLine);
						cb=cl;
					}
					else {
						currentblock.append(thisLine);
						currentblock.append("\n"); //EOL;
					}
				}
				if (fileindex.get(cl)==1) {
					currentblock.append(thisLine);
					currentblock.append("\n"); //EOL
				}
				cl++;
			} //end while
			//tidy up last part of file
			if(cl>cb) {
				currentblock.append("\n"); //EOL
				String thisBlock=currentblock.toString();
				newBlocks.add(thisBlock);
				//currentblock.delete(0,currentblock.length());
				//currentblock.append(thisLine);
				cb=cl;
			}
			scanner2.close();
			} //end try
			catch (Throwable t)
			{
				t.printStackTrace();
				//System.exit(0);
				return null;
			}
			//System.out.println(newBlocks);
			//System.exit(0);
			return newBlocks;
}

//single document test
public ClauseContainer parseMDblock(String input) {
		ArrayList<Integer> fileindex = new ArrayList<Integer>();
		StringBuffer mdStream = new StringBuffer();
		StringBuffer notesStream = new StringBuffer();
		Boolean visibility=true;
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
				//Array currentNotes[] = new Array(2);
				String thisRow=scanner1.nextLine();
				//Scanner scanner2= new Scanner(thisRow).useDelimiter("#");
				
				System.out.println(nl+") "+thisRow);
				if (thisRow.length()<2) {
					if (brackets==0) {
						fileindex.add(1);
					}
					else {
						fileindex.add(2); //notes
					}
				}
				else {
					String firstpart = thisRow.substring(0,2);
					Boolean singlelinenote = thisRow.contains("*/");
					System.out.println(nl+") "+firstpart);
					switch (firstpart) {
	            		case "# ":  
		            		if (brackets==0) {
		            			visibility=true;
		            			fileindex.add(0);
		            			label=thisRow;
		                    }
		                    else {
		                    	fileindex.add(2); //notes
		                    }
		                    break;
	                    case "#-":  
		                    if (brackets==0) {
		            			visibility=false;
		            			fileindex.add(0);
		            			label=thisRow;
		                    }
		                    else {
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
	                    	else {
	                    		fileindex.add(1); //md
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
			try {
				Scanner scanner2 = new Scanner(input); //default delimiter is EOL?
				if (scanner2==null) {
					System.out.println("No MD block content");
					return null;
				}
			
			nl=0;
			while (scanner2.hasNextLine()) {
				String thisLine=scanner2.nextLine();

				if (fileindex.get(nl)==0 || fileindex.get(nl)==1) {
					mdStream.append(thisLine);
					mdStream.append("\n"); //EOL
				}
				//notes line
				if (fileindex.get(nl)==2 || fileindex.get(nl)==3) {
					String replacement = thisLine.replace("/*","");
					String replacement2 = replacement.replace("*/","");
					notesStream.append(replacement2);
					notesStream.append("\n"); //EOL
				}
				//advance if ok
				if (nl<max) {
					nl++;
				}
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
		if (label.length()<1) {
			label="Default";
		}
		String label1=label.replace("#",""); //remove hash but leave minus sign?
		//String label2=label1.replace("#-","");
		ClauseContainer newNode=new ClauseContainer(label1,contents,notes);
		//At present visibility reflects the last markdown # code detected in file.
		newNode.setVisible(visibility);
		return newNode;
		} //end method
} //end class
