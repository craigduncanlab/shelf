//Class to hold a set of xmlStyles and work with them (manager)

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class xmlStylesManager {
	String stylesString="";
	xmlStyles myReferenceStyle =new xmlStyles();
  xmlStyles myActiveDocStyle =new xmlStyles();

//constructor
public xmlStylesManager(){
	
}

//For loading external list of styles (if needed)
public void loadDefaultStylesDB(){
  //External list of styles
  String filename = "dbstyles/styles_db.xml"; //file with a list of xml coded styles
  File myFile = new File(filename);
  ZipUtil myTool = new ZipUtil();
  String stylestext=myTool.getFileText(myFile);
  
  xmlTool myTag = new xmlTool();
  String start = "<w:style ";
  String end = "</w:style>";
  ArrayList<String> myStylesStrings = myTag.getTagAttribInclusive(stylestext,start,end);
  ArrayList<xstyle> myStyleObjects = new ArrayList<xstyle>();
  for (String item : myStylesStrings) {
    xstyle myNewItem = new xstyle();
    myNewItem.setStyleXML(item); //this will extract and set relevant name, id inside object automatically.
    myStyleObjects.add(myNewItem); 
  }
}


/*
//This is intended to take the upper part of a loaded Styles file, then add in the styles from a secondary source
//TO DO: make this operate with xmlStyles objects (loaded, saved etc)
public String recreateStylesFile(){
  String output=getPreStyle();
  ArrayList <xstyle> myStyleList = getStyles();
  for (xstyle item : myStyleList) {
    output=output+item.getStyle();
  }
  output=output+"</w:styles>";
  System.out.println(output);
  //System.exit(0);
  return output;
}
*/

public void setReferenceStyle(xmlStyles input){
  this.myReferenceStyle=input;
}

public xmlStyles getReferenceStyle(){
  return this.myReferenceStyle;
}

public void setActiveStyle(xmlStyles input){
  this.myActiveDocStyle = input;
}

//An active styles object
public xmlStyles getActiveStyle(){
  return this.myActiveDocStyle;
}

//load as reference?
public xmlStyles loadNewStyleObject(String filename){
  xmlStyles newStyle = new xmlStyles();
  newStyle.loadNewStylesDB(filename);
  //setActiveStyle(newStyle);
  return newStyle;
}

//load custom legal contract style list
public void loadLegalStyles(){
  xmlStyles newstyle = loadNewStyleObject("legalcontract.xml");
  setReferenceStyle(newstyle);
}

//Take in the offered document, add Styles, return 
/*
public ArrayList <xstyle> addInReferenceStyles(ArrayList<xmlStyles> input) {
    ArrayList<xmlStyles> myNewStyleList = input;
    ArrayList<xstyle> = currentStyles = myNewStyleList.getStyles();
    for (xstyle item : currentStyles) {
        myNewStyleList.add(item);
    }
    return myNewStyleList;
}
*/
}