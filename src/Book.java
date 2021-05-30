//(C) Craig Duncan 2017-2020
//www.craigduncan.com.au

//import utilities needed for Arrays lists etc
import java.util.*;
//FX and events
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//Padding
import javafx.geometry.Insets;
//text
//Scene - Text as text, with font option
import javafx.scene.text.Text; 
import javafx.scene.text.Font;
import javafx.scene.text.*;
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;

//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
//Serializable
import java.io.Serializable;
//For storing current Stage location
import javafx.stage.Stage;
//File input output for HTML exports
//file i/o
import java.io.*;
import java.io.File;
import java.io.IOException;
//net function for browser links
import java.net.URI;
import java.net.URISyntaxException;
// for image file copying
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;



public class Book extends StackPane {

//INSTANCE PARAMETERS
String docxfilepath="";
String urlpath = "";
String imagepath="";
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes per markdown ``` as usual
String code="";// to hold code (subset of notes in the markdown; stored separately here)
String lang="";// to hold human lang translation (subset of notes in the markdown).  Lang markdown.
String law="";// Future use: to hold legal text - subset of notes in markdown; jurisdiction element too?
String codelang=""; //future use: to store the type of markdown e.g. R markdown {R}
String humanlang="";//future use: to store the type of lang;
String shortname="";
String booklabel=""; //the title (heading 1) for this entry
String displaylabel="";  //this may be booklabel by default, but can add other details like date etc
String heading="";
String date="";
String time="";
String output="";
String htmlString="";
String mdString="";
String OOXMLtext="";
Boolean visible = true;
Text bookspinetext = new Text ("new book");//Default label text for every Book
double myXpos = 100; //default for shelves. not needed?
double myYpos = 50;
double myZpos=1; //layers
Boolean isAlert=false;
Color defaultColour=Color.WHITE;
Color userColour=Color.WHITE;//Color.LIGHTBLUE;
Color alertColour=Color.PINK; //RED
BookIcon myBookIcon;
String userMetaView; //how to display metadata
Integer rowNumber=0;
Integer columnNumber=0;
Integer layerNumber=1;
Integer stdWidth=80; //same as BookIcon width.
EventHandler myPressBox;
EventHandler myDragBox;
Integer displayMode=1;

//empty constructor no arguments
public Book() {

}

//constructor with label, main text and notes (DEFAULT)
public Book(EventHandler PressBox, EventHandler DragBox, String label, String text, String note) {
	setShortname(label); 
	setMD(text);
	setHTML(text); //TO DO: make this auto-update into HTML note MD using Stage editor
	setNotes(note);
	setDocName(label); //to be phased out.  It's just a MD section.
    setLabel(label);
    updateDisplay();
    setOutputText("");
    setPressBox(PressBox); //local
    setDragBox(DragBox);
    //Works with MainStage object
    //Book.this.setOnMousePressed(myPressBox);
   	//Book.this.setOnMouseDragged(myDragBox);
    //Book.this.setOnMouseReleased(myDragBox); 
    FXsetup();
}

//constructor for OOXML (docx) files
public Book(String ooxml) {
	setOOXMLtext(ooxml);
	setMD(ooxml);   
}

public Book(xmlBlock input) {
	setOOXMLtext(input.getBlockText());
	setNotes(input.getNotesText());
	setMD(input.getBlockText()); //check this is ok? 
	setLabel(input.getHeaderText());
}

//Function to set GUI event handlers separate to main data
public void setHandlers(EventHandler PressBox, EventHandler DragBox){
	setPressBox(PressBox); //local
    setDragBox(DragBox);
    FXsetup(); //will this work?
}

//general update text function
public void updateMDText(String label, String text, String note) {
	//setDocName(name);
	setMD(text);
    setLabel(label);
    setNotes(note);
}

//general update text function
public void updateEditedText(String filepath,String urlpath, String imagepath, String label,String datetext,String timetext,String mdtext, String note) {
	//setDocName(name);
	setdocfilepath(filepath);
	setimagefilepath(imagepath);
	seturlpath(urlpath);
	setdate(datetext);
	settime(timetext); //check for trailing
	System.out.println("Checks on updated edits:");
	System.out.println("date input:"+datetext);
	System.out.println("time input:"+timetext);
	System.out.println("stored date:"+this.getdate());
	System.out.println("stored time:"+this.gettime());
	//System.exit(0);
	setLabel(label);
	updateDisplay();
	setMD(mdtext);
    setNotes(note);
    setCode(code);
}


// --- OOXML

public String getOOXMLtext(){
	return this.OOXMLtext;
}

public void setOOXMLtext(String input){
	this.OOXMLtext=input;
}

// -- project info ---

//return the meta info regarding filepath
public String getdocfilepath() {
	return this.docxfilepath;
}

//store the meta info regarding filepath
public void setdocfilepath(String filep) {
	this.docxfilepath = filep;
}

//return the meta info regarding filepath
public String geturlpath() {
	return this.urlpath;
}

//store the meta info regarding filepath
public void seturlpath(String up) {
	this.urlpath = up;
}

//store the meta info regarding imagepath
public void setimagefilepath(String ip) {
	this.imagepath = ip;
}

public String getimagefilepath() {
	return this.imagepath;
}

//DEFAULT USER VIEWS
//GUI layout.  This is currently not affected by follower mode.
public String getUserView() {
	return this.userMetaView;
}

public void setUserView(String myView) {
	this.userMetaView=myView;
}

//Adjust mirror variables in Book - to be used to updated JavaFX variables
public void setXY(double cnx, double cny) {
	this.myXpos=cnx;
	this.myYpos=cny;
	updatePosition(); //update actual position in scene
}

public void setXYZ(double cnx, double cny, double cnz) {
	this.myXpos=cnx;
	this.myYpos=cny;
	this.myZpos=cnz;
	updatePosition(); //update actual position in scene
}

public void setX(double x) {
	this.myXpos=x;
	updatePosition(); //update actual position in scene
}

public void setY(double y) {
	this.myYpos=y;
	updatePosition(); //update actual position in scene
}

//not needed for rendering?
public void setZ(double z) {
	this.myZpos=z;
	updatePosition(); //update actual position in scene
}

//---UPDATE TEXT DISPLAYED ON BOX

public void setDisplayMode(Integer mode) {
	if (mode>0) {
		this.displayMode=mode;
	}
	updateDisplay();
}

 //helper function to set label of underlying BookIcon 
public void setLabel(String myString) {
    if (!myString.equals("")) {
    	this.booklabel=myString;
    	updateDisplay();
    }
}

public void updateDisplay(){
	if (this.displayMode==2) {
		String update = getdate()+" "+gettime()+" "+getLabel();
        setVisibleNodeText(update);
	}
	else if (this.displayMode==3) {
		String update = getdate()+" "+gettime();
        setVisibleNodeText(update);
	}
	else if (this.displayMode==1) {
		String update = getLabel();
        setVisibleNodeText(update);
	}
}

private void setVisibleNodeText(String myLabel) {
	this.displaylabel=myLabel;
	if (myLabel.length()>50) {
		myLabel=myLabel.substring(0,50); //limit book label to first "# " + 10 characters
		}
		this.bookspinetext.setText(myLabel);
}

// ---- 

public double getX() {
	return this.myXpos;
}

public double getY() {
	return this.myYpos;

}

public double getZ() {
	return this.myZpos;

}

public void setRow(Integer myShelf) {
	this.rowNumber=myShelf;
}

public Integer getRow() {
	return this.rowNumber;
}

public void setCol(Integer myCol) {
	this.columnNumber=myCol;
}

public Integer getCol() {
	return this.columnNumber;
}

public void setLayer(Integer myLayer) {
	this.layerNumber=myLayer;
}

public Integer getLayer() {
	return this.layerNumber;
}

//create a Score that will rank all Books by Shelf position lowest (top left) to highest (bottom right)
//The multiplier needs to be high enough that all rows are unique?
//TO DO: introduce 3D parameters?  Or not needed?
public Integer getRowScore(){
	Integer myScore=0;
	try {
		double dScore=getRow()*10000+getX();
		myScore = (int)dScore;
	}
	catch (NullPointerException e) {
		System.out.println(getLabel()+"[x,y]("+getX()+","+getY()+")");
	}
	return myScore;
}

//returns the mirrored position data in this object
//Actual JavaFX GUI position data is accessed separately (or not at all)
public double[] getXY() {
    //this.Xpos=this.getX();
    //this.Ypos=this.getY();
    return new double[]{this.myXpos,this.myYpos};
}

//get all 3D parameters.  Not needed yet.
public double[] getXYZ() {
    return new double[]{this.myXpos,this.myYpos,this.myZpos};
}


// Visibility

public void setVisible(Boolean setVis) {
	this.visible=setVis;
}

public Boolean getVisible() {
	return this.visible;
}


/*  Data methods

Also enables the GUI to toggle the 'view' - i.e. to change the node's preference and save it.
- must work in conjunction with setting a parent node to link to.
*/

public String getHeading() {
	return this.heading;
}

private String getShortname () {
	return getthisShortname();
}

public String getOutputText() {
	return getthisOutputText();
}

public String getNotes () {
	//return publicText(getdataDisplayNode().getthisNotes());
	return this.docnotes;
}

public String getCode () {
	//return publicText(getdataDisplayNode().getthisNotes());
	return this.code;
}

//default HTML
private String defaultHTML() {
	//return "<html dir="ltr"><head></head><body contenteditable="true"></body></html>"
	return "<html><head></head><body></body></html>";
}

//set the text that will be the main descriptive or clause text in this node
public void setNotes (String myString) {
	this.docnotes = myString;
}

//set the text that will be the main descriptive or clause text in this node
public void setCode (String myString) {
	this.code = myString;
}

public void setHTML (String myString) {
	this.htmlString = myString;
}

public void setMD (String myString) {
	this.mdString = myString;
}

public String getMD () {
	return this.mdString;
}

public String getHTML() {
	return this.htmlString;
}

private void setShortname (String myString) {
	this.shortname = myString;
}

//set the text that will be the main text for identifying this node
public void setHeading (String myString) {
	this.heading = myString;
}

//set the text that will be the main text for identifying this node (and using on SpriteBox label)
public void setDocName (String myString) {
	this.docname = myString;
}

public String getDocName () {
	return this.docname;
}

public void setdate (String myString) {
	this.date = myString;
}

public String getdate () {
	return this.date;
}

public void settime (String myString) {
	this.time = myString;
}

public String gettime () {
	return this.time;
}

public void setdocauthor (String myString) {
	this.docauthor = myString;
}

public String getdocauthor () {
	return this.docauthor;
}

public void setOutputText(String myString) {
	this.output = myString;
}

// --- PRIVATE METHODS ACCESSING PRIVATE DATA FOR THIS NODE

private String publicText(String myString) {
	return myString;
}

public String getthisNotes() {
	return this.docnotes;
}

private String getthisShortname () {
	return this.shortname;
}

//NODE'S OUTPUT TEXT FIELD
private String getthisOutputText() {
	return this.output;
}

public void setPressBox(EventHandler myPB){
	this.myPressBox=myPB;
	Book.this.setOnMousePressed(this.myPressBox);
}

public void setDragBox(EventHandler myDB) {
	this.myDragBox=myDB;
	Book.this.setOnMouseDragged(this.myDragBox);
    Book.this.setOnMouseReleased(this.myDragBox); 
}

public String getHTMLlink(String navlink, String newdir) {
	String logString=updateBodyHTML(navlink,newdir);
	//logString=logString+navlink;
	String output=addHTMLEndTags(logString);
	return output;
}

public void updateHTML() {
	String nav="";
	String newdir="";
	String logString=updateBodyHTML(nav,newdir);
	String newpage=addHTMLEndTags(logString);
	this.setHTML(newpage); 
	}

public String addHTMLEndTags(String logString){
	String output=logString+"</body></html>";
	return output;	
	
}


/*
<!DOCTYPE html>
<html lang="en"> 
<head>
<meta charset="utf-8"/>
*/
public String updateBodyHTML(String nav, String newdir) {
	String input = getMD();
	String label = getLabel();
	String notes = getNotes();
	String logString="";
	//take out any existing headers?
	//String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
	int index =0; //
	//top row or heading
	if(index==0) {
	 	logString = "<!DOCTYPE html><html lang=\"en\"><head>";
	 	logString=logString+"<meta charset=\"utf-8\"/>";
	 	logString=logString+"<title>"+label+"</title>";
	 	//logString=logString+"<script> border {border-style:dotted;}</script>"; //css
	 	//inherits main page style sheet?
	 	logString=logString+"<link rel=\"stylesheet\" href=\"../shelf.css\">";
	 	logString=logString+"</head>"+"<body>";// use the label for the html page (if needed)
	 	//logString=logString+"<p><b>"+label+"</b></p>";
	 	logString=logString+"<H1 class=\"a\">"+label+"</H1>"; //class a is arial
	 	logString=logString+nav; //navigation line (if any)
	 }
	 //iterate and create rest of file
	Scanner scanner1 = new Scanner(input);
 	String prefix = "<p>";
 	String suffix="</p>";

	 while (scanner1.hasNextLine()) {
	 	//just make paragraphs for now
	 	String thisLine=scanner1.nextLine();
	 	logString=logString+checkMD2(thisLine);
	 }

	 //include notes
	 if (notes.length()>0) {
		 Scanner scanner2 = new Scanner(notes);
		 //use class in .css external sheets where you have a .period in front of it
		 String prefixdiv="<div class=\"border\" id=\"border\">";
		 String suffixdiv="</div>";
		 logString=logString+prefixdiv;
		 while (scanner2.hasNextLine()) {
		 	String notesLine=scanner2.nextLine();
		 	logString=logString+p_Default(notesLine); //always default
		 	//System.out.println(notesLine);
		 }
		 logString=logString+suffixdiv;
	}
	//includes link
	 String linkout=includeLink();
	 System.out.println("link for html:"+linkout);
	 if (linkout.length()>0){
	 	logString=logString+linkout;
	 }
	 //include image (local)
	 //String imlink=includeImage();
	 String imlink=includeImageRel(newdir);
	 System.out.println("link for image:"+imlink);
	 if (imlink.length()>0){
	 	logString=logString+imlink;
	 }

	 return logString;
}

public String includeLink(){
	String output="";
	String linkpath=geturlpath();
     if (linkpath.length()>0) {
         String linkprefix="<p class=\"a\"><a href=\"";
         String linksuffix="\">Web link</a></p>";
         String linkfile = linkprefix+linkpath+linksuffix;
         output=output+linkfile;
    }
    return output;
}


//This uses local file system link to image.  
//TO DO: copy to /images/ folder inside this HTML export folder
public String includeImageRel(String newdir){
	String notes = getNotes(); //not sure why we need this for title
	//link.  Can we show the image?
	String output="";
    String imagepath=getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it (local files only)
    //From Java 8 use Files and Paths
     if (imagepath.length()>0) {
			File myFile = new File (imagepath);
			URI imageURI = myFile.toURI(); //this is an absolute local link.  Change to relative.
			System.out.println(imageURI);
			//DO A STD COPY
			int count=1;
			
			//<img src="img_girl.jpg" alt="Girl in a jacket" width="500" height="600">
			String linkfile = "image"+count+"."+imagepath.substring(imagepath.length()-3,imagepath.length());
			standardCopy(count,imagepath,newdir,linkfile); //TO DO: get image filenames or use sequence
			String linkpath="images/"+linkfile;
			String imgprefix="<p class=\"a\"><img src=\"";
			String imgsuffix="\" alt=\"user image\" width=\"600\" class=\"feature\" title=\""+notes+"\"></p>";
			//use linkfile not image URI for HTML folders
			String imgfile = imgprefix+linkpath+imgsuffix;
			output=output+imgfile;
    }
    return output;
}

public void standardCopy(int count,String imagepath, String newdir, String filename){
		
		String fullpath=newdir+"/images/"+filename;
		// Path of file where data is to copied
        Path pathIn = (Path)Paths.get(imagepath);
        // Path of file whose data is to be copied. To do: pass name or file ext in?
        Path pathOut = (Path)Paths.get(newdir,"images",filename);// or just String
  
        System.out.println("Path of target file: "
                           + pathOut.toString());
  
        System.out.println("Path of source file: "
                           + pathIn.toString());
  
        // Try block to check for exceptions
        try {
  			Files.copy(pathIn, pathOut,StandardCopyOption.REPLACE_EXISTING);
            // Printing number of bytes copied
            //System.out.println("Number of bytes copied: "+bts);
        }
  
        // Catch block to handle the exceptions
        catch (IOException e) {
  
            // Print the line number where exception occured
            e.printStackTrace();
        }
    }

//This uses local file system link to image.  
//TO DO: copy to /images/ folder inside this HTML export folder
public String includeImage(){
	String notes = getNotes(); //not sure why we need this for title
	//link.  Can we show the image?
	String output="";
    String imagepath=getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it (local files only)
    //From Java 8 use Files and Paths
     if (imagepath.length()>0) {
			File myFile = new File (imagepath);
			URI imageURI = myFile.toURI(); //this is an absolute local link.  Change to relative.
			System.out.println(imageURI);
			//<img src="img_girl.jpg" alt="Girl in a jacket" width="500" height="600">
			String imgprefix="<p class=\"a\"><img src=\"";
			String imgsuffix="\" alt=\"user image\" width=\"600\" title=\""+notes+"\"></p>";

			String imgfile = imgprefix+imageURI+imgsuffix;
			output=output+imgfile;
    }
    return output;
}

public String p_Default(String thisLine){
	String prefix = "<p class=\"a\">"; 
	//String prefix = "<p>";
 	String suffix="</p>";
 	String output=prefix+thisLine+suffix;
	return output;
}

public String checkMD2(String thisLine){
	String h2code="## ";
	String h3code="### ";
    String h2prefix="<H2 class=\"a\">"; //<span style=\"font-family: Arial;\">
    String h2suffix="</H2>";
    String h3prefix="<H3 class=\"a\">"; //<span style=\"font-family: Arial;\">
    String h3suffix="</H3>";
    int mdFormatCode=0;
    String balanceString="";
    String output="";
    //fix escape characters
    thisLine=fixASCII(thisLine);
    //
    int testlength=thisLine.length();
        if (testlength>3) {
            String testString=thisLine.substring(0,3);
            if (testString.equals(h2code)) {
                mdFormatCode=2;
                balanceString=thisLine.substring(3,testlength);
            }
        }  //end length if
        if (testlength>4) {
            String testString=thisLine.substring(0,4);
            if (testString.equals(h3code)) {
                mdFormatCode=3;
                balanceString=thisLine.substring(4,testlength);
            }
        }  //end length if

        switch (mdFormatCode) {
			case(2) : 	output=output+h2prefix+balanceString+h2suffix;
	            		break;
			case(3) : 	output=output+h3prefix+balanceString+h3suffix;
						break;
			default :	output=output+p_Default(thisLine);
        }
        return output;
}

//TO DO: replace characters out of range e.g. ä or specify foreign character set for HTML?
public String fixASCII(String thisLine) {
	String rep1=thisLine.replace("“","\"");
 	String rep2=rep1.replace("”","\"");
 	String rep3=rep2.replace("–","-");
 	String output=rep3.replace("ä","a");
 	return output;
}

//clone - uses simple constructor so must duplicate constructor functions
public Book cloneBook() {
	Book clone = new Book();
	clone.setHTML(this.htmlString); 
	clone.setLabel(this.booklabel);
	clone.setMD(this.mdString);
	clone.setNotes(this.docnotes);
	clone.setCode(this.code);
	clone.setdocfilepath(this.docxfilepath);
	clone.seturlpath(this.urlpath);
	clone.setimagefilepath(this.imagepath);
	clone.setDocName(this.docname);
	clone.setdocauthor(this.docauthor);
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setShortname(this.shortname);
	clone.setHeading(this.heading);
	clone.setdate(this.date);
	clone.settime(this.time);
	clone.setOutputText(this.output);
	clone.setHTML(this.htmlString);
	clone.setVisible(this.visible);
	clone.setX(this.myXpos); //small offset
	clone.setY(this.myYpos);
	clone.setZ(this.myZpos);
	clone.setAlert(this.isAlert);
	clone.setUserView(this.userMetaView);
	clone.setRow(this.rowNumber);
	clone.setCol(this.columnNumber);
	clone.setLayer(this.layerNumber);
	clone.setPressBox(this.myPressBox);
	clone.setDragBox(this.myDragBox);
	clone.FXsetup();
	return clone;
}

/* General setup for Child Node Contents of this Object in JavaFX 
No position data is set here for the actual Stackpane.
Position can also be set externally by the calling class */

//Initial GUI setup
public void FXsetup() {
    this.myBookIcon = new BookIcon();   //Uses defaults.  Essentially, it's a 'Rectangle'

    //we add text to the Rectangle

    Font boxfont=Font.font ("Arial", 12); //check this size on monitor/screen.  cf Verdana
    this.bookspinetext.setFont(boxfont);
    //this.bookspinetext.setRotate(270); //for vertical text
    this.bookspinetext.setFill(myBookIcon.colourPicker("black")); //black Text
    this.bookspinetext.setWrappingWidth(this.stdWidth);

    this.setCursor(Cursor.HAND);
    //add these other GUI child nodes to this Stackpane i.e. to this Object as a 'Stackpane' in JavaFX

    /* This will add text to the 'Rectangle' here.
    We could add text directly, but to get a nice padding beween text and edges we insert Text object into
    a StackPane first, and set some padding.
    StackPane stack = new StackPane();
	stack.getChildren().addAll(agent, text);
	*/
	//Any general Padding set on this StackPane object will pad out the enclosed rectangle (shifting it right)
	//this.setPadding(new Insets(10)); //because 'this' is a StackPane object in JavaFX?
    this.getChildren().addAll(myBookIcon,bookspinetext);  // - a BookIcon (type Rectangle) and a Text object
    
    updatePosition(); //set position relative to current scene?
}

 /* JAVAFX GUI NODES, OBJECTS AND ENVIRONMENT */

//indirectly update the JavaFX variable based on this object's mirror variables
//TO DO: use myZpos to control the visibility/layering of different layers
private void updatePosition() {
	//check range
	if (this.myXpos<0) {
        this.myXpos=0;
    }
    if (this.myYpos<0) {
        this.myYpos=0;
    }
    //change FX position
    this.setTranslateX(this.myXpos);
	this.setTranslateY(this.myYpos);
}

//helper function to return label of underlying BookIcon 
public String getLabel() {
    return this.booklabel; //return the stored value, not the book spine
    //this.bookspinetext.getText();
}

// ----------- COLOURS FOR STATES

public Boolean isAlert() {
    return this.isAlert;
}

//helper function to set alert and update appearance
public void doAlert() {
    this.isAlert=true;
    updateAppearance(); //<--bug here.
}
 public void endAlert() {
    this.isAlert=false;
    updateAppearance();
}

public void setAlert(Boolean alertState){
	this.isAlert=alertState;
}

private void updateAppearance() {
    
    if (this.isAlert==true) {
       this.myBookIcon.setColour(this.alertColour);
    }
    else {
    	this.myBookIcon.setColour(userColour);
    }
    }

public void SetColour(Color myColour) {
    this.myBookIcon.setColour(myColour);
}

public void SetDefaultColour (Color mycol) {
    this.defaultColour=mycol;
}

public String getColour() {
    return this.myBookIcon.getColour();
}


}