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

/* A Word document might have a style list with hundreds of defined 'styles' */

public void makeStylesAsObjects(){
	System.out.println("Preparing an array of styles from styles.xml");
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
    	 //System.out.println(currentStyle.getStyle());

    }
    //System.out.println(getStyles().toString());
}

/*
input: inclusive arraylist of styles tags from styles.xml (as full strings)

output: only a list of names of styles that contain outline lvl= 0
*/

public void setOutlineNames() {
	  System.out.println("Preparing a list of lvl 0 styles from styles list");
      ArrayList<xstyle> output = new ArrayList<xstyle>();
      int length = getStyles().size();  // number of blocks
      if (length>0) {
      	ArrayList<xstyle> styleList = getStyles();
      	/*for (xstyle test: styleList){
      		System.out.println(test.getStyle());
      		System.out.println(test.getOutlineLevel());
      		System.out.println(test.getName());
      		System.out.println(test.getId());
      	}
      	System.exit(0);
      	*/
        Iterator<xstyle> iter = styleList.iterator();  //the stored Stylelist (type xstyle)
          while (iter.hasNext()) {
              xstyle item = iter.next();
              String level=item.getOutlineLevel(); 
              if (level.equals("0")) {
              		String name=item.getName();
              		String sid=item.getId(); //where is this function?
              		output.add(item);
              		/*
              		System.out.println("Match in this para:");
              		System.out.println(item.getStyle());
              		System.out.println(name);
              		System.out.println(sid);  //if this is ok also record paraId in xmlPara.
                	output.add(item);
                	System.out.println("Exiting test setStyleLvlNames");
                	System.exit(0); //<-------STOPPING HERE
                	*/
              }
              else {
                //do nothing
              }
         } //end while
      } //end if
      //list and exit for now
      setOutline0Styles(output);
      /*
      System.out.println("Exiting with a list of styles (xmlStyles.java)");
      for (xstyle item: output) {
	      System.out.println(item.getId());
   		}
      System.exit(0); //<-------STOPPING HERE
      */
      
}

}