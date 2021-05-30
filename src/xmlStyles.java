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

}