//Class to hold a docx Styles list and operate on it

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class xmlStyles {
	String stylesString="";
	ArrayList<xstyle> styleList =new ArrayList<xstyle>();
	ArrayList<xstyle> outline0Styles; //Level 0 style names

//constructor
public xmlStyles(){
	
}

public void setStylesString(String input){
	this.stylesString=input;
	makeStylesAsObjects();
	setOutlineNames(); //arg: ArrayList of xstyle. getStyles().  This is ALL styles.
	//setLvl0Styles();
}

public void addStyle(xstyle input){
	this.styleList.add(input);
}

public ArrayList<xstyle> getStyles(){
	return this.styleList;
}

//return a subset of styles that satisfy outline level 0
public void setOutline0Styles(ArrayList<xstyle> input){
    this.outline0Styles=input;
}

public ArrayList<xstyle> getOutline0Styles(){
    return this.outline0Styles;
}

/* 
Find style tags in styles.xml and return as a String array.
Then create array of 'xstyle' objects to hold elements.
A Word document styles.xml might have a style list with hundreds of defined 'styles' 
*/

public void makeStylesAsObjects(){
	String contents = this.stylesString;
    String starttag="<w:style ";
    String endtag="</w:style>";
    xmlTool myP = new xmlTool();
    ArrayList<xstyle> myStyles = new ArrayList<xstyle>();
    ArrayList<String> result=myP.getTagAttribInclusive(contents,starttag,endtag);
    System.out.println("Style List (main)");
    for (String item: result) {
    	 xstyle currentStyle= new xstyle();
    	 currentStyle.setStyle(item);
    	 addStyle(currentStyle);
    }
}

/*
input: inclusive arraylist of styles tags from styles.xml (as full strings)

output: only a list of names of styles that contain outline lvl= 0
*/

public void setOutlineNames() {
      ArrayList<xstyle> output = new ArrayList<xstyle>();
      int length = getStyles().size();  // number of blocks
      if (length>0) {
      	ArrayList<xstyle> styleList = getStyles();
        Iterator<xstyle> iter = styleList.iterator();  //the stored Stylelist (type xstyle)
          while (iter.hasNext()) {
              xstyle item = iter.next();
              String level=item.getOutlineLevel(); 
              if (level.equals("0")) {
              		String name=item.getName();
              		String sid=item.getId(); //where is this function?
              		output.add(item);
              }
              else {
                //do nothing
              }
         } //end while
      } //end if
      setOutline0Styles(output);
}

}