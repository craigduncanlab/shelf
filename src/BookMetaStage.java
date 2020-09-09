//(c) Craig Duncan 2017-2020
//www.craigduncan.com.au

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
//File chooser
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
//Robot to simulate user key typing
import javafx.scene.robot.Robot;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
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
//skins for TextArea
import javafx.scene.control.skin.TextAreaSkin;
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


Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;

String filename = ""; //current filename for saving this stage's contents
//STAGE IDS
int location = 0;
String category="";
//Displayed Book (i.e. Node).  Will be updated through GUI.
Stage mainStage; //the parent's stage.  i.e Stage associated with MainStage object.
Book activeBook;
TextArea filepathTextArea = new TextArea();
TextArea imagepathTextArea = new TextArea();
TextArea urlTextArea = new TextArea();
TextArea mdTextArea = new TextArea();
TextArea bookLabelTextArea = new TextArea();
TextArea codeNotesTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
Integer textFocus=0;
Text filepathText;
Text imagepathText;
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
ArrayList<Object> BoxContentsArray = new ArrayList<Object>(); //generic store of contents of boxes


//Track current stage that is open.  Class variables
static StageManager currentFocus; 
static Book currentTarget; 

//constructor
public BookMetaStage() {
    this.outputTextArea.setWrapText(true);
    this.codeNotesTextArea.setWrapText(true);  //default
}

//standard open node viewer constructor.  Used by 'OpenRedNodeNow' method in Main
public BookMetaStage(Stage parent, Book myBook, EventHandler PressBox, EventHandler DragBox, EventHandler SaveKeyEvent) {
    //views and parent-child stage relationships
    this.mainStage=parent; //we need the Stage object inside the MainStage object.
    Stage currentStage = this.localStage; //created as an instance variable on this object creation.
    currentStage.initOwner(this.mainStage); //do this before Show.  This keeps this Stage on top of the parent.  use MODAL for further behaviour config
    //this.localStage.initOwner(parent);
    //store event handlers as local instance variables
    setPressBox(PressBox);
    setDragBox(DragBox);
    setKeyPress(SaveKeyEvent); //save key event common to both shelf and inspector views
    setKeyPress(NodeKeyHandler); //this can be different for workspace
    //position
    //data: new 'parent' node based on category alone
    setActiveBook(myBook);
    updateCurrentBookMetaView(); //updates contents but doesn't show stage unless requested
    showStage(); //to do: put default view in constructor
}

//GLOBAL view setting.  Make switch.
private void cycleUserView() {
    //User choice of view stored in node
    String myView = getActiveBook().getUserView();
    if (myView==null) {
        myView="all";
        }
    switch (myView) {
    
        case "all" : 
            getActiveBook().setUserView("metaedithtml");
            updateCurrentBookMetaView();
            break;
        case "metaedit" :
            getActiveBook().setUserView("metaedithtml");
            updateCurrentBookMetaView();
            break;
        case "metaedithtml" :
            getActiveBook().setUserView("HTMLonly");
            updateCurrentBookMetaView();
            break;
        case "HTMLonly" :
            getActiveBook().setUserView("metaedit");
            updateCurrentBookMetaView();
            break;
        default:
            getActiveBook().setUserView("HTMLonly");
            updateCurrentBookMetaView();
            break;
        }
    }

public Book getCurrentTarget() {
    return currentTarget;
}

//JAVAFX SCENE GRAPH GUI INFO (THIS IS NOT THE DATA NODE!)
public void setSceneRoot(Node myNode) {
    this.rootNode = myNode;
}

public Node getSceneRoot() {
    return this.rootNode;
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}

public double getBigY() {
    return this.myBigY;
}

//FILE I/O DATA
public String getFilename() {
    return this.filename;
}

public void setFilename(String myFile) {
    this.filename = myFile;
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
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, myKE);
}

//Set key handler at level of stage  - from passed in event
private void setKeyPressSprite(EventHandler<KeyEvent> myKE) {
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, KeyEventHandler);
}

EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
    System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

 //nb: This picks up key presses even when entering data into book meta stage TextAreas.
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
    System.out.println("is Shift Down: " + ke.isShiftDown());
    System.out.println("is Control Down: " + ke.isControlDown());
    System.out.println("is Meta(Command) Down: " + ke.isMetaDown());
    //'live' conversion of typing into HTML (not as efficient but typists don't notice)
    Book targetBook = BookMetaStage.this.getActiveBook();
    targetBook.setMD(mdTextArea.getText()); //cf updateBookMeta
    BookMetaStage.this.updateHTMLpreview(targetBook);

    //shortcuts
    if (ke.isMetaDown() && ke.getCode().getName().equals("Z")) {
         System.out.println("CMD-Z pressed");
         cycleUserView();
    }
    /*
    if (ke.isMetaDown() && ke.getCode().getName().equals("S")) {
         System.out.println("CMD-S pressed (will save metadata)");
         updateBookMeta();
    }
    */
    if (ke.isMetaDown() && (ke.getCode().getName().equals("W") || ke.getCode().getName().equals("I"))){
         System.out.println("CMD-W or CMD-I pressed (will close metadata stage)");
         System.out.println(htmlEditor.getHtmlText());
         closeThisStage();
    }
    //if just inspecting, use space to close
    if (ke.getCode()==KeyCode.SPACE && targetBook.getUserView()=="HTMLonly") {
        closeThisStage();
    }
    //to help with editor navigation.  cascading tab presses
    if (ke.getCode()==KeyCode.TAB) {
        ke.consume(); //so the TAB doesn't actually 'land' as a keystroke in the TextArea
        if (BookMetaStage.this.localScene.focusOwnerProperty().get() instanceof TextArea) {
                TextArea focusedTextArea = (TextArea)BookMetaStage.this.localScene.focusOwnerProperty().get();
              
            if (focusedTextArea==bookLabelTextArea) {
                mdTextArea.requestFocus();
            }
            else if (focusedTextArea==mdTextArea) {
                filepathTextArea.requestFocus();
            }
            else if (focusedTextArea==filepathTextArea) {
                imagepathTextArea.requestFocus();
            }
            else if (focusedTextArea==imagepathTextArea) {
                urlTextArea.requestFocus();
            }
            else if (focusedTextArea==urlTextArea) {
                codeNotesTextArea.requestFocus();
            }
            else if (focusedTextArea==codeNotesTextArea) {
                bookLabelTextArea.requestFocus();
            }
        }  
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
    codeNotesTextArea.setText(myText);
}

public String getInputText() {
    return codeNotesTextArea.getText();
}

/* Text Area in JavaFX inherits selected text method from
javafx.scene.control.TextInputControl
*/

private String getSelectedInputText() {
    return codeNotesTextArea.getSelectedText();
}

//set the identified JavaFX object (TextArea) for the Stage
public void setStageTextArea(TextArea myTA) {
    this.codeNotesTextArea = myTA;
}

//Return the JavaFX object (Node) 
public TextArea getInputTextNode() {
    return this.codeNotesTextArea;
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

//FILE LOADERS -To browse for files to add to links.  No filters?  or just PDF .docx, html etc?
public void mainFilepathLoader() {
    final FileChooser fileChooser = new FileChooser();
    Stage myStage = new Stage();
    myStage.setTitle("Get Filepath"); 
    File file = fileChooser.showOpenDialog(myStage);
    if (file != null) {
      processFilepath(file);
    } 
}

//image path loader
public void mainImagepathLoader() {
    final FileChooser fileChooser = new FileChooser();
    Stage myStage = new Stage();
    myStage.setTitle("Get Image path"); 
    File file = fileChooser.showOpenDialog(myStage);
    if (file != null) {
      processImagepath(file);
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

private void processImagepath(File imagepath){
  //String filename=System.out.print(file.toString()); // this is full path
    String name=imagepath.getName();  //is this shortname?
    String path=imagepath.getPath();//System.out.println(filepath.toString());
    //System.out.println(filepath.getPath());
    //System.exit(0);
    setLocalImagepath(path);
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
    Book myData = getActiveBook();
    myData.setdocfilepath(filepath);
    //refresh
    filepathTextArea.setText(getActiveBook().getdocfilepath());
}

public void setLocalURL (String filepath){
    Book myData = getActiveBook();
    myData.seturlpath(filepath);
    //refresh
    urlTextArea.setText(getActiveBook().geturlpath());
}

public void setLocalImagepath (String imagepath){
    Book myData = getActiveBook();
    myData.setimagefilepath(imagepath);
    //refresh
    imagepathTextArea.setText(getActiveBook().getimagefilepath());
}

/* --- BASIC GUI SETUP FOR OPEN NODE VIEWERS --- */
private void updateCurrentBookMetaView() {
    makeSceneForBookMetaView(); //sets up scene, not content
    //title bar
    refreshTitle();
    //provide information about path of current open node in tree
    String pathText = "Filepath:"+getActiveBook().getdocfilepath();
    filepathText.setText(pathText);
    imagepathText.setText("Image path:");
    urlText.setText("URL path:");
    bookLabelText.setText("Book Label:");
    multiLineNotesText.setText("Multi-line notes:");
    visibleBlockText.setText("Visibility:");
    visibleCheck.setSelected(true);
    mdHeadingText.setText("Markdown:");
    //this captures from the in-memory not the GUI?
    restoreBookMeta();

    }
/* ----- METADATA FUNCTIONS ----- */

//REFRESHES ALL GUI DATA FROM FILE 
public void restoreBookMeta() {
        //LHS
        Book updateNode=getActiveBook();

        filepathTextArea.setText(updateNode.getdocfilepath());
        imagepathTextArea.setText(updateNode.getimagefilepath());
        urlTextArea.setText(updateNode.geturlpath());
        bookLabelTextArea.setText(updateNode.getLabel());
        mdTextArea.setText(updateNode.getMD()); //update the markdown text
        codeNotesTextArea.setText(updateNode.getNotes());
        visibleCheck.setSelected(updateNode.getVisible()); //check box
        outputTextArea.setText(updateNode.getOutputText()); //output node contents
        //RHS
        //dynamic update of Book's HTML data
        updateHTMLpreview(updateNode);
}

public void updateBookMeta() {
        Book thisBook=getActiveBook();
        System.out.println("This book box : "+thisBook.toString());
        //System.exit(0);
        thisBook.updateEditedText(filepathTextArea.getText(),urlTextArea.getText(),imagepathTextArea.getText(), bookLabelTextArea.getText(),mdTextArea.getText(),codeNotesTextArea.getText());
        thisBook.setLabel(bookLabelTextArea.getText()); //update book label if needed
        updateHTMLpreview(thisBook); //some kind of refresh needed?
        System.out.println(thisBook.getLabel());
        //System.exit(0);
    }


private void setActiveBook(Book myBook) {
    this.activeBook = myBook;
}

public Book getActiveBook() {
    return this.activeBook;
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

/* Method to make new TextArea that has associated functions in this class */
public TextArea makeTextArea() {
    TextArea tempTextArea = new TextArea();
    setStageTextArea(tempTextArea); 
    return tempTextArea;
}

//Method to change title depending on data mode the node is in.
private String getTitleText(String myString) {
    System.out.println("Make Scene. User Node View: "+getActiveBook().getUserView());
    return getActiveBook().getDocName()+myString;
    }

//html editor



/* Method to build the viewer for the current open node.
Capable of showing a text area, a pane to display sprite boxes and an Edit/Update button
User can choose to see less (i.e. only work with some of what a node can contain)
i.e. can resemble a text editor, or graphical tree, or functional text processor with all three areas

State variable (userNodeView) defines which version of UI to display.
User can cycle through states of UI display through key press (CMD-Z)

*/

private void makeSceneForBookMetaView() {
        
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
        
        //TEXT AREAS INITIALISATION
        String terminalStyle="-fx-control-inner-background:#000000; -fx-font-family: Consolas; -fx-highlight-fill: #ffffff; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00; ";
        bookLabelTextArea.setPrefRowCount(1);
        bookLabelTextArea.setStyle(terminalStyle);
        mdTextArea.setPrefRowCount(20); //for markdown.  Add to boxPane
        mdTextArea.setWrapText(true);
        mdTextArea.setStyle(terminalStyle);
        imagepathTextArea.setPrefRowCount(1);
        filepathTextArea.setPrefRowCount(1);
        urlTextArea.setPrefRowCount(1);
        outputTextArea.setPrefRowCount(1);
        //
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
        imagepathText = new Text();
        bookLabelText = new Text();
        multiLineNotesText = new Text();
        visibleBlockText = new Text();
        mdHeadingText = new Text();
        urlText = new Text();
        //set view option
        
        VBox vertFrame;
        HBox visiblebox = new HBox(0,visibleBlockText,visibleCheck);
        Button btnBrowseFilepath = new Button();
        btnBrowseFilepath.setText("Browse");
        btnBrowseFilepath.setTooltip(new Tooltip ("Browse files to set"));
        btnBrowseFilepath.setOnAction(fileChooseAction);

        Button btnBrowseImagepath = new Button();
        btnBrowseImagepath.setText("Browse");
        btnBrowseImagepath.setTooltip(new Tooltip ("Browse images to set"));
        btnBrowseImagepath.setOnAction(chooseImageAction);

        HBox filepathBox = new HBox(0,filepathTextArea,btnBrowseFilepath,btnOpenDoc);
        HBox imagepathBox = new HBox(0,imagepathTextArea,btnBrowseImagepath);
        HBox urlpathBox = new HBox(0,urlTextArea,btnOpenURL);
        HBox widebox = new HBox(0,htmlEditor); //default - can change as below
        BorderPane borderPane = new BorderPane();
        //handle null case
        if (getActiveBook().getUserView()==null) {
            getActiveBook().setUserView("metaedithtml");

        }
        //compare states and update view
        if (getActiveBook().getUserView().equals("metaedit")) {
            vertFrame = new VBox(0,visiblebox,bookLabelText,bookLabelTextArea,mdHeadingText,mdTextArea,filepathText,filepathBox,imagepathText,imagepathBox,urlText,urlpathBox,multiLineNotesText,codeNotesTextArea,hboxButtons);
            vertFrame.setPrefSize(winWidth,winHeight);
            setTitle(getTitleText(" - Meta Edit View"));
            widebox = new HBox(0,vertFrame);
            widebox.setPrefSize(winWidth,winHeight); 
            scenewidth=winWidth;
        }
        else if (getActiveBook().getUserView().equals("metaedithtml")) {
            vertFrame = new VBox(0,visiblebox,bookLabelText,bookLabelTextArea,mdHeadingText,mdTextArea,filepathText,filepathBox,imagepathText,imagepathBox,urlText,urlpathBox,multiLineNotesText,codeNotesTextArea,hboxButtons);
            setTitle(getTitleText(" - Full View"));
            //htmlEditor.setPrefSize(winWidth,winHeight);  //use BorderPanes for better resizing.
            vertFrame.setPrefSize(winWidth,winHeight);//both, with equal width to HTML
            widebox = new HBox(0,vertFrame,htmlEditor);
            widebox.setPrefSize(dblwidth,winHeight);
            scenewidth=dblwidth;
        }
        else if(getActiveBook().getUserView().equals("HTMLonly")) {
            //vertFrame = new VBox(0,this.codeNotesTextArea,hboxButtons); //lose buttons?
            //vertFrame.setPrefSize(dblwidth,winHeight);
            htmlEditor.setPrefSize(dblwidth,winHeight); //if you don't set this it will resize to widebox as needed.

            setTitle(getTitleText(" - HTML View"));
            widebox = new HBox(0,htmlEditor); //htmlEditor is itself a component for view
            //widebox.setPrefSize(dblwidth,winHeight); //whole dbl width devoted to htmlEditor
            scenewidth=dblwidth;
            //System.exit(0);
        }
        borderPane.setCenter(widebox);  
        Scene tempScene = new Scene (borderPane,scenewidth,winHeight); //default width x height (px)
        //add event handler for mouse released event
        tempScene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEnterEventHandler);
         //add event handler for mouse dragged  event
        tempScene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragEventHandler);

        //add event handler for mouse pressed event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
                 System.out.println("Mouse click on Meta Stage detected! " + mouseEvent.getSource());
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
                     System.out.println("scene display node :"+getActiveBook().toString());
                     System.out.println("notes String :"+getActiveBook().getNotes());
                     System.out.println("Notes: "+codeNotesTextArea.getText());
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

public void updateHTMLpreview(Book myBook){
    String newHTML=getHTMLfromContents(myBook);
    myBook.setHTML(newHTML);
    htmlEditor.setHtmlText(newHTML);
}

//Convert the MD section of current Book to some HTML and update the HTML parameter     
public String getHTMLfromContents(Book myBook) {
    String input = myBook.getMD();
    String label = myBook.getLabel();
    String logString="";
    //take out any existing headers?
    //String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
    int index =0; //
    //top row or heading
    if(index==0) {
        logString = "<html><head><title>"+label+"</title></head>"+"<body>";// use the label for the html page (if needed)
        //logString=logString+"<p><b>"+label+"</b></p>";
        logString=logString+"<H1><span style=\"font-family: Arial;\">"+label+"<span style=\"font-family: Arial;\"></H1>";
     }
     //iterate and create rest of file
    Scanner scanner1 = new Scanner(input);
    String prefix = "<p><span style=\"font-family: Arial;\">";
    String suffix="</span></p>";
    // filter md content for h2 or p
    String h2code="## ";
    String h2prefix="<H2><span style=\"font-family: Arial;\">";
    String h2suffix="<span style=\"font-family: Arial;\"></H2>";
    int mdFormatCode=0;
    String balanceString="";
     while (scanner1.hasNextLine()) {
        //just make paragraphs for now, unless h2
        String thisLine=scanner1.nextLine();
        int testlength=thisLine.length();
        if (testlength>3) {
            String testString=thisLine.substring(0,3);
            balanceString=thisLine.substring(3,testlength);
        //System.out.println(testString);
            if (testString.equals(h2code)) {
                mdFormatCode=2;
            }
        }  //end length if
        if (mdFormatCode==2) {
            logString=logString+h2prefix+balanceString+h2suffix;
            mdFormatCode=0; //reset
        }
        else {
             logString=logString+prefix+thisLine+suffix; //normal paragraph
        }
       
     } //end while
     String linkpath=myBook.getdocfilepath();
     if (linkpath.length()>0) {
         String linkprefix="<p><span style=\"font-family: Arial;\"><a href=\"";
         String linksuffix="\">Filelink</a></span></p>";
         String linkfile = linkprefix+linkpath+linksuffix;
         logString=logString+linkfile;
    }
    //embedded links will probably only open www addresses in the inbuilt JavaFX WebView.  You can open these from the Button in edit view.
    String urlpath=myBook.geturlpath();
     if (urlpath.length()>0) {
         String urlprefix="<p><span style=\"font-family: Arial;\"><a href=\"";
         String urlsuffix="\">weblink</a></span></p>";
         String urlfile = urlprefix+urlpath+urlsuffix;
         logString=logString+urlfile;
    }
    //link.  Can we show the image?
    String imagepath=myBook.getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it
     if (imagepath.length()>0) {
        File myFile = new File (imagepath);
        URI imageURI = myFile.toURI();
        System.out.println(imageURI);
        //<img src="img_girl.jpg" alt="Girl in a jacket" width="500" height="600">
         String imgprefix="<p><img src=\"";
         String imgsuffix="\" alt=\"user image\" width=\"600\"></p>";

         String imgfile = imgprefix+imageURI+imgsuffix;
         logString=logString+imgfile;
    }
    /*
    String imagepath=myBook.getimagefilepath();
     if (imagepath.length()>0) {
         String imgprefix="<p><span style=\"font-family: Arial;\"><a href=\"";
         String imgsuffix="\">imagelink</a></span></p>";
         String imgfile = imgprefix+imagepath+imgsuffix;
         logString=logString+imgfile;
    }
    */
    logString=logString+"</body></html>";
    System.out.println(logString);
    return logString;
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
            //Book currentSprite = ((Book)(t.getSource()));
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
            //Book currentSprite = ((Book)(t.getSource()));
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

public void closeThisStage() {
    //BookMetaStage.this.getParentStage().getStage().show();
           //this.myTrk.setCurrentFocus(BookMetaStage.this);
    getStage().close();
}

// INPUT / OUTPUT 

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

//chooseImageAction  
EventHandler<ActionEvent> chooseImageAction = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            BookMetaStage.this.mainImagepathLoader();
            }
        };

}