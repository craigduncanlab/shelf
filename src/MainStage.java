//(c) Craig Duncan 2017-2020

//import utilities needed for Arrays lists etc
import java.util.*;
//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
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
SpriteBox focusSprite; //for holding active sprite in this scene.  Pass to app.
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
TextArea docNameTextArea = new TextArea();
TextArea mdTextArea = new TextArea();
TextArea headingTextArea = new TextArea();
TextArea inputTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
Text parentBoxText;
Text headingBoxText;
Text inputBoxText;
Text visibleBlockText;
Text mdHeadingText;
//Store the common event handlers here for use
EventHandler<MouseEvent> PressBox;
//EventHandler<MouseEvent> DragBox;
EventHandler<KeyEvent> KeyEventHandler;
//MenuBar
MenuBar localmenubar;
//html editor
 final HTMLEditor htmlEditor = new HTMLEditor();
//visibility checkbox
CheckBox visibleCheck = new CheckBox("Visible");
 //To hold Stage with open node that is current
BookMetaStage OpenNodeStage; 
Integer shelf1_Y;
Integer shelfgap;
Integer shelfthickness=15;
/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
BookMetaStage should store GUI objects in one way, data in another?  separation of concerns
Some kind of content manager for each stage?
Consider if subclasses of BookMetaStage could deal with flavours of BookMetaStage (e.g. position?
*/
ArrayList<Object> BoxContentsArray = new ArrayList<Object>(); //generic store of contents of boxes
double orgSceneX;
double orgSceneY;

double orgTranslateX;
double orgTranslateY;

//Track current stage that is open.  Class variables
static BookMetaStage currentFocus; //any BookMetaStage can set this to itself
static ClauseContainer currentTarget; //any Box(no?) or BookMetaStage can set this to its display node

//constructor
public MainStage() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//workspace constructor.  Filename details will be inherited from loaded node.
//Passes MenuBar from main application for now
//Passes general eventhandlers from Main (at present, also uses these for the boxes)
public MainStage(String title, MenuBar myMenu) {
    //ClauseContainer baseNode, 
    //category
    //NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
    //view
    setTitle(title);
    setMenuBar(myMenu);
    //TO DO: setLocalSpriteSelect(processLocalBoxClick);
    //set event handlers as local instance variables.  These are used at time of SpriteBox creation
    //They are set here so that the spriteboxes can access BookMetaStage level data
    //See the 'addNodeToView' function that creates a new SpriteBox here.
    setPressBox(processLocalBoxClick); //stores this in a variable to pass to SpriteBox.
    //setPressBox(PressBox);
    setDragBox(DragBox); //stores this to pass to SpriteBox 
    setKeyPressSprite(SpriteKeyHandler); //TO do - set a variable to pass to sprites=
    
    //we need to set sprite group etc

    newWorkstageFromGroup();
    System.out.println ("The initial spritegroup...");
    System.out.println (this.spriteGroup);
    System.out.println("Reference of this stage object MainStage");
    System.out.println(MainStage.this);
    resetSpriteOrigin();
    //This is for the 'clausecontainer node?'
    //setWSNode(baseNode); 
    //data
    //ClauseContainer WorkspaceNode = ;
    //setWSNode(new ClauseContainer(myCategory,"The workspace is base node of project.","myWorkspace")); //data
}


public void setTargetByViewer(BookMetaStage mySM) {
    currentTarget = mySM.getDisplayNode();
}

/* TENTATIVE - NOT USED? */
public void setTargetByBox(BookMetaStage mySM) {
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
private void setKeyPressSprite(EventHandler<KeyEvent> myKE) {
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
This method sets the node that is used for the display in this stage.
All other nodes added to this node are considered child nodes of this node:
they are added as child nodes to the data node; they are display in the section of the stage for this

*/

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

/*
//Method to update workspace appearance based on current node setting (usually root of project)
public void setWSNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    Group newGroup = new Group(); //new GUI node to show only new content.
    swapSpriteGroup(newGroup); //store the new GUI node for later use
    //resetSpriteorigin(); //not needed as in displayConceptSection
    //displayConceptSection(); //update WS view with new child boxes only
}
*/

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
private void setJavaFXStageParent(BookMetaStage ParentSM) {
    Stage myStage = getStage(); 
    Stage Parent = ParentSM.getStage();
    myStage.initOwner(Parent);
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
public void setInitStage(BookMetaStage myParentSM, Stage myStage, Group myGroup, String myTitle) {
   setStageName(myTitle);
   setStage(myStage);
   setJavaFXStageParent(myParentSM);
   setEditWindowPosition();
   setSpriteGroup(myGroup);
   setTitle(myTitle);
}

*/

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


//Method to change title depending on data mode the node is in.
private String getTitleText(String myString) {
    System.out.println("Make Scene. User Node View: "+getDisplayNode().getUserView());
    //main function        
    return getDisplayNode().getDocName()+myString;
   
}

/* New Local mouse event handler */
 EventHandler<KeyEvent> SpriteKeyHandler = new EventHandler<KeyEvent>() {
    @Override
    public void handle(KeyEvent ke) {
        //SpriteBox hasFocus = Main.this.getCurrentSprite();
        SpriteBox hadFocus=null;
        SpriteBox currentSprite = MainStage.this.getActiveSprite(); //clicksource
            if (ke.isMetaDown() && ke.getCode().getName().equals("E")) {
                 System.out.println("CMD-E pressed for sprite");
                 currentSprite.cycleBookColour();
            }
        }
    };

//This event handler is sent to the Sprites, but it can access variables from this object
EventHandler<MouseEvent> DragBox = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {

            SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            final EventType TYPE = t.getEventType(); //
            System.out.println("Event");
            System.out.println(TYPE.toString());
              
            if (currentSprite!=null) {
                MainStage.this.setActiveSprite(currentSprite); //clicked sprite
                double offsetX = t.getSceneX() - orgSceneX;
                double offsetY = t.getSceneY() - orgSceneY;
                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;
                //release situation
                if (t.MOUSE_RELEASED == TYPE) { //DRAG_
                    System.out.println("Mouse released");
                    //System.out.println("shelf 1 Y"+MainStage.this.shelf1_Y);
                    System.out.println("release position: x "+newTranslateX+" y "+newTranslateY);
                    //shelf parameters
                    Integer shelf1 = 200;
                    Integer shelf2=2*shelf1;
                    Integer shelf3=3*shelf2;
                    Integer bookiconheight=150;
                    Integer offset1=20;
                    Integer overshoot=20;
                    Integer offset2=offset1+200;
                    Integer offset3=offset2+200;
                    Integer offset4=offset3+200;
                    //if release points don't fit shelf range, simulate 'gravity' to shelf below
                    if (newTranslateY<=offset1+overshoot) {
                        System.out.println("Put on shelf 1");
                         newTranslateY=offset1;
                    }
                    else if (newTranslateY>offset1+overshoot && newTranslateY<=offset2) {
                        System.out.println("Put on shelf 2");
                         newTranslateY=offset2;
                    }
                    else if (newTranslateY>offset2+overshoot && newTranslateY<=offset3) {
                        System.out.println("Put on shelf 3");
                        newTranslateY=offset3;
                    }
                    else if (newTranslateY>offset3+overshoot) {
                        System.out.println("Put on shelf 4");
                        newTranslateY=offset4;
                    }
                    
                    //System.exit(0);
                    currentSprite.setTranslateX(newTranslateX);
                    currentSprite.setTranslateY(newTranslateY);
                    currentSprite.doAlert();
                    } 
                else {
                    System.out.println("The handler for drag box is acting");
                    //update position
                    currentSprite.setXY(newTranslateX,newTranslateY);
                    System.out.println("Main: Translate Position (X,Y): "+newTranslateX+","+newTranslateY);
                    //updates to sprite that triggered event
                    currentSprite.setTranslateX(newTranslateX);
                    currentSprite.setTranslateY(newTranslateY);
                    currentSprite.doAlert(); //in case single click event doesn't detect
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
        SpriteBox hadFocus=MainStage.this.getActiveSprite();
        SpriteBox currentSprite = (SpriteBox)t.getSource();  //selects a class for click source
        if (currentSprite!=null) {
            System.out.println("Found click on sprite");
            MainStage.this.setActiveSprite(currentSprite);
            //moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
            int clickcount = t.getClickCount();

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            orgTranslateX = currentSprite.getTranslateX();
            orgTranslateY = currentSprite.getTranslateY();
            System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);

            switch(clickcount) {
            //single click
            case 1:
                //moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
                System.out.println("One click");
                //change stage focus with just one click on spritebox (but node still closed)
                //OpenNodeStage=currentSprite.getStageLocation();
                //refreshNodeViewScene();
                break;
            case 2:
                System.out.println("Two clicks");
                
                //moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
                
                //Dbl Click action options depending on box type
               
                //OpenNodeStage=currentSprite.getStageLocation();
                //only open if not already open (TO DO: reset when all children closed)
                //prevent closing until all children closed
                //close all children when node closed.
                OpenRedNodeNow(currentSprite);
                
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


private void OpenRedNodeNow(SpriteBox currentSprite) {
     ClauseContainer currentNode = currentSprite.getBoxNode();
     OpenNodeStage = new BookMetaStage(MainStage.this, currentNode, PressBox, DragBox); 

}
/*switch(clickcount) {
    //single click
    case 1:
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        System.out.println("One click");
        //change stage focus with just one click on spritebox (but node still closed)
        OpenNodeStage=currentSprite.getStageLocation();
        //refreshNodeViewScene();
        break;
    case 2:
        System.out.println("Two clicks");
        
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        
        //Dbl Click action options depending on box type
       
        OpenNodeStage=currentSprite.getStageLocation();
        //only open if not already open (TO DO: reset when all children closed)
        //prevent closing until all children closed
        //close all children when node closed.
        OpenRedNodeNow(currentSprite);
        
        break;
    case 3:
        System.out.println("Three clicks");
        break;
}
*/


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


private void closeThisStage() {
    //BookMetaStage.this.getParentStage().getStage().show();
           //this.myTrk.setCurrentFocus(BookMetaStage.this);
    getStage().close();
}

//general function to save GUI text data into node (position data for concept boxes is already updated)
//also performs a save on shared parent.
//TO DO: remove and just save each text box content to an SQL database.
private void saveNodeText() {
    //getDisplayNode().updateText(htmlEditor.getHtmlText(),docNameTextArea.getText(),headingTextArea.getText(),inputTextArea.getText(),outputTextArea.getText());
    
    getDisplayNode().updateMDText(headingTextArea.getText(),mdTextArea.getText(),inputTextArea.getText());
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
        pntBox.setLabel(docNameTextArea.getText());
    }
    //error checking - log.  Leave this to show error for attempts with follower nodes.
    if (getDisplayNode().getNotes().equals(inputTextArea.getText())) {
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

//MainStage setup function
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
        //This is empty initially...
        Group displayAreaGroup = new Group(); //subgroup of Pane; where Sprites located
        
        workspacePane.getChildren().addAll(displayAreaGroup);
        setSpritePane(workspacePane); //store for later use
        setSpriteGroup(displayAreaGroup); //store for later use

        myBP.setTop(menubarGroup);
       //myBP.setMargin(workspacePane, new Insets(50,50,50,50)); //i.e. Y=-50='translateX=0'
        myBP.setMargin(workspacePane, new Insets(0,0,0,0));
        myBP.setCenter(workspacePane);
        //workspacePane.setPadding(new Insets(150,150,150,150));
        //Add some shelves
        //rectangle
        Integer offset = 0;
        this.shelf1_Y=200;
        Rectangle shelf1 = makeNewShelf(100,this.shelf1_Y+offset); 
        Rectangle shelf2 = makeNewShelf(100,2*this.shelf1_Y+offset); 
        Rectangle shelf3 = makeNewShelf(100,3*this.shelf1_Y+offset);
        Rectangle shelf4 = makeNewShelf(100,4*this.shelf1_Y+offset); 

        //setArcWidth(60);  //do this enough you get a circle.  option
        //setArcHeight(60);                
       
       //Color myColour = Color.BLACK;
        //this.boxcolour=mycol;//not updated yet?
        //Creating a line object
        Line line = new Line(); 
        //line thickness?
        //Setting the properties to a line 
        Integer lm=100;
        line.setStartX(250.0+lm); 
        line.setStartY(50.0); 
        line.setEndX(250.0+lm); 
        line.setEndY(800.0); 
        Line line2 = new Line(); 
        //line thickness?
        //Setting the properties to a line 
        line2.setStartX(750.0+lm); 
        line2.setStartY(50.0); 
        line2.setEndX(750.0+lm); 
        line2.setEndY(800.0); 
        
        //add the Border Pane and branches to root Group 
        myGroup_root.getChildren().addAll(myBP);
        //putting lines first means they appear at back
        myGroup_root.getChildren().addAll(line,line2,shelf1,shelf2,shelf3,shelf4); //line and shelf 1
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
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), Color.LIGHTGREY);

        
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
            JavaFX has ability to detect Text, Rectangle, ColBox (all components of a SpriteBox)
            Better to force it to detect a SpriteBox?
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
             public void handle(KeyEvent keyEvent) {
             System.out.println("Key pressed on workspace stage " + keyEvent.getSource());
             //if source = ... only then change focus 
            }
        });
        
        return workspaceScene;
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

/*
Internal method to add sprite to the Group/Pane of this Node Viewer 
This is to add an existing GUI 'box/node' to the Child Node section of this Viewer.
i.e. this adds a specific object, rather than updating the view from whole underlying data set.
*/

private void addSpriteToStage(SpriteBox mySprite) {
    if (mySprite==null) {
        System.out.println("No sprite to add");
        System.exit(0);
    }
    System.out.println("Sprite in addSprite, before addition");
    System.out.println(mySprite);
    System.out.println("SpriteGroup in addSprite, before addition");
    System.out.println(this.spriteGroup);
    //System.exit(0);
    //getSpriteGroup().getChildren().add(mySprite); //GUI tree 
    this.spriteGroup.getChildren().add(mySprite);
    System.out.println("SpriteGroup in addSprite, after addition");
    System.out.println(this.spriteGroup);
    
    // TO DO: general function to draw lines for all links in visible boxes
    /*
    //temporary line to test a line from box coord to diag coord
    double x = mySprite.getX();
    double y = mySprite.getY();
    double sblength=150;
    double sbheight=30;
    double sp2x=x+200;
    double sp2y=y+200;
    System.out.println("Adding line at:"+x+","+y);
    Line line = new Line(x+sblength,y+(sbheight/2),sp2x,sp2y+(sbheight/2));
    line.setStroke(Color.RED);
    getSpriteGroup().getChildren().add(line);
    */
    System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    positionSpriteOnStage(mySprite);
    advanceSpritePositionHor(); //default is to space along shelf
    setActiveSprite(mySprite); //local information
    mySprite.setStageLocation(MainStage.this); //give Sprite the object for use later.
}

//set active sprite.  if problem with tracker, ignore.
public void setActiveSprite(SpriteBox b) {
    if (this.focusSprite!=null) {
        SpriteBox hadFocus = this.focusSprite;
        hadFocus.endAlert();
    }
    this.focusSprite=b;
    this.focusSprite.doAlert();
}

//set active sprite.  if problem with tracker, ignore.
public SpriteBox getActiveSprite() {
    if (this.focusSprite==null) {
        System.out.println("No sprite in active function");
        System.exit(0);
    }
    return this.focusSprite;
}

//method to box up node as shape and add to GUI in node viewer

private void addNodeToView (ClauseContainer myNode) {
    //SpriteBox b = makeBoxWithNode(myNode); //relies on Main, event handlers x
    SpriteBox b = new SpriteBox(PressBox,DragBox,myNode);
    addSpriteToStage(b); //differs from Main 
    if (b==null) {
        System.out.println("SpriteBox null in addnodetoview");
        System.exit(0);
    }
    setActiveSprite(b);
    System.out.println("SpriteGroup in addnodetoview");
    System.out.println(this.spriteGroup);
   
}


//General method to add AND open a node if not on ws; otherwise place on workspace
//The BookMetaStage arg passed in as myWS should be 'Stage_WS' for all calls 
//Called by LoadSave and iterates through the nodes in the parsed MD file.
public void OpenNewNodeNow(ClauseContainer newNode) {
    System.out.println("SpriteGroup in OpenNewNodeNow");
    System.out.println(this.spriteGroup);
    //System.exit(0);
    System.out.println("OpenNewNode now...");
    newNodeForWorkspace(newNode); //to do : make this open up as child node in whiteboard.  i.e. (1) change whiteboard focus to 'master', (2) add as child box.
}

/* This method adds a single node to workspace without refreshing entire view */

private void newNodeForWorkspace(ClauseContainer myNode) {
    addChildNodeToDisplayNode(myNode); //data
    System.out.println("SpriteGroup in newNodeForWorkspace");
    System.out.println(this.spriteGroup);
    
    addNodeToView(myNode); //view
}

//Method to add a single child node to the open node in this view and update parent node (data)

private void addChildNodeToDisplayNode(ClauseContainer myChildNode) {
    getDisplayNode().addChildNode(myChildNode);
    myChildNode.setParentNode(getDisplayNode());
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
    if (mySprite.getX()!=0 || mySprite.getY()!=0) {
        double x = mySprite.getX();
        double y = mySprite.getY();
        mySprite.setTranslateX(x);
        mySprite.setTranslateY(y);
    }
    else {
        mySprite.setTranslateX(spriteX);
        mySprite.setTranslateY(spriteY); 
    } 
    mySprite.setStageLocation(MainStage.this); //needed if stage is not o/w tracked
    if (mySprite.getStageLocation()!=MainStage.this) {
        System.out.println("Problem with adding sprite:"+mySprite.toString());
    }
    else {
        System.out.println("Positioned sprite at:"+mySprite.toString()+" ("+spriteX+","+spriteY+")");
    }
}

public void resetSpriteOrigin() {
    this.spriteY=20;
    this.spriteX=100;
}

//new 'shelf'??
private void newSpriteColum() {
    this.spriteY=this.spriteY+this.shelfgap;
    this.spriteX=100;
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
//Layout horizontall on one shelf.
private void advanceSpritePositionHor() {
        if (this.spriteX>880) {
                this.spriteY=spriteY+this.shelfgap; //drop down
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
                this.spriteY=spriteY+this.shelfgap;
                this.spriteX=0;
            }
            else {
                this.spriteY = this.spriteY+this.shelfgap;
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