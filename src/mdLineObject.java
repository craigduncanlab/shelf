//mdSubset Class to hold generic line-related data from .md, docx text streams
/*
This retains line codes from original file, to help subdivide for Books object later.
It is a helper Class in the data model that processes text stream into Books
*/

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class mdLineObject {
	String lineText="";
	int lineIndex=0;
	int blocktype=1; //default.  in substance this is the 'fileindex' code for this line
	String headerText="";

//constructor
public mdLineObject(){

}

//set a linecode for this line based on a few prefix characters
//line code distinguishes between division in stream (#) or RMD headers
//setMDlinecode() is '0' or '1' - it doesn't code for comments.

public void setMDLineCode() {
		String thisRow=getLineText();
		int blocktype=1; //default, even if row length small
		//if row is long enough to check for hash prefix; if row is not already inside a notes section
		if (thisRow.length()>=2) {
			if (thisRow.substring(0,2).equals("# ")) {
				setLineCode(0);
				String hline = thisRow.substring(2,thisRow.length());
				setHeaderText(hline);
			}
		}
     //return blocktype;
 }

public int getLineCode(){
	return this.blocktype;
}

public void setLineCode(int input){
	this.blocktype=input;
}

public void setLineText(String input){
	this.lineText=input;
}

public String getLineText(){
	return this.lineText;
}

public void setLineIndex(int input){
	this.lineIndex=input;
}

public int getLineIndex(){
	return this.lineIndex;
}

private void setHeaderText(String input){
	this.headerText=input;
}

public String getHeaderText(){
	return this.headerText;
}

}