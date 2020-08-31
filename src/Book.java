
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class
//(C) Craig Duncan 2017-2020
//-old spritebox
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

//mark this class this to allow for changes to variables in class (refactoring)
//private static final long serialVersionUID = -64702044414208496L;


//This book's metadata
int numClauses=0; //this will hold number of clauses
//NODE INPUT FIELDS
String docxfilepath="";
String urlpath = "";
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String shortname="";
String heading="";
String date="";
//this book's local reference for input/ouput
int noderef=0;
//Book OUTPUT FIELDS
String output="";
 
//NODE CATEGORIES (FOR WORLD NODES) TO DO: Turn into a Book array
ArrayList<NodeCategory> nodeCatList = new ArrayList<NodeCategory>();
//This node's data and level in tree:
Clause dataClause = new Clause(); 

String nodecategory = "";
//counters
int count=0; //general purpose counter for node
int branchcount=0; //general purpose counter for this branch of node
//html text
String htmlString="";
String mdString="";
//depth, level count for DFS
// Visibility
Boolean visible = true;
//OLD spritebook variables
Text boxlabel = new Text ("new box");//Default label text for every SpriteBox
//String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
double myXpos = 100; //default for shelves. not needed?
double myYpos = 50;
Boolean isAlert=false;
//
Color defaultColour=Color.WHITE;
Color userColour=Color.LIGHTBLUE;
//String alertColour="red";
Color alertColour=Color.RED;
BookIcon myBookIcon;
String userMetaView; //how to display metadata

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
    setOutputText("");
    //Works with MainStage object
    Book.this.setOnMousePressed(PressBox);
    Book.this.setOnMouseDragged(DragBox);
    Book.this.setOnMouseReleased(DragBox); 
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
public void updateEditedText(String filepath,String urlpath,String label, String mdtext, String note) {
	//setDocName(name);
	setdocfilepath(filepath);
	seturlpath(urlpath);
	setLabel(label);
	setMD(mdtext);
    setNotes(note);
    //setOutputText(outputtext);
    //setHTML(htmltext);
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

public double getX() {
	return this.myXpos;
}

public double getY() {
	return this.myYpos;

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

//THIS NODE'S CLAUSE DATA (OBSOLETE)
/*
public void addNodeClause(Clause thisClause) {
	this.dataClause = thisClause;
}

public Clause getNodeClause() {
	return this.dataClause;
}
*/
public Book cloneBook() {
	Book clone = new Book();
	clone.setHTML(this.htmlString); 
	clone.setMD(this.mdString);
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setNotes(this.docnotes);
	return clone;
}

/* General setup for Child Node Contents of this Object in JavaFX 
No position data is set here for the actual Stackpane.
Position can also be set externally by the calling class */

public void FXsetup() {
    this.myBookIcon = new BookIcon();   //Uses defaults.
    Font boxfont=Font.font ("Verdana", 12); //check this size on monitor/screen
    this.boxlabel.setFont(boxfont);
    this.boxlabel.setRotate(270); 
    this.boxlabel.setFill(myBookIcon.colourPicker("black")); //black Text
    this.boxlabel.setWrappingWidth(130);
    this.setCursor(Cursor.HAND);
    //add these other GUI child nodes to this Stackpane i.e. to this Object as a 'Stackpane' in JavaFX
    this.getChildren().addAll(myBookIcon,boxlabel);  // - a BookIcon (type Rectangle) and a Text object
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
    return this.boxlabel.getText();
}

 //helper function to set label of underlying BookIcon 
public void setLabel(String myString) {
    if (!myString.equals("")) {
        this.boxlabel.setText(myString);
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

private void updateAppearance() {
    
    //this.setLabel(this.docname);
    //this.SetColour(thisNode.getNodeColour());
    //this.SetDefaultColour(thisNode.getNodeColour());
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