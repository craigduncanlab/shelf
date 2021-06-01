/*
Class to hold xmlPara objects as a block, based on outline levels.
This retains line codes from original file, to help subdivide for Books object later.
It is a helper Class in the data model that processes text stream into Books
*/

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class xmlBlock {
	ArrayList<xmlPara> blockParas =new ArrayList<xmlPara>();
	int blockIndex=0;
	int blocktype=1; //default.  in substance this is the 'fileindex' code for this line
	String blockText="";
	String notesText="";
	String headerText="";
	int OutlineLevel=0; //a way to classify blocks for display etc.
	String StyleXML="";

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

public void setStyleXML(String input){
	this.StyleXML=input;
}

public String getStyleXML(){
	return this.StyleXML;
}

public void importblockParas(ArrayList<xmlPara> myParas){
	for (xmlPara myItem: myParas) {
		blockParas.add(myItem);
	}
}

//Does not update the blocktext each time a new xmlPara object is added to the block
public void addLineObject(xmlPara myObject){
	blockParas.add(myObject);
}

public int getStoredLines(){
	return blockParas.size();
}

//make text from <w:p> paragraphs in this block.  
public void makeBlockText(){
	String output="";
	int numLevels=1;
	for (xmlPara myItem: this.blockParas) {
		int code = myItem.getLineCode(); //alternatively, get outline level
		int level = myItem.getOutlineLevel();
		//The range of outline levels that will be included as headings/new blocks, starting at 0
		if (level<numLevels) { 
			myItem.setOutlineLevel(myItem.getOutlineLevel());
			String text = myItem.getParaString();
			String plain = myItem.getplainText(); //removed from <w:t>
			output=output+text+System.getProperty("line.separator");
			setHeaderText(plain);
			setStyleXML(myItem.getStyleXML());
			}
		//code 99 for paragraphs that are included in the blocks as general text/notes etc
		else  {
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
	for (xmlPara myItem: this.blockParas) {
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