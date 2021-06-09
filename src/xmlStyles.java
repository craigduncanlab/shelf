//Class to hold a docx Styles list and operate on it (manager)

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class xmlStyles {
	String stylesString="";
  String summaryStyleString="";
	ArrayList<xstyle> styleList =new ArrayList<xstyle>();
  ArrayList<xstyle> outlineStyles; //All styles with an outline level
	ArrayList<xstyle> outline0Styles; //Level 0 style names
  ArrayList<xstyle> outline1Styles; //Level 1 style names (id)
  String preStyles=""; //to hold first part of a docx styles.xml file
//constructor
public xmlStyles(){
	
}

//add a new style to existing Style List
public void addStyle(xstyle input){
  this.styleList.add(input);
}

private void updateSummaryStylesString() {
  setOutlineNames(); //arg: ArrayList of xstyle. getStyles().  This is ALL styles.
  makeSummaryStylesList(); 
}

//called externally by docXML when new Styles.xml is obtained
public void setStylesXML(String input){
	this.stylesString=input;
  extractPreStyles();
  convertStylesXMLtoObjects();
  updateSummaryStylesString();
}
  
public String getStylesXML(){
  return this.stylesString;
}

public void setPreStyle(String input){
    this.preStyles=input;
}

private String getPreStyle(){
  return this.preStyles;
}

public String getSummaryStylesString(){
  return this.summaryStyleString;
}

public void setSummaryStylesString(String input){
  this.summaryStyleString=input;
}

//swap in a new style list
public void setNewStyleList(ArrayList<xstyle> input){
  this.styleList=input;
}

public ArrayList<xstyle> getStyleObjects(){
	return this.styleList;
}

//All styles with an outline level
public void setOutlineStyles(ArrayList<xstyle> input){
    this.outlineStyles=input;
}

public ArrayList<xstyle> getOutlineStyles(){
    return this.outlineStyles;
}

//return a subset of styles that satisfy outline level 0
public void setOutline0Styles(ArrayList<xstyle> input){
    this.outline0Styles=input;
}

public ArrayList<xstyle> getFields(){
    return this.outline0Styles;
}

public String getFieldsAsString(){
  String output="";
  for (xstyle item: outline0Styles){
    output=output+item.getId()+" ["+item.getOutlineLevel()+"]"+System.getProperty("line.separator");
  }
  return output;
}

//return a subset of styles that satisfy outline level 1
public void setOutline1Styles(ArrayList<xstyle> input){
    this.outline1Styles=input;
}

public ArrayList<xstyle> getOutline1Styles(){
    return this.outline1Styles;
}

/* 
Find style tags in styles.xml and return as a String array.
Then create array of 'xstyle' objects to hold elements.
A Word document styles.xml might have a style list with hundreds of defined 'styles' 
This process will *always* add Note and Code styles if they are not there.
*/

public void convertStylesXMLtoObjects(){
	String contents = this.stylesString;
    String starttag="<w:style ";
    String endtag="</w:style>";
    xmlTool myP = new xmlTool();
    ArrayList<xstyle> myStyles = new ArrayList<xstyle>();
    ArrayList<String> result=myP.getTagAttribInclusive(contents,starttag,endtag);
    System.out.println("Style List (main)");
    for (String item: result) {
    	 xstyle currentStyle= new xstyle();
    	 currentStyle.setStyleXML(item);
    	 addStyle(currentStyle);
    }
    //for each new Styles XML save the preparatory section
    //System.out.println("Extracted pre styles");
  }

//prepare a list of styleId's, based on styleIdObjects, as string for easy access
private void makeSummaryStylesList(){
  ArrayList<xstyle> styleList = getStyleObjects();
  String output="";
  for (xstyle item : styleList) {
    //System.out.println("summary item:"+item.getId()+","+item.getName());
    output=output+item.getId()+" ["+item.getOutlineLevel()+"]"+System.getProperty("line.separator");
  }
  if (getSummaryStylesString().length()>0) {
    System.out.println("Trying to amend summary styles list but it exists");
    //System.exit(0);
  }
  setSummaryStylesString(output);
}

/*
input: inclusive arraylist of styles tags from styles.xml (as full strings)

output: only a list of names of styles that contain outline lvl= 0
*/

public void setOutlineNames() {
      ArrayList<xstyle> output = new ArrayList<xstyle>();
      ArrayList<xstyle> output0 = new ArrayList<xstyle>();
      ArrayList<xstyle> output1 = new ArrayList<xstyle>();
      int length = getStyleObjects().size();  // number of blocks
      if (length>0) {
      	ArrayList<xstyle> styleList = getStyleObjects();
        Iterator<xstyle> iter = styleList.iterator();  //the stored Stylelist (type xstyle)
          while (iter.hasNext()) {
              xstyle item = iter.next();
              int level=item.getOutlineLevel(); 
              //If we have any outline level at all, add to list
              if (level!=99) {
              		output.add(item);
              }
              if (level==0) {
                  output0.add(item);
              }
              if (level==1) {
                  output1.add(item);
              }
         } //end while
      } //end if
      setOutlineStyles(output);
      setOutline0Styles(output0);
      setOutline1Styles(output1);
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
    myNewItem.setStyleXML(item); //this will extract & set relevant name, id inside object automatically.
    myStyleObjects.add(myNewItem); 
  }
}

private void extractPreStyles(){
    String contents = this.stylesString;
    if (this.stylesString.length()==0){
      System.out.println("No styles string in xmlStyles");
      System.exit(0);
    }
     System.out.println("ss:"+this.stylesString);
    String starttag="<?xml";  //start of the xml file.
    String endtag="<w:style "; //end at the first style tag.
    xmlTool myP = new xmlTool();
    
    ArrayList<String> result=myP.getTagAttribInclusive(contents,starttag,endtag);
    if (result.size()>=0) {
      preStyles=result.get(0); //get first array item as the string
    }
    System.out.println("result:"+result.toString());
    System.out.println("starttag:"+starttag);
    String subs=preStyles.substring(0,preStyles.length()-endtag.length());
    setPreStyle(subs);
    System.out.println("Start of style file:");
    System.out.println(getPreStyle());
}


public void recreateStylesFile(){
  String output=getPreStyle();
  ArrayList <xstyle> myStyleList = getStyleObjects();
  for (xstyle item : myStyleList) {
    output=output+item.getStyleXML();
  }
  output=output+"</w:styles>";
  System.out.println(output);
  //System.exit(0);
  //return output;
  //update here
  this.stylesString=output;
  setOutlineNames();
  updateSummaryStylesString();
}

//helper for logging
public void logging(){
   ArrayList<xstyle> myStyleList = getStyleObjects();
   for (xstyle updatedStyle : myStyleList) {
      System.out.println(updatedStyle.getId());
  }
  System.out.println("\n Style String...");
  System.out.println(getStylesXML());
  System.out.println("\n Now for summary...");
  System.out.println(getSummaryStylesString());
}

}