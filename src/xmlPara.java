//mdSubset Class to hold generic para or line-related data from docx text streams
/*
This retains line codes from original file, to help subdivide for Books object later.
It is a helper Class in the data model that processes text stream into Books
*/

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class xmlPara {
	String lineText="";
	int lineIndex=0;
	int blocktype=99; //default.  linecode.  in substance this is the 'fileindex' code for this line
	String pStyle="";
	String paraId="";
	String textId="";
	String numId="";
	int outLineLevel=99; //TO DO: maybe store here if the style indicates the outline level?
	String plainText="";
	String styleXML="";
	String bookmarkName="";
	String bookmarkId="";

//constructor
public xmlPara(){

}

public int getLineCode(){
	return this.blocktype;
}

public void setLineCode(int input){
	this.blocktype=input;
}

//Main function to set the text for this para, and extract the paragraph style name and blockcode (0= outline level 0)
public void setParaString(String input){
	this.lineText=input;
	extractParams();
}

public String getParaString(){
	return this.lineText;
}

public void setLineIndex(int input){
	this.lineIndex=input;
}

public int getLineIndex(){
	return this.lineIndex;
}

public void setBookmarkId(String input){
	this.bookmarkId=input;
}

public String getBookmarkId(){
	return this.bookmarkId;
}

public void setBookmarkName(String input){
	this.bookmarkName=input;
}

public String getBookmarkName(){
	return this.bookmarkName;
}

public String getpStyle(){
	return this.pStyle;
}

public void setpStyle(String input){
	this.pStyle=input;
}

public String getparaId(){
	return this.paraId;
}

//HEX number
public void setparaId(String input){
	this.paraId=input;
}

public String gettextId(){
	return this.paraId;
}

public void settextId(String input){
	this.paraId=input;
}

public String getplainText(){
	return this.plainText;
}

public void setplainText(String input){
	this.plainText=input;
}

public int getOutlineLevel(){
	return this.outLineLevel;

}

//usually done by comparing it to the relevant style parameter, at document level.
public void setOutlineLevel(int input){
	this.outLineLevel=input;
}

public String getStyleXML(){
	return this.styleXML;
}

public void setStyleXML(String input){
	this.styleXML=input;
}

// --- 

/*
input: name of parameter, where it is specified in OOXML tag.  Do not include = or ""

output: value of parameter

*/

public void extractParams(){
	extractpStyle();
	extractParaId();
	extractTextId();
	extractTextTags();
	extractBookmarkStart();
}

/* 
get paragraph's styleId of the current document.xml paragraph <w:p>
*/

public void extractpStyle(){
	String parameter = "pStyle w:val";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.lineText,parameter);
	//System.out.println("Detected style in this para:"+output);
	setpStyle(output);
}

//:textId

public void extractTextId(){
	String parameter = ":textId";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.lineText,parameter);
	//System.out.println("Detected style in this para:"+output);
	settextId(output);
}

public void extractParaId(){
	String parameter = ":paraId";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.lineText,parameter);
	//System.out.println("Detected style in this para:"+output);
	setparaId(output);
}

private void extractBookmarkStart() {
	String text = extractBookmarkStartTag();
	String parameter = "bookmarkStart w:id";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(text,parameter);
	setBookmarkId(output);
	String parameter2 = "w:name";
	String output2= myP.getParameterValue(text,parameter2);
	setBookmarkName(output2);
}

private String extractBookmarkStartTag(){
	String output="";
	String starttag="<w:bookmarkStart ";
	String endtag="/>";
	xmlTool myTool = new xmlTool();
	ArrayList<String> result = myTool.getTagAttribInclusive(this.lineText,starttag,endtag); 
	if (result.size()>0) {
		output=result.get(0); //first result
	}
	return output;
}

/*
input: a paragraph string from document.xml

output: just the <w:t> tags section
*/

public void extractTextTags(){
	String starttag="<w:t>";
	String endtag="</w:t>";
	xmlTool myTool = new xmlTool();
	ArrayList<String> result = myTool.getTagAttribInclusive(this.lineText,starttag,endtag); 
	ArrayList<String> result2 =  new ArrayList<String>();
	//remove ad hoc internal <w:t> tag
	for (String item: result) {
		item = item.replace("<w:t xml:space=\"preserve\">",""); 
		result2.add(item);
	}
	String output = removeTags(result2,starttag,endtag);
	//System.out.println(output);
	setplainText(output);
}

/*
Remove input tags from string
Input: a <w:t> set from <x:p> sourced from document.xml

*/

public String removeTags(ArrayList<String> inputList, String tag1, String tag2) {
	String empty="";
	String output="";
	for (String input: inputList) {
		//TO DO: move this to <w:t> text specific area
		String output1 = input.replace(tag1,empty);
		String output2 = output1.replace(tag2,empty);
		output=output+output2;
	}
	return output;
}

}