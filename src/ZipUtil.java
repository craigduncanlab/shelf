import java.util.zip.*;
import java.util.zip.ZipInputStream;
import java.io.*;  //Buffered Reader, File Reader, IOException, FileNoteFoundException etc
import java.util.*; //scanner, HashMap, ArrayList etc, Zip...
import java.nio.charset.StandardCharsets; 

/* This Class was originally designed to open files, but it can also substitute new components for re-use */

public class ZipUtil {
	//instance variables to store unzipped Word/docx contents
	File firstfile;
	String myDocument="";
	String myStyles="";
	String myNumbering="";

public ZipUtil() {

}

// -- getters and setters for Word Docs

public String getDocument(){
	return this.myDocument;
}

public void setDocument(String input) {
	this.myDocument=input;
}

public String getStyles(){
	return this.myStyles;
}

public void setStyles(String input) {
	this.myStyles=input;
}

public String getNumbering(){
	return this.myNumbering;
}

public void setNumbering(String input) {
	this.myNumbering=input;
}

//--- main I/O

//input is a File or String?
public ArrayList<ZipEntry> readZip(String fileZip){
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	try  {
		FileInputStream myFileStream = new FileInputStream(fileZip);
		ZipInputStream zis = new ZipInputStream(myFileStream);
	    ZipEntry zipItem = zis.getNextEntry();
	    while (zipItem != null) {
	    		zipItem = zis.getNextEntry();
	    		if (zipItem!=null) {
	    			String name =zipItem.getName();
	    			System.out.println(name);
	    		}
	    }
	}
	catch (FileNotFoundException fnf) {
		System.out.println("Exception");
	}
	catch (IOException e) {
		System.out.println("Exception");
	}
	return myItemList;
}

//Simple utility to return contents of file as String
public String getFileText(File myFile) {
  	StringBuffer myText = new StringBuffer(); //mutable String
  	String endOfLine="\n";
   try {
    Scanner scanner1 = new Scanner(myFile);
    if (scanner1==null) {
      System.out.println("No text/html content");
      return null;
    }
    int nl=0;
    while (scanner1.hasNextLine()) {
      nl++;
      System.out.println(nl);
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
    return null;
  }
  return myText.toString();
}

//will save this file (assumes it is text, html etc)
public void basicFileWriter(String logstring,String filename) {
    //String reportfile=this.templatefolder+filename+".md";

    try {
      PrintStream console = System.out;
      PrintStream outstream = new PrintStream(new FileOutputStream(filename,false)); //true = append.  This overwrites.
      System.setOut(outstream);
      //String logString = Integer.toString(myNode.getNodeRef())+"@@P"+myNode.getDocName()+"@@P"+myNode.getHeading()+"@@P"+myNode.getNotes()+"@@P"+myNode.getHTML()+"@@P@EOR";
      System.out.print(logstring); //don't use println.  No CR needed.
      outstream.close();
      System.setOut(console);
    }
        catch (Throwable t)
        {
            t.printStackTrace();
            return;
        }
}  


// ---ZIP

//need to check compression format...
/* inputs are:
String myRefFile="wordlib/StylesTemplate.docx";
fileZip is the .docx file
docxml is the new document.xml text
outFile is the output file name (no extension)
also: target is the internal path inside the zip file for document.xml
*/

public ArrayList<ZipEntry> readAndReplaceZip(docXML tempDoc, String templateDocx, String outFile) {
	
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	File f = new File(outFile); //outputfile.  
	String maindoc="word/document.xml";
	String styledoc="word/styles.xml";
	String docxml = tempDoc.getDocString();
	xmlStyles myStyle = tempDoc.getStylesObject();
	String stylexml = myStyle.getStylesXML();
	try  {
		FileInputStream myFileStream = new FileInputStream(templateDocx); //reads in all parts of templateDocx
		FileOutputStream myOutputStream = new FileOutputStream(f);
		//Setup 2 streams
		ZipInputStream zis = new ZipInputStream(myFileStream);
		ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		out.setLevel(6); //setcompression level for deflated entries.  Should be =6 for docx
		out.setMethod(ZipEntry.DEFLATED);
	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		 //a ZipEntry is used in the Zip file system.  automatically closes last entry
	    	count++;
	    		
	    	String name =zipItem.getName();

	    	if (name.equals(maindoc)) {
				System.out.println(name);
				ZipEntry newItem = new ZipEntry(maindoc);
				out.putNextEntry(newItem);
				byte[] data = docxml.getBytes();  //turn String into bytes since thats what ZipFileOutputStream wants
   				newItem.setSize(data.length); //set size
				System.out.println(docxml);
				System.out.println(data.length);
				System.out.println("About to write");
				out.write(data);
				out.closeEntry();
			}
			else if (name.equals(styledoc)) {
				System.out.println(name);
				ZipEntry newItem = new ZipEntry(styledoc);
				out.putNextEntry(newItem);
				byte[] data = stylexml.getBytes();  //turn String into bytes since thats what ZipFileOutputStream wants
   				newItem.setSize(data.length); //set size
				System.out.println(stylexml);
				System.out.println(data.length);
				System.out.println("About to write");
				out.write(data);
				out.closeEntry();
			}
			//otherwise, we're going to write the ZipEntry file data back into the new file
			else {
	    			//we need to buffer the byte reads because we don't know byte size
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    				int rdlength;
    				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
    		    				
    				while ((rdlength = zis.read(data,0,data.length)) != -1) {
    						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
    				}
    				buffer.flush(); //finish buffering
					byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
					String mystring = new String(byteArray, StandardCharsets.UTF_8);
    				System.out.println(mystring);
					//decode with utf8 if string files
					if (name.indexOf("xml") != -1 || name.indexOf("rels") != -1) {
        				System.out.println("This is a string not binary file: "+name);
   					}
   					//images etc
   					if (name.indexOf("xml") == -1 && name.indexOf("rels") == -1) {
        				System.out.println("This is a binary file: "+name);
   					}
   					ZipEntry zipOutItem = new ZipEntry(name);
   					zipOutItem.setSize(byteArray.length); //set size
    				out.putNextEntry(zipOutItem); //
					out.write(byteArray);
    				out.closeEntry();  //optional if you use 'get next entry'
	    		}	
	    		zipItem = zis.getNextEntry();
	    }
	    out.close(); //close the output stream and file.  zis?
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	return myItemList;
}

/* extract the document.xml from the opened .docx 

This will update myProject with the content of lvl 0 headings from styles.xml too

inputs are:
A File object with the input file that is selected .docx from FileChooser

Variables used:
String myRefFile=input file path.  if we are passed a File object, just getPath()
e..g. "wordlib/StylesTemplate.docx";

target is the internal path inside the zip file for document.xml

fileZip is the local .docx file path, with filename.docx included

outFile is the output file name (no extension).  Unusued

Output: string contents of the document.xml in the .docx file

*/

//read in the document.xml from file
public void OpenDocX(File file) {
	this.firstfile=file;
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	String mainfile="word/document.xml";
	String numfile="word/numbering.xml";
	String stylesfile="word/styles.xml";
	String myText=""; //the variable to hold data and return as output
	String myNum="";
	String myStyles="";
	try  {
		//String fileZip = file.getName();
		String fileZip = file.getPath(); //or use myProject?
		//file-level streams
		FileInputStream myFileStream = new FileInputStream(fileZip);
		ZipInputStream zis = new ZipInputStream(myFileStream);

		//Setup 2 zip streams (these are relative to the input/output files)
		/*
		String outFile = "docOpentest.docx"; //unused
		File f = new File(outFile); //outputfile
		FileOutputStream myOutputStream = new FileOutputStream(f);
		*/
		//ByteArrayOutputStream
		//ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		

	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		 //a ZipEntry is used in the Zip file system.  automatically closes last entry
	    	count++;
	    		
	    	String name = zipItem.getName();
	    	System.out.println(name);
	    	//unzip the target file to a String
	    	if (name.equals(mainfile)) {
				//we need to buffer the byte reads because we don't know byte size
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int rdlength;
				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
		    				
		    	//write the zipped file, as read, to a buffer (i.e. now it is uncompressed)
				while ((rdlength = zis.read(data,0,data.length)) != -1) {
						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
				}
				buffer.flush(); //finish buffering
				byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
				//Obtain the String from the byte buffer
				myText = new String(byteArray, StandardCharsets.UTF_8);
				setDocument(myText);
				//System.out.println(myText);
				//out.closeEntry(); //close our output buffer
			} //end of target loop.  
			if (name.equals(numfile)) {
				//we need to buffer the byte reads because we don't know byte size
				ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
				int rdlength;
				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
		    				
		    	//write the zipped file, as read, to a buffer (i.e. now it is uncompressed)
				while ((rdlength = zis.read(data,0,data.length)) != -1) {
						buffer2.write(data,0, rdlength); //add our latest bytes to the buffer
				}
				buffer2.flush(); //finish buffering
				byte[] byteArray = buffer2.toByteArray(); //convert buffer back to byte array of actual length
				//Obtain the String from the byte buffer
				myNum = new String(byteArray, StandardCharsets.UTF_8);
				setNumbering(myNum);
				//System.out.println(myNum);
				//out.closeEntry(); //close our output buffer
			} //end of target loop.  Otherwise, do nothing in this loop
			if (name.equals(stylesfile)) {
				//we need to buffer the byte reads because we don't know byte size
				ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
				int rdlength;
				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
		    				
		    	//write the zipped file, as read, to a buffer (i.e. now it is uncompressed)
				while ((rdlength = zis.read(data,0,data.length)) != -1) {
						buffer2.write(data,0, rdlength); //add our latest bytes to the buffer
				}
				buffer2.flush(); //finish buffering
				byte[] byteArray = buffer2.toByteArray(); //convert buffer back to byte array of actual length
				//Obtain the String from the byte buffer
				myStyles = new String(byteArray, StandardCharsets.UTF_8);
				setStyles(myStyles);
				//System.out.println(myStyles);
				//out.closeEntry(); //close our output buffer
				//System.exit(0);
			}
			zipItem = zis.getNextEntry();
	    } //end while
	    //out.close(); //close the output stream and file.  close zis?
	    zis.close();
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	//return myXML;
}

//need to check compression format...
public ArrayList<ZipEntry> readAndWriteZip(String fileZip){
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	File f = new File("content/test.docx"); //outputfile
	//String target="word/document.xml";
	try  {
		FileInputStream myFileStream = new FileInputStream(fileZip);
		FileOutputStream myOutputStream = new FileOutputStream(f);
		//Setup 2 streams
		ZipInputStream zis = new ZipInputStream(myFileStream);
		ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		out.setLevel(6); //setcompression level for deflated entries.  Should be =6 for docx
		out.setMethod(ZipEntry.DEFLATED);
	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		count++;
	    		
	    		String name =zipItem.getName();
    			//we need to buffer the byte reads because we don't know byte size
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int rdlength;
				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
				//check that the actual read bytes isn't empty and make sure UTF-8 is used.
				while ((rdlength = zis.read(data,0,data.length)) != -1) {
						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
				}
				buffer.flush(); //finish buffering
				byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
				String mystring = new String(byteArray, StandardCharsets.UTF_8);
				System.out.println(mystring);
				//decode with utf8 if string files
				if (name.indexOf("xml") != -1 || name.indexOf("rels") != -1) {
    				System.out.println("This is a string not binary file: "+name);
    				
					}
					//images etc
					if (name.indexOf("xml") == -1 && name.indexOf("rels") == -1) {
    				System.out.println("This is a binary file: "+name);
					}
					ZipEntry zipOutItem = new ZipEntry(name);
					zipOutItem.setSize(byteArray.length); //set size
				out.putNextEntry(zipOutItem); //
				out.write(byteArray);
				out.closeEntry();  //optional if you use 'get next entry'
				zipItem = zis.getNextEntry();
	    }
	    out.close(); //close the output stream and file.  
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	return myItemList;
}

public ArrayList<ZipEntry> readAndReplaceStyles(String stylexml,  String outFile) {
	String oldfile = this.firstfile.getPath(); //get first file's information
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	File f = new File(outFile); //outputfile. Needs a full path.
	//String maindoc="word/document.xml";
	String styledoc="word/styles.xml";
	//String docxml = tempDoc.getDocString();
	//xmlStyles myStyle = tempDoc.getStylesObject();
	//String stylexml = myStyle.getStylesXML();
	try  {
		FileInputStream myFileStream = new FileInputStream(oldfile); //reads in all parts of templateDocx
		FileOutputStream myOutputStream = new FileOutputStream(f);
		//Setup 2 streams
		ZipInputStream zis = new ZipInputStream(myFileStream);
		ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		out.setLevel(6); //setcompression level for deflated entries.  Should be =6 for docx
		out.setMethod(ZipEntry.DEFLATED);
	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		 //a ZipEntry is used in the Zip file system.  automatically closes last entry
	    	count++;
	    		
	    	String name =zipItem.getName();
	    	/*
	    	if (name.equals(maindoc)) {
				System.out.println(name);
				ZipEntry newItem = new ZipEntry(maindoc);
				out.putNextEntry(newItem);
				byte[] data = docxml.getBytes();  //turn String into bytes since thats what ZipFileOutputStream wants
   				newItem.setSize(data.length); //set size
				System.out.println(docxml);
				System.out.println(data.length);
				System.out.println("About to write");
				out.write(data);
				out.closeEntry();
			}
			*/
			if (name.equals(styledoc)) {
				System.out.println(name);
				ZipEntry newItem = new ZipEntry(styledoc);
				out.putNextEntry(newItem);
				byte[] data = stylexml.getBytes();  //turn String into bytes since thats what ZipFileOutputStream wants
   				newItem.setSize(data.length); //set size
				System.out.println(stylexml);
				System.out.println(data.length);
				System.out.println("About to write");
				out.write(data);
				out.closeEntry();
			}
			//otherwise, we're going to write the ZipEntry file data back into the new file
			else {
	    			//we need to buffer the byte reads because we don't know byte size
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    				int rdlength;
    				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
    		    				
    				while ((rdlength = zis.read(data,0,data.length)) != -1) {
    						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
    				}
    				buffer.flush(); //finish buffering
					byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
					String mystring = new String(byteArray, StandardCharsets.UTF_8);
    				System.out.println(mystring);
					//decode with utf8 if string files
					if (name.indexOf("xml") != -1 || name.indexOf("rels") != -1) {
        				System.out.println("This is a string not binary file: "+name);
   					}
   					//images etc
   					if (name.indexOf("xml") == -1 && name.indexOf("rels") == -1) {
        				System.out.println("This is a binary file: "+name);
   					}
   					ZipEntry zipOutItem = new ZipEntry(name);
   					zipOutItem.setSize(byteArray.length); //set size
    				out.putNextEntry(zipOutItem); //
					out.write(byteArray);
    				out.closeEntry();  //optional if you use 'get next entry'
	    		}	
	    		zipItem = zis.getNextEntry();
	    }
	    out.close(); //close the output stream and file.  zis?
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	return myItemList;
}

}

