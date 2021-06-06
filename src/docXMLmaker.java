//Class to create a Word docx when it did not already exist
//(c) Craig Duncan 2021

import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class docXMLmaker {
	HashMap<String,String> myHM;

public docXMLmaker() {

}

//write out Word just from MD notes in blocks, using some Standard styles
//Does not use any of the OOXML from original docx files.
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
            String myWordString=makeOOXMLfromBook(myNode); //this gets wordcodes every time
            
            myWordOutput.append(myWordString);
            myWordString="";
             //option: prepare string here, then write once.
        }
        //System.exit(0);
        WordWriter(myWordOutput.toString(),filepath);
}


//basic writer to create a de novo Word Doc from just the String (i.e. mdnotes)

public void WordWriter(String inputstring, String filename) {
	//our docXML will contain templateDoc styles.xml, updated with Note, Code styles.
    String templateDoc="wordlib/StylesTemplate.docx";
    docXML tempDoc = new docXML();
    File file = new File(templateDoc);
    tempDoc.openDocx(file); 

    //we need to take inputstring, insert it into document.xml to then zip it up with rest of docx
    File sourceFile = new File("wordlib/LittleDoc.xml");
    ZipUtil util = new ZipUtil();
    String myDocument = util.getFileText(sourceFile); //alternatively, extract from StylesTemplate
    String newDoc=myDocument.replace("XXXXXX",inputstring); //we now have a new document.xml
   	tempDoc.setDocString(newDoc); //put it in our docXML for document.xml

    util.readAndReplaceZip(tempDoc,templateDoc,filename);
}

//Convert the MD section of current Book to styled Word paragraphs	
public String makeOOXMLfromBook(Book myBook) {
	String mdtext = myBook.getMD();
	String ooxml = myBook.getOOXMLtext(); //this is not saved yet.
	String label = myBook.getLabel();
	String dtstring=myBook.getdate();
	String styleIdstring=myBook.getStyleId();
	String notes = myBook.getNotes();
	String headingstring = "";
	if (dtstring.length()>0) { //&& tmstring.length()>0
		headingstring=dtstring+","+" : "+label;//tmstring
	}
	else if (dtstring.length()>0) {  //&& tmstring.length()==0
		headingstring=dtstring+" : "+label;
	}
	else if (dtstring.length()==0) { //&& tmstring.length()>0
		headingstring=label;
	}
	else if (dtstring.length()==0) { //&& tmstring.length()==0
		headingstring=label;
	}
	if (this.myHM==null) {
		this.myHM = readWordStyles();
	}
	 //These are paragraphs with styles already embedded.
	
 	String h1_para=myHM.get("H1");
 	String h2_para=myHM.get("H2");
 	String defPara=myHM.get("Default");
 	//to DO: write in the StyleId of block into first paragraph?
 	String notePara="<w:p w14:paraId=\"33928186\" w14:textId=\"22222222\" w:rsidR=\"0094238C\" w:rsidRDefault=\"002D55C6\"><w:pPr><w:pStyle w:val=\"Note\"/></w:pPr><w:r><w:t>XXX</w:t></w:r></w:p>";
 	String codePara="<w:p w14:paraId=\"33928190\" w14:textId=\"77777777\" w:rsidR=\"0094238C\" w:rsidRDefault=\"002D55C6\"><w:pPr><w:pStyle w:val=\"Code\"/></w:pPr><w:r><w:t>XXX</w:t></w:r></w:p>";
 	//There is no style with 'id' of RCode, only an alias for Code.  So this should be 'not found'
 	String rCodePara="<w:p w14:paraId=\"33928194\" w14:textId=\"77777777\" w:rsidR=\"0094238C\" w:rsidRDefault=\"002D55C6\"><w:pPr><w:pStyle w:val=\"RCode\"/></w:pPr><w:r><w:t>XXX</w:t></w:r></w:p>";
	/*
 	System.out.println(h1_style);
 	System.out.println(h2_style);
 	System.out.println(p_style);
 	System.exit(0);
 	*/
 	StringBuffer output = new StringBuffer();
 	String fix_h=fixEscapeChars(headingstring);
 	//DO THE HEADING
 	String h = h1_para.replace("XXX",fix_h); //heading1
 	output.append(h);

 	//ITERATE THROUGH THE MARKDOWN TEXT, LINE BY LINE
 	//(At present this still accepts as text the parts of a markdown file that are co-ords)
 	
 	Scanner scanner1 = new Scanner(mdtext);
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
			 		wordline=h2_para.replace("XXX",thisLine.substring(3,thisLine.length()));
			 		lasthead2=true;
	 			}
	 			else {
	 				if (thisLine.length()>0) {
	 					wordline=defPara.replace("XXX",thisLine);
	 					lasthead2=false;
	 				}
	 				else if(lasthead2=true) {
	 					lasthead2=false; //don't add another line
	 				}
	 		}
	 	}
	 	else if (thisLine.length()<=3) {
	 		wordline=defPara.replace("XXX",thisLine);
	 		lasthead2=false;
	 	}	
	 	output.append(wordline);
	 }
	 //add notes
	 if (notes.length()>0) {
	 		//output.append(notePara.replace("XXX",notes));
	 		output.append(notePara.replace("XXX",notes));
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


}