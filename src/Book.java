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


public class Book extends StackPane {

//INSTANCE PARAMETERS
String docxfilepath="";
String urlpath = "";
String imagepath="";
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String shortname="";
String booklabel=""; //the title (heading 1) for this entry
String displaylabel="";  //this may be booklabel by default, but can add other details like date etc
String heading="";
String date="";
String time="";
String output="";
String htmlString="";
String mdString="";
Boolean visible = true;
Text bookspinetext = new Text ("new book");//Default label text for every Book
double myXpos = 100; //default for shelves. not needed?
double myYpos = 50;
Boolean isAlert=false;
Color defaultColour=Color.WHITE;
Color userColour=Color.LIGHTBLUE;
Color alertColour=Color.RED;
BookIcon myBookIcon;
String userMetaView; //how to display metadata
Integer rowNumber=0;
Integer columnNumber=0;
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
	settime(timetext);
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
}

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

public void setX(double x) {
	this.myXpos=x;
	updatePosition(); //update actual position in scene
}

public void setY(double y) {
	this.myYpos=y;
	updatePosition(); //update actual position in scene
}

public void setDisplayMode(Integer mode) {
	if (mode>0) {
		this.displayMode=mode;
	}
	updateDisplay();
}

public void updateDisplay(){
	if (this.displayMode==2) {
		String update = getdate()+" "+gettime()+" "+getLabel();
        setDisplayText(update);
	}
	else if (this.displayMode==3) {
		String update = getdate()+" "+gettime();
        setDisplayText(update);
	}
	else if (this.displayMode==1) {
		String update = getLabel();
        setDisplayText(update);
	}
}

//filter the display text for display and update the node data (visible book spine)
//use this only as private function
private void setDisplayText(String input){
	this.displaylabel=input;
	/*
	if (this.displaylabel.length()>50){
		this.setDisplayText(this.displaylabel.substring(0,50));
	}
	*/
	setVisibleNodeText(this.displaylabel); 
}

public double getX() {
	return this.myXpos;
}

public double getY() {
	return this.myYpos;

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

//create a Score that will rank all Books by Shelf position lowest (top left) to highest (bottom right)
//The multiplier needs to be high enough that all rows are unique?
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

//default HTML
private String defaultHTML() {
	//return "<html dir="ltr"><head></head><body contenteditable="true"></body></html>"
	return "<html><head></head><body></body></html>";
}

//set the text that will be the main descriptive or clause text in this node
public void setNotes (String myString) {
	this.docnotes = myString;
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

//clone - uses simple constructor so must duplicate constructor functions
public Book cloneBook() {
	Book clone = new Book();
	clone.setHTML(this.htmlString); 
	clone.setLabel(this.booklabel);
	clone.setMD(this.mdString);
	clone.setNotes(this.docnotes);
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
	clone.setAlert(this.isAlert);
	clone.setUserView(this.userMetaView);
	clone.setRow(this.rowNumber);
	clone.setCol(this.columnNumber);
	clone.setPressBox(this.myPressBox);
	clone.setDragBox(this.myDragBox);
	clone.FXsetup();
	return clone;
}

/* General setup for Child Node Contents of this Object in JavaFX 
No position data is set here for the actual Stackpane.
Position can also be set externally by the calling class */

public void FXsetup() {
    this.myBookIcon = new BookIcon();   //Uses defaults.
    Font boxfont=Font.font ("Arial", 12); //check this size on monitor/screen.  cf Verdana
    this.bookspinetext.setFont(boxfont);
    //this.bookspinetext.setRotate(270); //for vertical text
    this.bookspinetext.setFill(myBookIcon.colourPicker("black")); //black Text
    this.bookspinetext.setWrappingWidth(this.stdWidth);
    this.setCursor(Cursor.HAND);
    //add these other GUI child nodes to this Stackpane i.e. to this Object as a 'Stackpane' in JavaFX
    this.getChildren().addAll(myBookIcon,bookspinetext);  // - a BookIcon (type Rectangle) and a Text object
    updatePosition(); //set position relative to current scene?
}

 /* JAVAFX GUI NODES, OBJECTS AND ENVIRONMENT */

//indirectly update the JavaFX variable based on this object's mirror variables
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


private void setVisibleNodeText(String myLabel) {
	if (myLabel.length()>50) {
		myLabel=myLabel.substring(0,50); //limit book label to first "# " + 10 characters
		}
		this.bookspinetext.setText(myLabel);
}

 //helper function to set label of underlying BookIcon 
public void setLabel(String myString) {
    if (!myString.equals("")) {
    	this.booklabel=myString;
    	updateDisplay();
    }
}

// ----------- COLOURS FOR STATES

public Boolean isAlert() {
    return this.isAlert;
}

//helper function to set alert and update appearance
public void doAlert() {
    this.isAlert=true;
    updateAppearance();
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