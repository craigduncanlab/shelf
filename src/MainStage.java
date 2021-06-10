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
//Screen positioning
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
double scrollSceneWidth=0.8*myBigX;
double scrollSceneHeight=0.8*myBigY;
ArrayList<Stage> myStageList = new ArrayList<Stage>();
int spriteX = 0;
int spriteY = 0;
String stageName = "";
String stageTitle = "";
String displayOption = "Row";

//Book reference_ParentNode = new Book();
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group bookgroupNode;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;
Book focusBook; //for holding active sprite in this scene.  Pass to app.
Book clipboardBook; //for cut,copy and paste
//To hold Stage with open node that is current
BookMetaStage bookMetaInspectorStage; 

/*
String filename = "";
String shortfilename=""; //current filename for saving this stage's contents
*/
String category="";
//Displayed Book (i.e. Node).  Will be updated through GUI.
//Book displayNode = new Book();
int doccount=0; //document counter for this stage

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
Integer cellcols=50;
Integer cellrows=50; //make this 50 for sure
Integer cellgap_x=80; //cellwidth x dimension
Integer cellgap_y=100; //cell width y dimension
Integer firstcell_x=this.cellgap_x;
Integer firstcell_y=0;
Integer gridwidth=1000;
Integer gridlineheight=1;
Integer filenameoffset_y=20;
Integer filenameoffset_x=400; //how far across the filename appears
Integer cellrowoffset_y=30;
Integer boxtopmargin=10;
Color shelfborder=Color.BLACK;
Color gridOuterColor=Color.LIGHTBLUE; //Color.WHITE;
//Color shelffill=Color.WHITE; //background to 'grid' cf. DARKSLATEGREY


ArrayList<Layer>layersCollection = new ArrayList<Layer>();

//To store collection of "Books" with main content data.  Also stores X,Y in each book for now.
Project myProject = new Project(); //this is OK without waiting for constructor?
//ArrayList<Book> booksOnShelf = new ArrayList<Book>(); //generic store of contents of boxes

//Currently, 'selected' is kind of GUI property, reflected in individual Book property as well.
ArrayList<Book> selectedBooks = new ArrayList<Book>(); //for GUI selections
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
    //Book baseNode, 
    //category
    //NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
    //view
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
    //TO DO: setLocalSpriteSelect(processLocalBoxClick);
    //set event handlers as local instance variables.  These are used at time of Book creation
    //They are set here so that the Books can access BookMetaStage level data
    //See the 'addNodeToView' function that creates a new Book here.
    setPressBox(processLocalBoxClick); //stores this in a variable to pass to Book.
    //setPressBox(PressBox);
    setDragBox(DragBox); //stores this to pass to Book 
    setKeyPressHandler(SaveKeyEventHandler); //TO do - set a variable to pass to sprites=
    
    //we need to set sprite group etc

    newWorkstageFromGroup(title);
    currentLayer.setLayerNumber(1);
    System.out.println ("The initial bookgroupNode...");
    System.out.println (this.bookgroupNode);
    System.out.println("Reference of this stage object MainStage");
    System.out.println(MainStage.this);
    resetBookOrigin();
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

Opens up a file, creates a generic 'block' object to hold meaningful divisions in file,
creates an array of those blocks for the GUI.

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
    unpackBooksToView(); //all books now set up in myProject object.
    //return myBookSet;

    }

//DISPLAY OPTIONS

public void unpackBooksAsCol() {
    if (myProject.getNumberBooks()>20){
        wrapBooks();
    } 
    repositionProjectBooksVertical(myProject.getBooksOnShelf());//just in order they were added
}

public void unpackBooksAsRow() {
    if (myProject.getNumberBooks()>20){
        wrapBooks();
    } 
    repositionProjectBooksHorizontal(myProject.getBooksOnShelf());
}

public void wrapBooks() {
  wrapProjectBooksHorizontal(myProject.getBooksOnShelf(),1);
}

//Adds the book passed here (already in the Project list) to the stage.  AddNewBookToView
public void AddNewBookToStage(Book newBook) throws NullPointerException {
    try {
        setActiveBook(newBook);
        //setXYfromRowCol(newBook); X and Y are already set
        if (this.bookgroupNode.getChildren().contains(newBook)) {
            System.out.println("Detected book already on stage");
            System.exit(0);
        }
        this.bookgroupNode.getChildren().add(newBook); //<----JAVAFX ADDS OBJECT. CAN'T DO TWICE (ERROR)
        System.out.println("finished adding new book to project.  R,C:"+newBook.getRow()+","+newBook.getCol());
        //System.exit(0);
    }
    catch (NullPointerException e) {
        System.out.println("NullPointerException in AddNewBookToStage");
        System.exit(0);
    } 
    //
    //addBookToStage(newBook); 
    System.out.println("finished adding new book to stage");
}

//Adds books in project to stage.  
//If called by UnpackBookstoView then it clears books to avoid duplicates. 
//This is the logical place to ensure that event handlers and FX setup is completed.
//This must be done BEFORE calling 'setActiveBook'

private void AddProjectBooksToStage() throws NullPointerException {
    System.out.println("Adding project books to stage");
   
    ArrayList<Book> myBookList = myProject.getBooksOnShelf();
    System.out.println("Project books:"+myBookList.size());
    
    for (Book item: myBookList) {
    try {
        //This if for FX purposes, but must be done as part of creation of new books ASAP, before setActive.
        item.setHandlers(PressBox,DragBox);
        setActiveBook(item);
        this.bookgroupNode.getChildren().add(item); //<----JAVAFX ADDS OBJECT. CAN'T DO TWICE (ERROR)
        setXYfromRowCol(item);
        //positionBookOnStage(newBook);  //not needed outside drags
    }
    catch (NullPointerException e) {
        System.out.println("NullPointerException in AddNewBookToStage");
        System.exit(0);
    } 
  } //end for loop
}

/* input:
The blocklist (strings) plus docXML info to obtain metadata
//TO DO: Use objects/Project data model to store/get blocklists, rather than arbitrary blocklists.
Two steps here:
Update the GUI location for the blocks
Add the blocks to stage.
Have MainStage functions that will add blocks vertically or horizontally (2 Views)

TO DO: Have a separate data object that can be created by file format classes:
One that has no JavaFX dependencies.

*/

//This updates view based on Project.  These are not 'new' books being added.

public void repositionProjectBooksVertical(ArrayList<Book> myBookSet){
  //not yet the project booklist? DO THAT.  get these books from project.
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int rowcount=0;
  //STEP 1: SET GUI PARAMETERS BEFORE ADDING TO STAGE
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
          Book thisBook =iter.next(); 
          if (thisBook!=null) {
           
            //set position as part of data model
            thisBook.setRow(rowcount); //default col is 0.
            thisBook.setCol(0);
            //This if for FX purposes, but must be done as part of creation of new books ASAP.
            //thisBook.setHandlers(PressBox,DragBox);
            setXYfromRowCol(thisBook); //update stage XY position (GUI/VIEW)
            }
            else {
              System.out.println("No book to add");
            }           
            rowcount++;
          } //end while
     } //end if
     //---ABOVE JUST CHANGES POSITIONS, ASSUMING ALREADY IN PROJECT/STAGE
  } 

//TO DO: Allow function to take a row number for insert.

public void repositionProjectBooksHorizontal(ArrayList<Book> myBookSet){
  //not yet the project booklist? DO THAT.  get these books from project.
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int colcount=1;
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
          Book thisBook =iter.next(); 
          //
          if (thisBook!=null) {
            //set position as part of data model
            thisBook.setCol(colcount); //default col is 0.
            thisBook.setRow(1);
            }
            else {
              System.out.println("No book to add");
            }
            //This if for FX purposes, but must be done as part of creation of new books ASAP.
            //thisBook.setHandlers(PressBox,DragBox);
            setXYfromRowCol(thisBook); //update stage XY position (GUI/VIEW)
            //AddNewBookToStage(thisBook); //adds new book to stage (adds to project only if needed) 
            colcount++;
          } //end while
     } //end if
     //Books should only need to be added to the project and Stage once.  After that, it's just changing position etc
     //---ABOVE JUST CHANGES POSITIONS, ASSUMING ALREADY IN PROJECT/STAGE
  } 

public void wrapProjectBooksHorizontal(ArrayList<Book> myBookSet, int space){
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int colcount=0;
  int rowcount=1;
  int wrapcol=11;//column to wrap on
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
        //if books do not appear change the pointer to iter.next() directly
          Book thisBook =iter.next(); 
          //
          if (thisBook!=null) {
            //set position as part of data model
            thisBook.setCol(colcount); //default col is 0.
            thisBook.setRow(rowcount);
            }
            else {
              System.out.println("No book to add");
            }
            //This if for FX purposes, but must be done as part of creation of new books ASAP.
            //thisBook.setHandlers(PressBox,DragBox);
            setXYfromRowCol(thisBook); //update stage XY position (GUI/VIEW)
            //AddNewBookToStage(thisBook); //adds new book to stage (adds to project only if needed) 
            if (colcount>wrapcol) {
                //checkers
              if (space==2 && rowcount % 2 != 0) {
                colcount=1;
              }
              else {
                colcount=0;
              }
              rowcount=rowcount+1;
            }
            else {
              colcount=colcount+space;
            }
          } //end while
          
          
     } //end if
  } 

public void wrapProjectBooksCheckers(){
    wrapProjectBooksHorizontal(myProject.getBooksOnShelf(), 2);
}

//Simple utility to return contents of file as String
//This is used to read in styles for Word doc output.
// TO DO: create a docx class for output, store styles in there.

public String getFileText(File myFile) {
  StringBuffer myText = new StringBuffer(); //mutable String
  String endOfLine="\n"; //to do - operating system independent
  try {
    Scanner scanner1 = new Scanner(myFile);
    if (scanner1==null) {
      System.out.println("No text/html content");
      return null;
    }
    int nl=0;
    while (scanner1.hasNextLine()) {
      nl++;
      String thisRow=scanner1.nextLine();
      System.out.println(thisRow);
      myText.append(thisRow);
      myText.append(endOfLine);
    }
    scanner1.close();
  }
  catch (Throwable t)
  {
    t.printStackTrace();
    //System.exit(0);
    return null;
  }
  //System.out.println(myText);
  //System.exit(0);
  return myText.toString();
}


//use row for positions
//NO need to return file as it will be stored in specific file that is opened.
//TO DO: Describe funciton as 'Open File as Row'?
public void openFileAsRow(Integer row) {
        this.spriteX=(int)convertColtoX(0);
        this.spriteY=(int)convertRowtoY(row);
        System.out.println("Row check:"+row+", spriteY:"+this.spriteY);
        //System.exit(0);
        // final FileChooser fileChooser = new FileChooser();
        File file = new File (myProject.getFilename());
        if (file==null) {
            file = new File ("untitled.md");
        } 
        Stage myStage = new Stage();
        myStage.setTitle("Open File");
        this.currentFileChooser.setTitle("Open A File");
        //this.currentFileChooser.setSelectedExtensionFilter(this.myExtFilter);   
        this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
        File tryfile = currentFileChooser.showOpenDialog(myStage);
        if (tryfile != null) {
          file = tryfile;
          openFileGetBooklist(file); //add books to project
          AddProjectBooksToStage();
          unpackBooksAsRow(); 
        } 
        else {
            //DO SOMETHING
        }
    //return file;
}


//what is returned by getrow?
public Integer getRowofActiveBook() {
    Integer row=0;
    Book thisBook = getActiveBook();
    if (thisBook!=null) {
        row=thisBook.getRow();
    }
    return row;
}

//use row for positions
//This is called from 'Main' so it returns the 'File' for future use.
//TO DO: store in myproject.
//It could equally return the 'myProject' object that contains file details etc?
//Distinguish between 'Import to Project' and 'Open to GUI and show contents' (as here)
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
     setDocxForView(currentDoc); //update visible stylesXML information.
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
     setDocxForView(currentDoc); //update visible stylesXML information.
  }
}

//for save as
public void saveAs() {
    Stage myStage = new Stage();
    myStage.setTitle("Save As ...");
    //change filename just in case, for Word
    String myFilename= myProject.getFilename(); //SaveAs will use current path so only name is needed
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
        writeOutBooksToWord(); //This is just writing from markdown + notes for now (not the docx)
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
    unpackBooksToView(); //clears stage and adds new books 
}

//function to change way box labels are displayed
public void setDisplayModeTitles(Integer input){
  if (input>0 && input<6) {
    ArrayList<Book> myBooksonShelves = myProject.listBooksShelfOrder(); 
    Integer booknum=myBooksonShelves.size();
    for (int x=0;x<booknum;x++) {
          Book item = myBooksonShelves.get(x);
          item.setDisplayMode(input);
    }
      //'booksOnShelf' is the global arraylist holding books
      myProject.setBooksOnShelf(myBooksonShelves); //change the pointer (if needed?)
    }
    System.out.println("Display Mode set: "+input);
}


//wrap layout of books to 10 books wide (default)
//Is this redundant now there is 'wrapBooks'?
/*
//This sets both row,col and X,Y.  TO DO:  set only Row, Col attributes in main API and let layout manager position cell.
public void unpackBooksWrapped() {
    //until reloaded the item order in memory isn't same as GUI
    //ArrayList<Book> myBooksonShelves = getBooksOnShelf(); 
    //'booksOnShelf' is the global arraylist holding books
    ArrayList<Book> myBooksonShelves = myProject.listBooksShelfOrder(); 
    Integer booknum=myBooksonShelves.size();
    Integer xcount=0;
    Integer ycount=0;
    for (int x=0;x<booknum;x++) {
        Book item = myBooksonShelves.get(x);
        if (xcount<9) {
          xcount=xcount+1;
        }
        else {
          xcount=0;
          ycount=ycount+1;
        }
        setXYfromNewRowCol(item,ycount,xcount);
    }   
    myProject.setBooksOnShelf(myBooksonShelves); //change the pointer (if needed?)
}
*/
public void singleSelection(Book thisBook){
  
  if (this.selectedBooks.size()>0) {
      for (Book item: this.selectedBooks) {
         item.endAlert();
      }
  }
  
  try {
      ArrayList<Book> newSelection= new ArrayList<Book>();
      newSelection.add(thisBook);
      this.focusBook=thisBook; 
      thisBook.doAlert(); //Change this so there is a general 'undo alert'
      this.selectedBooks=newSelection;
  }
  catch (Throwable t) //for greater detail for debugging
        {
            t.printStackTrace();
            return;
        }
}

//no need for these to be sorted.  However, books in selection should be 
public void refreshSelectedBooksColour() {
  ArrayList<Book> sorted= myProject.getBooksOnShelf(); //can this be stored, only updated when needed?
  Iterator <Book> myIterator = sorted.iterator();
  while(myIterator.hasNext()){
    Book item = myIterator.next();
    if (this.selectedBooks.contains(item)) {
      item.doAlert();
    }
    else {
      item.endAlert();
    }
  }
}

public void toggleExtraSelection(Book thisBook){
    //ArrayList<Book> myBooksonShelves = getBooksOnShelf();

    if (selectedBooks.contains(thisBook)) {
        int num = selectedBooks.size();
        System.out.println("selectedBooks items:"+num);
        for (int x =0;x<num;x++){
          System.out.println(selectedBooks.get(x).getLabel());
        }
        selectedBooks.remove(thisBook);
        thisBook.endAlert();
        System.out.println("Toggle, but found book");
    }
    else if (!selectedBooks.contains(thisBook)) {
        selectedBooks.add(thisBook);
        thisBook.doAlert();
        this.focusBook=thisBook;
        System.out.println("Toggle, but did not find book");
    }
}

public void shiftedSelection(Book thisBook) {
      Book firstBook = this.selectedBooks.get(0);
      ArrayList<Book> newList = new ArrayList<Book>();
      //newList.add(firstBook); //start selection again with only origin book
      this.selectedBooks = newList;
      ArrayList<Book> sorted= myProject.listBooksShelfOrder(); //can this be stored, only updated when needed?
      Iterator <Book> myIterator = sorted.iterator();
      Boolean selection=false;
      Boolean stop=false;
      while(myIterator.hasNext()) {
      Book item = myIterator.next();
      item.endAlert(); //reset

      //find start of selection
      if (item==firstBook && !this.selectedBooks.contains(thisBook)){
            selection=true;
      }
      if (item==thisBook && !this.selectedBooks.contains(firstBook)){
            selection=true;
      }
      //find end of selection
      if (item==firstBook && this.selectedBooks.contains(thisBook)){
            stop=true;
            selection=false;
            this.selectedBooks.add(item);
            item.doAlert();
      }
      if (item==thisBook && this.selectedBooks.contains(firstBook)){
            stop=true;
            selection=false;
            this.selectedBooks.add(item);
            item.doAlert();
      }
      //if still in mid range selection
      if (selection==true && !this.selectedBooks.contains(item)) {
            this.selectedBooks.add(item); 
            item.doAlert();     
      }
    } //end while
   }



/*
//will save this file (assumes it is text, html etc)
private void basicFileWriter(String logstring,String filename) {
    //String reportfile=this.templatefolder+filename+".md";

    try {
      PrintStream console = System.out;
      PrintStream outstream = new PrintStream(new FileOutputStream(filename,false)); //true = append.  This overwrites.
      System.setOut(outstream);
      //String logString = Integer.toString(myNode.getNodeRef())+"@@P"+myNode.getDocName()+"@@P"+myNode.getHeading()+"@@P"+myNode.getNotes()+"@@P"+myNode.getHTML()+"@@P@EOR";
      System.out.print(logstring); //don't use println.  No CR needed.
      outstream.close();
      System.setOut(console);
    }
        catch (Throwable t)
        {
            t.printStackTrace();
            return;
        }
} 
*/ 

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
public void setDocxForView(docXML input){
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
1. Open file functions create a set of books in the myProject Object.
2. 
This function:
1. Add all project books to Stage node (FX).  
2. Calls one of the methods that changes the position of books (X,Y parameters that work with FX)

TO DO: pick a default layout according to last selected 'Layout' option?
*/
public void unpackBooksToView() {

          if (myProject.getNumberBooks()<1){
            System.out.println("No books in project to unpack");
            return;
          }

          //DO THIS ONCE PER OPEN FILE
          clearAllBooksFromStage(); //clear all stage nodes because we are starting again
          AddProjectBooksToStage();  //adds current project books back to stage nodes

          //if we wanted to reinstate books from stored positions we'd call  setXYfromRowCol(item); instead of bulk options below
          //reposition books

          if (myProject.getExt().equals("docx")) {
            docXML currentDoc = myProject.getOpenDocx();
            setDocxForView(currentDoc); //update RHS tabs with relevant data
            }

          if (this.displayOption.equals("Row)")) {
                unpackBooksAsRow();
          }
          else if (this.displayOption.equals("Col))")) {
                unpackBooksAsCol();
          }
          else if (this.displayOption.equals("Wrap")) {
                wrapBooks();
          }
          else {
                wrapBooks();
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

public int getDocCount() {
    return this.doccount;
}

public void resetDocCount() {
    this.doccount=0;
}

public int advanceDocCount() {
    return this.doccount++;
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

//JAVA FX TEXT AREAS - GETTERS AND SETTERS

public void setOutputText(String myText) {
    outputTextArea.setText(myText);
}

public String getOutputText() {
    return outputTextArea.getText();
}

//Return the JavaFX object (Node) 
public TextArea getOutputTextNode() {
    return this.outputTextArea;
}

//Input text area e.g. importer
public void setInputText(String myText) {
    inputTextArea.setText(myText);
}

public String getInputText() {
    return inputTextArea.getText();
}

/* Text Area in JavaFX inherits selected text method from
javafx.scene.control.TextInputControl
*/

private String getSelectedInputText() {
    return inputTextArea.getSelectedText();
}

//set the identified JavaFX object (TextArea) for the Stage
public void setStageTextArea(TextArea myTA) {
    this.inputTextArea = myTA;
}

//Return the JavaFX object (Node) 
public TextArea getInputTextNode() {
    return this.inputTextArea;
}

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

public void setCategory(String myCat) {
    this.category=myCat;
}

public String getCategory() {
    return this.category;
}

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
setter for the Group sprite boxes will be added to
*/

public void setGridGroup(Group myGroup) {
    this.bookgroupNode = myGroup;
}

public Group getGridGroup() {
    return this.bookgroupNode;
}

public void setSpriteScrollPane(ScrollPane myPane) {
    this.spriteScrollPane = myPane;
}

public ScrollPane getSpriteScrollPane() {
    return this.spriteScrollPane;
}


public void clearAllBooks() {
    myProject.clearAllBooks(); 
    clearAllBooksFromStage();
}

public void clearAllBooksFromStage() {
    this.bookgroupNode.getChildren().clear();
    this.resetBookOrigin();
    String title = "default.md"; //this is not a full path cf other situations
    setFilename(title); //to do - clear all File object in Project?
    //To do - reconcile data here with Main class and Project class.  Or is just GUI?
    this.parentClass.resetFileNames(title); //to update general file name etc
    //clean up workspace
    styleTextArea.setText(""); //to display in tab_StyleXML
    styleSummaryTextArea.setText("");
    fieldsTextArea.setText("");
    bookmarksTextArea.setText("");
}

/*
public void swapbookgroupNode(Group myGroup) {
    Pane myPane = getSpritePane();
    myPane.getChildren().remove(getbookgroupNode());
    setbookgroupNode(myGroup);
    myPane.getChildren().addAll(myGroup);
}
*/

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

//getters and setters
public void setCurrentXY(double x, double y) {

	this.latestX=x;
    this.latestY=y;
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

/*
//The scene only contains a pane to display sprite boxes
private Scene makeSceneForBoxes(ScrollPane myPane) {
        
        Scene tempScene = new Scene (myPane,650,400); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on SM scene detected! " + mouseEvent.getSource());
         //setStageFocus("document");
             }
        });
        updateScene(tempScene);
        return tempScene;
}
*/

//Method to change title depending on data mode the node is in.
private String getTitleText(String myString) {
    System.out.println("Make Scene. User Node View: "+getActiveBook().getUserView());
    //main function        
    return getActiveBook().getDocName()+myString;
   
}

//snap to grid - move to nearest cell based on <= 50% out of position
public double snapYtoShelf(Book myBook, double newTranslateY){
    Integer modY=(int)newTranslateY%(int)this.cellgap_y;
    Integer quotY=(int)((newTranslateY-modY)/this.cellgap_y);
    Integer half = (int)this.cellgap_y/2;
    System.out.println("Num: "+newTranslateY+"cellgapY: "+this.cellgap_y+" quot:"+quotY+" mod:"+modY);
    if (modY>half) {
        quotY=quotY+1;
    }
    //checkbounds
    if (quotY<0) {
        quotY=0;
    }
    if(quotY>(this.cellrows-1)) {
        quotY=(this.cellrows-1);
    }
    myBook.setRow(quotY);
    newTranslateY=(this.cellgap_y*quotY)+this.cellrowoffset_y+this.boxtopmargin;
    return newTranslateY;
}

//snap to grid - move to nearest cell based on <= 50% out of position
public double snapXtoShelf(Book myBook, double newTranslateX){
    Integer modX=(int)newTranslateX%(int)this.cellgap_x;
    Integer quotX=(int)((newTranslateX-modX)/this.cellgap_x);
    Integer half = (int)this.cellgap_x/2;
    System.out.println("Num: "+newTranslateX+"cellgapX: "+this.cellgap_x+" quot:"+quotX+" mod:"+modX);
    if (modX>half) {
        quotX=quotX+1;
    }
    //checkbounds
    if (quotX<0) {
        quotX=0;
    }
    if(quotX>this.cellcols+1) {
        quotX=this.cellcols+1;
    }
    myBook.setCol(quotX);
    newTranslateX=this.cellgap_x*quotX;
    return newTranslateX;
}

/* New Local mouse event handler */
 EventHandler<KeyEvent> SaveKeyEventHandler = new EventHandler<KeyEvent>() {
    @Override
    public void handle(KeyEvent ke) {
        //Book hasFocus = Main.this.getcurrentBook();
        Book hadFocus=null;
        Book currentBook = MainStage.this.getActiveBook(); //clicksource
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
            if (currentBook!=null && MainStage.this.selectedBooks.size()==1) {
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
                                       
                    newTranslateY=MainStage.this.snapYtoShelf(currentBook,newTranslateY);
                    newTranslateX=MainStage.this.snapXtoShelf(currentBook,newTranslateX);
                    //System.exit(0);
                    currentBook.setTranslateX(newTranslateX);
                    currentBook.setTranslateY(newTranslateY);

                    currentBook.doAlert();
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
        Book hadFocus=MainStage.this.getActiveBook();
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
                  Integer latestRow=currentBook.getRow();
                  Integer latestCol=currentBook.getCol();
                  System.out.println("TO row: "+ latestRow+ " col: "+latestCol);
                  MainStage.this.toggleExtraSelection(currentBook);
                }
                if (t.isShiftDown()) {
                  Integer latestRow=currentBook.getRow();
                  Integer latestCol=currentBook.getCol();
                  System.out.println("TO row: "+ latestRow+ " col: "+latestCol);
                  MainStage.this.shiftedSelection(currentBook);
                }

                else if (!t.isShiftDown() && !t.isControlDown() && !t.isMetaDown()) {
                  MainStage.this.singleSelection(currentBook);
                }
                /*if (MainStage.this.metaMode) {
                  System.out.println("One click with Meta");
                  Integer latestRow=currentBook.getRow();
                  Integer latestCol=currentBook.getCol();
                  System.out.println("TO row: "+ latestRow+ " col: "+latestCol);
                  MainStage.this.metaMode=false;
                  MainStage.this.toggleExtraSelection(currentBook);
                }
                else if (MainStage.this.shiftMode) {
                  System.out.println("One click with SHIFT");
                  Integer latestRow=currentBook.getRow();
                  Integer latestCol=currentBook.getCol();
                  System.out.println("TO row: "+ latestRow+ " col: "+latestCol);
                }

                else if (MainStage.this.shiftMode==false && MainStage.this.metaMode==false) {
                  MainStage.this.singleSelection(currentBook);
                }
                */
                
                //change stage focus with just one click on Book (but node still closed)
                //bookMetaInspectorStage=currentBook.getStageLocation();
                //refreshNodeViewScene();
                break;
            case 2:
                System.out.println("Two clicks");
                
                //moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
                
                //Dbl Click action options depending on box type
               
                //bookMetaInspectorStage=currentBook.getStageLocation();
                //only open if not already open (TO DO: reset when all children closed)
                //prevent closing until all children closed
                //close all children when node closed.
                OpenRedBookNow(currentBook);
                
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


/* This function passes current book, selection information and event handlers to a new Java Object (BookMetaStage).
The JavaObject (BookMetaStage) creates a custom JavaFX.stage object that functions as a book data editor.
The editor stage opened will be able to edit the contents of the currently selected Book (cell). */
//TO DO: check Book isn't the basis for inspector stage before opening new stage.
private void OpenRedBookNow(Book currentBook) {
     //Book currentBook= getActiveBook(); //currentBook.getBoxNode();
     //bookMetaInspectorStage.closeThisStage(); //close open stage.  No save checks? //TO DO: close all child stages
     Stage parent = this.localStage; // the Stage associated with this object, not the MainStage object itself.
     bookMetaInspectorStage = new BookMetaStage(parent, currentBook, PressBox, DragBox, SaveKeyEventHandler); 
     
     System.out.println("set BookMetaStage...");
     setMetaStageParams(bookMetaInspectorStage);  //stores the UI 'stage' as the local stage.  That's all.
     bookMetaInspectorStage.storeSelectedBooks(this.selectedBooks); //pass any selection to the editor to use (for fills etc)
     System.out.println("new Stage Parameters Set ...");
}
/*switch(clickcount) {
    //single click
    case 1:
        moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
        System.out.println("One click");
        //change stage focus with just one click on Book (but node still closed)
        bookMetaInspectorStage=currentBook.getStageLocation();
        //refreshNodeViewScene();
        break;
    case 2:
        System.out.println("Two clicks");
        
        moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
        
        //Dbl Click action options depending on box type
       
        bookMetaInspectorStage=currentBook.getStageLocation();
        //only open if not already open (TO DO: reset when all children closed)
        //prevent closing until all children closed
        //close all children when node closed.
        OpenRedNodeNow(currentBook);
        
        break;
    case 3:
        System.out.println("Three clicks");
        break;
}
*/


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
(b) sprite display area, which is inside a border pane and group for layout reasons.

This method does not update content of the Sprite-display GUI node.

*/
/* Structure of GUI Nodes:

myGroup_root--->
myBP(top)-->menuBarGroup-->myMenu
Tabs here?
tab_Visual:
myBP(center)-->myScrollPane-->filename+workspacePane (Pane) -->displayAreaGroup (for BookIcons etc to be added)+ RowLines (Line) + ColLines (Line)
Tab B:

TO DO: the Pane should only display a 'Window' of content and then shift content, so that it is an infinite size and does not need to be defined.
We might need at least 30 rows for some outline views of Word docs etc.
*/

private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        //myBP(top)-->menuBarGroup-->myMenu
        //holds the menubar at top, workspace in center of BorderPane
        
       //<--- ** LEFT region of the BorderPane ** --->

        Group layerGroup = new Group();

        //an array list for list view
        
        
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
        BorderPane myBP = new BorderPane(); 
        
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
        //ayerListView.setCellFactory(p -> new DraggableCell<>());
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

        //Set title of this stage to the file name
        /*
        String myupdate=this.shelfFileName.getText();
        setTitle(myupdate);
        */
        /*
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        
        primaryStage.setScene(new Scene(borderPane));
        */
        menubarGroup.getChildren().addAll(myMenu);
        
        //<--- ** centre region of the BorderPane ** --->
        /*
        this.shelfFileName.setY(this.filenameoffset_y);
        this.shelfFileName.setX(this.filenameoffset_x); 
        */
        Pane workspacePane = new Pane(); 
        //workspacePane.setPrefWidth(this.wsPaneWidth);
        //workspacePane.setPrefHeight(this.wsPaneHeight);
        //the Pane holding the group allows movement of Books independently, without relative movement
         /*ScrollPane boxPane = makeScrollGroup();
        boxPane.setPannable(true);
        boxPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
        boxPane.setVmax(500);
        */
        Group displayAreaGroup = new Group(); //subgroup of Pane; where Squares/Boxes located
        //myScrollPane.getChildren().addAll(displayAreaGroup);
        //VBox centrelayout = new VBox(this.shelfFileName,workspacePane);

        //TABS in CENTRE PANE. FOR SCROLLPANE
         //Create Tabs for Tab Pane, which will sit inside editor
        //Tab tab_Visual = new Tab();
        tab_Visual.setText("Visual");
        

       // --- IF USING A SCROLLPANE HERE, ADD THAT TO THE BP
        ScrollPane myScrollPane = new ScrollPane(workspacePane); //content is the workspacePane


        myScrollPane.setPrefViewportWidth(this.scrollSceneWidth);
        myScrollPane.setPrefViewportHeight(this.scrollSceneHeight);
        myScrollPane.pannableProperty().set(false);  //to prevent panning by mouse
        myScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS")); //AS_NEEDED or ALWAYS
        myScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
       
        

        //--- IF USING A PANE

        //myScrollPane.setVmax(500);
        workspacePane.getChildren().addAll(displayAreaGroup);
        //String wpaneStyle="-fx-background-color:white; ";
        //String wpaneStyle="-fx-background-color:LightGrey; "; //main grid background colour
        String wpaneStyle="-fx-background-color: CADETBLUE; "; 
        workspacePane.setStyle(wpaneStyle);
         //make it big enough for number of rows/cols
        //ScrollPane myScrollPane= new ScrollPane();
        String scrollpaneStyle="-fx-background-color:blue; ";
        myScrollPane.setStyle(scrollpaneStyle);
        
        setGridGroup(displayAreaGroup); //store dAG in bookgroupNode variable as global variable
        //myScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
       // myScrollPane.setVmax(500);
        //Pane workspacePane = new Pane(); //to hold a group, holding a bookgroupNode
        //This is empty initially...

        //setSpritePane(workspacePane); //store for later use
        

        
        //<--- ** ADD CONTENTS TO BORDERPANE REGIONS ** --->
        myBP.setLeft(myLayerScroller);
        myBP.setMargin(myLayerScroller, new Insets(0,10,0,0));
        myBP.setTop(menubarGroup); //this includes the top menu.  Do not set anywhere else
        //myBP.setMargin(workspacePane, new Insets(50,50,50,50)); //i.e. Y=-50='translateX=0'
        myBP.setMargin(myScrollPane, new Insets(0,0,0,0));
        
        myBP.setCenter(myTabsGroup);
        tab_Visual.setContent(myScrollPane);
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
        
        //Make horizontal lines for grid, and add to FX root node for this Stage
       
        ArrayList<Line> myRowLines=new ArrayList<Line>();
        double startX=0.0; //+cellrowoffset_y;
        double endX=(this.cellcols+2)*this.cellgap_x;
        for (int i=0;i<this.cellrows+2;i++) {
            Line line = new Line(startX,(i*cellgap_y)+cellrowoffset_y,endX,(i*cellgap_y)+cellrowoffset_y);
            myRowLines.add(line); //future use
            workspacePane.getChildren().add(line); //put them here so they are not 'erased' and remains visible
            //displayAreaGroup.getChildren().add(line);
        }
        ArrayList<Line> myColLines=new ArrayList<Line>();
        double startY=0.0+this.cellrowoffset_y;
        double endY=((this.cellrows+2)*this.cellgap_y)+this.cellrowoffset_y;
        //we need 2 extra lines
        for (int i=0;i<this.cellcols+3;i++) {
            //System.out.println(cellrows+", i:"+i+" startY:"+startY+" endY:"+endY);
            Line line2 = new Line(i*this.cellgap_x,startY,i*this.cellgap_x,endY);
            myColLines.add(line2); //future use
            workspacePane.getChildren().add(line2);
            //displayAreaGroup.getChildren().add(line);
        }
              
        //make up a pane to display filename of bookshelf (not used)
        //Pane shelfFilePane = new Pane();
        //shelfFilePane.getChildren().add(this.shelfFileTextArea);
                
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
                        Book myBook= MainStage.this.getActiveBook();
                        myBook.setUserView("metaedithtml");
                        MainStage.this.OpenRedBookNow(myBook);
                    }
                    catch (NullPointerException e) {
                        //do nothing
                    }
                }
                //On Mac keyboards the 'delete' key activates as 'BACK_SPACE'
                if (ke.getCode()==KeyCode.DELETE || ke.getCode()==KeyCode.BACK_SPACE) {
                    System.out.println("BS/DELETE pressed (will delete book with focus)");
                    try {
                        Book myBook= MainStage.this.getActiveBook();
                        MainStage.this.removeBookFromStage(myBook);
                    }
                    catch (NullPointerException e) {
                        //do nothing
                    }
                    //MainStage.this.bookMetaInspectorStage = new BookMetaStage(MainStage.this, currentBook, PressBox, DragBox);
                }
                if (ke.getCode()==KeyCode.SPACE) {
                    System.out.println("SPACEBAR pressed (will open stage to inspect HTML in built-in Web Browser)");
                    try {
                        Book myBook= MainStage.this.getActiveBook();
                        myBook.setUserView("HTMLonly");
                        MainStage.this.OpenRedBookNow(myBook);
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
                     //currentBook.cycleBookColour();
                }
                if (ke.isMetaDown() && ke.getCode().getName().equals("W")) {
                    System.out.println("CMD-W pressed (will clear bookshelf)");
                    clearAllBooks();
                }
                if (ke.isMetaDown() && ke.getCode().getName().equals("O")) {
                     System.out.println("CMD-O pressed for open");
                     Integer row = 0;
                     MainStage.this.openNewFile();
                     //currentBook.cycleBookColour();
                }
                //Stage_WS.addNewBookToView();
                if (ke.isMetaDown() && ke.getCode().getName().equals("N")) {
                     System.out.println("CMD-N pressed for new book");
                     MainStage.this.addNewBookToView();
                     //then to open new link automatically for editing
                     Book myBook= MainStage.this.getActiveBook();
                     myBook.setUserView("metaedit");
                     MainStage.this.OpenRedBookNow(myBook);
                }
                //copy
                if (ke.isMetaDown() && ke.getCode().getName().equals("C")) {
                     System.out.println("CMD-C pressed for copy book");
                     Book myBook= MainStage.this.getActiveBook();
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
                        //adjacent to clipboard (is this always active book? if so use adjacent function)
                        double xp=MainStage.this.clipboardBook.getX();
                        MainStage.this.clipboardBook.setX(xp+cellgap_x); //offset
                        System.out.println(MainStage.this.clipboardBook+", check: "+myBook);
                        MainStage.this.AddNewBookToStage(MainStage.this.clipboardBook);
                        //System.exit(0);
                     }  
                }
                //nudge left
                if (ke.isMetaDown() && ke.getCode().getName().equals("E")) {
                     System.out.println("CMD-E pressed for nudge left");
                     
                        Book myBook= MainStage.this.getActiveBook();
                        Integer row=myBook.getRow();
                        Integer col=myBook.getCol();
                        MainStage.this.nudgeCellLeftInRow(row,col);
                        //System.exit(0);
                     
                }
                //nudge right
                if (ke.isMetaDown() && ke.getCode().getName().equals("R")) {
                     System.out.println("CMD-R pressed for nudge right");
                     //cf try
                        Book myBook= MainStage.this.getActiveBook();
                        Integer row=myBook.getRow();
                        Integer col=myBook.getCol();
                        MainStage.this.nudgeCellRightInRow(row,col);
                        //System.exit(0);
                }
                //change focus to right
                if (ke.getCode()==KeyCode.RIGHT) {
                  ke.consume();
                  System.out.println("Right key pressed for move right");
                  Book myBook= MainStage.this.getActiveBook();
                  MainStage.this.moveActiveRight(myBook);
               }
               //change focus to left
               if (ke.getCode()==KeyCode.LEFT) {
                  ke.consume();
                  System.out.println("Left key pressed for move left");
                  Book myBook= MainStage.this.getActiveBook();
                  MainStage.this.moveActiveLeft(myBook);
               }
               //change focus up
               if (ke.getCode()==KeyCode.UP) {
                  ke.consume();
                  System.out.println("Up key pressed");
                  Book myBook= MainStage.this.getActiveBook();
                  MainStage.this.moveActiveUp(myBook);
               }
               //change focus down
               if (ke.getCode()==KeyCode.DOWN) {
                  ke.consume();
                  System.out.println("Down key pressed");
                  Book myBook= MainStage.this.getActiveBook();
                  MainStage.this.moveActiveDown(myBook);
               }

             }
        });
        
        return workspaceScene;
    }

//set active sprite.  if problem with tracker, ignore.
public void setActiveBook(Book b) {

   singleSelection(b);
  /*
    try {
        if (this.focusBook!=null) {
            Book hadFocus = this.focusBook;
            hadFocus.endAlert();
        }
    }
    catch (Exception e) {
         System.out.println("Exception in setActiveBook");
         e.printStackTrace(new java.io.PrintStream(System.out));
         System.exit(0);
    }
    this.focusBook=b;
    this.focusBook.doAlert();
    */
}

//set active sprite.  if problem with tracker, ignore.
public Book getActiveBook() {
    if (this.focusBook==null) {
        System.out.println("No book in setActiveBook method");
        return null;//just creates one
    }
    return this.focusBook;
}

//method to put new book (that doesn't exist in Project) on stage.  
//cf if you have an existing Book object. addBookToStage
//This should also (in due course) take layer into account? i.e. Book's layer and Z properties.

public void addNewBookToView () {
    Book b = new Book(PressBox,DragBox,"untitled","","");
    b = getNewBookPositionAdjacent(b);
    myProject.addBookToProject(b); //data model
    AddNewBookToStage(b); //view.  differs from Main  
}

public void removeBookFromStage(Book thisBook) {
    //TO DO: remove Node (data) ? is it cleaned up by GUI object removal?
    this.bookgroupNode.getChildren().remove(thisBook); //view/GUI
    myProject.removeBook(thisBook);
    getStage().show(); //refresh GUI
    
}

public Book getNewBookPositionAdjacent(Book b){
    
    b.setLayer(this.currentLayer.getLayerNumber()); //give book a layer reference
    //
    Book lastBook=getActiveBook();
    if (lastBook!=null) {
        int row = lastBook.getRow();
        int col = lastBook.getCol();
        b.setCol(col+1);
        b.setRow(row);
        setXYfromRowCol(b);
    }
    if (b==null) {
        System.out.println("Book null in addnodetoview");
        System.exit(0);
    }
    return b;
}

public void setBooksOnShelf(ArrayList<Book> inputObject) {
    myProject.setBooksOnShelf(inputObject);
}

public void insertRow(Integer firstrow){
    ArrayList<Book> bookList = myProject.getBooksOnShelf();
    Iterator<Book> myIterator=bookList.iterator();
    while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        if (checkRow>=firstrow) {
            checkRow=checkRow+1;
            item.setRow(checkRow);
            double newY=convertRowtoY(checkRow);
            item.setY(newY);
            //System.out.println("Set a row for a Book to +1");
        }
    }
}

//nudge cell right
public void nudgeCellRightInRow(Integer firstrow, Integer firstcol){
    ArrayList<Book> bookList = myProject.getBooksOnShelf();
    Iterator<Book> myIterator=bookList.iterator();
    while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        Integer checkCol=item.getCol();
        if (checkRow==firstrow && checkCol>=firstcol) {
            checkCol=checkCol+1;
            item.setCol(checkCol);
            double newX=convertColtoX(checkCol);
            item.setX(newX);
            double newY=convertRowtoY(checkRow);
            item.setY(newY);
            //System.out.println("Set a col for a Book to +1");
        }
    }
}

//nudgeCellLeftInRow

public void nudgeCellLeftInRow(Integer firstrow, Integer firstcol){
    ArrayList<Book> bookList = myProject.getBooksOnShelf();
    Iterator<Book> myIterator=bookList.iterator();
    while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        Integer checkCol=item.getCol();
        if (checkRow==firstrow && checkCol>=firstcol && firstcol>0) {
            checkCol=checkCol-1;
            item.setCol(checkCol);
            double newX=convertColtoX(checkCol);
            item.setX(newX);
            double newY=convertRowtoY(checkRow);
            item.setY(newY);
            //System.out.println("Set a col for a Book to -1");
        }
    }
}

//moveactiveright
public void moveActiveRight(Book myBook) {
    System.out.println("move right...");
    Integer row=myBook.getRow();
    Integer col=myBook.getCol();
    Boolean moved = false;
    ArrayList<Book> sorted= myProject.listBooksShelfOrder();
    Iterator <Book> myIterator = sorted.iterator();
    while(myIterator.hasNext()) {
      Book item = myIterator.next();
      System.out.println(item+" "+item.getLabel());
      if (item.getRow()==row && item.getCol()==col && moved==false) {
          if (myIterator.hasNext()) {
              MainStage.this.setActiveBook(myIterator.next());
              moved=true;
          }
      }
   }
}

//moveactiveright
public void moveActiveLeft(Book myBook) {
    //System.out.println("move left...");
    Integer row=myBook.getRow();
    Integer col=myBook.getCol();
    Boolean moved = false;
    ArrayList<Book> sorted= myProject.listBooksShelfOrder();
    Integer mySize=sorted.size();
    ListIterator <Book> myIterator = sorted.listIterator(mySize); //must pass argument at time of creation to set at right end
    Boolean start=false;
    while(myIterator.hasPrevious()) {
      Book item = myIterator.previous();
      if (item.getRow()==row && item.getCol()==col && moved==false) {
        if (myIterator.hasPrevious()) {
          MainStage.this.setActiveBook(myIterator.previous());
          moved=true;
        }  
      }
   }
}

//At present this function moves to existing 'boxes' not grid position
public void moveActiveUp(Book myBook) {
    //System.out.println("move left...");
    Integer row=myBook.getRow();
    Integer col=myBook.getCol();
    Boolean moved = false;
    ArrayList<Book> sorted= myProject.listBooksShelfOrder();
    Integer mySize=sorted.size();
    ListIterator <Book> myIterator = sorted.listIterator(mySize); //must pass argument at time of creation to set at right end
    Boolean start=false;
    while(myIterator.hasPrevious()) {
      Book item = myIterator.previous();
      if (item.getRow()<row && item.getCol()==col && moved==false) {
          MainStage.this.setActiveBook(item);
          moved=true;
        }  
   }
}

//At present this function moves to existing 'boxes' not grid position
public void moveActiveDown(Book myBook) {
    //System.out.println("move left...");
    Integer row=myBook.getRow();
    Integer col=myBook.getCol();
    Boolean moved = false;
    ArrayList<Book> sorted= myProject.listBooksShelfOrder();
    Integer mySize=sorted.size();
    Iterator <Book> myIterator = sorted.iterator(); //must pass argument at time of creation to set at right end
    Boolean start=false;
    while(myIterator.hasNext()) {
      Book item = myIterator.next();
      if (item.getRow()>row && item.getCol()==col && moved==false) {
          MainStage.this.setActiveBook(item);
          moved=true;
        }  
   }
}

//this acts as a layout API - converts raw row, col to pixel coords
public double convertRowtoY(Integer myRow){
     double newY=(myRow*this.cellgap_y)+this.cellrowoffset_y+this.boxtopmargin;
     return newY;
}

public double convertColtoX(Integer myCol) {
    double newX=(myCol*this.cellgap_x);
    return newX;
}

/*
Function to update Book X,Y position based on Row,Col coordinates

*/

public void setXYfromRowCol(Book myBook) {
    int minimumY=40;
    double newY=this.cellgap_y*myBook.getRow()+minimumY;
    double newX=this.cellgap_x*myBook.getCol();
    myBook.setXY(newX,newY);
}

/*
Function to update Book X,Y position based on Row,Col coordinates
*/

public void setXYfromNewRowCol(Book myBook, Integer row, Integer col) {
    int minimumY=40;
    double newY=this.cellgap_y*row+minimumY;
    double newX=this.cellgap_x*col;
    myBook.setXY(newX,newY);
}

/*
public ArrayList<Book> getBooksOnShelf() {
    return this.booksOnShelf;
}
*/


//TO DO: simplify so that Row,Col is used if not being dragged.

public void positionBookOnStage(Book myBook) {
        
    if (myBook.getY()!=0) {
        double ypos=snapYtoShelf(myBook,myBook.getY());
        double xpos=snapXtoShelf(myBook,myBook.getX()); //
        myBook.setY(ypos);
        myBook.setX(xpos);
    }
    else {
        //myBook.setX(this.spriteX); //xpos advances according to advance method
        //TO DO?  set column for this book?
        double xpos=snapXtoShelf(myBook,this.spriteX); //
        double ypos=snapYtoShelf(myBook,this.spriteY); //does setRow
        this.spriteY=(int)ypos;
        this.spriteX=(int)xpos;
        myBook.setX(xpos);
        myBook.setY(ypos);
        //myBook.setTranslateX(this.spriteX);
        //myBook.setTranslateY(this.spriteY);
    }
    myBook.setTranslateY(myBook.getY());
    myBook.setTranslateX(myBook.getX());
}

public void resetBookOrigin() {
    this.spriteY=firstcell_y;
    this.spriteX=firstcell_x;
}

//new 'shelf'??
private void newBookColumn() {
    this.spriteY=this.spriteY+this.cellgap_y;
    this.spriteX=firstcell_x;
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
//TO DO: use Row, Col and convert to abs X,Y later
private void advanceBookPositionHor() {
        if (this.spriteX>((this.cellcols+1)*this.cellgap_x)) {
                this.spriteY=spriteY+this.cellgap_y; //drop down
                this.spriteX=firstcell_x;
            }
            else {
                this.spriteX = this.spriteX+this.cellgap_x; //uniform size for now
            }
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