//(c) Craig Duncan 2017-2020 

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.geometry.Insets;
//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
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
//lines for joining
import javafx.scene.shape.*;
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
//Scene colour and Background Fills
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//ArrayList etc
import java.util.*;
//For serialization IO 
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
//File chooser
import javafx.stage.FileChooser;
//key events
import javafx.scene.input.KeyEvent;

public class Main extends Application {
    //setup instance variables here.  Static if shared across class (i.e. static=same memory location used)
    //instance variables for Screens to hold them if changed.
    Stage textStage = new Stage(); //basic constructor for main text stage
    
    TextArea textArea1 = new TextArea();
    TextArea textArea2 = new TextArea();
    TextArea textArea3 = new TextArea();
    TextArea textArea4 = new TextArea();
    String myTextFile="";
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //General managers
    SpriteManager mySpriteManager;
    ControlsManager myControlsManager = new ControlsManager();
    //
    BookMetaStage ParentStageSM;//= new BookMetaStage();
    Stage ParentStage;
    //Main Stage (Workspace window) that owns all other Stages
    MainStage Stage_WS;
    //Text Output windows (no edits)
    BookMetaStage Stage_Output;
    
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Toolbar
    BookMetaStage Stage_Toolbar;
    Stage toolbarStage = null;
    Group toolbarGroup = null;
    Scene toolbarScene = null;   
    //Clause editor
    BookMetaStage Stage_EDITNODEPROP;
    TextArea labelEdit;
    TextArea headingEdit;
    TextArea textEdit;
    TextArea categoryEdit;
    TextArea dateEdit;
    Clause editClause;
    Event editEvent;
    //Container editor
    TextArea docnameEdit;
    TextArea authorEdit;
    TextArea notesEdit;
    TextArea CCdateEdit;
    ClauseContainer myEditCC;
    //Group editGroup_root;
    Stage editorStage;
    Pane editGroup_root;
    //document loaded sequence
    int loaddocnum=0;
    int libdocnum=0;
    //move active sprite tracking to here from spritemanager class (redundant)
    SpriteBox activeSprite;
    SpriteTracker myTracker; // = new SpriteTracker();
    //STAGE IDS
    int location = 0;
    //Menus that need to be individually referenced/updated
    Menu theFileMenu;
    Menu theRecentMenu;
    Recents theRecent;
    Menu theViewMenu;
    Menu theNewNodeMenu;
    Menu theWorldsMenu;
    Menu theNotesMenu;
    Menu theProtocolMenu;
    Menu theEventsMenu;


    ArrayList<NodeCategory> nodeCatList;

    //To hold Stage with open node that is current
    BookMetaStage OpenNodeStage;  
    ClauseContainer NodeTarget;
    //to hold Master Node for project i.e. data
    ClauseContainer masterNode = new ClauseContainer();
    //
    WhiteBoard mainWhiteBoard = new WhiteBoard();


//main launches from Application class
public static void main(String[] args) {
        launch(args);
  }


//---EVENT HANDLER FUNCTIONS

private void toggleView(BookMetaStage mySM) {
             
    mySM.toggleStage();
    OpenNodeStage=mySM;
}

//General function for box clicks
private void processBoxClick(MouseEvent t) {

SpriteBox hadFocus=null;
SpriteBox currentSprite = (SpriteBox)t.getSource();  //selects a class for click source

int clickcount = t.getClickCount();

orgSceneX = t.getSceneX();
orgSceneY = t.getSceneY();

orgTranslateX = currentSprite.getTranslateX();
orgTranslateY = currentSprite.getTranslateY();
System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);

switch(clickcount) {
    //single click
    case 1:
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        System.out.println("One click");
        //change stage focus with just one click on spritebox (but node still closed)
        //refreshNodeViewScene();
        break;
    case 2:
        System.out.println("Two clicks");
        
        moveAlertFromBoxtoBox(getCurrentSprite(),currentSprite);
        
        //Dbl Click action options depending on box type
       
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

private MenuBar makeMenuBar() {
        
        //MENUBAR SETUP
        MenuBar menuBar = new MenuBar();
        // --- FILE MENU ---
        Menu menuFile = new Menu("File");
        //setFileMenu(menuFile);
        MenuItem OpenTempl = new MenuItem("Open MD document");
        MenuItem SaveName = new MenuItem("Save (selected)");
        MenuItem SaveTempl = new MenuItem("Save As (selected)");
        MenuItem SaveAllTempl = new MenuItem("Save All");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem PrintTree = new MenuItem("Print as HTML");
        PrintTree.setOnAction(writeHTML);
        
        //there is no Stage_WS defined at this point
        this.theRecentMenu = new Menu("Open Recent $");
        //refreshRecentMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            System.exit(0);
            }
        });
         menuFile.getItems().addAll(OpenTempl,this.theRecentMenu,SaveName,SaveTempl,SaveAllTempl,
            OutputWork,
            PrintTree,exit);
        
        //--- MENU CONCEPTS
        Menu menuConcept = new Menu("Block");
        MenuItem newNode = new MenuItem("New Block");
        newNode.setOnAction(newNodeMaker);
        MenuItem conceptDelete = new MenuItem("Delete Selected");
        conceptDelete.setOnAction(deleteCurrentSprite);
        menuConcept.getItems().addAll(newNode,conceptDelete);

        //--- OUTPUT MENU ---
        Menu menuOutput = new Menu("Output");
        MenuItem saveOutput = new MenuItem("Save");
        menuOutput.getItems().addAll(saveOutput);
        saveOutput.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
                
            System.out.println("Save Output selected!");
            EDOfileApp myfileApp = new EDOfileApp("output(PDock).txt");
            myfileApp.replaceText(Stage_Output.getOutputText());
            }
        });
        
         // --- TEXT MENU ---
        MenuItem FileOpen = new MenuItem("FileOpen");
        SaveName.setOnAction(saveDocName);
        SaveTempl.setOnAction(saveTemplate);
        OpenTempl.setOnAction(openTemplate);
       
        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuFile, menuConcept);     

        //create an event filter so we can process mouse clicks on menubar (and ignore them!)
        menuBar.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
            System.out.println("MenuBar click detected! " + mouseEvent.getSource());
            
            refreshRecentMenu();
             }
        });

        return menuBar;
}

private void refreshRecentMenu() {
    if (this.Stage_WS==null) {
        System.out.println("Stage WS is null refresh recent");
        System.exit(0);
    }
    Recents myRec = new Recents(this.Stage_WS, new LoadSave(this.Stage_WS));  //create a new object with the loadsave functions with workspace.
    ArrayList<String> latest = myRec.getList();
    if (latest!=null) {
        this.theRecentMenu.getItems().clear();
    }
    Iterator<String> myIterator = latest.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
        String filename = myIterator.next();
        MenuItem myMI = myRec.makeMenuItem(filename);
        System.out.println("menu item added:"+filename);
        this.theRecentMenu.getItems().add(myMI);
    }
}

/*
Method to end alert status for current sprite and reassign
Currently this looks at all Sprite Boxes globally (regardless of viewer/location)
*/
private void moveAlertFromBoxtoBox(SpriteBox hadFocus, SpriteBox mySprite) {

    if (Stage_WS.getActiveSprite()==null) {
            System.out.println("ActiveSprite is null move alert");
            System.exit(0);
        }
    Stage_WS.setActiveSprite(mySprite);
    }
 

//general method to store currentSprite

private void setCurrentSprite(SpriteBox mySprite) {
    if (Stage_WS.getActiveSprite()==null) {
            System.out.println("ActiveSprite is null set current sprite");
            System.exit(0);
        }
    Stage_WS.setActiveSprite(mySprite);
}

private SpriteBox getCurrentSprite() {
    //return this.activeSprite;
    return Stage_WS.getActiveSprite();  
}

/*
General method to place sprite on Stage.  Uses Stage Manager class 
Since data nodes are to mirror GUI, update parent child relations here too
27.4.18 - change approach so that it adds this node (rather than box) as sub-node to another node.
The node viewer will then be responsible for display of child nodes (e.g. boxes)
7.6.18 - used by 'copy sprite to destination'.  TO DO:  copy node, send to stage managers to handle.'
e.g. targetStage.OpenNewNodeNow? or targetStage.PlaceNodeNow...needs work
*/

private void placeSpriteOnStage(SpriteBox mySprite, BookMetaStage targetStage) {
    
    setCurrentSprite(mySprite); 
    targetStage.addNewSpriteToStage(mySprite); 
    }

//This is a move not a copy.  

private void placeCurrentSpriteOnStage(BookMetaStage targetStage) {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    deleteSpriteGUI(currentSprite);
    currentSprite.unsetParentNode(); //To DO: let node/viewer handle this.
    targetStage.addNewSpriteToStage(currentSprite);
}

/* Method to remove current SpriteBox and contents 
*/

public void deleteSpriteGUI(SpriteBox mySprite) {
    
    if (mySprite!=null) {
        mySprite.getStageLocation().removeSpriteFromStage(mySprite);
    }
    else
    {
        System.out.println("Error : no sprite selected to delete");
    }
}

// INPUT / OUTPUT
private void saveDocTree(ClauseContainer saveNode) {
    LoadSave myLS = new LoadSave();
    myLS.saveName(saveNode);
    myLS.Close();
    String filename=saveNode.getDocName();
    Recents myR = new Recents();
    myR.updateRecents(filename);
}

//STAGE METHODS

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
       
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("Powerdock App");
        primaryStage.hide();
        
        ParentStageSM = new BookMetaStage();
        ParentStage = new Stage();
        ParentStageSM.setStage(ParentStage);
        ParentStageSM.setTitle("Powerdock");

        //master Node for save all workspace
        //masterNode.updateText("<html><body></body></html>","workspace","workspace(saved)","input","output");
        System.out.println("masterNode created.");
        
        //nodeCatList = makeLawWorldCategories(); <---optional, to restore NodeCats
        //
        MenuBar myMenu = makeMenuBar();
        this.myTracker = new SpriteTracker();
        if (this.myTracker==null) {
            System.out.println("MyTRK is null start application");
            System.exit(0);
        }
        //The main Stage for Workspace.  
        Stage_WS = new MainStage("Workspace", myMenu);  //sets up GUI for view
        
        if (this.Stage_WS==null) {
            System.out.println("Stage_WS is null start application");
            System.exit(0);
        }
        else {
            System.out.println("Stage_WS created.");
        }
        

        /* Setup a general text Output Stage (for workspace?) 
        Stage_Output = new BookMetaStage(Stage_WS,"Output");
        Stage_Output.setupTextOutputWindow();
        */

        //Temporary: demonstration nodes at start
        //Stage_WS.setCurrentFocus(Stage_WS);
        //OpenNodeStage = Stage_WS.getCurrentFocus();
    }

    /* Event handler added to box with clause content 
    This is added to each stage created, so can be called from there*/

    EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            processBoxClick(t);
            t.consume();
        }
    };
    
     /* This is an eventhandler interface to create a new eventhandler class for the SpriteBox objects 
     These currently have no limits on how far you can drag 
     Handle release events in Stage Managers ?*/

    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            SpriteBox currentSprite = ((SpriteBox)(t.getSource()));
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            //end alert status for sprite
            SpriteBox hasFocus = getCurrentSprite();
            hasFocus.endAlert();
            //change the active sprite to the current touched sprite.
            setCurrentSprite(currentSprite); //clicked sprite
            System.out.println("The handler for drag box is acting");
            //update position
            currentSprite.setXY(newTranslateX,newTranslateY);
            System.out.println("Main: Translate Position (X,Y): "+newTranslateX+","+newTranslateY);
            //updates to sprite that triggered event
            currentSprite.setTranslateX(newTranslateX);
            currentSprite.setTranslateY(newTranslateY);
            currentSprite.doAlert(); //in case single click event doesn't detect
            t.consume();//check
        }
    };

    //BUTTON EVENT HANDLERS

    EventHandler<ActionEvent> deleteCurrentSprite = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
        
            deleteSpriteGUI(getCurrentSprite());
            }
        };

    EventHandler<ActionEvent> OpenNodeViewNow = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
        
        OpenRedNodeNow(getCurrentSprite());
        }
    };

    //Open a new stage in all cases (a kind of refresh)
    //This stage opens up as a sub-stage of the MainStage
    public void OpenRedNodeNow (SpriteBox currentSprite) { 
        if (this.myTracker==null) {
            System.out.println("MyTRK is null openrednodenow");
            System.exit(0);
        }
        ClauseContainer currentNode = currentSprite.getBoxNode();
        OpenNodeStage = new BookMetaStage(Stage_WS, currentNode, PressBoxEventHandler, DragBoxEventHandler); 

     }


    // --- EVENT HANDLERS

    // new spritebox on stage

    EventHandler<ActionEvent> newNodeMaker = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //create a new node
                //find current node
                ClauseContainer parentNode = OpenNodeStage.getDisplayNode();
                //new node with category and currentNode as parent
                ClauseContainer newDataNode = new ClauseContainer(parentNode);
                
                //place a COPY (REF) of node in the relevant open node.  Testing...
                Stage_WS.OpenNewNodeNow(newDataNode); // check they both update
                /* place a NEW object in the relevant open node... */
                //OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
                    System.out.println("Nodes ");
                    System.out.println("Context Node: "+OpenNodeStage.getDisplayNode().getChildNodes().toString());
            }
    };
        
        //to load a new template to workspace (e.g. from markdown)
        EventHandler<ActionEvent> openTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave(Stage_WS);
            //myLS.makeLoad(Main.this.Stage_WS);
            myLS.makeLoad(); //not attached to stage?

            if (Main.this.Stage_WS==null) {
                System.out.println("Problem with passing Stage_WS to openTemplate");
            }
            }
        };

        //save all (i.e. workspace etc)
        EventHandler<ActionEvent> saveAll = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            /*ClauseContainer thisNode;
                    if (Main.this.masterNode!=null) {
                        myLS.makeSave(Main.this.Stage_WS,Main.this.masterNode);
                    }
                    else {
                       myLS.Close();
                    }
                
                */
                }
            };

        //save  template
        EventHandler<ActionEvent> saveDocName = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            if (Main.this.getCurrentSprite()!=null) {
                ClauseContainer thisNode = Main.this.getCurrentSprite().getBoxNode();
                //Main.this.saveDocTree(thisNode);
            }
            //use the persistent Stage_WS instance to get the current stage (class variable)
            /*
            LoadSave myLS = new LoadSave();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        myLS.saveName(thisNode);
                        //update recent docs list
                        String filename=thisNode.getDocName();
                        Recents myR = new Recents();
                        myR.updateRecents(filename);
                    }
                    else {
                       myLS.Close();
                    }
                    */
                }
            }; 

         //save As template
        EventHandler<ActionEvent> saveTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        myLS.makeSave(Main.this.Stage_WS,thisNode);
                        //update recent docs list
                        String filename=thisNode.getDocName();
                        Recents myR = new Recents();
                        myR.updateRecents(filename);
                    }
                    else {
                       myLS.Close();
                    }
                }
            }; 

        //write out html content from this node tree
        //save template
        EventHandler<ActionEvent> writeHTML = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            makeHTML mh = new makeHTML();
            ClauseContainer thisNode;
                    if (Main.this.getCurrentSprite()!=null) {
                        thisNode = Main.this.getCurrentSprite().getBoxNode();
                        mh.HTMLoutput(thisNode,thisNode.getDocName());
                    }
                    else {
                       //mh.Close();
                    }
                }
            };
}