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
	int outlineLevel=0; //a way to classify blocks for display etc.
	String StyleXML="";
	String styleId=""; //corresponds to the style id in the paragraph that forms the header etc
	String blockBookmark="";
	ArrayList<String>bookmarkList=new ArrayList<String>();
	String plaintext=""; //text in <w:t> tags

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

//fule text of style object, not the id
public void setStyleXML(String input){
	this.StyleXML=input;
}

public String getStyleXML(){
	return this.StyleXML;
}

public void setStyleId(String input){
	this.styleId=input;
}

public String getStyleId(){
	return this.styleId;
}

public void setBookmark(String input){
	this.blockBookmark=input;
}

public String getBookmark(){
	return this.blockBookmark;
}

public void importblockParas(ArrayList<xmlPara> myParas){
	for (xmlPara myItem: myParas) {
		blockParas.add(myItem);
	}
}

public void setOutlineLevel(int input){
	this.outlineLevel=input;
}

//outline level for the para that forms the first block after dividing text
public int getOutlineLevel(){
	return this.outlineLevel;
}

//Does not update the blocktext each time a new xmlPara object is added to the block
public void addLineObject(xmlPara myObject){
	blockParas.add(myObject);
}

public int getStoredLines(){
	return blockParas.size();
}

//make text from <w:p> paragraphs in this block.  
public void makeBlockXMLfromXMLParas(){
	String XMLoutput="";
	String plainoutput="";
	int numLevels=1;
	int referenceLevel=0;
	for (xmlPara myItem: this.blockParas) {
		int code = myItem.getLineCode(); //alternatively, get outline level
		int level = myItem.getOutlineLevel();
		//The range of outline levels that will be included as headings/new blocks, starting at 0
		//if (level<numLevels) { 
		if (level==referenceLevel) { 
			myItem.setOutlineLevel(myItem.getOutlineLevel());
			setOutlineLevel(referenceLevel);
			String text = myItem.getParaString();
			String plain = myItem.getplainText(); //removed from <w:t>
			XMLoutput=XMLoutput+text+System.getProperty("line.separator");
			setHeaderText(plain);
			setStyleXML(myItem.getStyleXML());
			setStyleId(myItem.getpStyle()); //styleId
			setBookmark(myItem.getBookmarkName()); //only if a bookmark coincides with a reference level
			}
		//code 99 for paragraphs that are included in the blocks as general text/notes etc
		else  {
			String text = myItem.getParaString();
			String plaintext = myItem.getplainText();
			XMLoutput=XMLoutput+text+System.getProperty("line.separator");
			plainoutput=plainoutput+plaintext+System.getProperty("line.separator");
			//make a list of bookmarks that are in this block of XML
			if (myItem.getBookmarkName().length()>0) {
				this.bookmarkList.add(myItem.getBookmarkName());
			}
		}
	}
	setBlockXMLText(XMLoutput);
	setPlainText(plainoutput);
	}

public void makePlainTextfromXMLParas(){
	String plainoutput="";
	int numLevels=1;
	int referenceLevel=0;
	for (xmlPara myItem: this.blockParas) {
		int code = myItem.getLineCode(); //alternatively, get outline level
		int level = myItem.getOutlineLevel();
		//The range of outline levels that will be included as headings/new blocks, starting at 0
		//if (level<numLevels) { 
		if (level==referenceLevel) { 
			//DO NOTHING
			}
		
		else  {
			String plaintext = myItem.getplainText();
			plainoutput=plainoutput+plaintext+System.getProperty("line.separator");
			}
		}
	setPlainText(plainoutput);
	}

public String getBookmarkListAsString(){
	String output="";
	for (String item : bookmarkList) {
		output=output+item+System.getProperty("line.separator");
	}
	return output;
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

public void setPlainText(String input){
	this.plaintext=input;
}

public String getPlainText(){
	return this.plaintext;
}

public void setBlockXMLText(String input) {
	this.blockText=input;
}

//gets block text based on current block (xmlParas list)
public String getBlockXMLText(){
	makeBlockXMLfromXMLParas();
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