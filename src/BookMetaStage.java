//(c) Craig Duncan 2017-2020

//import utilities needed for Arrays lists etc
import java.util.*;
//file i/o
import java.io.*;
import java.io.File;
//net function for browser links
import java.net.URI;
import java.net.URISyntaxException;

//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.FileChooser; //for choosing files
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
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

public class BookMetaStage {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();
ArrayList<Stage> myStageList = new ArrayList<Stage>();
int spriteX = 0;
int spriteY = 0;
String stageName = "";
String stageTitle = "";

ClauseContainer reference_ParentNode = new ClauseContainer();
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;
SpriteBox focusbox; //for holding active sprite in this scene.  Pass to app.
SpriteTracker myTrk;
SpriteBox parentBox;//to hold the calling box for this viewer.  
//Do not create new object here or circular constructors! Do in constructor

String filename = ""; //current filename for saving this stage's contents
//STAGE IDS
int location = 0;
String category="";
//Displayed ClauseContainer (i.e. Node).  Will be updated through GUI.
ClauseContainer displayNode = new ClauseContainer();
int doccount=0; //document counter for this stage

//NODE'S TEXT CONTENT
//For storing main text output area for this Stage (if any)
//As of 26.4.2018: make this the default area to hold the node's own text (for stages that display a frame that is also an open node).  Always editable.

//This TextArea is the GUI display object for the nodes' docnotes String.  Edit button will update the node's (ClauseContainer) actual data
TextArea filepathTextArea = new TextArea();
TextArea urlTextArea = new TextArea();
TextArea mdTextArea = new TextArea();
TextArea bookLabelTextArea = new TextArea();
TextArea notesTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
Text filepathText;
Text bookLabelText;
Text multiLineNotesText;
Text visibleBlockText;
Text mdHeadingText;
Text urlText;
//Store the common event handlers here for use
EventHandler<MouseEvent> PressBox;
EventHandler<MouseEvent> DragBox;
EventHandler<KeyEvent> KeyEventHandler;
//MenuBar
MenuBar localmenubar;
//html editor
 final HTMLEditor htmlEditor = new HTMLEditor();
//visibility checkbox
CheckBox visibleCheck = new CheckBox("Visible");
MainStage mainStage;

/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
StageManager should store GUI objects in one way, data in another?  separation of concerns
Some kind of content manager for each stage?
Consider if subclasses of StageManager could deal with flavours of StageManager (e.g. position?
*/
ArrayList<Object> BoxContentsArray = new ArrayList<Object>(); //generic store of contents of boxes


//Track current stage that is open.  Class variables
static StageManager currentFocus; //any StageManager can set this to itself
static ClauseContainer currentTarget; //any Box(no?) or StageManager can set this to its display node

//constructor
public BookMetaStage() {
    this.outputTextArea.setWrapText(true);
    this.notesTextArea.setWrapText(true);  //default
}

//temporary constructor for old windows (toolbars, output etc)
public BookMetaStage(MainStage parent, String myTitle) {
    Stage myStage = new Stage();
    setStage(myStage);
    setTitle(myTitle);
    setDragBox(DragBox);
    setJavaFXStageParent(parent);
    this.mainStage=parent;
    this.outputTextArea.setWrapText(true);
    this.notesTextArea.setWrapText(true);  //default
    setToolBarWindowPosition();
    //cycleUserView();
}

//standard open node viewer constructor.  Used by 'OpenRedNodeNow' method in Main
public BookMetaStage(MainStage parent, ClauseContainer myNode, EventHandler PressBox, EventHandler DragBox, SpriteBox currentSprite) {
    //view
    setJavaFXStageParent(parent);
    this.mainStage=parent;
    setParentBox(currentSprite);
    //store event handlers as local instance variables
    setPressBox(PressBox);
    setDragBox(DragBox);
    setKeyPress(NodeKeyHandler); //this can be different for workspace
    //position
    setEditWindowPosition();
    //data: new 'parent' node based on category alone
    setDisplayNode(myNode);
     //data 
    //
    /*
    this.myTrk = spTrk;
    if (this.myTrk==null) {
        System.out.println("myTrk null in constructor openrednodenow");
        System.exit(0);
    }
    this.myTrk.setCurrentFocus(BookMetaStage.this); //set focus on creation
    //parent.setCurrentFocus(BookMetaStage.this);//this duplicated previous line since class variable?
    //
    */
    updateOpenNodeView(); //updates contents but doesn't show stage unless requested
    showStage(); //to do: put default view in constructor
}

//standard open node viewer constructor using an existing Spritebox with node 
//TO DO: extract node from spritebox first, then use other constructor?
public BookMetaStage(SpriteTracker spTrk, MainStage parent, SpriteBox myBox, EventHandler PressBox, EventHandler DragBox) {
    setJavaFXStageParent(parent);
    this.mainStage=parent;
    setParentBox(myBox); //data 
    //
    //myBox.setChildStage(BookMetaStage.this);
    setPressBox(PressBox);
    setDragBox(DragBox);
    setKeyPress(NodeKeyHandler); //this can be different for workspace
    //
    /*
    this.myTrk = spTrk;
    if (this.myTrk==null) {
        System.out.println("myTrk null in constructor existing spritebox");
        System.exit(0);
    }
    this.myTrk.setCurrentFocus(BookMetaStage.this);  //set focus on creation
    //parent.setCurrentFocus(BookMetaStage.this);//this duplicated previous line since class variable?
    */
    setEditWindowPosition();
    updateOpenNodeView();
    showStage();
}


//GLOBAL view setting.  Make switch.
private void cycleUserView() {
    //User choice of view stored in node
    String myView = getDisplayNode().getUserView();
    if (myView==null) {
        myView="all";
        }
    switch (myView) {
    
        case "all" : 
            getDisplayNode().setUserView("textonly");
            updateOpenNodeView();
            break;
        case "textonly" :
            getDisplayNode().setUserView("inputoutput");
            updateOpenNodeView();
            break;
        case "inputoutput" :
            getDisplayNode().setUserView("nodeboxesonly");
            updateOpenNodeView();
            break;
        case "nodeboxesonly" :
            getDisplayNode().setUserView("all");
            updateOpenNodeView();
            break;
        default:
            getDisplayNode().setUserView("all");
            updateOpenNodeView();
            break;
        }
    }

//any instance can return the global variable with focus stage
public StageManager getCurrentFocus() {
    return this.myTrk.getCurrentFocus();//notice not a 'this' as not an instance
}

//setter: should generally only set it to current instance
public void setCurrentFocus(StageManager mySM) {
    this.myTrk.setCurrentFocus(mySM); ; //notice not a 'this' as not an instance
}

public void setTargetByViewer(StageManager mySM) {
    currentTarget = mySM.getDisplayNode();
}

/* TENTATIVE - NOT USED? */
public void setTargetByBox(StageManager mySM) {
    currentTarget = mySM.getDisplayNode();
}

public ClauseContainer getCurrentTarget() {
    return currentTarget;
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

//JAVAFX SCROLLERS FOR TEXT OUTPUT - DEFAULT
//Method to operate on external object passed to function (does not return)
//to DO - separate JavaFX objects wrapper functions class?

//add scene to stage
public void putTextScrollerOnStage() {
    ScrollPane rootnode_scroll = new ScrollPane();
    configDefaultScroller(rootnode_scroll); //scroller with text
    Scene textOutputScene = makeSceneScrollerAsRoot(rootnode_scroll);
    Stage textOutputStage = new Stage();
    storeSceneAndStage(textOutputScene, textOutputStage);
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
private void setKeyPress(EventHandler<KeyEvent> myKE) {
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, NodeKeyHandler);
}

//Set key handler at level of stage in node editor
private void setKeyPressSprite(EventHandler<KeyEvent> myKE) {
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, KeyEventHandler);
}

EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
    System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

 EventHandler<KeyEvent> NodeKeyHandler = new EventHandler<KeyEvent>() {
 @Override
 public void handle(KeyEvent ke) {
    System.out.println("Key Event on current Stage:"+BookMetaStage.this.toString());
    System.out.println("Key Press (keycode):"+ke.getCode());
    //System.out.println("Key Press (keycode textual):"+ke.getCode().getKeyCode());
    System.out.println("Key Press (keycode name):"+ke.getCode().getName());
    System.out.println("Key Press (as string):"+ke.getCode().toString());
    System.out.println("KeyPress (as source): " + ke.getSource());
    System.out.println("KeyPress (as higher-level event type): " + ke.getEventType());
    System.out.println("KeyPress (unicode): " + ke.getCharacter());
    System.out.println("is Control Down: " + ke.isControlDown());
    System.out.println("is Meta(Command) Down: " + ke.isMetaDown());
    if (ke.isMetaDown() && ke.getCode().getName().equals("Z")) {
         System.out.println("CMD-Z pressed");
         cycleUserView();
    }
 }
};

private void configDefaultScroller(ScrollPane myScroll) {
    myScroll.setFitToHeight(true);
    myScroll.setFitToWidth(true);
    //setup text scroll node
    double width = 600; 
    double height = 500; 
    myScroll.setPrefHeight(height);  
    myScroll.setPrefWidth(width);
    //set to this object's outputtext area
    myScroll.setContent(getOutputTextNode()); 
    setSceneRoot(myScroll);
}

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
    notesTextArea.setText(myText);
}

public String getInputText() {
    return notesTextArea.getText();
}

/* Text Area in JavaFX inherits selected text method from
javafx.scene.control.TextInputControl
*/

private String getSelectedInputText() {
    return notesTextArea.getSelectedText();
}

//set the identified JavaFX object (TextArea) for the Stage
public void setStageTextArea(TextArea myTA) {
    this.notesTextArea = myTA;
}

//Return the JavaFX object (Node) 
public TextArea getInputTextNode() {
    return this.notesTextArea;
}

//SIMPLE SCENE GETTERS AND SETTERS AS JAVA FX WRAPPER

public void storeSceneAndStage (Scene myScene, Stage myStage) {
    setStage(myStage);
    updateScene(myScene);
}

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

//FILE LOADERS
public void mainFilepathLoader() {
    final FileChooser fileChooser = new FileChooser();
    Stage myStage = new Stage();
    myStage.setTitle("Get Filepath");
    File file = fileChooser.showOpenDialog(myStage);
    if (file != null) {
      processFilepath(file);
    } 
}

private void processFilepath(File filepath){
  //String filename=System.out.print(file.toString()); // this is full path
    String name=filepath.getName();  //is this shortname?
    String path=filepath.getPath();//System.out.println(filepath.toString());
    //System.out.println(filepath.getPath());
    //System.exit(0);
    setLocalFilepath(path);
}

// a File object is really a filepath class
//Desktop.browse() launches the local web browser.
public void openWordDoc(String filepath) {
   try {
     if (Desktop.isDesktopSupported()) {
       Desktop.getDesktop().open(new File(filepath));
     }
   } catch (IOException ioe) {
     ioe.printStackTrace();
  }
}

public void openThisDoc() {
   String filepath = filepathTextArea.getText();
   if (filepath.equals(null)) {
    return;
   }
   try {
     if (Desktop.isDesktopSupported()) {
       Desktop.getDesktop().open(new File(filepath));
     }
   } catch (IOException ioe) {
     ioe.printStackTrace();
  }
}

public void openThisURL() {
   String upath = urlTextArea.getText();
   String checkedURI;
   if (upath.equals(null)) {
    return;
   }
   String prefix = upath.substring(0,7);
   String webcheck = upath.substring(0,3);
   if (webcheck.equals("www")) {
       //if (prefix.equals("http://")==false) {
        System.out.println("Must include http:// at beginning of web URL");
        checkedURI="http://"+upath;
        urlTextArea.setText(checkedURI);
        setLocalURL(checkedURI);
        //udpatedisplay 
   }
   else {
    System.out.println("http prefix ok web address");
    checkedURI=upath;
    setLocalURL(checkedURI);
   }

   try {
     URI uri = new URI(checkedURI);
     if (Desktop.isDesktopSupported()) {
       Desktop.getDesktop().browse(uri);
     }
   } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (URISyntaxException use) {
      use.printStackTrace();
  }
}

//called by file chooser.  Obtains a filepath to store in current node
//TO DO: call this from BookMetaStage?
public void setLocalFilepath (String filepath){
    ClauseContainer myData = getDisplayNode();
    myData.setdocfilepath(filepath);
    //refresh
    filepathTextArea.setText(getDisplayNode().getdocfilepath());
}

public void setLocalURL (String filepath){
    ClauseContainer myData = getDisplayNode();
    myData.seturlpath(filepath);
    //refresh
    urlTextArea.setText(getDisplayNode().geturlpath());
}

/* --- BASIC GUI SETUP FOR OPEN NODE VIEWERS --- */
private void updateOpenNodeView() {
    makeSceneForNodeEdit();
    resetSpriteOrigin();
    //title bar
    refreshTitle();
    //provide information about path of current open node in tree
    SpriteBox parBX = getParentBox();
    String parentSTR="";
    if (parBX==null) {
        parentSTR="[WS]";
    }
    else {
        if (parBX.getStageLocation()!=null) {
            parentSTR=parBX.getStageLocation().getTitle();
        }
        /*
        if(parBX.getBoxDocName()!=null) {
            parentSTR=parBX.getBoxDocName();

        }
        */
    }
    /** Use this if you want to display path info:
        String pathText = "Path/file:"+parentSTR+"-->"+getDisplayNode().getDocName()+"(viewing)"; 
    */
    String pathText = "Filepath:"+getDisplayNode().getdocfilepath();
    filepathText.setText(pathText);
    urlText.setText("URL path:");
    bookLabelText.setText("Book Label:");
    multiLineNotesText.setText("Multi-line notes:");
    visibleBlockText.setText("Visibility:");
    visibleCheck.setSelected(true);
    mdHeadingText.setText("Markdown:");
    restoreBookMeta();
    displayConceptSection();

    }
/* ----- DATA (DISPLAY) NODE FUNCTIONS ----- */

/* 
This method sets the node that is used for the display in this stage.
All other nodes added to this node are considered child nodes of this node:
they are added as child nodes to the data node; they are display in the section of the stage for this

*/

//REFRESHES ALL GUI DATA FROM FILE 
public void restoreBookMeta() {
        //LHS
        ClauseContainer updateNode=getDisplayNode();

        filepathTextArea.setText(updateNode.getdocfilepath());
        urlTextArea.setText(updateNode.geturlpath());
        bookLabelTextArea.setText(updateNode.getHeading());
        mdTextArea.setText(updateNode.getMD()); //update the markdown text
        notesTextArea.setText(updateNode.getNotes());
        visibleCheck.setSelected(updateNode.getVisible()); //check box
        outputTextArea.setText(updateNode.getOutputText()); //output node contents
        //RHS
        htmlEditor.setHtmlText(updateNode.getHTML());
}

public void updateBookMeta() {
        ClauseContainer updateNode=getDisplayNode();
        SpriteBox thisBook = getParentBox();
        System.out.println("This book box : "+thisBook.toString());
        //System.exit(0);
        updateNode.updateEditedText(filepathTextArea.getText(),urlTextArea.getText(),bookLabelTextArea.getText(),mdTextArea.getText(),notesTextArea.getText());
        thisBook.setBoxLabel(bookLabelTextArea.getText()); //update book label if needed
         //some kind of refresh needed?
        System.out.println(thisBook.getLabel());
        //System.exit(0);
    }


private void setDisplayNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default for serialisation only
}

public ClauseContainer getDisplayNode() {
    return this.displayNode;
}

public void addWSNode(ClauseContainer myNode) {

}

//Method to update workspace appearance based on current node setting (usually root of project)
/*
public void setWSNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    Group newGroup = new Group(); //new GUI node to show only new content.
    swapSpriteGroup(newGroup); //store the new GUI node for later use
    //resetSpriteorigin(); //not needed as in displayConceptSection
    displayConceptSection(); //update WS view with new child boxes only
}
*/

public void openNodeInViewer(ClauseContainer myNode) {

    setDisplayNode(myNode);
    updateOpenNodeView();
}

public ClauseContainer Node() {
    return this.displayNode;
}

//set the parent node for Nodes enclosed in boxes (i.e. level above)
private void setRefParentNode(ClauseContainer myParentID) {
    this.reference_ParentNode = myParentID;
}

private ClauseContainer getRefParentNode() {
    return this.reference_ParentNode;
}

/* GUI FUNCTIONS FOR WORKING WITH BOXES, NODES */

public void setParentBox (SpriteBox myPB) {
    this.parentBox = myPB;
    setDisplayNode(myPB.getBoxNode());
}

public SpriteBox getParentBox () {
    return this.parentBox;
}

/* Box up a container of Sprites and place on Stage 
Refreshes stage from display node, but doesn't show if invisible*/

 private void displayConceptSection() {
    
        ClauseContainer parentNode = getDisplayNode();
        resetSpriteOrigin();//to ensure they are at top
        //SpriteBox lastBox = new SpriteBox();
        ArrayList<ClauseContainer> childNodes = parentNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = childNodes.iterator();

        //only operates if there are Child Nodes to add
        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            System.out.println("Current child node to be added: "+thisNode.toString());
            System.out.println("WS Viewer :"+BookMetaStage.this);
            addNodeToView(thisNode);
        }
        //return getFocusBox();
        }

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
public void setSpriteGroup(Group myGroup) {
    this.spriteGroup = myGroup;
}

public Group getSpriteGroup() {
    return this.spriteGroup;
}

public void setSpritePane(Pane myPane) {
    this.spritePane = myPane;
}

public Pane getSpritePane() {
    return this.spritePane;
}

public void setSpriteScrollPane(ScrollPane myPane) {
    this.spriteScrollPane = myPane;
}

public ScrollPane getSpriteScrollPane() {
    return this.spriteScrollPane;
}


public void swapSpriteGroup(Group myGroup) {
    Pane myPane = getSpritePane();
    myPane.getChildren().remove(getSpriteGroup());
    setSpriteGroup(myGroup);
    myPane.getChildren().addAll(myGroup);
}

/*
public void swapSpriteChildGroup(Group myGroup) {
    ScrollPane myPane = getSpriteScrollPane();
    myPane.getChildren().remove(getSpriteGroup());
    setSpriteGroup(myGroup);
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

/*Method to set parent stage.  Call this before showing stage 

This is for GUI relationships, not data tree relationships.

nb If the stage has been called from a SpriteBox, the tree parent is the box, but
that box lies within a stage that can be used as parent stage here
(or make all stages the child of Stage_WS)
*/

private void setJavaFXStageParent(MainStage parent) {
    //Stage topStage = BookMetaStage.this.getParentStage().getStage();
    //Stage Parent = ParentSM.getStage();
    //myStage.initOwner(parent);
}

/* 

The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
The workspace (WS) is, in effect, a large window placed at back.
TO DO: check x y and within tolerable limits

*/
private void setEditWindowPosition() {
    setStagePosition(100,300);
    stageFront();
    }

//set workspace Window Position
private void setWorkspaceWindowPosition() {
   setStagePosition(0,0);
   stageBack();
}

//toolbars and other misc output
private void setToolBarWindowPosition() {
    setStagePosition(800,300);
    stageFront();
}
/* 

The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
The workspace (WS) is, in effect, a large window placed at back.
TO DO: Make the MenuBar etc attach to a group that is at back,
then add WIP spritexboxes to a 'Document Group' that replaces Workspace with 'Document' menu



//TO DO: set position based on NodeCat.
public void setPositionArchived() {

     switch(this.stageName){

            case "workspace":
                setStagePosition(0,0);
                stageBack();
                break;

            case "editor":
                //myStage.initOwner(Parent);  //this must be called before '.show()' on child
                setStagePosition(850,0);
                stageFront();
                break;

            case "project":
                setStagePosition(800,300);
                stageFront();
                break;

            case "project library":
                setStagePosition(800,300);
                stageFront();
                break;

            case "library":
                setStagePosition(1000,300);
                stageFront();
                break;

            case "collection":
                setStagePosition(800,100);
                stageFront();
                break;
                
            case "document":
                setStagePosition(400,200);
                stageFront();
                break;

            case "Toolbar":
                setStagePosition(1000,50);
                stageFront();
                break;

            case "Output":  
                setStagePosition(150,550);
                stageFront();
                break;

            case "Import":
                setStagePosition(800,200);
                stageFront();
                break;
            
            default:
                setStagePosition(200,200);
                stageFront();
                break;
    }
    
}
 */
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

//public interface setter helper - currently not used

/*
public void setInitStage(StageManager myParentSM, Stage myStage, Group myGroup, String myTitle) {
   setStageName(myTitle);
   setStage(myStage);
   setJavaFXStageParent(myParentSM);
   setEditWindowPosition();
   setSpriteGroup(myGroup);
   setTitle(myTitle);
}

*/

/* Method to make new Scene with known Group for Sprite (Concept Boxes) display */
public ScrollPane makeScrollGroup () {
    Group myGroup = new Group(); //the group that holds the box nodes.
    setSpriteGroup(myGroup);
    //attach event handler to this group 
    //myGroup.setOnMouseEntered(mouseEnterEventHandler);
    //other parts of JavaFX tree
    Pane myPane = new Pane();
    myPane.setOnMouseReleased(mouseEnterEventHandler);
    setSpritePane(myPane);
    myPane.getChildren().add(myGroup);
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(myPane);
    return outerScroll;
}

/* Method to make new TextArea that has associated functions in this class */
public TextArea makeTextArea() {
    TextArea tempTextArea = new TextArea();
    setStageTextArea(tempTextArea); 
    return tempTextArea;
}

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

/* Method to refresh GUI objects from underlying data (as saved) */

private void refreshNodeViewScene() {
        ClauseContainer updateNode = getDisplayNode();
        notesTextArea.setText(updateNode.getNotes());
        urlTextArea.setText(updateNode.geturlpath());
        notesTextArea.setWrapText(true);
        filepathTextArea.setText(updateNode.getdocfilepath());
        bookLabelTextArea.setText(updateNode.getHeading());
        htmlEditor.setHtmlText(updateNode.getHTML());
        //output node contents
        outputTextArea.setText(updateNode.getOutputText());
        //redisplay boxes
        Group newGroup = new Group(); //new GUI node to show only new content.
        swapSpriteGroup(newGroup); //store the new GUI node for later use
        resetSpriteOrigin();
        displayConceptSection();
}

//Method to change title depending on data mode the node is in.
private String getTitleText(String myString) {
    System.out.println("Make Scene. User Node View: "+getDisplayNode().getUserView());
    //main function        
    if (getDisplayNode().isFollower()==true) {
        return getDisplayNode().getDocName()+"--- NODE IN FOLLOW MODE [NO CHANGES SAVED] ---"+myString;
    }
    else {
        return getDisplayNode().getDocName()+myString;
    }
}

//html editor



/* Method to build the viewer for the current open node.
Capable of showing a text area, a pane to display sprite boxes and an Edit/Update button
User can choose to see less (i.e. only work with some of what a node can contain)
i.e. can resemble a text editor, or graphical tree, or functional text processor with all three areas

State variable (userNodeView) defines which version of UI to display.
User can cycle through states of UI display through key press (CMD-Z)

Should presence of update buttons be dependent on node not in "Follower" mode?
i.e. should GUI/cycle options be varied for a follower node? 
especially if it is a non-edit node?       

*/

private void makeSceneForNodeEdit() {
        
        /*ScrollPane boxPane = makeScrollGroup();
        boxPane.setPannable(true);
        boxPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
        boxPane.setVmax(500);
        */
        
        //NODE VIEWER DIMENSIONS
        int winWidth=650;
        int dblwidth=2*winWidth;
        int winHeight=700;
        int scenewidth=winWidth;
        //HTML editor
        htmlEditor.setPrefSize(winWidth,winHeight);
        //TEXT AREAS
        bookLabelTextArea.setPrefRowCount(1);
        mdTextArea.setPrefRowCount(20); //for markdown.  Add to boxPane
        notesTextArea.setPrefRowCount(7); //for notes
        notesTextArea.setWrapText(true);
        filepathTextArea.setPrefRowCount(1);
        urlTextArea.setPrefRowCount(1);
        outputTextArea.setPrefRowCount(1);
        //
        //add mdTextArea to BoxPane
        //ScrollPane boxPane = new ScrollPane();
        //boxPane.setPrefSize(winWidth, winHeight-300);
        //boxPane.setContent(mdTextArea);
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Confirm edits"));
        btnUpdate.setOnAction(UpdateBookMetaText);
        //Button for cancel
        Button btnEditCancel = new Button();
        btnEditCancel.setText("Close");
        btnEditCancel.setTooltip(new Tooltip ("Press to Cancel current edits"));
        btnEditCancel.setOnAction(closeWindow);
        Button btnOpenDoc = new Button();
        btnOpenDoc.setText("Open");
        btnOpenDoc.setTooltip(new Tooltip ("Opens in Default Application"));
        btnOpenDoc.setOnAction(OpenWordAction);
        HBox hboxButtons = new HBox(0,btnUpdate,btnEditCancel);
        Button btnOpenURL = new Button();
        btnOpenURL.setText("Open");
        btnOpenURL.setTooltip(new Tooltip ("Opens in Default Application"));
        btnOpenURL.setOnAction(OpenURLAction);
        //
        filepathText = new Text();
        bookLabelText = new Text();
        multiLineNotesText = new Text();
        visibleBlockText = new Text();
        mdHeadingText = new Text();
        urlText = new Text();
        //set view option
        HBox widebox;
        VBox vertFrame;
        HBox visiblebox = new HBox(0,visibleBlockText,visibleCheck);
        Button btnBrowseFilepath = new Button();
        btnBrowseFilepath.setText("Browse");
        btnBrowseFilepath.setTooltip(new Tooltip ("Browse files to set"));
        btnBrowseFilepath.setOnAction(fileChooseAction);

        HBox filepathBox = new HBox(0,filepathTextArea,btnBrowseFilepath,btnOpenDoc);
        HBox urlpathBox = new HBox(0,urlTextArea,btnOpenURL);

        //handle null case
        if (getDisplayNode().getUserView()==null) {
            getDisplayNode().setUserView("all");
        }
        if (getDisplayNode().getUserView().equals("textonly")) {
            vertFrame = new VBox(0,bookLabelText,bookLabelTextArea,hboxButtons);
             vertFrame.setPrefSize(winWidth,winHeight);
            setTitle(getTitleText(" - HTML Text View"));
            widebox = new HBox(0,vertFrame,htmlEditor);
            widebox.setPrefSize(dblwidth,winHeight);
        }
        else if (getDisplayNode().getUserView().equals("inputoutput")) {
            vertFrame = new VBox(0,visiblebox,bookLabelText,bookLabelTextArea,mdHeadingText,mdTextArea,multiLineNotesText,notesTextArea,hboxButtons);
             vertFrame.setPrefSize(winWidth,winHeight);
            setTitle(getTitleText(" Markdown View"));
            widebox = new HBox(0,vertFrame);
            widebox.setPrefSize(dblwidth,winHeight);
        }

        else if(getDisplayNode().getUserView().equals("nodeboxesonly")) {
            vertFrame = new VBox(0,filepathBox,mdHeadingText,mdTextArea,hboxButtons);
            vertFrame.setPrefSize(winWidth,winHeight);
            setTitle(getTitleText(" - Concepts View"));
            widebox = new HBox(0,vertFrame);
            widebox.setPrefSize(dblwidth,winHeight);
        }
         //this is now the default?
            else {
            vertFrame = new VBox(0,visiblebox,filepathText,filepathBox,urlText,urlpathBox,bookLabelText,bookLabelTextArea,mdHeadingText,mdTextArea,multiLineNotesText,notesTextArea,hboxButtons);
            setTitle(getTitleText(" - Full View"));
            vertFrame.setPrefSize(winWidth,winHeight);
            widebox = new HBox(0,vertFrame,htmlEditor);
            widebox.setPrefSize(dblwidth,winHeight);
            scenewidth=dblwidth;
            //widebox.getChildren().add()
        }
        //
        Pane largePane = new Pane();
        largePane.setPrefSize(scenewidth, winHeight);
        largePane.getChildren().add(widebox); //toggle option? 
        Scene tempScene = new Scene (largePane,scenewidth,winHeight); //default width x height (px)
        //add event handler for mouse released event
        tempScene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEnterEventHandler);
         //add event handler for mouse dragged  event
        tempScene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragEventHandler);

        //add event handler for mouse pressed event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
                 System.out.println("Mouse click on a node (StageManager scene) detected! " + mouseEvent.getSource());
                 //setStageFocus("document");
                 /*
                 if (!getCurrentFocus().equals(BookMetaStage.this)) {
                    /* Rfresh NodeViewScene
                    If this needs to be done, save first.
                    Otherwise current unsaved text is lost when click on scene occurs.  */
                    //refreshNodeViewScene();
                    //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
                    /*
                 }
                 //error checking i.e. like jUnit assert
                 if (getCurrentFocus()==BookMetaStage.this) {
                    System.out.println("Change of Viewer Focus OK in Viewer!");
                     System.out.println("makescene Viewer :"+BookMetaStage.this);
                     System.out.println("scene display node :"+getDisplayNode().toString());
                     System.out.println("notes String :"+getDisplayNode().getNotes());
                     System.out.println("Notes: "+notesTextArea.getText());
                 }
                 else {
                    System.out.println("Problem with change Viewer Focus");
                    System.out.println("makescene Present Viewer :"+BookMetaStage.this);
                    System.out.println("Current Focus :"+getCurrentFocus());
                 }
                 */
         }
        });
        updateScene(tempScene);
}

/* New Local mouse event handler */

  
/*Mouse event handler - to deal with boxes being dragged over this stage manager and release
If this is attached to the panel in the GUI where the child nodes sit, it is easy to handle a 'drop'
Currently utilises the 'makeScrollGroup' and addNewSpriteToStage methods.
The setSpriteGroup group must also add this event handler to that group.
*/

EventHandler<MouseEvent> mouseEnterEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            //TO DO: check if mouse is dragging/pressed
            //System.out.println("Detected mouse released - Stage Manager Group"+BookMetaStage.this.getSpriteGroup().toString());
            //t.consume();//check
        }
    };

//mouse drag

    EventHandler<MouseEvent> mouseDragEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            //TO DO: check if mouse is dragging/pressed
            //System.out.println("Detected mouse drag - Stage Manager Group"+BookMetaStage.this.getSpriteGroup().toString());
            //t.consume();//check
        }
    };


//General close window handler
EventHandler<ActionEvent> closeWindow = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
           BookMetaStage.this.closeThisStage();
        }
    };

private void closeThisStage() {
    //BookMetaStage.this.getParentStage().getStage().show();
           //this.myTrk.setCurrentFocus(BookMetaStage.this);
    getStage().close();
}

//general function to save GUI text data into node (position data for concept boxes is already updated)
//also performs a save on shared parent.
//TO DO: remove and just save each text box content to an SQL database.
private void saveNodeText() {
    //getDisplayNode().updateText(htmlEditor.getHtmlText(),filepathTextArea.getText(),bookLabelTextArea.getText(),notesTextArea.getText(),outputTextArea.getText());
    
    getDisplayNode().updateMDText(bookLabelTextArea.getText(),mdTextArea.getText(),notesTextArea.getText());

    ClauseContainer fileNode=getDisplayNode().getUltimateParent();
    if (fileNode==null) {
     System.out.println ("No parent node to save.  saveNodeText method");
     System.exit(0);
     }
    saveDocTree(fileNode); //save the whole tree (of updated text)
    System.out.println("saved Parent: "+getDisplayNode().getUltimateParent().toString());
    //parentBox - should we insist on one?
    SpriteBox pntBox = getParentBox();
    if (pntBox!=null) {
        pntBox.setLabel(filepathTextArea.getText());
    }
    //error checking - log.  Leave this to show error for attempts with follower nodes.
    if (getDisplayNode().getNotes().equals(notesTextArea.getText())) {
        System.out.println("Node updated OK!");
    }
    else {
         System.out.println("Problem with node update.");
        }
    }

// INPUT / OUTPUT 

//nb this method mirrors that which can be called from Main
private void saveDocTree(ClauseContainer saveNode) {
    LoadSave myLS = new LoadSave();
    myLS.saveName(saveNode);
    //myLS.Close();
    String filename=saveNode.getDocName();
    Recents myR = new Recents();
    myR.updateRecents(filename);
}

//Create Eventhandler to use with stages that allow edit button

EventHandler<ActionEvent> UpdateBookMetaText = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            BookMetaStage.this.updateBookMeta();
            }
        };

EventHandler<ActionEvent> OpenWordAction = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            BookMetaStage.this.openThisDoc();
            }
        };

EventHandler<ActionEvent> OpenURLAction = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            BookMetaStage.this.openThisURL();
            }
        };


//event handler to call file chooser
EventHandler<ActionEvent> fileChooseAction = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            BookMetaStage.this.mainFilepathLoader();
            }
        };


private void newWorkstageFromGroup() {
    Group myGroup = makeWorkspaceTree();
    Scene myScene = makeWorkspaceScene(myGroup);
    Stage myStage = new Stage();
    setStage(myStage);
    updateScene(myScene);
    setWorkspaceWindowPosition();
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

private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        BorderPane myBP = new BorderPane(); //holds the menubar, spritegroup
        Group menubarGroup = new Group(); //subgroup
        MenuBar myMenu = getMenuBar();
        menubarGroup.getChildren().addAll(myMenu);
        
        //the Pane holding the group allows movement of SpriteBoxes independently, without relative movement
        
        Pane workspacePane = new Pane(); //to hold a group, holding a spritegroup
        Group displayAreaGroup = new Group(); //subgroup of Pane; where Sprites located
        
        workspacePane.getChildren().addAll(displayAreaGroup);
        setSpritePane(workspacePane); //store for later use
        setSpriteGroup(displayAreaGroup); //store for later use

        myBP.setTop(menubarGroup);
        myBP.setMargin(workspacePane, new Insets(50,50,50,50));
        myBP.setCenter(workspacePane);
        //workspacePane.setPadding(new Insets(150,150,150,150));
        //Add some shelves
        //rectangle
        Rectangle shelf1 = makeNewShelf(100,231);
        Rectangle shelf2 = makeNewShelf(100,421);
        Rectangle shelf3 = makeNewShelf(100,611);

        //setArcWidth(60);  //do this enough you get a circle.  option
        //setArcHeight(60);                
       
       //Color myColour = Color.BLACK;
        //this.boxcolour=mycol;//not updated yet?
        //Creating a line object
        Line line = new Line(); 
         
        //Setting the properties to a line 
        line.setStartX(100.0); 
        line.setStartY(75.0); 
        line.setEndX(500.0); 
        line.setEndY(75.0); 
        
        //add the Border Pane and branches to root Group 
        myGroup_root.getChildren().addAll(myBP);
        myGroup_root.getChildren().addAll(line,shelf1,shelf2,shelf3); //line and shelf 1
        //store the root node for future use
        setSceneRoot(myGroup_root); //store 
        //for box placement within the Scene - attach them to the correct Node.
        return myGroup_root;  
    }

private Rectangle makeNewShelf(Integer x, Integer y) {
        
        Rectangle shelf1= new Rectangle();
        shelf1.setFill(Color.WHITE); //default
        shelf1.setStroke(Color.BLACK); //stroke is border colour
        shelf1.setWidth(1000);
        shelf1.setHeight(15);
        shelf1.setX(x);
        shelf1.setY(y);
        return shelf1;
    }
       



private Scene makeWorkspaceScene(Group myGroup) {
        
        //construct scene with its root node
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), Color.BEIGE);

        
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
            /*
            areas and targets differ depending on objects on stage (invisible stretch)
            Ignore the MenuBar here
            JavaFX has ability to detect Text, Rectangle, ColBox (all components of a SpriteBox)
            Better to force it to detect a SpriteBox?
            Although clicking on text could be useful for updating headings/filenames
            It is possible to change focus with a click, but exclude MenuBar targets
            (these seemed to be instances of LabeledText 
            i.e. class com.sun.javafx.scene.control.LabeledText)
             */
            //if (mouseEvent.getTarget()==getSceneGUI()) {
            if (mouseEvent.getTarget() instanceof Scene) {
                System.out.println("Clicked on scene; updated focus");
                System.out.println("ws Viewer :"+BookMetaStage.this);
                //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
                mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof BorderPane) {
                 System.out.println("Clicked on Border Pane ; updated focus");
                 System.out.println("ws Viewer :"+BookMetaStage.this);
                 //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
                 mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof Pane) {
                 System.out.println("ws Clicked on Pane ; updated focus");
                 //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
                 mouseEvent.consume(); //to stop the bubbling?
            }
            //to distinguish Text on Menu from Text on boxes you can interrogate what the Text is to see if it's a menu
            else if (mouseEvent.getTarget() instanceof Text) {
                 System.out.println("ws Clicked on Text ; no change to focus");
                 //this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else if (mouseEvent.getTarget() instanceof ColBox) {
                 System.out.println("ws Clicked on box ; updated focus");
                 System.out.println("ws Viewer :"+BookMetaStage.this);
                 //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else if (mouseEvent.getTarget() instanceof Rectangle) {
                 System.out.println("ws Clicked on box ; updated focus");
                 //BookMetaStage.this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else if (mouseEvent.getTarget() instanceof Labeled) {
                 System.out.println("ws Clicked on Labeled ; no change to focus");
                 //this.myTrk.setCurrentFocus(BookMetaStage.this);
            }
            else {
                System.out.println("ws Click not identified : no change to focus");
            }

            }
        });

        workspaceScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent keyEvent) {
             System.out.println("Key pressed on workspace stage " + keyEvent.getSource());
             //if source = ... only then change focus 
            }
        });
        
        return workspaceScene;
    }

//SPRITE BOX ASSIST FUNCTIONS

public void setFollow(SpriteBox mySprite) {
    ClauseContainer parentLinkNode = mySprite.getBoxNode();
    getDisplayNode().setFollow(parentLinkNode);
}

//method sets unfollow mode, but won't change the stored parent link for now (allows toggle)
public void unsetFollow() {
    //ClauseContainer parentLinkNode = mySprite.getBoxNode();
    getDisplayNode().unsetFollow();
}

//not used yet.  decide if it can toggle mode without parent specified.
public void toggleFollow() {
    
}

/* public function to add a box (as a child node) to this Viewer.
Add node to view will also call the addsprite to stage to complete this.
*/
public void addNewSpriteToStage(SpriteBox mySprite) {
        //mySprite.getBoxNode()
        addChildNodeToDisplayNode(mySprite.getBoxNode()); //data
        //addSpriteToStage(mySprite); //view 
        addNodeToView(mySprite.getBoxNode());
    }

//Method to add child node based on the contents of an identified NodeBox in GUI.
//also sets parent node of the node in the sprite box to this Stage Manager
/*private void addChildBoxToDisplayNode(SpriteBox mySprite) {
    getDisplayNode().addChildNode(mySprite.getBoxNode());
    mySprite.getBoxNode().setParentNode(getDisplayNode());
}
*/
//public method to allow Main controller to initiate child node creation in viewer

public void selectedAsChildNode() {
    String sampleText = getSelectedInputText();
    //construct new node using available inputs (i.e. suitable constructor)
    NodeCategory NC_clause = new NodeCategory ("clause",0,"blue"); //mirror main
    ClauseContainer myNode = new ClauseContainer(NC_clause,getDisplayNode(),sampleText,sampleText.substring(0,8));
    //
    newNodeAsChildNode(myNode); //data and view for node viewer
}

public void setFocusBox(SpriteBox myBox) {
    this.focusbox = myBox;
}

public SpriteBox getFocusBox() {
    return this.focusbox;
}



/*
Internal method to add sprite to the Group/Pane of this Node Viewer 
This is to add an existing GUI 'box/node' to the Child Node section of this Viewer.
i.e. this adds a specific object, rather than updating the view from whole underlying data set.
*/

private void addSpriteToStage(SpriteBox mySprite) {
    getSpriteGroup().getChildren().add(mySprite); //GUI tree 
    System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    positionSpriteOnStage(mySprite);
    advanceSpritePositionHor(); //default is to space along shelf
    setFocusBox(mySprite); //local information
    mySprite.setStageLocation(this.mainStage); //give Sprite the object for use later.
}


//General method to add AND open a node if not on ws; otherwise place on workspace
//The StageManager arg passed in as myWS should be 'Stage_WS' for all calls 

public void OpenNewNodeNow(ClauseContainer newNode, StageManager myWS) {
    System.out.println("OpenNewNode now...");
     if (BookMetaStage.this.equals(myWS)) { 
        addChildNodeToDisplayNode(newNode); //data
        SpriteBox b = new SpriteBox(this.PressBox,this.DragBox,newNode);
        addSpriteToStage(b); //differs from Main 
        if (b==null) {
            System.out.println("SpriteBox null in addnodetoview");
            System.exit(0);
    }
    setFocusBox(b);
    }
}

/* This method adds a single node to workspace without refreshing entire view */
/*
private void newNodeForWorkspace(ClauseContainer myNode) {
    //same as opennewnodenow
}
*/

//method to box up node as shape and add to GUI in node viewer

private void addNodeToView (ClauseContainer myNode) {
    //SpriteBox b = makeBoxWithNode(myNode); //relies on Main, event handlers x
    SpriteBox b = new SpriteBox(this.PressBox,this.DragBox,myNode);
    addSpriteToStage(b); //differs from Main 
    if (b==null) {
        System.out.println("SpriteBox null in addnodetoview");
        System.exit(0);
    }
    setFocusBox(b);
    
}


//General method to PLACE AN EXISTING NODE
//in current focus...
//The StageManager arg passed in as myWS should be 'Stage_WS' for all calls 

public void PlaceNodeNow(ClauseContainer myNode, StageManager myWS) {
    System.out.println("PlaceNewNode now...");
     if (BookMetaStage.this.equals(myWS)) { 
     //newNodeForWorkspace(newNode);
     System.out.println("Adding new node to Workspace");
}
        else {
             //newNodeAsChildNode(newNode);
        }
}



/* This method adds the child nodes of the parentNode passed as arg 
to the Open Node, as its child nodes and then updates the view.
*/

public void addOpenNodeChildren (ClauseContainer parentNode) {
    getDisplayNode().addNodeChildren(parentNode);
    updateOpenNodeView();
}



//Method to add a single child node to the open node in this view and update parent node (data)

private void addChildNodeToDisplayNode(ClauseContainer myChildNode) {
    getDisplayNode().addChildNode(myChildNode);
    myChildNode.setParentNode(getDisplayNode());
}

/* Method to add node as child node of parent AND update/display all nodes */

private void newNodeAsChildNode(ClauseContainer newNode) {
    addChildNodeToDisplayNode(newNode); //data
    updateOpenNodeView(); //view
}

public void removeSpriteFromStage(SpriteBox thisSprite) {
    thisSprite.unsetParentNode(); //data
    //TO DO: remove Node (data) ? is it cleaned up by GUI object removal?
    this.spriteGroup.getChildren().remove(thisSprite); //view/GUI
    thisSprite.resetLocation(); //??
    getStage().show(); //refresh GUI
    
}

public void setContentsArray(ArrayList<Object> inputObject) {
    this.BoxContentsArray = inputObject;
}

public ArrayList<Object> getContentsArray() {
    return this.BoxContentsArray;
}

public void positionSpriteOnStage(SpriteBox mySprite) {
        
    if (mySprite!=null) {  //might be no current sprite if not dbl clicked
            mySprite.endAlert();
    }
    //advanceSpritePosition();
    //if sprite does not have its own location, use GUI location
    //use Translate relative to origin of node (i.e. not scene as a whole)

    System.out.println("x: "+mySprite.getX()+ "y: "+mySprite.getY());
    System.exit(0);
    if (mySprite.getX()!=0 || mySprite.getY()!=0) {

        double x = mySprite.getX();
        double y = mySprite.getY();
        mySprite.setTranslateX(x);
        mySprite.setTranslateY(y);
    }
    else {
        advanceSpritePositionHor();
        mySprite.setTranslateX(this.spriteX);
        mySprite.setTranslateY(this.spriteY); 
    } 
    mySprite.setStageLocation(this.mainStage); //needed if stage is not o/w tracked
    if (mySprite.getStageLocation()!=this.mainStage) {
        System.out.println("Problem with adding sprite:"+mySprite.toString());
    }
    else {
        System.out.println("Positioned sprite at:"+mySprite.toString()+" ("+spriteX+","+spriteY+")");
    }
}

public void resetSpriteOrigin() {
    this.spriteY=0;
    this.spriteX=0;
}

//new 'shelf'??
public void newSpriteColum() {
    this.spriteY=spriteY+160;
    this.spriteX=0;
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
//Layout horizontall on one shelf.
private void advanceSpritePositionHor() {
        if (this.spriteX>880) {
                this.spriteY=spriteY+200; //gap (shelves)
                this.spriteX=0;
            }
            else {
                this.spriteX = this.spriteX+65; //uniform size for now
            }
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
//This function runs new boxes down vertically
//The alternative to this is to package boxes in a list view.
//"A ListView displays a horizontal or vertical list of items from which the user may select, or with which the user may interact."
//Observable List https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ListView.html
private void advanceSpritePositionVert() {
        if (this.spriteY>660) {
                this.spriteY=spriteY+200;
                this.spriteX=0;
            }
            else {
                this.spriteY = this.spriteY+200;
            }
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}

public double getBigY() {
    return this.myBigY;
}

//SPECIFIC TEXT OUTPUT WINDOW OPTION

//Function to setup independent output window
//This is only called for the Stage_Output instance.
//TO DO: discard or put into StageManager constructor

public void setupTextOutputWindow() {

    putTextScrollerOnStage();
    setOutputText("Some future contents");
    hideStage();
    setToolBarWindowPosition();
}


}