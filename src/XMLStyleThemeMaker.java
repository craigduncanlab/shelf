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
    for (xstyle aStyle : myUStyleList) {
       String thisId=aStyle.getId();
       //System.out.println(thisId);
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
      //put details into new object
    updatedXMLStyles.recreateStylesFile();

    myUStyleList = getUpdatedStylesXML().getStyleObjects();
	}
}

//TO DO: custom parameter setter for xstyle objects
//<w:aliases w:val="RCode,PythonCode"/>
////Source Code F8F8F8 not 00E2AA
private void addCodeStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Code\"><w:name w:val=\"Code\"/><w:aliases w:val=\"RCode,PythonCode\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"F8F8F8\"/></w:pPr><w:rPr><w:i/><w:rFonts ascii=\"Consolas\" w:hAnsi=\"Consolas\"</w:rPr></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleXML(codeStyle);
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles.
  //System.out.println("Code style added");
  //System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
}

private void addNoteStyle(){
  String codeStyle = "<w:style w:type=\"paragraph\" w:customStyle=\"1\" w:styleId=\"Note\"><w:name w:val=\"Note\"/><w:basedOn w:val=\"Indent1\"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"E3D200\"/></w:pPr><w:rPr><w:i/></w:rPr><w:outlineLvl w:val=\"4\"/></w:style>";
  xstyle newStyle = new xstyle();
  newStyle.setStyleXML(codeStyle);
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles.
  //System.out.println("Note style added");
  //System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
}
private void addNormalReallyStyle() {
  /*
  String codeStyle="<w:style w:type=\"paragraph\" w:styleId=\"NormalReally\"><w:name w:val=\"NormalReally\"/><w:basedOn w:val=\"Normal\"/><w:uiPriority w:val=\"0\"/><w:qFormat/><w:pPr><w:jc w:val=\"both\"/><w:outlineLvl w:val=\"4\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Times New Roman\" w:eastAsia=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\"/><w:sz w:val=\"22\"/><w:lang w:bidi=\"ar-SA\"/></w:rPr></w:style>";
  */
  xstyle newStyle = new xstyle();
  newStyle.setStyleAttrib("NormalReally",4,"Times New Roman",10,""); //id, outline,font,size, shade
  updatedXMLStyles.addStyle(newStyle); //add to current list of styles (via XML string).
  /*
  System.out.println("Normal Really style added");
  System.out.println(newStyle.getId()+":"+newStyle.getStyleXML());
  System.exit(0);
  */
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