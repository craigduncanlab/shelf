//mdBlock Class to hold line-related objects from .md, docx text streams
/*
This retains line codes from original file, to help subdivide for Books object later.
It is a helper Class in the data model that processes text stream into Books
*/

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class mdBlock {
	ArrayList<mdLineObject> blockLines =new ArrayList<mdLineObject>();
	int blockIndex=0;
	int blocktype=1; //default.  in substance this is the 'fileindex' code for this line
	String blockText="";
	String notesText="";
	String headerText="";

//constructor
public mdBlock(){

}

public void setHeaderText(String input){
	this.headerText=input;
}

public String getHeaderText(){
	return this.headerText;
}

public void importBlockLines(ArrayList<mdLineObject> myLines){
	for (mdLineObject myItem: myLines) {
		blockLines.add(myItem);
	}
}

public void addLineObject(mdLineObject myObject){
	blockLines.add(myObject);
}

public int getStoredLines(){
	return blockLines.size();
}

//make text from code 1
public void makeBlockText(){
	String output="";
	for (mdLineObject myItem: this.blockLines) {
		int code = myItem.getLineCode();
		if (code==0) {
			String text = myItem.getLineText();
			//if we have a header line set the block header
			if (text.substring(0,2).equals("# ")) {
				String hline = text.substring(2,text.length());
				setHeaderText(hline); //set block header based on the 0 coded line
				text=hline; //maybe do text="";?
			}
			//For now, header line is still included here:
			output=output+text+System.getProperty("line.separator");
		}
		if (code==1) {
			String hline = myItem.getLineText();
			output=output+hline+System.getProperty("line.separator");
		}
	}
	setBlockText(output);
}

//make notes text from code 6.  This makes multiple notes into a single 'notes' item 
//However, this may not be the goal in all cases.

public void makeNotesText(){
	String output="";
	for (mdLineObject myItem: this.blockLines) {
		int code = myItem.getLineCode();
		if (code==6) {
			String line = myItem.getLineText();
			output=output+line+System.getProperty("line.separator");
		}
	}
	setNotesText(output);
}

public void setBlockText(String input) {
	this.blockText=input;
}

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