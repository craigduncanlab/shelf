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

//called externally by docXML when new Styles.xml is obtained
public void setStylesXML(String input){
	this.stylesString=input;
  convertStylesXMLtoObjects();
  checkAndAddStyles();
  setOutlineNames(); //arg: ArrayList of xstyle. getStyles().  This is ALL styles.
  //makeSummaryStylesList(); 
  //checkForNoteStyle();
	//setLvl0Styles();
}

private void updateSummaryStylesString(String input) {
  this.stylesString=input;
  setOutlineNames(); //arg: ArrayList of xstyle. getStyles().  This is ALL styles.
  makeSummaryStylesList(); //prepare a list of styleId's as string for easy access
}

public String getStylesString(){
  return this.stylesString;
}

public String getSummaryStylesString(){
  return this.summaryStyleString;
}

public void setSummaryStylesString(String input){
  this.summaryStyleString=input;
}

//add a new style to existing Style List
public void addStyle(xstyle input){
	this.styleList.add(input);
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

public ArrayList<xstyle> getOutline0Styles(){
    return this.outline0Styles;
}

//return a subset of styles that satisfy outline level 1
public void setOutline1Styles(ArrayList<xstyle> input){
    this.outline1Styles=input;
}

public ArrayList<xstyle> getOutline1Styles(){
    return this.outline1Styles;
}

public void setPreStyle(String input){
    this.preStyles=input;
}

public String getPreStyle(){
  return this.preStyles;
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
    extractPreStyles(); //for each new Styles XML save the preparatory section
    //System.out.println("Extracted pre styles");
  }

private void checkAndAddStyles() {
    String newStyles="";
    Boolean noteTest=false;
    Boolean codeTest=false;
    Boolean nrTest=false;
    for (xstyle currentStyle : this.styleList) {
       String thisId=currentStyle.getId();
       System.out.println(thisId);
       if (thisId.equals("Note")) {
        noteTest=true;
       }
       if (thisId.equals("Code")) {
        codeTest=true;
       }
       if (thisId.equals("NormalReally")) {
        nrTest=true;
       }
    }
    
    if (codeTest==false){
      addCodeStyle();
    }
    if (noteTest==false){
      addNoteStyle();
    }
    if (nrTest==false){
      addNormalReallyStyle();
    }
    //update styles if necessary
    if (codeTest==false || noteTest==false || nrTest==false) {
      newStyles=recreateStylesFile();
      updateSummaryStylesString(newStyles);
    }
    //System.out.println(newStyles);
    //System.exit(0);
}

//TO DO: custom parameter setter for xstyle objects
//<w:aliases w:val="RCode,PythonCode"/>
////Source Code F8F8F8 not 00E2AA
public void addCodeStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Code\"><w:name w:val=\"Code\"/><w:aliases w:val=\"RCode,PythonCode\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F8F8F8\"/></w:pPr><w:rPr><w:i/><w:rFonts ascii=\"Consolas\" w:hAnsi=\"Consolas\"</w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleXML(codeStyle);
  addStyle(newStyle); //add to current list of styles.
  //System.out.println("Code style added");
  //System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
}

public void addNoteStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Note\"><w:name w:val=\"Note\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"E3D200\"/></w:pPr><w:rPr><w:i/></w:rPr><w:outlineLvl w:val=\"4\"/></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleXML(codeStyle);
  addStyle(newStyle); //add to current list of styles.
  //System.out.println("Note style added");
  //System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
}

public void addNormalReallyStyle() {
  /*
  String codeStyle="<w:style w:type=\"paragraph\" w:styleId=\"NormalReally\"><w:name w:val=\"NormalReally\"/><w:basedOn w:val=\"Normal\"/><w:uiPriority w:val=\"0\"/><w:qFormat/><w:pPr><w:jc w:val=\"both\"/><w:outlineLvl w:val=\"4\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Times New Roman\" w:eastAsia=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\"/><w:sz w:val=\"22\"/><w:lang w:bidi=\"ar-SA\"/></w:rPr></w:style>";
  */
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("NormalReally",4,"Times New Roman",10,""); //id, outline,font,size, shade
  addStyle(newStyle); //add to current list of styles (via XML string).
  /*
  System.out.println("Normal Really style added");
  System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
  System.exit(0);
  */
}

//<?xml 
public void extractPreStyles(){
    String contents = this.stylesString;
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

//For loading external list of styles (if needed)
//input file must have .xml extension
//This loads from the dbstyles folder (may need a File Chooser program)
//This should work with an XMLStyles Object, so make it external.

public ArrayList<xstyle>loadNewStylesDB(String inputstyle){
  //External list of styles
  String filename = "dbstyles/"+inputstyle;; //file with a list of xml coded styles
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
  return myStyleObjects; //return, do not specify use
}


public String recreateStylesFile(){
  String output=getPreStyle();
  ArrayList <xstyle> myStyleList = getStyleObjects();
  for (xstyle item : myStyleList) {
    output=output+item.getStyleXML();
  }
  output=output+"</w:styles>";
  System.out.println(output);
  //System.exit(0);
  return output;
}

}