//(c) Craig Duncan 2017-2020
//www.craigduncan.com.au

//import utilities needed for Arrays lists etc
import java.util.*; //collections too
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
//File i/o
import java.io.*;
import java.io.File;
//Desktop etc and file chooser
import java.awt.Desktop;

/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
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

String filename = "";
String shortfilename=""; //current filename for saving this stage's contents

String category="";
//Displayed Book (i.e. Node).  Will be updated through GUI.
//Book displayNode = new Book();
int doccount=0; //document counter for this stage

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
Integer cellrows=20;
Integer cellgap_x=80; //cellwidth x dimension
Integer cellgap_y=100; //cell width y dimension
Integer firstcell_x=this.cellgap_x;
Integer firstcell_y=0;
Integer gridwidth=1000;
Integer gridlineheight=1;
Integer filenameoffset_y=20;
Integer filenameoffset_x=250;
Integer cellrowoffset_y=30;
Integer boxtopmargin=10;
Color shelfborder=Color.BLACK;
Color shelffill=Color.DARKSLATEGREY;

/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
BookMetaStage should store GUI objects in one way, data in another?  separation of concerns
Some kind of content manager for each stage?
Consider if subclasses of BookMetaStage could deal with flavours of BookMetaStage (e.g. position?
*/
ArrayList<Book> booksOnShelf = new ArrayList<Book>(); //generic store of contents of boxes
double orgSceneX;
double orgSceneY;

double orgTranslateX;
double orgTranslateY;
Main parentStage;
FileChooser currentFileChooser = new FileChooser();
FileChooser.ExtensionFilter myExtFilter = new FileChooser.ExtensionFilter("Shelf markdown","*.md");

//Track current stage that is open.  Class variables
static BookMetaStage currentFocus; //any BookMetaStage can set this to itself
//static Book currentTarget; //any Box(no?) or BookMetaStage can set this to its display node

//constructor
public MainStage() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//workspace constructor.  Filename details will be inherited from loaded node.
//Passes MenuBar from main application for now
//Passes general eventhandlers from Main (at present, also uses these for the boxes)
public MainStage(String title, MenuBar myMenu, Main parentStage) {
    //Book baseNode, 
    //category
    //NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
    //view
    this.parentStage=parentStage;
    setTitle(title);
    setMenuBar(myMenu);
    //TO DO: setLocalSpriteSelect(processLocalBoxClick);
    //set event handlers as local instance variables.  These are used at time of Book creation
    //They are set here so that the Bookes can access BookMetaStage level data
    //See the 'addNodeToView' function that creates a new Book here.
    setPressBox(processLocalBoxClick); //stores this in a variable to pass to Book.
    //setPressBox(PressBox);
    setDragBox(DragBox); //stores this to pass to Book 
    setKeyPressHandler(SaveKeyEventHandler); //TO do - set a variable to pass to sprites=
    
    //we need to set sprite group etc

    newWorkstageFromGroup();
    System.out.println ("The initial bookgroupNode...");
    System.out.println (this.bookgroupNode);
    System.out.println("Reference of this stage object MainStage");
    System.out.println(MainStage.this);
    resetBookOrigin();
    //fileops
    this.currentFileChooser.getExtensionFilters().add(myExtFilter);
}


public void processMarkdown(File file) {
  //String filename=System.out.print(file.toString()); // this is full path
    String thefilename=file.getName();
    String last=thefilename.substring(thefilename.length() - 3);
    if (last.equals(".md")==true) {
      TemplateUtil myUtil = new TemplateUtil();
      String contents = myUtil.getFileText(file);
      this.shelfFileName.setText(thefilename); //update name on shelf view
      //Recents myR = new Recents();
      //myR.updateRecents(file.getName());
      Parser myParser=new Parser();
      // Split the MD file.  blocklist is list of 
      ArrayList<String> blocklist= myParser.splitMDfile(contents); //should return file split into blocks per #
      int length = blocklist.size();  // number of blocks
      System.out.println(length);
      if (length>0) {
        Iterator<String> iter = blocklist.iterator(); 
          while (iter.hasNext()) {
              Book newBook=myParser.parseMDfile(MainStage.this,PressBox,DragBox,iter.next());
              if (newBook!=null) {
                System.out.println("Starting iteration of block lines in MD");
                AddNewBookFromParser(newBook);
              }
         } //end while
      } //end if
      
      System.out.println("Finished parse in 'open button' makeStage");
      //LoadSave.this.ListOfFiles();// print out current directory
    }
}

public void processMarkdownAsRow(File file) {
  //String filename=System.out.print(file.toString()); // this is full path
    String thefilename=file.getName();
    String last=thefilename.substring(thefilename.length() - 3);
    if (last.equals(".md")==true) {
      TemplateUtil myUtil = new TemplateUtil();
      String contents = myUtil.getFileText(file);
      this.shelfFileName.setText(thefilename); //update name on shelf view
      //Recents myR = new Recents();
      //myR.updateRecents(file.getName());
      Parser myParser=new Parser();
      // Split the MD file.  blocklist is list of 
      ArrayList<String> blocklist= myParser.splitMDfile(contents); //should return file split into blocks per #
      int length = blocklist.size();  // number of blocks
      System.out.println(length);
      if (length>0) {
        Iterator<String> iter = blocklist.iterator(); 
          while (iter.hasNext()) {
              Book newBook=myParser.parseMDfileAsRow(MainStage.this,PressBox,DragBox,iter.next());
              if (newBook!=null) {
                System.out.println("Starting iteration of block lines in MD");
                AddNewBookFromParser(newBook);
              }
         } //end while
      } //end if
      
      System.out.println("Finished parse in 'open button' makeStage");
      //LoadSave.this.ListOfFiles();// print out current directory
    }
}

public Integer getRowofActiveBook() {
    Integer row=0;
    Book thisBook = getActiveBook();
    if (thisBook!=null) {
        row=thisBook.getRow();
    }
    return row;
}

//use row for positions
public File openMarkdown() {
        //System.exit(0);
        // final FileChooser fileChooser = new FileChooser();
        File file = new File (this.getFilename());
        if (file==null) {
            file = new File ("untitled.md");
        } 
        Stage myStage = new Stage();
        myStage.setTitle("Open File");
        this.currentFileChooser.setTitle("Open A Shelf File");
        //this.currentFileChooser.setSelectedExtensionFilter(this.myExtFilter);   
        this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
        File tryfile = currentFileChooser.showOpenDialog(myStage);
        if (tryfile != null) {
          file = tryfile;
          processMarkdown(file);
          setFilename(file.getPath());
          setShortFilename(file.getName());
        } 
        else {
            //DO SOMETHING
        }
    return file;
}

//use row for positions
public File openMarkdownAsRow(Integer row) {
        this.spriteX=(int)convertColtoX(0);
        this.spriteY=(int)convertRowtoY(row);
        System.out.println("Row check:"+row+", spriteY:"+this.spriteY);
        //System.exit(0);
        // final FileChooser fileChooser = new FileChooser();
        File file = new File (this.getFilename());
        if (file==null) {
            file = new File ("untitled.md");
        } 
        Stage myStage = new Stage();
        myStage.setTitle("Open File");
        this.currentFileChooser.setTitle("Open A Shelf File");
        //this.currentFileChooser.setSelectedExtensionFilter(this.myExtFilter);   
        this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
        File tryfile = currentFileChooser.showOpenDialog(myStage);
        if (tryfile != null) {
          file = tryfile;
          processMarkdownAsRow(file);
          setFilename(file.getPath());
          setShortFilename(file.getName());
        } 
        else {
            //DO SOMETHING
        }
    return file;
}

//for save as
public void saveAs() {
    Stage myStage = new Stage();
    myStage.setTitle("Save As ...");
    String myFilename=getShortFilename(); //SaveAs will use current path so only name is needed
    this.currentFileChooser.setTitle("Save Shelf File As");
    this.currentFileChooser.setInitialFileName(myFilename);  
    this.currentFileChooser.setSelectedExtensionFilter(myExtFilter); 
    //System.exit(0);
    File file = currentFileChooser.showSaveDialog(myStage);
    if (file != null) {
        setFilename(file.getPath());
        setShortFilename(file.getName());
        writeFileOut();
        } 
        else {
            //DO SOMETHING
        }
    }

//for direct save
public void writeFileOut() {
    //using existing filename
    String filepath=this.getFilename();
    System.out.println("Saving: "+filepath);
    ArrayList<Book> mySaveBooks = listBooksShelfOrder();//getBooksOnShelf();
    Iterator<Book> myIterator = mySaveBooks.iterator();
    String myOutput="";
         while (myIterator.hasNext()) {
            Book myNode=myIterator.next();
            //System.out.println(myNode.toString());
            String myString=convertBookMetaToString(myNode);
            myOutput=myOutput+myString;
             //option: prepare string here, then write once.
        }
        basicFileWriter(myOutput,filepath);
        //System.out.println(myOutput);
        //System.exit(0);
}

//sort books by shelf order
public ArrayList<Book> listBooksShelfOrder() {
    ArrayList<Book> myBooksonShelves = getBooksOnShelf();
    ArrayList<Integer> scoreIndexes = new ArrayList();
    Integer booknum=myBooksonShelves.size();
    for (int x=0;x<booknum;x++) {
        Book item = myBooksonShelves.get(x);
        Integer score=item.getRowScore();
        scoreIndexes.add(score);
    }
    Collections.sort(scoreIndexes); //performs a sort
    //System.out.println(scoreIndexes);
    //System.exit(0);
    System.out.println("Books num: "+booknum);
    ArrayList<Book> sortedBooks = new ArrayList<Book>();
    Iterator<Integer> myIterator = scoreIndexes.iterator();
    //System.out.println("Book score printout:\n");
         while (myIterator.hasNext()) {
            Integer targetscore = myIterator.next();
            System.out.println("target:"+targetscore);
            Iterator<Book> bookIterator = myBooksonShelves.iterator();
            while (bookIterator.hasNext()) {
                Book item = bookIterator.next();
                Integer test = item.getRowScore();
                System.out.println("test score:"+test);
                if (test.equals(targetscore)) {
                    System.out.println("matched");
                    sortedBooks.add(item);
                }
            }
        }
        /*Iterator<Book> bookIterator=sortedBooks.iterator();
        while (bookIterator.hasNext()) {
            Book item = bookIterator.next();
            String label = item.getLabel();
            System.out.println(label);
        }
        System.exit(0);
        */
        return sortedBooks;
}

//Convert this book meta into a String of markdown.  Only write links if data is there.
public String convertBookMetaToString(Book myBook) {
    String myOutput="# "+trim(myBook.getLabel()); //check on EOL
    String markdown=myBook.getMD();
    String filteredMD=trim(markdown); //trims but inserts EOL
    myOutput=myOutput+filteredMD; //check on EOL
    if (myBook.getdocfilepath().length()>5) {
        String tmp = myBook.getdocfilepath();
        Integer len = tmp.length();
        myOutput=myOutput+"[filepath]("+tmp+")"+System.getProperty("line.separator");
        /*if (tmp.substring(len-1,len)!="\n") {
             myOutput=myOutput+"\n";
        }; 
        */
    }
    if (myBook.getRow()>=0 && myBook.getCol()>=0) {
        myOutput=myOutput+"[r,c]("+myBook.getRow()+","+myBook.getCol()+")"+System.getProperty("line.separator");
    }
    if (myBook.geturlpath().length()>6) {
        myOutput=myOutput+"[url]("+myBook.geturlpath()+")"+System.getProperty("line.separator");
    }
    if (myBook.getX()>0 || myBook.getY()>0) {
        myOutput=myOutput+"[x,y]("+myBook.getX()+","+myBook.getY()+")"+System.getProperty("line.separator");
    }
    if (myBook.getthisNotes().length()>0) {
        String notes = myBook.getthisNotes();
        String filteredNote=trim(notes);
        myOutput=myOutput+"```"+System.getProperty("line.separator")+filteredNote+"```"+System.getProperty("line.separator");
    }
    return myOutput;
}

private String trim(String input){
    Scanner scanner1 = new Scanner(input).useDelimiter(System.getProperty("line.separator"));
    ArrayList<String> myList = new ArrayList<String>();
    while (scanner1.hasNext()) {
        String item=scanner1.next();
        System.out.println(item+","+item.length());
        myList.add(item);
    }
    Integer stop=0;
    Integer trimcount=0;
    Integer listlength=myList.size();
    for (int i=listlength-1;i>0;i=i-1) {
        int size = myList.get(i).length();
        if (stop==0 && size==0) {
            trimcount++;
        }
        else {
            stop=1;
        }
    }
    //System.out.println(trimcount);
    //System.out.println(listlength-trimcount-1+","+myList.get(listlength-trimcount-1));
    //System.out.println(listlength-trimcount+","+myList.get(listlength-trimcount));
    StringBuffer newString = new StringBuffer();
    int end = listlength-(trimcount-1);
    if (end<0 || end>listlength-1) {
        end=0; 
        System.out.println("listlength: "+listlength+" trim: "+trimcount);
        //System.exit(0);
        end=listlength;
    }
    for (int i =0;i<end;i++) {
        newString=newString.append(myList.get(i));
        newString=newString.append(System.getProperty("line.separator"));
    }
    //System.out.println(newString);
    //System.exit(0);
    String output = newString.toString();
    return output;
}

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

//JAVAFX SCENE GRAPH GUI INFO (THIS IS NOT THE DATA NODE!)
public void setSceneRoot(Node myNode) {
    this.rootNode = myNode;
}

public Node getSceneRoot() {
    return this.rootNode;
}


//FILE I/O DATA
public String getFilename() {
    return this.filename;
}

public void setFilename(String myFile) {
    this.filename = myFile;
    this.shelfFileName.setText(this.filename);
}

public void setShortFilename(String myFile) {
    this.shortfilename = myFile;
    //this.shelfFileName.setText(this.filename);
}

public String getShortFilename() {
    return this.shortfilename;
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
     getStage().setScene(myScene); //JavaFX in the GUI
     this.localScene = myScene; //local copy/reference
}

//SIMPLE STAGE GETTERS AND SETTERS FOR CUSTOM GUI.  WRAPPER FOR JAVAFX SETTERS

public void setStageName(String myName) {
    this.stageName = myName;
    this.localStage.setTitle(myName);
}

public String getStageName() {
    return this.stageName;
}

//probably redundant - keep name or title
public void setTitle(String myTitle) {
    this.stageTitle = myTitle;
}

public String getTitle() {
    return this.stageTitle;
}

private void refreshTitle() {
    this.localStage.setTitle(getTitle());
}

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
    this.bookgroupNode.getChildren().clear();
    this.booksOnShelf.clear(); 
    this.resetBookOrigin();
    this.parentStage.resetFileNames(); //to update general file name etc
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
              
            if (currentBook!=null) {
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
            MainStage.this.setActiveBook(currentBook);
            //moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
            int clickcount = t.getClickCount();

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            orgTranslateX = currentBook.getTranslateX();
            orgTranslateY = currentBook.getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);

            switch(clickcount) {
            //single click
            case 1:
                //moveAlertFromBoxtoBox(getcurrentBook(),currentBook);
                System.out.println("One click");
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


//TO DO: check Book isn't the basis for inspector stage before opening new stage.
private void OpenRedBookNow(Book currentBook) {
     //Book currentBook= getActiveBook(); //currentBook.getBoxNode();
     //bookMetaInspectorStage.closeThisStage(); //close open stage.  No save checks? //TO DO: close all child stages
     Stage parent = this.localStage; // the Stage associated with this object, not the MainStage object itself.
     bookMetaInspectorStage = new BookMetaStage(parent, currentBook, PressBox, DragBox, SaveKeyEventHandler); 
     setMetaStageParams(bookMetaInspectorStage);

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
private void newWorkstageFromGroup() {
    Group myGroup = makeWorkspaceTree(); //myGroup_root
    Scene myScene = makeWorkspaceScene(myGroup); //new scene with myGroup_root = this.localscene
    Stage myStage = new Stage();
    setStage(myStage);
    updateScene(myScene);
    setStagePosition(0,0);
    stageBack();
    showStage();
}

/* 

Java FX View setup:
Create root node and branches that is ready for placing in a Scene.

Sets up workspace stage with 2 subgroups for vertical separation:
(a) menu bar
(b) sprite display area, which is inside a border pane and group for layout reasons.

This method does not update content of the Sprite-display GUI node.

*/
//myGroup_root--->
//myBP(top)-->menuBarGroup-->myMenu
//myBP(center)-->workspacePane-->displayAreaGroup (for BookIcons etc to be added)
private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        BorderPane myBP = new BorderPane(); //holds the menubar at top, workspace in center of BorderPane
        Group menubarGroup = new Group(); //subgroup
        MenuBar myMenu = getMenuBar();
        menubarGroup.getChildren().addAll(myMenu);
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

       // --- IF USING A SCROLLPANE HERE, ADD THAT TO THE BP
        ScrollPane myScrollPane = new ScrollPane(workspacePane); //content is the workspacePane
        myScrollPane.setPrefViewportWidth(this.scrollSceneWidth);
        myScrollPane.setPrefViewportHeight(this.scrollSceneHeight);
        myScrollPane.pannableProperty().set(false);  //to prevent panning by mouse
        myScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS")); //AS_NEEDED or ALWAYS
        myScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
        //myScrollPane.setStyle(scrollpaneStyle);
        myBP.setMargin(myScrollPane, new Insets(0,0,0,0));
        myBP.setCenter(myScrollPane);

        //--- IF USING A PANE

        //myScrollPane.setVmax(500);
        workspacePane.getChildren().addAll(displayAreaGroup);
         //make it big enough for number of rows/cols
        //ScrollPane myScrollPane= new ScrollPane();
        String scrollpaneStyle="-fx-background-color:transparent; ";
        
        setGridGroup(displayAreaGroup); //store as instance variable for referencing elsewhere
        //myScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
       // myScrollPane.setVmax(500);
        //Pane workspacePane = new Pane(); //to hold a group, holding a bookgroupNode
        //This is empty initially...

        //setSpritePane(workspacePane); //store for later use
        
        myBP.setTop(menubarGroup);
        //myBP.setMargin(workspacePane, new Insets(50,50,50,50)); //i.e. Y=-50='translateX=0'
        myBP.setMargin(myScrollPane, new Insets(0,0,0,0));
        myBP.setCenter(myScrollPane);
        //myBP.setCenter(myScrollPane);
        //workspacePane.setPadding(new Insets(150,150,150,150));
        //
        //Make horizontal lines for grid, and add to FX root node for this Stage
        this.shelfFileName.setY(this.filenameoffset_y);
        this.shelfFileName.setX(this.filenameoffset_x); 
        myGroup_root.getChildren().add(this.shelfFileName);
       
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
        Pane shelfFilePane = new Pane();
        shelfFilePane.getChildren().add(this.shelfFileTextArea);
                
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
       
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), Color.WHITE);
                
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
                     MainStage.this.openMarkdown();
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
                        double xp=MainStage.this.clipboardBook.getX();
                        MainStage.this.clipboardBook.setX(xp+cellgap_x); //offset
                        System.out.println(MainStage.this.clipboardBook+", check: "+myBook);
                        MainStage.this.AddNewBookFromParser(MainStage.this.clipboardBook);
                        //System.exit(0);
                     }  
                }
            }
        });
        
        return workspaceScene;
    }

//set active sprite.  if problem with tracker, ignore.
public void setActiveBook(Book b) {
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
}

//set active sprite.  if problem with tracker, ignore.
public Book getActiveBook() {
    if (this.focusBook==null) {
        System.out.println("No book in setActiveBook method");
        return null;//just creates one
    }
    return this.focusBook;
}


//Called by LoadSave and iterates through the nodes in the parsed MD file.
public void AddNewBookFromParser(Book newBook) throws NullPointerException {
    System.out.println("bookgroupNode in AddNewBookFromParser");
    System.out.println(this.bookgroupNode);
    try {
        addBookToStage(newBook);
        System.out.println(newBook.toString());
        //System.exit(0);
    }
    catch (NullPointerException e) {
        System.out.println("NullPointerException in AddNewBookFromParser");
        System.exit(0);
    } 
}

//method to put new book on stage.  cf if you have an existing Book object. addBookToStage

public void addNewBookToView () {
    //Book b = makeBoxWithNode(myNode); //relies on Main, event handlers x
    
    Book b = new Book(PressBox,DragBox,"untitled","","");
    //
    Book lastBook=getActiveBook();
    if (lastBook!=null) {
        double xp=lastBook.getX();
        double yp=lastBook.getY();
        b.setX(xp+cellgap_x); //offset
        b.setY(yp); //same
    }
    //adjusthorizontal
    double xpos=b.getX();
    double ypos=b.getY();
    b.setTranslateX(xpos);
    b.setTranslateY(ypos); 
    setActiveBook(b);
    addBookToStage(b); //differs from Main 
    if (b==null) {
        System.out.println("Book null in addnodetoview");
        System.exit(0);
    }
   
    System.out.println("bookgroupNode in addnodetoview");
    System.out.println(this.bookgroupNode);  
}

/*
Internal method to add sprite to the Group/Pane of this Node Viewer 
This is to add an existing GUI 'box/node' to the Child Node section of this Viewer.
i.e. this adds a specific object, rather than updating the view from whole underlying data set.
*/

private void addBookToStage(Book myBook) {

    if (myBook==null) {
        System.out.println("addBookToStage.  No Book to add");
        System.exit(0);
    }
    Book newBook = myBook;

    //if trying to paste into scene twice...FX error  OK but need to deal with it.
    if (this.booksOnShelf.contains(myBook)) {
      newBook = myBook.cloneBook(); //make a new object to add to avoid object duplication.
    } 
    
    this.bookgroupNode.getChildren().add(newBook);
    this.booksOnShelf.add(newBook);  //add to metadata collection TO DO: cater for deletions.
    try { 
        setActiveBook(newBook); 
        }
    catch (NullPointerException e ){
        System.out.println("NullPointer Stage...1:");
        System.exit(0);
    }
    advanceBookPositionHor();
    positionBookOnStage(newBook); //snap to shelf after horizontal move    
}

public void removeBookFromStage(Book thisBook) {
    //TO DO: remove Node (data) ? is it cleaned up by GUI object removal?
    this.bookgroupNode.getChildren().remove(thisBook); //view/GUI
    this.booksOnShelf.remove(thisBook);
    //to do: remove Book from ArrayList too.
    getStage().show(); //refresh GUI
    
}

public void setBooksOnShelf(ArrayList<Book> inputObject) {
    this.booksOnShelf = inputObject;
}

public void insertRow(Integer firstrow){
    ArrayList<Book> bookList =getBooksOnShelf();
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
public void insertCellInRow(Integer firstrow, Integer firstcol){
    ArrayList<Book> bookList =getBooksOnShelf();
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
            System.out.println("Set a col for a Book to +1");
        }
    }
}

//nudgeCellLeftInRow

public void nudgeCellLeftInRow(Integer firstrow, Integer firstcol){
    ArrayList<Book> bookList =getBooksOnShelf();
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
            System.out.println("Set a col for a Book to -1");
        }
    }
}

public double convertRowtoY(Integer myRow){
     double newY=(myRow*this.cellgap_y)+this.cellrowoffset_y+this.boxtopmargin;
     return newY;
}

public double convertColtoX(Integer myCol) {
    double newX=(myCol*this.cellgap_x);
    return newX;
}

public ArrayList<Book> getBooksOnShelf() {
    return this.booksOnShelf;
}

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
//Layout horizontall on one shelf.
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

}