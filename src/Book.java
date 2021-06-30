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
String type = "text"; //default. Options include tables, code, notes.
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
String styleId="";
int outlineLevel=1; //default
String output="";
String htmlString="";
String mdString="";
String OOXMLtext="";
String styleXML="";
String bookmarkName="";
String bookmarkString="";
Boolean visible = true;
Text bookspinetext = new Text ("new book");//Default label text for every Book
double myXpos = 100; //default for shelves. not needed?
double myYpos = 50;
double myZpos=1; //layers
Boolean isAlert=false;
Color defaultColour=Color.WHITE;
Color userColour=Color.WHITE;//Color.LIGHTBLUE;
Color alertColour=Color.PINK; //RED
BookIcon myBookIcon = new BookIcon();
String userMetaView; //how to display metadata
Integer rowNumber=0;
Integer columnNumber=0;
Integer layerNumber=1;
Integer stdWidth=80; //same as BookIcon width.
EventHandler myPressBox;
EventHandler myDragBox;
String displayMode="title";
String splitType="OutlineLvl0"; //default
ooxmlTable myTable = new ooxmlTable();
String blockType = "text"; //default.  notes, table, code,text.  Underlying block
String inputType = "md";

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

//constructor for mdFile files

public Book(mdBlock input) {
	setOOXMLtext("");
	setBlockType(input.getBlockType());
	setInputType(input.getInputType());
    setNotes(input.getNotesText());
    setLabel(input.getHeaderText());
    setOutlineLevel(1);
    setMD(input.getBlockText());
}

//preferred constructor now: builds book from XML block data
public Book(xmlBlock input) {
	setBlockType(input.getBlockType());
	setInputType(input.getInputType());
	setOOXMLtext(input.getBlockXMLText()); //this also calls the method that populates the text.
	setOOXMLtable(input.getXMLtable());
	setNotes(input.getNotesText());
	setMD(input.getPlainText()); 
	setLabel(input.getHeaderText());
	setStyleXML(input.getStyleXML()); //full text
	setStyleId(input.getStyleId()); //the styleId
	setOutlineLevel(input.getOutlineLevel());
	setBookmark(input.getBookmark()); //retrieves bookmark if it coincides with para of ref level heading
	setBookmarkString(input.getBookmarkListAsString());
	setSplitType(input.getSplitType());
	if (getSplitType().equals("Bookmark")){ 
        setDisplayMode("bookmark"); //bookmarks default. update?
    }
}

public void setBlockType(String input){
	this.blockType=input;
}

public String getBlockType(){
	return this.blockType;
}

/* Getters and setters for file that was loaded to setup Book */


public void setInputType(String input){
	this.inputType=input;
}

public String getInputType(){
	return this.inputType;
}

//pick up a table object embedded in xmlBlock
//ensure there is at least a table object, even if unusued.
public void setOOXMLtable(ooxmlTable input){
	if (input!=null) {
		this.myTable = input;
	}
}

public ooxmlTable getOOXMLtable(){
	return this.myTable;
}

//Function to set GUI event handlers separate to main data
public void setHandlers(EventHandler PressBox, EventHandler DragBox){
	setPressBox(PressBox); //local
    setDragBox(DragBox);
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
public void updateEditedText(String filepath,String urlpath, String imagepath, String label,String styleIdtext, int outlineLeveltext, String mdtext, String tabnote, String note, String bookmarkName) {
	//setDocName(name);
	setdocfilepath(filepath);
	setimagefilepath(imagepath);
	seturlpath(urlpath);
	//setdate(datetext);
	setStyleId(styleIdtext); //check for trailing
	setOutlineLevel(outlineLeveltext);
	System.out.println("Checks on updated edits:");
	//System.out.println("date input:"+datetext);
	System.out.println("styleId input:"+styleIdtext);
	System.out.println("stored date:"+this.getdate());
	System.out.println("stored styleId:"+this.getStyleId());
	//System.exit(0);
	setLabel(label);
	updateDisplay();
	setMD(mdtext);
	setBookmark(bookmarkName);
	//remove this ambiguity.  Make one 'code'
	if (tabnote.length()>0){
		setNotes(tabnote);
	}
	else {
	    setNotes(note);
	}
    setCode(code);
}

// SPLIT MODE

public void setSplitType(String input){
	this.splitType=input;
}

private String getSplitType(){
	return this.splitType;
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

//Adjust mirror variables in Book - to be used to update JavaFX variables
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

public void setDisplayMode(String mode) {
	if (!mode.equals("")) {
		this.displayMode=mode;
	}
	updateDisplay();
}

 //helper function to set label of underlying BookIcon 
public void setLabel(String myString) {
    if (!myString.equals("")) {
    	this.booklabel=myString; //cf displaylabel
    	updateDisplay();
    }
}

/*
Update text on front of book/box depending on selected display mode
*/

private void updateDisplay() {
	String update = getLabel();//default
	if (this.displayMode.equals("title")) {
		update = getLabel();  
	}
	else if (this.displayMode.equals("combo")) {
		update = getOutlineLevel()+" "+getStyleId();
	}
	else if (this.displayMode.equals("field")) {
		update = getStyleId();
	}
	else if (this.displayMode.equals("date")) {
		update = getdate();//getOutlineLevel()+" "+getStyleId();
	}
	else if (this.displayMode.equals("bookmark")) {
		update = getBookmark();
		if (update.length()==0){
			update=getBookmarkString(); 
		}
	}
	setVisibleNodeText(update);
}

private void setVisibleNodeText(String myLabel) {
	//logDisplayMode(myLabel);
	this.displaylabel=myLabel;
	if (myLabel.length()>50) {
		myLabel=myLabel.substring(0,48); //limit book label to first "# " + 10 characters
		}
		this.bookspinetext.setText(myLabel);
}

private void logDisplayMode(String input){
	logger(input);
}

private void logger (String input){
	System.out.println(input);
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

public void setRow(Integer input) {
	if (input>=0){
		this.rowNumber=input;
	}
}

public Integer getRow() {
	return this.rowNumber;
}

public void setCol(Integer input) {
	if (input>=0){
		this.columnNumber=input;
	}
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

public String getStyleXML(){
	return this.styleXML;
}

public void setStyleXML(String input){
	this.styleXML=input;
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

public String getStyleId () {
	return this.styleId;
}

public void setStyleId(String input){
	this.styleId=input;
}

public int getOutlineLevel(){
	return this.outlineLevel;
}

//jdk16 compliant
public String getOutlineLevelAsString(){
	String output = Integer.toString(getOutlineLevel());
	return output;
}

public void setOutlineLevel(int input){
	this.outlineLevel=input;
}

public void setBookmark(String input){
	this.bookmarkName=input;
}

public String getBookmark(){
	return this.bookmarkName;
}

public void setBookmarkString(String input){
	this.bookmarkString=input;
}

public String getBookmarkString(){
	return this.bookmarkString;
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
	HTMLmaker myMaker= new HTMLmaker(this);
	String logString=myMaker.updateBodyHTML(navlink,newdir);
	String output=myMaker.addHTMLEndTags(logString);
	return output;
}

public void updateHTML() {
	String nav="";
	String newdir="";
	HTMLmaker myMaker= new HTMLmaker(this);
	String logString=myMaker.updateBodyHTML(nav,newdir);
	String newpage=myMaker.addHTMLEndTags(logString);
	this.setHTML(newpage); 
	}


//clone - uses simple constructor so must duplicate constructor functions
public Book cloneBook() {
	Book clone = new Book();
	clone.setHTML(this.htmlString); 
	clone.setLabel(this.booklabel);
	clone.setMD(this.mdString);
	clone.setNotes(this.docnotes);
	//ooxml
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
	clone.setStyleId(this.styleId);
	clone.setOutlineLevel(this.outlineLevel);
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
	clone.FXsetup(); //prepares the clone for the FX environment as well.
	return clone;
}

/* General setup for Child Node Contents of this Object in JavaFX 
No position data is set here for the actual Stackpane.
Position can also be set externally by the calling class */

//Initial GUI setup
public void FXsetup() {
    // this.myBookIcon is essentially a 'Rectangle'

    //we add text to the Rectangle

    Font boxfont=Font.font ("Arial", 12); //check this size on monitor/screen.  cf Verdana
    this.bookspinetext.setFont(boxfont);
    //this.bookspinetext.setRotate(270); //for vertical text
    this.bookspinetext.setFill(myBookIcon.colourPicker("black")); //black Text
    this.bookspinetext.setWrappingWidth(this.stdWidth);

    this.setCursor(Cursor.HAND);
    //add these other GUI child nodes to this Stackpane i.e. to this Object as a 'Stackpane' in JavaFX

    /* This will add text to the 'Rectangle' here.
   	TO DO: insert some padding
   */
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
    try {
    if (this.isAlert==true) {
       this.myBookIcon.setColour(this.alertColour);
    }
    else {
    	this.myBookIcon.setColour(userColour);
    }
    }
    catch (Throwable t)
        {
            t.printStackTrace();
            return;
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