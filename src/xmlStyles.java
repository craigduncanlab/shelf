//Class to hold a docx Styles list and operate on it

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class xmlStyles {
	String stylesString="";
	ArrayList<xstyle> styleList =new ArrayList<xstyle>();
  ArrayList<xstyle> outlineStyles; //All styles with an outline level
	ArrayList<xstyle> outline0Styles; //Level 0 style names
  ArrayList<xstyle> outline1Styles; //Level 1 style names (id)
  String preStyles=""; //to hold first part of a docx styles.xml file
//constructor
public xmlStyles(){
	
}

public void setStylesString(String input){
	this.stylesString=input;
	makeStylesAsObjects();
	setOutlineNames(); //arg: ArrayList of xstyle. getStyles().  This is ALL styles.
  //checkForNoteStyle();
	//setLvl0Styles();
}

public String getStylesString(){
  return this.stylesString;
}

public void addStyle(xstyle input){
	this.styleList.add(input);
}

public ArrayList<xstyle> getStyles(){
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

public void makeStylesAsObjects(){
	String contents = this.stylesString;
    String starttag="<w:style ";
    String endtag="</w:style>";
    xmlTool myP = new xmlTool();
    Boolean noteTest=false;
    Boolean codeTest=false;
    ArrayList<xstyle> myStyles = new ArrayList<xstyle>();
    ArrayList<String> result=myP.getTagAttribInclusive(contents,starttag,endtag);
    System.out.println("Style List (main)");
    for (String item: result) {
    	 xstyle currentStyle= new xstyle();
    	 currentStyle.setStyle(item);
    	 addStyle(currentStyle);
       String thisId=currentStyle.getId();
       System.out.println(thisId);
       if (thisId.equals("Note")) {
        noteTest=true;
       }
       if (thisId.equals("Code")) {
        codeTest=true;
       }
    }
    System.out.println("Style analysis:");
    System.out.println("Note StyleId present:"+noteTest);
    System.out.println("Code StyleId present:"+codeTest);
    if (codeTest==false){
      addCodeStyle();
    }
    if (noteTest==false){
      addNoteStyle();
    }
    //update styles if necessary
    if (codeTest==false || noteTest==false) {
      extractPreStyles(); //do this before make styles
      String newStyles=recreateStylesFile();
      setStylesString(newStyles);
    }
}

//TO DO: custom parameter setter for xstyle objects
public void addCodeStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Code\"><w:name w:val=\"Code\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"00E2AA\"/></w:pPr><w:rPr><w:i/></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyle(codeStyle);
  addStyle(newStyle); //add to current list of styles.
  System.out.println("Code style added");
  System.out.println(newStyle.getId()+":"+newStyle.getStyle());
}

public void addNoteStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Note\"><w:name w:val=\"Note\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"E3D200\"/></w:pPr><w:rPr><w:i/></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyle(codeStyle);
  addStyle(newStyle); //add to current list of styles.
  System.out.println("Note style added");
  System.out.println(newStyle.getId()+":"+newStyle.getStyle());
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

/*
input: inclusive arraylist of styles tags from styles.xml (as full strings)

output: only a list of names of styles that contain outline lvl= 0
*/

public void setOutlineNames() {
      ArrayList<xstyle> output = new ArrayList<xstyle>();
      ArrayList<xstyle> output0 = new ArrayList<xstyle>();
      ArrayList<xstyle> output1 = new ArrayList<xstyle>();
      int length = getStyles().size();  // number of blocks
      if (length>0) {
      	ArrayList<xstyle> styleList = getStyles();
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
      /*
      for (xstyle item: output) {
        System.out.println(item.getId());
        System.out.println(item.getOutlineLevel());
        System.out.println(item.getStyle());
      }
      System.exit(0);
      */
      setOutline0Styles(output0);
      setOutline1Styles(output1);
}



//For loading external list of styles (if needed)
public void loadStylesDB(){
  //External list of styles
  String filename = "wordlib/styles_db.xml"; //file with a list of xml coded styles
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
    myNewItem.setStyle(item); //this will extract relevant name, id inside object automatically.
    myStyleObjects.add(myNewItem); 
  }
}

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

}