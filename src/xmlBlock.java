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

	//The parameters that the 'block' might hold depend on how we split the file
	String blockText="";
	String notesText="";
	String headerText="";
	int outlineLevel=0; //a way to classify blocks for display etc.
	String StyleXML="";
	String styleId=""; //corresponds to the style id in the paragraph that forms the header etc
	String blockBookmark="";
	ArrayList<String>bookmarkList=new ArrayList<String>();
	String plaintext=""; //text in <w:t> tags
	String splitType="OutlineLvl0"; //default
	String bookmarkedText="";

//constructor
public xmlBlock(){
	setHeaderText("First page");
}

public void initialiseBlockContents(){
	makeBlockXMLfromXMLParas();
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

public void setBookmarkedText(String input){
	this.bookmarkedText=input;
}

public String getBookmarkedText(){
	return this.bookmarkedText;
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

/* We have a block and we need to populate sub-fields, elements of the block.

This function populates some of the block-level meta-data from the set of xmlParas it contains
It assumes the first xmlPara is the most significant, based on the block-splitting that occurred prior.
(previously the test was: is the outlinelevel of xmlPara=0, but if this is already established, no need to recheck first para)
*/

private void makeBlockXMLfromXMLParas(){
	int paracount=0;
	for (xmlPara myItem: this.blockParas) {
		int code = myItem.getLineCode(); //alternatively, get outline level
		int level = myItem.getOutlineLevel();
		if (paracount==0) { //use first para for block metadata: no longer dependent on block splitting criteria
			if(getSplitType().equals("OutlineLvl0")) { 
				setOutlineLevel(0); //unnecessary?
				setBlockParametersAtSplitPoint(myItem);
				//do not add in the header (plaintext) line to the rest of the block para.
			}
			else if (getSplitType().equals("Bookmark")){
				myItem.setOutlineLevel(myItem.getOutlineLevel()); //??
				setBlockParametersAtSplitPoint(myItem);
				setHeaderText(myItem.getBookmarkId());  //TO DO: capture header text from last line of previous block.  Do externally.
			}
			else if (getSplitType().equals("PageBreak")){
				setBlockParametersAtSplitPoint(myItem);
				//TO DO: capture header text from last line of previous block.  Do externally.
			}
			//add in text on split line to the block text, unless it is an outline heading
			if (!getSplitType().equals("OutlineLvl0")) {
				setPlainText(getPlainText()+myItem.getplainText()+System.getProperty("line.separator"));
			}
		}
		//line code 99 for paragraphs that are included in the blocks as general text/notes etc
		else  {
			String text = myItem.getParaString();
			setBlockXMLText(getBlockXMLText()+myItem.getParaString()+System.getProperty("line.separator"));
			setPlainText(getPlainText()+myItem.getplainText()+System.getProperty("line.separator"));
			//make a list of bookmarks that are in this block of XML.  If we split on bookmarks we only need first.
			if (getSplitType().equals("Bookmark") && myItem.getBookmarkName().length()>0) {
				this.bookmarkList.add(myItem.getBookmarkName());
			}
		}
		paracount=paracount+1;
	}
	}

private void setBlockParametersAtSplitPoint(xmlPara input){
	setBlockXMLText(getBlockXMLText()+input.getParaString()+System.getProperty("line.separator"));
	//cf are bookmarks always on same row as relevant text that describes them?  cf. FCA
	//block-level header
	setStyleXML(input.getStyleXML()); //block-level 'style' attribution, for outline level
	setStyleId(input.getpStyle()); //styleId for block-level style attribution
	setHeaderText(input.getplainText());
	setBookmark(input.getBookmarkName()); //only if a bookmark coincides with a reference level
	setBookmarkedText(input.getplainText());
	String bookmarkData=getBookmarkedText(); //put text of bookmark as header (rather than name)
	//setBookmarkData(bookmarkData);	
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

public void setSplitType(String input){
	this.splitType=input;
}

private String getSplitType(){
	return this.splitType;
}

public String getPlainText(){
	return this.plaintext;
}

public void setBlockXMLText(String input) {
	this.blockText=input;
}

//gets block text based on current block (xmlParas list).  OutlineLevel splits.  Called externally by Book constructor.
public String getBlockXMLText(){
	//makeBlockXMLfromXMLParas();
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