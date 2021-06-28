//(c) Craig Duncan 2017-2021
//www.craigduncan.com.au

//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File;
//for file handling
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
//File chooser
import javafx.stage.FileChooser;
//Screen positioning and size/bounds
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
//lines and shapes for joining
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Labeled;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
//Scene - general appearance & layout of Background Fills, Stages, nodes
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode; //for testing keyevent
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType; //needed for Mouse Event testing
//Paint
import javafx.scene.paint.Color;
//Menus
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
//html editor
import javafx.scene.web.HTMLEditor;
//Drag n Drop events
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
//Desktop etc and file chooser
import java.awt.Desktop;
//collections for listview
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener; //for obs list
//adding listview
import javafx.util.Callback; //for listview cells


/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

/* TO DO:
Separate JavaFX stage logic/objects from the underlying data.
Underlying Data should be stored in a "Project" instance and retrieved here or modified as necessary
At present, this class is an intwined controller/viewer so think about better separation of concerns.
Also, remove data file input/output to Project instance too.
*/

/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
Should 'selection' state be stored only on controller, or stored with data in Project object?
Design issue: (i.e serialise this with data, put in separate file?  Store-in memory separately?)
*/

public class MainStage {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();
double wsPaneWidth=0.8*myBigX;
double wsPaneHeight=0.8*myBigY;
/*
double scrollSceneWidth=0.8*myBigX;
double scrollSceneHeight=0.8*myBigY;
*/
ArrayList<Stage> myStageList = new ArrayList<Stage>();
String stageName = "";
String stageTitle = "";
String displayOption = "Row";

//Book reference_ParentNode = new Book();
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
gridSpace myGridSpace = new gridSpace();
//Group bookgroupNode;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;
Book clipboardBook; //for cut,copy and paste
//To hold Stage with open node that is current
BookMetaStage bookMetaInspectorStage; 

/*
String filename = "";
String shortfilename=""; //current filename for saving this stage's contents
*/
String category="";


//TABS FOR RHS
TabPane myTabsGroup = new TabPane();
Tab tab_Visual = new Tab();
Tab tab_StyleXML = new Tab();
Tab tab_Styles_docx = new Tab();
Tab tab_Fields_docx = new Tab();
Tab tab_Bookmarks = new Tab();
TextArea styleTextArea = new TextArea();
TextArea styleSummaryTextArea = new TextArea();
TextArea fieldsTextArea = new TextArea();
TextArea bookmarksTextArea = new TextArea();

//NODE'S TEXT CONTENT
//For storing main text output area for this Stage (if any)
//As of 26.4.2018: make this the default area to hold the node's own text (for stages that display a frame that is also an open node).  Always editable.

//This TextArea is the GUI display object for the nodes' docnotes String.  Edit button will update the node's (Book) actual data
TextArea docNameTextArea = new TextArea();
TextArea mdTextArea = new TextArea();
TextArea headingTextArea = new TextArea();
TextArea inputTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
TextArea shelfFileTextArea = new TextArea();
Text shelfFileName = new Text("untitled.md");
Text parentBoxText;
Text headingBoxText;
Text inputBoxText;
Text visibleBlockText;
Text mdHeadingText;
//Store the common event handlers here for use
EventHandler<MouseEvent> PressBox;
//EventHandler<MouseEvent> DragBox;
//EventHandler<KeyEvent> SaveKeyEventHandler;
//MenuBar
MenuBar localmenubar;
//html editor
 final HTMLEditor htmlEditor = new HTMLEditor();
//visibility checkbox
CheckBox visibleCheck = new CheckBox("Visible");

Integer margin_yaxis;
Integer margin_xaxis;
/*
Integer cellcols=50;
Integer cellrows=50; //make this 50 for sure
Integer cellgap_x=80; //cellwidth x dimension
Integer cellgap_y=100; //cell width y dimension
Integer firstcell_x=this.cellgap_x;
Integer firstcell_y=0;
Integer cellrowoffset_y=30;

Integer gridwidth=1000;
Integer gridlineheight=1;
*/
Integer filenameoffset_y=20;
Integer filenameoffset_x=400; //how far across the filename appears
//Integer boxtopmargin=10;
Color shelfborder=Color.BLACK;
Color gridOuterColor=Color.LIGHTBLUE; //Color.WHITE;
//Color shelffill=Color.WHITE; //background to 'grid' cf. DARKSLATEGREY


ArrayList<Layer>layersCollection = new ArrayList<Layer>();

//To store collection of "Books" with main content data.  Also stores X,Y in each book for now.
Project myProject = new Project(); //this is OK without waiting for constructor?
//ArrayList<Book> booksOnShelf = new ArrayList<Book>(); //generic store of contents of boxes

//Currently, 'selected' is kind of GUI property, reflected in individual Book property as well.
//ArrayList<Book> selectedBooks = new ArrayList<Book>(); //for GUI selections
//
ArrayList myFileList = new ArrayList(); 
ObservableList<String> myObList;
//
double orgSceneX;
double orgSceneY;

double orgTranslateX;
double orgTranslateY;
Main parentClass;
//Stage parentStage;
FileChooser currentFileChooser = new FileChooser();
FileChooser.ExtensionFilter myExtFilter; 

//Track current stage that is open.  Class variables
static BookMetaStage currentFocus; //any BookMetaStage can set this to itself
//static Book currentTarget; //any Box(no?) or BookMetaStage can set this to its display node

Boolean metaMode;
Boolean shiftMode;
Integer maxLayer;
Layer firstLayer= new Layer();
Layer currentLayer = firstLayer;
Stage primaryStage;

//constructor
public MainStage() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
    this.metaMode=false;
    this.shiftMode=false;
}

//workspace constructor.  Filename details will be inherited from loaded node.

//Passes MenuBar from main application for now
//Passes general eventhandlers from Main (at present, also uses these for the boxes)
public MainStage(String title, MenuBar myMenu, Main parent, Stage myprimarystage) {
    
    this.metaMode=false;
    this.shiftMode=false;
    this.parentClass=parent;
    this.primaryStage=myprimarystage;
    setExtensions();
    //this.parentStage=parent.getStage();
    //<---Use a system menu for mac ? Problematic with the focus --->
    /*
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
          myMenu.useSystemMenuBarProperty().set(true);
        }
    */
    setMenuBar(myMenu); //storesmenubar object for later
    
    setPressBox(processLocalBoxClick); //stores this in a variable to pass to Book.

    setDragBox(DragBox); //stores this to pass to Book 
    setKeyPressHandler(SaveKeyEventHandler); //TO do - set a variable to pass to sprites=
    
    //we need to set sprite group etc

    newWorkstageFromGroup(title);
    currentLayer.setLayerNumber(1);
    System.out.println ("The initial bookgroupNode...");
    System.out.println (myGridSpace.getGridGroup());
    System.out.println("Reference of this stage object MainStage");
    System.out.println(MainStage.this);
    
    //fileops
    this.currentFileChooser.getExtensionFilters().add(myExtFilter);
}

/* 
Function to set extensions that are permitted to be Opened in open dialogue
*/

public void setExtensions(){
  String string1="*.md";
  String string2="*.rmd";
  String string3="*.docx";
  ArrayList<String> myextlist = new ArrayList<String>();
  myextlist.add(string1);
  myextlist.add(string2);
  myextlist.add(string3);
  myExtFilter = new FileChooser.ExtensionFilter("Stream formats",myextlist);
}

/* This function checks to see what filename extension is, and sets ones app recognises

Based on this, it will set the kind of file the project is working with as an input.
This will affect how the program interprets input, and converts to OOP etc

TO DO: check minimum filename length of 5

*/
public Boolean checkExtensions(File file){
    String thefilename=file.getName();
    String ext1=thefilename.substring(thefilename.length() - 3);
    String ext2=thefilename.substring(thefilename.length() - 4);
    String ext3=thefilename.substring(thefilename.length() - 5);
    
    if (ext1.equals(".md")==true) {
      myProject.setExtension("md");
      return true;
    }

    else if (ext2.equals(".rmd")==true || ext2.equals(".Rmd")==true){
      myProject.setExtension("rmd");
      return true;
    }

     else if (ext3.equals(".docx")==true){
      myProject.setExtension("docx");
      return true;
    }
    else {
      return false;
    }
}

/*
General file opening function:

1. Opens up a file
2. Creates a generic 'block' object to hold meaningful divisions in file,
3. Creates an array of those blocks for the GUI.
4. Arranges for file to be opened and all GUI Views updated.

This does not internally process the blocks to distinguish notes and other text, but this is easy to do for markdown since this is already coded for .

*/

public void openFileGetBooklist(File file) {
    System.out.println("processing docx/markdown file...");
    System.out.println(file.toString()); // this is full path
    String thefilename=file.getName();
    Boolean isOK = checkExtensions(file); //also sets extensions in myProject
    
    Parser myParser=new Parser();
    //File splitting, depending on file type

    if (isOK==true && myProject.getExt().equals("md")) {
        setFileForView(file);
        myProject.setFile(file);
        mdFile myMD = new mdFile();
        myMD.openMD(file);
        //myBookSet=myMD.getBooklist(); //could just use myProject later.
        myProject.setOpenMD(myMD); //books added to project if created upon opening
        setMDForView(myMD);
    } 
    //should return file split into blocks per #
    if (isOK==true && myProject.getExt().equals("rmd")) {
        setFileForView(file);
        myProject.setFile(file);
        System.out.println("Processing RMD file.");
        mdFile myRMD = new mdFile();
        myRMD.openMD(file);
        //myBookSet=myRMD.getBooklist(); //could just use myProject later.
        myProject.setOpenRMD(myRMD); //books added to project if created upon opening
        setMDForView(myRMD);
    }
    if (isOK==true && myProject.getExt().equals("docx")) {
        //integrate filename information into docx object?
        setFileForView(file);
        myProject.setFile(file);
        docXML myDoc = new docXML();
        int outcome=myDoc.openDocx(file);
        if (outcome==0) {
            myProject.setOpenDocx(myDoc); //books added to project if created upon opening
        } 
    }
    unpackBooksToAllViews(); //all books now set up in myProject object.
    
    }

//DISPLAY OPTIONS
// ------- BEGIN GRIDSPACE API ----- 

/*
Function: Adds books in project to gridSpace scrollpane.
(Is called by unpackBooksToAllViews)

This is the logical place to ensure that event handlers and FX setup is completed,
because this is where the Project books are added to the gridSpace object.
This must be done BEFORE calling 'setActiveBook'

The default starting row,col is 0,0 which differs from the Display Options.
*/

private void AddProjectBooksToGridView() throws NullPointerException {
    
    ArrayList<Book> myBookList = myProject.getBooksOnShelf();
    //logBooksToView(myBookList.size());
    for (Book item: myBookList) {
    try {
        //This if for FX purposes, but must be done as part of creation of new books ASAP, setting alerts
        item.setHandlers(PressBox,DragBox);
        myGridSpace.addBook(item); 
    }
    catch (NullPointerException e) {
        System.out.println("NullPointerException in AddNewBookToStage");
        System.exit(0);
    } 
  } //end for loop
}

public void logBooksToView(Integer num){
    System.out.println("Adding project books to stage");
    System.out.println("Project books:"+num);
}

public void insertRow(String input){
    myGridSpace.insertRowActive(input);
}

public void cellShift(String input){
    myGridSpace.cellShift(input);
}

//set active sprite.  if problem with tracker, ignore.
public void setActiveBook(Book b) {
   myGridSpace.singleSelection(b);
}

//method to put new book (that doesn't exist in Project) on stage.  
//cf if you have an existing Book object. addBookToStage
//This should also (in due course) take layer into account? i.e. Book's layer and Z properties.

public void addNewBookToView () {
    Book b = new Book(PressBox,DragBox,"untitled","","");
    myProject.addBookToProject(b); //data model
    myGridSpace.pasteBook(b); //view.  differs from Main  
}

public void removeBookFromProjectAndViews(Book thisBook) {
    //TO DO: remove Node (data) ? is it cleaned up by GUI object removal?
    myGridSpace.remove(thisBook);
    myProject.removeBook(thisBook);
    getStage().show(); //refresh GUI
    
}

public Book getActiveViewBook(){
    return myGridSpace.getActiveBook();
}

public void removeActiveBook(){
    removeBookFromProjectAndViews(getActiveViewBook());
}

public void moveFocusCell(String input){
    myGridSpace.moveFocusCell(input);
}

// ------- END GRIDSPACE API ----- 

//use row for positions
//This is called from 'Main' so it returns the 'File' for future use.

public File openNewFile() {
        //System.exit(0);
        // final FileChooser fileChooser = new FileChooser();
        File file = new File (myProject.getFilename()); //just in case
        if (file==null) {
            file = new File ("untitled.md");
            System.out.println("No File at Time of Open handler");
        } 
        Stage myStage = new Stage();
        myStage.setTitle("Open File");
        this.currentFileChooser.setTitle("Open A File");
        //this.currentFileChooser.setSelectedExtensionFilter(this.myExtFilter);   
        this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
        //Now try and get File
        File tryfile = currentFileChooser.showOpenDialog(myStage);
        if (tryfile != null) {
          file = tryfile;
          
          //if markdown activate process markdown directly
          openFileGetBooklist(file);
        }
        else {
            //DO SOMETHING
        }
    System.out.println("Finished processing file for MainStage");
    return file; 
}


//
public void addNewStyleTheme(String input){
  if (input.equals("Evidence")){
    XMLStyleThemeMaker themeTool = new XMLStyleThemeMaker();
  }
  if (input.equals("MD")){
     XMLStyleThemeMaker themeTool = new XMLStyleThemeMaker();
     xmlStyles current = myProject.getOpenDocx().getStylesObject(); //this is the full styles.xml string
     themeTool.setCurrentStylesXML(current);
     themeTool.addMDStyles();
     //the new styles object in docx already has summary information updated
     docXML currentDoc = myProject.getOpenDocx();
     currentDoc.setStylesObject(themeTool.getUpdatedStylesXML());
     //currentDoc.getStylesObject().logging();
     setDocxForMainTabs(currentDoc); //update visible stylesXML information.
  }
  if (input.equals("Letter")){
     XMLStyleThemeMaker themeTool = new XMLStyleThemeMaker();
     xmlStyles current = myProject.getOpenDocx().getStylesObject(); //this is the full styles.xml string
     themeTool.setCurrentStylesXML(current);
     themeTool.addLetterStyles();
     //the new styles object in docx already has summary information updated
     docXML currentDoc = myProject.getOpenDocx();
     currentDoc.setStylesObject(themeTool.getUpdatedStylesXML());
     //currentDoc.getStylesObject().logging();
     setDocxForMainTabs(currentDoc); //update visible stylesXML information.
  }
}

/*
Function for general Save As

SaveAs will use current path so prompts with filename only.
writeOutBooksToWord() writes from the markdown + notes elements for now (not the docx content)
       
*/
public void saveAs() {
    Stage myStage = new Stage();
    myStage.setTitle("Save As ...");
    //change filename just in case, for Word
    String myFilename= myProject.getFilename(); 
    //if Word let's prompt to make sure we don't harm original
    if (myProject.getExt().equals("docx")) {
      String fn=myProject.getFilenameNoExt();
      String filename=fn+"_v2.md";
      myProject.setFilenameFromString(filename);
      myFilename=filename;
    }
    this.currentFileChooser.setTitle("Save Project/File As");
    this.currentFileChooser.setInitialFileName(myFilename);  
    this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
    //System.exit(0);
    File file = currentFileChooser.showSaveDialog(myStage);
    if (file != null) {
        setFileForView(file); //sets name of file in myProject
        myProject.setFile(file);
        System.out.println(myProject.getFilename());
        System.out.println("SaveAsCalled");
        writeFileOut();
        writeOutBooksToWord(); 
        writeOutHTML();
        } 
        else {
            //DO SOMETHING
        }
    }

//for save as
public void saveAsDocx() {
    Stage myStage = new Stage();
    myStage.setTitle("Resave docx As ...");
    //String fn=myProject.getFilepathNoExt();
    if (!myProject.getExt().equals("docx")) {
      //do nothing.  Future: dialogue box.
      return;
    }
    String fn= myProject.getFilenameNoExt();
    String filename=fn+".docx";
    this.currentFileChooser.setTitle("Save Project/File As");
    this.currentFileChooser.setInitialFileName(filename);  
    this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
    //System.exit(0);
    File file = currentFileChooser.showSaveDialog(myStage);
    if (file != null) {
        setFileForView(file); //sets name of file in myProject
        myProject.setFile(file);
        System.out.println(myProject.getFilename());
        //writeFileOut();
        writeOutBooksToWord(); //This is just writing from markdown + notes for now (not the docx)
        //writeOutHTML();
        } 
        else {
            //DO SOMETHING
        }
    }

//saveAsDocxStyles
public void saveAsDocxStyles() {
    Stage myStage = new Stage();
    myStage.setTitle("UpdateDocx with New Styles Only");
    //String fn=myProject.getFilepathNoExt();
    if (!myProject.getExt().equals("docx")) {
      //do nothing.  Future: dialogue box.
      return;
    }
    String fn= myProject.getFilenameNoExt();
    String filename=fn+".docx";
    this.currentFileChooser.setTitle("Save Project/File As");
    this.currentFileChooser.setInitialFileName(filename);  
    this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
    //System.exit(0);
    File file = currentFileChooser.showSaveDialog(myStage);
    if (file != null) {
        setFileForView(file); //sets name of file in myProject
        myProject.setFile(file);
        myProject.writeDocxNewStyles();
        } 
        else {
            //DO SOMETHING
        }
    }

//for save Row as
public void saveRowAs(Integer row) {
    Stage myStage = new Stage();
    myStage.setTitle("Save As ...");
    //does this add extension before the '_row?'
    String myFilename=myProject.getFilenameNoExt()+"_row"; //SaveAs will use current path so only name is needed.  Add suffix to avoid accidents.
    this.currentFileChooser.setTitle("Save Row As File");
    this.currentFileChooser.setInitialFileName(myFilename); //with no ext? 
    this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
    //System.exit(0);
    File file = currentFileChooser.showSaveDialog(myStage);
    if (file != null) {
        //do we want to do anything here?
        /*
        setFileForView(file);
        */
        myProject.setRowFile(file);
        
        writeRowOut(row);
        } 
        else {
            //DO SOMETHING
        }
    }

//for save of "Books"
public void writeOutHTML(){
  ArrayList<Book> mySaveBooks = myProject.listBooksShelfOrder();
  HTMLFile myHTML = new HTMLFile();
  String name=myProject.getFilename(); //maybe redundant.  Full path?
  String filename=myProject.getFilenameNoExt(); //otherwise get from label 
  //Path path = Paths.get(nameExt); //from filename.  But if you have changed this?
  //String parent=path.getParent().toString();
  //This works for Mac.  Better to obtain path through new Directory
  String htmlfolderpath = myProject.getParentPath()+"/"+filename;
  myHTML.writeOutHTML(mySaveBooks,name,filename,htmlfolderpath);
}

//for direct save - of 'Books'
public void writeFileOut() {
    //ArrayList<Book> mySaveBooks = myProject.listBooksShelfOrder();
    //writeOutBooks(mySaveBooks); 
    myProject.writeMDFileOut(); 
}

//for direct Row save
//Currently retains the grid position of this row. i.e. doesn't write new entries back to row '0' but it could.
//To do: move to Project instance
public void writeRowOut(Integer row) {
    myProject.writeRowOut(row);
}

//We can only arrive here from a Save As, not Save.  It saves word based on mdnotes, conversion to OOXML
//So that's only a basic version of a Word docx (not based on an original Word doc)
//TO DO (FUTURE): Write out a new docx using the docXML contents.
public void writeOutBooksToWord() {    //
    myProject.writeOutMDBooksToWord();
    /*
    ArrayList<Book> mySaveBooks = myProject.listBooksShelfOrder();//getBooksOnShelf();
    String filepath=myProject.getFilename();
    docXMLmaker myDocSave = new docXMLmaker(); //we may be able to use the existing docXML in future.
    myDocSave.writeOutWordFromBooks(filepath,mySaveBooks);
    */
}

//function to change the way the opened text files are split for display
public void setSplitOption(String input) {
    myProject.setSplitOption(input);
    myProject.updateSplitOptionBooks(); //based on split option
    unpackBooksToAllViews(); //clears stage and adds new books 
}

public void setLayoutMode(String input){
    myGridSpace.setLayoutMode(input); 
}

/* 
   Function to change way box labels are displayed
   Books in 'project' not updated, but if object pointers are same, will be.
*/
public void setDisplayMode(String input){
    myGridSpace.setLabelsMode(input); 
}


//JAVAFX SCENE GRAPH GUI INFO (THIS IS NOT THE DATA NODE!)
public void setSceneRoot(Node myNode) {
    this.rootNode = myNode;
}

public Node getSceneRoot() {
    return this.rootNode;
}


//FILE I/O DATA
/*
public String getFilename() {
    return myProject.getFilename();
}
*/

//This can be called locally, or from 'Main' class which will setFile initially
public void setFileForView(File myFile){
  this.shelfFileName.setText(myProject.getFilename());
  this.setTitle(myProject.getFilename()); //update title on window
  myFileList.add(myFile.getName()); //add string reference
  myObList.setAll(myFileList); //does a clear and refresh?
  System.out.println(myFileList.size()+","+myFile.getName());
}

//update view parameters that are based on a newly opened docx
public void setDocxForMainTabs(docXML input){
  docXML myPDoc = myProject.getOpenDocx();
  String test = myPDoc.getStylesObject().getStylesXML();
  styleTextArea.setText(test); //to display in tab_StyleXML
  String summaryOfStyles=myPDoc.getStylesObject().getSummaryStylesString();
  styleSummaryTextArea.setText(summaryOfStyles); //to display in tab_Styles_docx
  fieldsTextArea.setText(myPDoc.getStylesObject().getFieldsAsString()); //to display in tab_Fields_docx
  bookmarksTextArea.setText(myPDoc.getListBookmarks());
}

public void setMDForView(mdFile input) {
          //Nothing special for now?
}

/* 
Prior to calling this function this should have been completed:
1. Open file functions will create a set of Books in the myProject Object.

This function:
1. Add all Project books to gridSpace object (FX).  
2. Calls one of the methods that changes the position of books (X,Y parameters that work with FX)

TO DO: pick a default layout according to last selected 'Layout' option?
*/
public void unpackBooksToAllViews() {
          
          if (myProject.getNumberBooks()<1){
            System.out.println("No books in project to unpack");
            return;
          }

          //DO THIS ONCE PER OPEN FILE
          clearAllViews(); //clear all stage nodes because we are starting again
          AddProjectBooksToGridView();  //adds current project books back to gridSpace view
          //Optional tab information : tabs only filled for docx
          if (myProject.getExt().equals("docx")) {
            docXML currentDoc = myProject.getOpenDocx();
            setDocxForMainTabs(currentDoc); //update RHS tabs with relevant data
            }
        } 

//set filename to currently open file.
//add filename to current list view (TO DO: remove once closed)
public void setFilename(String myFile) {
    myProject.setFilenameFromString(myFile);
    String fn = myProject.getFilename(); //after checks
    this.shelfFileName.setText(fn);
    this.setTitle(fn); //update title on window
}

//make new scene with Scroller
private Scene makeSceneScrollerAsRoot (ScrollPane myRootNode) {

int setWidth=500;
int setHeight=250;
Scene myScene = new Scene (myRootNode,setWidth,setHeight); //width x height (px)
//this operates as a lambda - i.e events still detected by Main?
myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, myMouseLambda);
return myScene;
}
 
public void setPressBox(EventHandler<MouseEvent> myEvent) {
    this.PressBox=myEvent;
}

public void setDragBox(EventHandler<MouseEvent> myEvent) {
    this.DragBox=myEvent;
}

//Set key handler at level of stage in node editor
private void setKeyPressHandler(EventHandler<KeyEvent> myKE) {
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, myKE);
}

EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
    System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

//SIMPLE SCENE GETTERS AND SETTERS AS JAVA FX WRAPPER

private Scene getSceneLocal() {
    return this.localScene;
}

private Scene getSceneGUI () {
     return getStage().getScene(); //JavaFX
}

private void updateScene (Scene myScene) {
     this.localScene = myScene; //local copy/reference
}

//SIMPLE STAGE GETTERS AND SETTERS FOR CUSTOM GUI.  WRAPPER FOR JAVAFX SETTERS

/*
public void setStageName(String myName) {
    this.stageName = myName;
    this.localStage.setTitle(myName);
}
*/

public String getStageName() {
    return this.stageName;
}

//probably redundant - keep name or title
public void setTitle(String myTitle) {
    this.stageTitle = myTitle;
    System.out.println("Title set to:"+myTitle);
    //this.localStage.setTitle(myTitle); //update on screen
    this.primaryStage.setTitle(myTitle);
}

public String getTitle() {
    return this.stageTitle;
}

/*
private void refreshTitle() {
    this.localStage.setTitle(getTitle());
}
*/

//for passing in a menubar from main (for now: 29.4.18)
public void setMenuBar(MenuBar myMenu) {
    this.localmenubar = myMenu;
}

public MenuBar getMenuBar() {
    return this.localmenubar;
}

/* ----- DATA (DISPLAY) NODE FUNCTIONS ----- */

/*
//set the parent node for Nodes enclosed in boxes (i.e. level above)
private void setRefParentNode(Book myParentID) {
    this.reference_ParentNode = myParentID;
}

private Book getRefParentNode() {
    return this.reference_ParentNode;
}
*/
/* GUI FUNCTIONS FOR WORKING WITH BOXES, NODES */

/* ----- GENERAL GUI FUNCTIONS ----- */

//setter for the Stage
public void setStage(Stage myStage) {
    this.localStage = myStage;
}

//getter for the Stage
public Stage getStage() {
    return this.localStage;
}

/*
    A function that will clear all books from grid UI, AND the Project books
    
*/

public void clearAllBooks() {
    myProject.clearAllBooks(); 
    clearAllViews();
    String title = "default.md"; //this is not a full path cf other situations
    setFilename(title); //to do - clear all File object in Project?
    //To do - reconcile data here with Main class and Project class.  Or is just GUI?
    this.parentClass.resetFileNames(title); //to update general file name etc
}

/*
    A function that will clear all books from grid UI, without clearing the Project books
    This will allow repopulating grid UI again
*/

public void clearAllViews() {
    myGridSpace.clear();
    cleanAllTabContent();
}

public void cleanAllTabContent(){
    styleTextArea.setText(""); //to display in tab_StyleXML
    styleSummaryTextArea.setText("");
    fieldsTextArea.setText("");
    bookmarksTextArea.setText("");
    //change title if we want to
}

private void setStagePosition(double x, double y) {
    this.localStage.setX(x);
    this.localStage.setY(y);
}

private void stageBack() {
    this.localStage.toBack();
}

private void stageFront() {
    this.localStage.toFront();
}

//method to fix the BookMetaStage instance position relative to screen dimensions
private void setMetaStageParams(BookMetaStage newInspectorStage) {
    //Stage myMainStage = getStage(); 
    Stage myLocalStage = newInspectorStage.getStage();
    //centred on screen dimensions, not on the parent stage
    //myLocalStage.setX((ScreenBounds.getWidth() - myLocalStage.getWidth()) / 2); 
    //myLocalStage.setY((ScreenBounds.getHeight()-(myLocalStage.getHeight()) / 2); 
    //myLocalStage.setAlwaysOnTop(true); //<---- No, it's sometimes useful, more often annoying.  Tie to the base scene (myBP?)
    //myLocalStage.initOwner(this.localStage); //init owner must be the window (a Stage).  Do this before .show()
}

//STAGE MANAGEMENT FUNCTIONS

public void showStage() {
    this.localStage.show(); 
}

public void hideStage() {
    this.localStage.hide(); 
}

public void toggleStage() {
    Stage myStage = getStage();         
    if (myStage==null) {
        System.out.println("Problem with Stage setup +"+myStage.toString());
    }
    if (myStage.isShowing()==false) {
        showStage();
        return;
    }
    if (myStage.isShowing()==true) {
        hideStage();
        return;
    }
}

/* New Local mouse event handler */
 EventHandler<KeyEvent> SaveKeyEventHandler = new EventHandler<KeyEvent>() {
    @Override
    public void handle(KeyEvent ke) {
        //Book hasFocus = Main.this.getcurrentBook();
        Book hadFocus=null;
        Book currentBook = MainStage.this.getActiveViewBook(); //clicksource
            //in Edit Stage only a straight "Save" not SaveAs is checked for
            if (ke.isMetaDown() && ke.getCode().getName().equals("S")) {
                 System.out.println("CMD-S pressed for save");
                 MainStage.this.bookMetaInspectorStage.updateBookMeta(); //update inspector edits first
                 MainStage.this.writeFileOut();
                 //currentBook.cycleBookColour();
            }
      }
    };

//This event handler is sent to the Sprites, but it can access variables from this object
EventHandler<MouseEvent> DragBox = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {

            Book currentBook = ((Book)(t.getSource()));
            final EventType TYPE = t.getEventType(); //
            System.out.println("Event");
            System.out.println(TYPE.toString());
            // && MainStage.this.metaMode==false && MainStage.this.shiftMode==false
            if (currentBook!=null && MainStage.this.myGridSpace.getNumberSelectedBooks()==1) {
                MainStage.this.setActiveBook(currentBook); //clicked sprite
                double offsetX = t.getSceneX() - orgSceneX;
                double offsetY = t.getSceneY() - orgSceneY;
                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;
                //release situation
                if (t.MOUSE_RELEASED == TYPE) { //DRAG_
                    System.out.println("Mouse released");
                    System.out.println("release position: x "+newTranslateX+" y "+newTranslateY);
                    //shelf parameters
                    MainStage.this.myGridSpace.snapBook(currentBook,newTranslateX,newTranslateY);                   
                    } 
                else {
                    System.out.println("The handler for drag box is acting");
                    //update position
                    currentBook.setXY(newTranslateX,newTranslateY);
                    System.out.println("Main: Translate Position (X,Y): "+newTranslateX+","+newTranslateY);
                    //updates to sprite that triggered event
                    currentBook.setTranslateX(newTranslateX);
                    currentBook.setTranslateY(newTranslateY);
                    currentBook.doAlert(); //in case single click event doesn't detect
                    //t.consume();//check - maybe don't do this if you want to work with release
                }
            }
        }
    };

//General function for box clicks
EventHandler<MouseEvent> processLocalBoxClick = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {

        final EventType TYPE = t.getEventType(); //
        if (t.MOUSE_PRESSED == TYPE) { //DRAG_
            System.out.println("MOUSE PRESSED");
            //System.exit(0);
        }
        else {
            System.out.println(TYPE);
        }
        Book hadFocus=MainStage.this.getActiveViewBook();
        Book currentBook = (Book)t.getSource();  //selects a class for click source
        if (currentBook!=null) {
            System.out.println("Found click on sprite");
            //MainStage.this.setActiveBook(currentBook);
            //moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
            int clickcount = t.getClickCount();

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            orgTranslateX = currentBook.getTranslateX();
            orgTranslateY = currentBook.getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);
            
            Integer firstRow=hadFocus.getRow();
            Integer firstCol=hadFocus.getCol();
            System.out.println("FROM row: "+ firstRow+ " col: "+firstCol);

            switch(clickcount) {
            //single click
            case 1:
                //moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
                System.out.println("One click");
                System.out.println("Meta mode:"+MainStage.this.metaMode);
                System.out.println("Shiftmode:"+MainStage.this.shiftMode);

                //test for multiple selection
                if (t.isMetaDown()) {
                  System.out.println("One click with Meta");
                  //logger(currentBook);
                  MainStage.this.myGridSpace.toggleExtraSelection(currentBook);
                }
                if (t.isShiftDown()) {
                  //logger(currentBook);
                  MainStage.this.myGridSpace.shiftedSelection(currentBook);
                }

                else if (!t.isShiftDown() && !t.isControlDown() && !t.isMetaDown()) {
                  MainStage.this.myGridSpace.singleSelection(currentBook);
                }
                
                break;
            case 2:
                System.out.println("Two clicks");
                openBlockEditorNow(currentBook);
                
                break;
            case 3:
                System.out.println("Three clicks");
                break;
            }
        }
        else {
             System.out.println("Click on box but no sprite detected");
        }
    }
};

public void logger(Book currentBook){
    Integer latestRow=currentBook.getRow();
    Integer latestCol=currentBook.getCol();
    System.out.println("TO row: "+ latestRow+ " col: "+latestCol);
}

/* This function passes current book, selection information and event handlers to a new Java Object (BookMetaStage).
The JavaObject (BookMetaStage) creates a custom JavaFX.stage object that functions as a book data editor.
The editor stage opened will be able to edit the contents of the currently selected Book (cell). */
//TO DO: check Book isn't the basis for inspector stage before opening new stage.
private void openBlockEditorNow(Book currentBook) {
     //Book currentBook= getActiveViewBook(); //currentBook.getBoxNode();
     //bookMetaInspectorStage.closeThisStage(); //close open stage.  No save checks? //TO DO: close all child stages
     Stage parent = this.localStage; // the Stage associated with this object, not the MainStage object itself.
     bookMetaInspectorStage = new BookMetaStage(parent, currentBook, PressBox, DragBox, SaveKeyEventHandler); 
     
     System.out.println("set BookMetaStage...");
     setMetaStageParams(bookMetaInspectorStage);  //stores the UI 'stage' as the local stage.  That's all.
     bookMetaInspectorStage.storeSelectedBooks(myGridSpace.getSelectedBooks()); //pass any selection to the editor to use (for fills etc)
     System.out.println("new Stage Parameters Set ...");
}


/*Mouse event handler - to deal with boxes being dragged over this stage manager and release
If this is attached to the panel in the GUI where the child nodes sit, it is easy to handle a 'drop'
Currently utilises the 'makeScrollGroup' and addNewSpriteToStage methods.
The setbookgroupNode group must also add this event handler to that group.
*/

EventHandler<MouseEvent> mouseEnterEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //Book currentBook = ((Book)(t.getSource()));
            //TO DO: check if mouse is dragging/pressed
            //System.out.println("Detected mouse released - Stage Manager Group"+BookMetaStage.this.getbookgroupNode().toString());
            //t.consume();//check
        }
    };

//mouse drag

    EventHandler<MouseEvent> mouseDragEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //Book currentBook = ((Book)(t.getSource()));
            //TO DO: check if mouse is dragging/pressed
            //System.out.println("Detected mouse drag - Stage Manager Group"+BookMetaStage.this.getbookgroupNode().toString());
            //t.consume();//check
        }
    };


private void closeThisStage() {
    getStage().close();
}

// INPUT / OUTPUT 

//MainStage setup function
private void newWorkstageFromGroup(String title) {
    
    Group myGroup = makeWorkspaceTree(); //myGroup_root.  
    Scene myScene = makeWorkspaceScene(myGroup); //new scene with myGroup_root; eventHandlers added
    //Stage myStage = new Stage();
    setStage(this.primaryStage);
    setTitle(title);
    updateScene(myScene);
    this.primaryStage.setScene(myScene); //JavaFX in the GUI
    this.primaryStage.show();
    //setStagePosition(0,0);
    //stageBack();

    //showStage();
}

/* 

Java FX View setup:
Create root node and branches that is ready for placing in a Scene.

Sets up workspace stage with 2 subgroups for vertical separation:
(a) menu bar
(aa) layers listview
(b) display area

This method does not update content of the Sprite-display GUI node.

*/
/* Structure of GUI Nodes:

myGroup_root--->
myBP(top)-->menuBarGroup-->myMenu
myBP(center)-->tabGroup tab_Visual etc 
tab_Visual: ScrollPane and contents from gridSpace object.  Added to tabsGroup.

*/

private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        //myBP(top)-->menuBarGroup-->myMenu
        //holds the menubar at top, workspace in center of BorderPane
        
       //<--- ** LEFT region of the BorderPane ** --->

        Group layerGroup = new Group();

        //create observable list backed by an array list
        //takes 'List' as an argument.  Obs List = Changes update automatically in view.
        //ArrayList myFileList = new ArrayList(); //can't be 'List'
        //myFileList is an ArrayList
        //ObservableList<String> myItems = FXCollections.observableArrayList(myFileList);
       // myObList = FXCollections.observableArrayList(myFileList);
        //need listener to do something when list changes.  Refresh?

        //for list of open files
        //myFileList.add("Tester");
        //ObservableList<String> 
        myObList = FXCollections.observableArrayList(myFileList);
        ListView<String> layerListView = new ListView<String>(myObList); 
       
        //listen for changes to list; not clicks
        myObList.addListener(new ListChangeListener() { 
          @Override
            public void onChanged(ListChangeListener.Change change) {
                change.next(); //required
                if (change.wasAdded()) {
                   System.out.println("Detected addition to list");
                 }
                //MainStage.this.refresh();
              }
        });
        
        //ListView<String> layerListView = new ListView<String>(myObList); 
        //layerListView.setItems(myItems);
        layerListView.setPrefWidth(200);
        layerListView.setPrefHeight(300);

        //<----Customised cells section ---->
        
        //cells only need definition when structure being customised
        //basic approach is to set the column, then the cells
        //layerListView.setCellFactory(p -> new DraggableCell<>());
        /*
        layerListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
        //above line also creates the new ListCell as second item
        //override the call function which ListView 'calls' to obtain cells
          @Override
          public ListCell<String> call(ListView<String> param) {
            //The new cell is a cell with an override on update 
            //each 'item' here is an object that will be provided by the listview
            //the method called here must be available on the objects in the ListView
            //Cell may be represented by text or Checkbox...
              ListCell<String> myCell = new ListCell<String>();
              if (param.equals("")) {
                myCell.setText("Hello this was empty"); //for buttons need node, label
                return myCell;
              }
              else {
                myCell.setText(param.getItems().getText()); //use the listview param passed to this function
                return myCell;
              }
              };
            }); //end definition of callback
            /*ListCell<String> cell = new ListCell<String>(){
            @Override
                  protected void updateItem(String item) {
                  super.updateItem(item);
                  if (item != null) {
                    setText(item.getPlate());
                  }
                else {
                  setText(""); //clear for blank entries on re-use.
                }
              }
             
            }; //end of cell call function override
          return cell;  //the cell is the value returned from 'call'
          }
           */ //end of the override
        
        //<---end customised cells section ---> 
        
        //Add some Tabs so we can easily shift between gridview and viewing Styles.xml or similar.

        //choiceboxlistcell
        ScrollPane myLayerScroller = new ScrollPane(layerListView);

        //<--- ** TOP region of the BorderPane ** --->

        Group menubarGroup = new Group(); //subgroup
        
        MenuBar myMenu = getMenuBar(); //retrieve stored menu bar object

        menubarGroup.getChildren().addAll(myMenu);
        
        tab_Visual.setText("Visual");
               
        //<--- ** ADD CONTENTS TO BORDERPANE REGIONS ** --->
        BorderPane myBP = new BorderPane(); 
        myBP.setLeft(myLayerScroller);
        myBP.setMargin(myLayerScroller, new Insets(0,10,0,0));
        myBP.setTop(menubarGroup); //this includes the top menu.  Do not set anywhere else
        //myBP.setMargin(workspacePane, new Insets(50,50,50,50)); //i.e. Y=-50='translateX=0'
        myBP.setMargin(myGridSpace.getScrollPane(), new Insets(0,0,0,0));
        
        myBP.setCenter(myTabsGroup);
        tab_Visual.setContent(myGridSpace.getScrollPane());
        tab_StyleXML.setText("StyleXML");
        tab_StyleXML.setContent(styleTextArea);
        styleTextArea.setWrapText(true);

        tab_Bookmarks.setText("Bookmarks");
        tab_Bookmarks.setContent(bookmarksTextArea);
        bookmarksTextArea.setWrapText(true);

        tab_Styles_docx.setText("Styles");
        tab_Styles_docx.setContent(styleSummaryTextArea);
        styleSummaryTextArea.setWrapText(true);
        
        tab_Fields_docx.setText("Fields");
        tab_Fields_docx.setContent(fieldsTextArea);
        fieldsTextArea.setWrapText(true);
        // Add tabs in order
        myTabsGroup.getTabs().addAll(tab_Visual,tab_Fields_docx,tab_Bookmarks,tab_Styles_docx,tab_StyleXML);
        
        //add the Border Pane and branches to root Group 
        myGroup_root.getChildren().addAll(myBP);
        //putting lines first means they appear at back
      
        //store the root node for future use
        setSceneRoot(myGroup_root); //store 
        //for box placement within the Scene - attach them to the correct Node.
        return myGroup_root;  
    }

private Scene makeWorkspaceScene(Group myGroup) {
        
        //construct scene with its root node Color.DARKSLATEGREY
       
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), this.gridOuterColor);
                
        //nb do not change focus unless click on sprite group
        //Nodes etc inherit Event Target so you can check it in the event chain.
        
        //filter for capture, handler for sorting through the bubbling
        workspaceScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent mouseEvent) {
             //System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
             //System.out.println("Workspace is "+BookMetaStage.this.toString());
             //System.out.println("Here is the target: "+mouseEvent.getTarget());
             //System.out.println("Target class: "+mouseEvent.getTarget().getClass());
             if (getSceneGUI()!=getSceneLocal()) {
                  System.out.println("Problem with storing Scene");
             }
              else if (mouseEvent.getTarget() instanceof Rectangle) {
                 System.out.println("ws Clicked on box ; updated focus");
                 
            }
            /*
            areas and targets differ depending on objects on stage (invisible stretch)
            Ignore the MenuBar here
            JavaFX has ability to detect Text, Rectangle, ColBox (all components of a Book)
            Better to force it to detect a Book?
            Although clicking on text could be useful for updating headings/filenames
            It is possible to change focus with a click, but exclude MenuBar targets
            (these seemed to be instances of LabeledText 
            i.e. class com.sun.javafx.scene.control.LabeledText)
             */
            //if (mouseEvent.getTarget()==getSceneGUI()) {

            /*
            if (mouseEvent.getTarget() instanceof Scene) {
                System.out.println("Clicked on scene; updated focus");
                
               
                mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof BorderPane) {
                 System.out.println("Clicked on Border Pane ; updated focus");
                
               
                 mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof Pane) {
                 System.out.println("ws Clicked on Pane ; updated focus");
                
                 mouseEvent.consume(); //to stop the bubbling?
            }
            //to distinguish Text on Menu from Text on boxes you can interrogate what the Text is to see if it's a menu
            else if (mouseEvent.getTarget() instanceof Text) {
                 System.out.println("ws Clicked on Text ; no change to focus");
                 //this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else if (mouseEvent.getTarget() instanceof ColBox) {
                 System.out.println("ws Clicked on box ; updated focus");
                
                
            }
            else if (mouseEvent.getTarget() instanceof Rectangle) {
                 System.out.println("ws Clicked on box ; updated focus");
                 
            }
            else if (mouseEvent.getTarget() instanceof Labeled) {
                 System.out.println("ws Clicked on Labeled ; no change to focus");
                 //this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else {
                System.out.println("ws Click not identified : no change to focus");
            }
            */

            }
        });

        workspaceScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent ke) {
                 System.out.println("Key pressed on workspace stage " + ke.getSource());
                 System.out.println("KeyCode: "+ke.getCode());
                 //open book if CMD-O
                 //shortcuts for selections
                  if (ke.isMetaDown()) {
                     MainStage.this.metaMode=true;
                     //System.out.println("Pressed CMD");
                  }
                  else {
                     MainStage.this.metaMode=false;
                  }
                  if (ke.isShiftDown()){
                     MainStage.this.shiftMode=true;
                     //System.out.println("Pressed Shift");
                  }
                  else {
                    MainStage.this.shiftMode=false;
                  }
                 if (ke.isMetaDown() && ke.getCode().getName().equals("I") || ke.getCode()==KeyCode.ENTER) {
                    System.out.println("CMD-I or ENTER pressed (will open metadata inspector stage)");
                    try {
                        Book myBook= MainStage.this.getActiveViewBook();
                        myBook.setUserView("metaedithtml");
                        MainStage.this.openBlockEditorNow(myBook);
                    }
                    catch (NullPointerException e) {
                        //do nothing
                    }
                }
                //On Mac keyboards the 'delete' key activates as 'BACK_SPACE'
                if (ke.getCode()==KeyCode.DELETE || ke.getCode()==KeyCode.BACK_SPACE) {
                    System.out.println("BS/DELETE pressed (will delete book with focus)");
                    try {
                        MainStage.this.removeActiveBook();
                    }
                    catch (NullPointerException e) {
                        //do nothing
                    }
                    //MainStage.this.bookMetaInspectorStage = new BookMetaStage(MainStage.this, currentBook, PressBox, DragBox);
                }
                if (ke.getCode()==KeyCode.SPACE) {
                    System.out.println("SPACEBAR pressed (will open stage to inspect HTML in built-in Web Browser)");
                    try {
                        Book myBook= MainStage.this.getActiveViewBook();
                        myBook.setUserView("HTMLonly");
                        MainStage.this.openBlockEditorNow(myBook);
                    }
                    catch (NullPointerException e) {
                        //do nothing
                    }
                    //MainStage.this.bookMetaInspectorStage = new BookMetaStage(MainStage.this, currentBook, PressBox, DragBox);
                }
                //This operates independently to save event handler passed to bookmetastage
                if (ke.isMetaDown() && ke.isShiftDown() && ke.getCode().getName().equals("S")) {
                    ke.consume();
                    System.out.println("CMD-SHIFT-S pressed for save");
                    MainStage.this.saveAs();
                }
                else if (ke.isMetaDown() && ke.getCode().getName().equals("S")) {
                    System.out.println("CMD-S pressed for save");
                    MainStage.this.writeFileOut();
                }
                if (ke.isMetaDown() && ke.getCode().getName().equals("W")) {
                    System.out.println("CMD-W pressed (will clear bookshelf)");
                    clearAllBooks();
                }
                if (ke.isMetaDown() && ke.getCode().getName().equals("O")) {
                     System.out.println("CMD-O pressed for open");
                     Integer row = 0;
                     MainStage.this.openNewFile();
                }
                //Stage_WS.addNewBookToView();
                if (ke.isMetaDown() && ke.getCode().getName().equals("N")) {
                     System.out.println("CMD-N pressed for new book");
                     MainStage.this.addNewBookToView();
                     //then to open new link automatically for editing
                     Book myBook= MainStage.this.getActiveViewBook();
                     myBook.setUserView("metaedit");
                     MainStage.this.openBlockEditorNow(myBook);
                }
                //copy
                if (ke.isMetaDown() && ke.getCode().getName().equals("C")) {
                     System.out.println("CMD-C pressed for copy book");
                     Book myBook= MainStage.this.getActiveViewBook();
                     MainStage.this.clipboardBook=myBook.cloneBook();  //pointer to book, but memory doesn't survive this ?
                     System.out.println(MainStage.this.clipboardBook+", old: "+myBook);
                     //System.exit(0);
                }
                //paste
                if (ke.isMetaDown() && ke.getCode().getName().equals("V")) {
                     System.out.println("CMD-V pressed for paste book");
                     //cf try
                     if (MainStage.this.clipboardBook!=null) {
                        Book myBook = MainStage.this.clipboardBook;
                        MainStage.this.myGridSpace.pasteBook(myBook);
                     }  
                }
                //nudge left
                if (ke.isMetaDown() && ke.getCode().getName().equals("E")) {
                     System.out.println("CMD-E pressed for nudge left");
                        MainStage.this.cellShift("left");//active                    
                }
                //nudge right
                if (ke.isMetaDown() && ke.getCode().getName().equals("R")) {
                     System.out.println("CMD-R pressed for nudge right");
                     MainStage.this.cellShift("right");
                }
                //change focus to right
                if (ke.getCode()==KeyCode.RIGHT) {
                  ke.consume();
                  System.out.println("Right key pressed for move right");
                  MainStage.this.moveFocusCell("right");
               }
               //change focus to left
               if (ke.getCode()==KeyCode.LEFT) {
                  ke.consume();
                  System.out.println("Left key pressed for move left");
                  MainStage.this.moveFocusCell("left");
               }
               //change focus up
               if (ke.getCode()==KeyCode.UP) {
                  ke.consume();
                  System.out.println("Up key pressed");
                  MainStage.this.moveFocusCell("up");
               }
               //change focus down
               if (ke.getCode()==KeyCode.DOWN) {
                  ke.consume();
                  System.out.println("Down key pressed");
                  MainStage.this.moveFocusCell("down");
               }

             }
        });
        
        return workspaceScene;
    }

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}

public double getBigY() {
    return this.myBigY;
}

//EVENT HANDLERS FOR THE MAIN GRID WITH CELLS
//Will these be additional to the main stage?
EventHandler<KeyEvent> NodeKeyHandler2 = new EventHandler<KeyEvent>() {
 @Override
 public void handle(KeyEvent ke) {
    System.out.println("Key Event on current Stage:"+MainStage.this.toString());
    System.out.println("Key Press (keycode):"+ke.getCode());
    //System.out.println("Key Press (keycode textual):"+ke.getCode().getKeyCode());
    System.out.println("Key Press (keycode name):"+ke.getCode().getName());
    System.out.println("Key Press (as string):"+ke.getCode().toString());
    System.out.println("KeyPress (as source): " + ke.getSource());
    System.out.println("KeyPress (as higher-level event type): " + ke.getEventType());
    System.out.println("KeyPress (unicode): " + ke.getCharacter());
    System.out.println("is Shift Down: " + ke.isShiftDown());
    System.out.println("is Control Down: " + ke.isControlDown());
    System.out.println("is Meta(Command) Down: " + ke.isMetaDown());
    //'live' conversion of typing into HTML (not as efficient but typists don't notice)
    }
  };
}