//class to prepare custom Style Themes to insert into docx documents
//Encapsulates new style sheet here until tested
import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class XMLStyleThemeMaker{
	
	xmlStyles currentXMLStyles = new xmlStyles();
	xmlStyles updatedXMLStyles = new xmlStyles();
	ArrayList<xstyle> styleList = new ArrayList<xstyle>();
	//mdStyles markdownStyleList = new mdStyles();

public XMLStyleThemeMaker(){
	
}



public void setCurrentStylesXML(xmlStyles input){
	this.currentXMLStyles = input;

}

public xmlStyles getCurrentStylesXML(){
	return this.currentXMLStyles;
}

public xmlStyles getUpdatedStylesXML(){
	return this.updatedXMLStyles;
}

public void addMDStyles() {
	ArrayList<xstyle> myStyleList = getCurrentStylesXML().getStyleObjects();
	/*
	System.out.println("These are the id's of all styles in cloned docx styles");
	for (xstyle currentStyle : myStyleList) {
	 		System.out.println(currentStyle.getId());
	}
	System.exit(0);
	*/
	updatedXMLStyles.setStylesXML(currentXMLStyles.getStylesXML());
	ArrayList<xstyle> myUStyleList = getUpdatedStylesXML().getStyleObjects();
	/*
	ArrayList<xstyle> myUStyleList = getUpdatedStylesXML().getStyleObjects();
	System.out.println("These are the id's of all styles in cloned docx styles");
	for (xstyle updatedStyle : myUStyleList) {
	 		System.out.println(updatedStyle.getId());
	}
	System.exit(0);
	*/
	String newStyles="";
    Boolean noteTest=false;
    Boolean codeTest=false;
    Boolean nrTest=false;
    Boolean eventTest=false;
    int failedTests=0;
    for (xstyle aStyle : myUStyleList) {
       String thisId=aStyle.getId();
       //System.out.println(thisId);
       if (thisId.equals("Note")) {
        noteTest=true;
       }
       if (thisId.equals("Code")) {
        codeTest=true;
       }
       if (thisId.equals("RecordHeading")) {
        nrTest=true;
       }
       if (thisId.equals("Event")) {
        eventTest=true;
       }
    }
    
    if (codeTest==false){
      addCodeStyle();
      failedTests++;
    }
    if (nrTest==false){
      addNormalRecordStyle();
      failedTests++;
    }
    if (noteTest==false){
      addNoteStyle();
      failedTests++;
    }
    if (eventTest==false){
    	failedTests++;
    	addEventStyle();
    }
    //update styles if necessary
    if (codeTest==false || noteTest==false || nrTest==false ) {
      //put details into new object
    updatedXMLStyles.recreateStylesFile();

    //myUStyleList = getUpdatedStylesXML().getStyleObjects();
	}
}

//TO DO: custom parameter setter for xstyle objects
//<w:aliases w:val="RCode,PythonCode"/>
////Source Code F8F8F8 not 00E2AA
//shad can be a run or a paragraph property

//id, BasedOn,afterPara,outline,font,size,shade, afterPts,color
private void addCodeStyle(){
  
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("Code","Normal","",4,"Consolas",22,"shade",0,"",""); 
  
  String test = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Code\"><w:name w:val=\"Code\"/><w:basedOn w:val=\"Normal\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:outlineLvl w:val=\"4\"/><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F8F8F8\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Consolas\" w:hAnsi=\"Consolas\"/><w:sz w:val=\"22\"/></w:rPr></w:style>";
  if (!newStyle.getStyleXML().equals(test)){
  	System.out.println("Test:");
  	System.out.println(test);
  	System.out.println("Failed:");
  	System.out.println(newStyle.getStyleXML());
  	System.exit(0);
  }
  //newStyle.setStyleXML(codeStyle);
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles.
}

//id, BasedOn,afterPara,outline,font,size,shade, afterPts,color
private void addNoteStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Note\"><w:name w:val=\"Note\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"E3D200\"/></w:pPr><w:rPr><w:i/></w:rPr><w:outlineLvl w:val=\"4\"/></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("Note","Normal","",4,"Times New Roman",23,"note",0,"",""); //id, outline,font,size, shade
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles.
}

public void addEventStyle() {
   String test="<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Event\"><w:name w:val=\"Event\"/><w:basedOn w:val=\"Heading1\"/><w:next w:val=\"Normal\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:outlineLvl w:val=\"0\"/><w:spacing w:after=\"120\"/></w:pPr><w:rPr><w:color w:val=\"2F5496\"/><w:sz w:val=\"32\"/></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("Event","Heading1","Normal",0,"",32,"",120,"blue",""); 
  if (!newStyle.getStyleXML().equals(test)){
  	System.out.println("Test:");
  	System.out.println(test);
  	System.out.println("Failed:");
  	System.out.println(newStyle.getStyleXML());
  	System.exit(0);
  }
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles.
}

private void addNormalRecordStyle() {

  String test="<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"RecordHeading\"><w:name w:val=\"RecordHeading\"/><w:basedOn w:val=\"Normal\"/><w:next w:val=\"Normal\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:outlineLvl w:val=\"0\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\"/><w:b/><w:sz w:val=\"22\"/></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("RecordHeading","Normal","Normal",0,"Times New Roman",22,"",0,"","bold"); 
  if (!newStyle.getStyleXML().equals(test)){
  	System.out.println("Test:");
  	System.out.println(test);
  	System.out.println("Failed:");
  	System.out.println(newStyle.getStyleXML());
  	System.exit(0);
  }
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles (via XML string).
}


public void addLetterStyles() {
	ArrayList<xstyle> myStyleList = getCurrentStylesXML().getStyleObjects();
	/*
	System.out.println("These are the id's of all styles in cloned docx styles");
	for (xstyle currentStyle : myStyleList) {
	 		System.out.println(currentStyle.getId());
	}
	System.exit(0);
	*/
	updatedXMLStyles.setStylesXML(currentXMLStyles.getStylesXML());
	ArrayList<xstyle> myUStyleList = getUpdatedStylesXML().getStyleObjects();
	/*
	ArrayList<xstyle> myUStyleList = getUpdatedStylesXML().getStyleObjects();
	System.out.println("These are the id's of all styles in cloned docx styles");
	for (xstyle updatedStyle : myUStyleList) {
	 		System.out.println(updatedStyle.getId());
	}
	System.exit(0);
	*/
	String newStyles="";
    Boolean dateTest=false;
    Boolean writerTest=false;
    Boolean addressTest=false;
    Boolean subjectTest=false;
    Boolean messageTest=false;
    Boolean recipientTest=false;
    int failedTests=0;
    for (xstyle aStyle : myUStyleList) {
       String thisId=aStyle.getId();
       //System.out.println(thisId);
       if (thisId.equals("Address")) {
        addressTest=true;
       }
       if (thisId.equals("Recipient")) {
        recipientTest=true;
       }
       if (thisId.equals("Writer")) {
        writerTest=true;
       }
       if (thisId.equals("Date")) {
        dateTest=true;
       }
       if (thisId.equals("SubjectLine")) {
        subjectTest=true;
       }
       if (thisId.equals("Message")) {
        messageTest=true;
       }
    }
    
    if (addressTest==false){
      addLevel0Style("Address");
      failedTests++;
    }
    if (addressTest==false){
      addLevel0Style("Recipient");
      failedTests++;
    }
    if (writerTest==false){
      addLevel0Style("Writer");
      failedTests++;
    }
    if (dateTest==false){
      addLevel0Style("Date");
      failedTests++;
    }
    if (subjectTest==false){
      addLevel0Style("SubjectLine");
      failedTests++;
    }
    if (messageTest==false){
      addLevel0Style("Message");
      failedTests++;
    }
   
    //update styles if necessary
    if (failedTests>0) {
    	//put details into new object
    	updatedXMLStyles.recreateStylesFile();
	}
}

//All it basically requires is a name for the style, its 'styleId'
public void addLevel0Style(String input){
  addStyleOutline0BasedOnNormal(input,0); 
}

//All it basically requires is a name for the style, its 'styleId'
public void addLevel1Style(String input){
  addStyleOutline0BasedOnNormal(input,1); 
}

//Does not set font or text size.  Based on Normal.
//Sets outline level to 0, with the font name

private void addStyleOutline0BasedOnNormal(String styleName, int level) {

  String test="<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\""+styleName+"\"><w:name w:val=\""+styleName+"\"/><w:basedOn w:val=\"Normal\"/><w:next w:val=\"Normal\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:outlineLvl w:val=\"0\"/></w:pPr><w:rPr></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  //(String id, String basedOn, String nextp, int outline, String font, int size, String shade, int after, String color, String format)
  newStyle.setStyleAttrib(styleName,"Normal","Normal",level,"",0,"",0,"",""); 
  if (!newStyle.getStyleXML().equals(test)){
  	System.out.println("Test:");
  	System.out.println(test);
  	System.out.println("Failed:");
  	System.out.println(newStyle.getStyleXML());
  	System.exit(0);
  }
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles (via XML string).
}

private void addBoldStyleBasedOnNormal(String styleName) {

  String test="<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\""+styleName+"\"><w:name w:val=\""+styleName+"\"/><w:basedOn w:val=\"Normal\"/><w:next w:val=\"Normal\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:outlineLvl w:val=\"0\"/></w:pPr><w:rPr><w:b/></w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib(styleName,"Normal","Normal",0,"",0,"",0,"","bold"); 
  if (!newStyle.getStyleXML().equals(test)){
  	System.out.println("Test:");
  	System.out.println(test);
  	System.out.println("Failed:");
  	System.out.println(newStyle.getStyleXML());
  	System.exit(0);
  }
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles (via XML string).
}


//For loading external list of styles (if needed)
//input file must have .xml extension
//This loads from the dbstyles folder (may need a File Chooser program)
//This should work with an XMLStyles Object, so make it external.

//TO DO: this should update a new xmlStyles object

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

}