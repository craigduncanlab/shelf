//mdBlock Class to hold line-related objects from .md, docx text streams
/*
This retains line codes from original file, to help subdivide for Books object later.
It is a helper Class in the data model that processes text stream into Books
*/

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class xmlBlock {
	ArrayList<xmlPara> blockLines =new ArrayList<xmlPara>();
	int blockIndex=0;
	int blocktype=1; //default.  in substance this is the 'fileindex' code for this line
	String blockText="";
	String notesText="";
	String headerText="";

//constructor
public xmlBlock(){
	setHeaderText("First page");
}

public void setHeaderText(String input){
	this.headerText=input;
}

public String getHeaderText(){
	return this.headerText;
}

public void importBlockLines(ArrayList<xmlPara> myLines){
	for (xmlPara myItem: myLines) {
		blockLines.add(myItem);
	}
}

//Does not update the blocktext each time a new xmlPara object is added to the block
public void addLineObject(xmlPara myObject){
	blockLines.add(myObject);
}

public int getStoredLines(){
	return blockLines.size();
}

//make text from <w:p> paragraphs in this block.  
public void makeBlockText(){
	String output="";
	for (xmlPara myItem: this.blockLines) {
		int code = myItem.getLineCode();
		if (code==0) {
			String text = myItem.getParaString();
			String plain = myItem.getplainText(); //removed from <w:t>
			output=output+text+System.getProperty("line.separator");
			setHeaderText(plain);
			}
		if (code==1) {
			String text = myItem.getParaString();
			output=output+text+System.getProperty("line.separator");
		}
	}
	setBlockText(output);
	}


//make notes text from code 6.  This makes multiple notes into a single 'notes' item 
//However, this may not be the goal in all cases.

public void makeNotesText(){
	String output="";
	for (xmlPara myItem: this.blockLines) {
		int code = myItem.getLineCode();
		if (code==6) {
			String line = myItem.getParaString();
			output=output+line+System.getProperty("line.separator");
		}
	}
	setNotesText(output);
}

public void setBlockText(String input) {
	this.blockText=input;
}

//gets block text based on current block (xmlParas list)
public String getBlockText(){
	makeBlockText();
	return this.blockText;
}

public void setNotesText(String input) {
	this.notesText=input;
}

public String getNotesText(){
	makeNotesText();
	return this.notesText;
}

}