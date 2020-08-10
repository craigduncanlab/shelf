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
    StageManager ParentStageSM;//= new StageManager();
    Stage ParentStage;
    //Main Stage (Workspace window) that owns all other Stages
    StageManager Stage_WS;
    //Text Output windows (no edits)
    StageManager Stage_Output;
    
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Toolbar
    StageManager Stage_Toolbar;
    Stage toolbarStage = null;
    Group toolbarGroup = null;
    Scene toolbarScene = null;   
    //Clause editor
    StageManager Stage_EDITNODEPROP;
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
    StageManager OpenNodeStage;  
    ClauseContainer NodeTarget;
    //to hold Master Node for project i.e. data
    ClauseContainer masterNode = new ClauseContainer();
    //
    WhiteBoard mainWhiteBoard = new WhiteBoard();


//main launches from Application class
public static void main(String[] args) {
        launch(args);
  }

//-- Using instances of WordTool objects ---

private String getTextfromFile(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getFileAsString(fname);
}

private String getMostCommon(String fname) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromFile(fname);
}

private void printStatsfromFile(String fname) {
    WordTool myTool = new WordTool();
    myTool.printCountFromFile(fname);
}

private ClauseContainer grabDefinitionsFile(String fname) {
    WordTool myTool = new WordTool();
    String data = myTool.getFileAsString(fname);
    ClauseContainer defbox = myTool.doDefTextSearch(data);
    return defbox;
}

private ClauseContainer NodeFromDefinitionsSampleText(String mydata) {
    WordTool myTool = new WordTool();
    ClauseContainer defbox = myTool.doDefTextSearch(mydata);
    return defbox;
} 

//return a ClauseContainer object with clauses after using text document as input

private ClauseContainer NodeFromClausesSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    ClauseContainer clauseCarton = myTool.ClauseImport(mydata);
    return clauseCarton;
} 

/* Convert String into a Node with contained clauses */

private ClauseContainer NodeFromStatuteSampleText(String mydata) {
    WordTool myTool = new WordTool();
    //TO DO: add options for different clause extractions
    if (mydata==null) {
        System.out.println("Problem with sampling text for extraction");
    }
    ClauseContainer clauseCarton = myTool.StatuteSectionImport(mydata);
    return clauseCarton;
} 

//---EVENT HANDLER FUNCTIONS

private void toggleView(StageManager mySM) {
             
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
}     

private String getMatched(String data) {
    WordTool myTool = new WordTool();
    ClauseContainer defbox = myTool.doDefTextSearch(data);
    return defbox.getClauseAndText();
                
}

//used by event handler
private String getCommonWordsNow(String data) {
    WordTool myTool = new WordTool();
    return myTool.getCommonWordsFromString(data);
}

/* Make menuBar for workspace */

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
        
        
        //--- MENU NEW
        /*
        Menu menuNew = new Menu("New");
        MenuItem newNode = new MenuItem("Box");
        newNode.setOnAction(newNodeMaker);
        menuNew.getItems().addAll(newNode);
        */

        //--- MENU CONCEPTS
        Menu menuConcept = new Menu("Block");
        MenuItem newNode = new MenuItem("New Block");
        newNode.setOnAction(newNodeMaker);
        MenuItem conceptMove = new MenuItem("Move To Target");
        conceptMove.setOnAction(MoveBoxtoTarget);
        MenuItem conceptDelete = new MenuItem("Delete Selected");
        conceptDelete.setOnAction(deleteCurrentSprite);
        menuConcept.getItems().addAll(newNode,conceptMove,conceptDelete);

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
        Menu menuInputText = new Menu("Input Text");
        MenuItem FileOpen = new MenuItem("FileOpen");
        MenuItem WordCount = new MenuItem("WordCount");
        MenuItem InputFile = new MenuItem("InputFile");
        MenuItem GetDefText = new MenuItem("GetDefText");
        MenuItem GetDefs = new MenuItem("GetDefs");
        MenuItem GetClauses = new MenuItem("GetClauses");
        MenuItem GetSections = new MenuItem("GetSections");
        MenuItem NodeFromSelection = new MenuItem("Selection->ChildNode");

        MenuItem DictTempl = new MenuItem("DictionaryTemplate");
        MenuItem DictTemplCounts =  new MenuItem("DictionaryTemplateCounts");
        MenuItem AustliiCounts =  new MenuItem("AustliiCounts");
        MenuItem AustliiFirmCounts = new MenuItem("AustliiFirmCounts");
        
        WordCount.setOnAction(updateWordCounts); //argument is an EventHandler with ActionEvent object
        GetDefText.setOnAction(extractDefinitions);
        GetDefs.setOnAction(makeDefBoxesFromText);
        GetClauses.setOnAction(makeClauseBoxesFromText);
        GetSections.setOnAction(makeBoxesFromStatuteText);
        NodeFromSelection.setOnAction(makeSelectedChildNode);
        SaveName.setOnAction(saveDocName);
        SaveTempl.setOnAction(saveTemplate);
        OpenTempl.setOnAction(openTemplate);
        SaveAllTempl.setOnAction(saveAll);
        DictTempl.setOnAction(makeDictNode);
        DictTemplCounts.setOnAction(makeDictCountsNode);
        AustliiCounts.setOnAction(countAustliiDictionary);
        AustliiFirmCounts.setOnAction(countAustliiFirms);

        menuInputText.getItems().addAll(
            WordCount,GetDefText,GetDefs,GetClauses,GetSections,DictTempl,DictTemplCounts,AustliiCounts,AustliiFirmCounts,NodeFromSelection);
       
        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuFile, menuConcept, menuInputText, menuOutput);     

        //create an event filter so we can process mouse clicks on menubar (and ignore them!)
        menuBar.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
            System.out.println("MenuBar click detected! " + mouseEvent.getSource());
            //OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus after click
            System.out.println("MB Open Node, Viewer :"+OpenNodeStage.toString());
            System.out.println("MB Viewer :"+Stage_WS.getCurrentFocus());
            //mouseEvent.consume(); //consume this event - so menu works or not?
            //TO DO: proceed but ignore it for change of focus purposes?
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

    if (this.myTracker==null) {
            System.out.println("MyTRK is null move alert");
            System.exit(0);
        }
    this.myTracker.setActiveSprite(mySprite);
    }
 

//general method to store currentSprite

private void setCurrentSprite(SpriteBox mySprite) {
    if (this.myTracker==null) {
            System.out.println("MyTRK is null set current sprite");
            System.exit(0);
        }
    this.myTracker.setActiveSprite(mySprite);
}

private SpriteBox getCurrentSprite() {
    //return this.activeSprite;
    return myTracker.getCurrentSprite();  
}

/*
General method to place sprite on Stage.  Uses Stage Manager class 
Since data nodes are to mirror GUI, update parent child relations here too
27.4.18 - change approach so that it adds this node (rather than box) as sub-node to another node.
The node viewer will then be responsible for display of child nodes (e.g. boxes)
7.6.18 - used by 'copy sprite to destination'.  TO DO:  copy node, send to stage managers to handle.'
e.g. targetStage.OpenNewNodeNow? or targetStage.PlaceNodeNow...needs work
*/

private void placeSpriteOnStage(SpriteBox mySprite, StageManager targetStage) {
    
    setCurrentSprite(mySprite); 
    targetStage.addNewSpriteToStage(mySprite); 
    }

//This is a move not a copy.  

private void placeCurrentSpriteOnStage(StageManager targetStage) {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    deleteSpriteGUI(currentSprite);
    currentSprite.unsetParentNode(); //To DO: let node/viewer handle this.
    targetStage.addNewSpriteToStage(currentSprite);
}

//Set current selected Sprite's node as data link parent. 
//REDUNDANT.  WAS USED BY EVENT HANDLER 

private void setCurrentSpriteDataParent() {
    SpriteBox currentSprite = getCurrentSprite(); //not based on the button
    if (currentSprite !=null) {
        currentSprite.endAlert(); 
        System.out.println("Ended alert current:"+currentSprite.toString());
    }
    OpenNodeStage=Stage_WS.getCurrentFocus();
    System.out.println("Stage that is to have follower set:"+OpenNodeStage.toString());
    System.out.println("Source sprite for parent data:"+currentSprite.toString());
    OpenNodeStage.setFollow(currentSprite);
    if (OpenNodeStage.getDisplayNode().isFollower()==false) {
        System.out.println ("Error in setting follower status for:"+OpenNodeStage.toString());
    }
}

//Does this merely require copying the Data Node and calling node-based SM function?

public void copySpriteToDestination(SpriteBox mySprite, StageManager myStageMan) {
            
            System.out.println("Sprite to copy:"+mySprite.toString());
            SpriteBox b = mySprite.clone(); //clone event handlers or add?
            System.out.println("Sprite clone:"+b.toString());
            myStageMan.showStage();
            placeSpriteOnStage(b, myStageMan);  
}

public void copyCurrentSpriteToDestination(StageManager myStageMan) {
            
            SpriteBox mySprite = getCurrentSprite();
            copySpriteToDestination(mySprite,myStageMan); 
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
        
        ParentStageSM = new StageManager();
        ParentStage = new Stage();
        ParentStageSM.setStage(ParentStage);
        ParentStageSM.setTitle("Powerdock");

        //master Node for save all workspace
        masterNode.updateText("<html><body></body></html>","workspace","workspace(saved)","input","output");
        System.out.println("masterNode created.");
        //general application nodes
        NodeCategory NC_WS = new NodeCategory ("workspace",99,"white");
        //nodeCatList = makeLawWorldCategories(); <---optional, to restore NodeCats
        //
        MenuBar myMenu = makeMenuBar();
        this.myTracker = new SpriteTracker();
        if (this.myTracker==null) {
            System.out.println("MyTRK is null start application");
            System.exit(0);
        }
        Stage_WS = new StageManager(this.myTracker,"Workspace", NC_WS, masterNode, myMenu, PressBoxEventHandler, DragBoxEventHandler);  //sets up GUI for view
        
        if (this.Stage_WS==null) {
            System.out.println("Stage_WS is null start application");
            System.exit(0);
        }
        else {
            System.out.println("Stage_WS created.");
        }
        

        /* Setup a general text Output Stage (for workspace?) */
        Stage_Output = new StageManager(Stage_WS,"Output");
        Stage_Output.setupTextOutputWindow();

        //Temporary: demonstration nodes at start
        Stage_WS.setCurrentFocus(Stage_WS);
        OpenNodeStage = Stage_WS.getCurrentFocus();
    }

    /* Event handler added to box with clause content */

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

    /* 
    Method enables you to copy or move in these easy steps:
    (1) Click on a box to make it active (red).
    (2) Click to target stage (not on a box).
    (3) Select move to target {TO DO: Shortcut key}

    This works because the sprite with the red alert (current sprite) doesn't lose focus
    even when a click to a new stage (but not a box) changes the focus. 
    */

    EventHandler<ActionEvent> MoveBoxtoTarget = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            placeCurrentSpriteOnStage(OpenNodeStage); 
            }
    };

    EventHandler<ActionEvent> CopytoTarget = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
            OpenNodeStage = Stage_WS.getCurrentFocus();
            copyCurrentSpriteToDestination(OpenNodeStage);
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

    public void OpenRedNodeNow (SpriteBox currentSprite) { 
        if (this.myTracker==null) {
            System.out.println("MyTRK is null openrednodenow");
            System.exit(0);
        }

        OpenNodeStage = new StageManager(this.myTracker, Stage_WS, currentSprite, PressBoxEventHandler, DragBoxEventHandler); 

     }


    // --- EVENT HANDLERS

    // new spritebox on stage

    EventHandler<ActionEvent> newNodeMaker = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //create a new node
                NodeCategory NC_default = new NodeCategory("default",33,"darkblue");
                //find current node
                OpenNodeStage=Stage_WS.getCurrentFocus();
                ClauseContainer parentNode = OpenNodeStage.getDisplayNode();
                //new node with category and currentNode as parent
                ClauseContainer newDataNode = new ClauseContainer(NC_default,parentNode);
                
                //place a COPY (REF) of node in the relevant open node.  Testing...
                OpenNodeStage=Stage_WS.getCurrentFocus(); //update focus id.
                OpenNodeStage.OpenNewNodeNow(newDataNode,Stage_WS); // check they both update
                /* place a NEW object in the relevant open node... */
                //OpenNodeStage.OpenNewNodeNow(new ClauseContainer(myCat),Stage_WS);
                    System.out.println("Nodes ");
                    System.out.println("Context Node: "+OpenNodeStage.getDisplayNode().getChildNodes().toString());
            }
    };
        
     
    //printClauseList
        EventHandler<ActionEvent> printClauseList = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
             //textmakerTextArea.setText("This is where list of clauses will appear");
             Stage_WS.getDisplayNode().doPrintIteration();
             String output=Stage_WS.getDisplayNode().getClauseAndText();
             Stage_Output.setOutputText(output);
            }
        };

    //Make boxes for imported definitions

    EventHandler<ActionEvent> makeDefBoxesFromText = 
    new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            String sample = OpenNodeStage.getInputText();
            //TO DO: add node to openstage
            //To do: call this from inside StageManager instance
            ClauseContainer nodeSample = NodeFromDefinitionsSampleText(sample);
            //handle data node updates through the Stage object
            OpenNodeStage.addOpenNodeChildren(nodeSample);
        }
    };

     //Make boxes for imported clauses

    EventHandler<ActionEvent> makeClauseBoxesFromText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        
             //use the persistent Stage_WS instance to get the current stage (class variable)
             OpenNodeStage = Stage_WS.getCurrentFocus();
             String sample = OpenNodeStage.getInputText();
             ClauseContainer nodeSample = NodeFromClausesSampleText(sample);
             OpenNodeStage.addOpenNodeChildren(nodeSample);
        }
    };
    

     //Make boxes for imported statute clauses

    EventHandler<ActionEvent> makeBoxesFromStatuteText = 
    new EventHandler<ActionEvent>() {
        @Override 

        public void handle(ActionEvent event) {
        //TO DO: get source of data
        OpenNodeStage = Stage_WS.getCurrentFocus();
        String sample = OpenNodeStage.getInputText();
        if (sample.equals("")) {
            System.out.println("No text to sample");
        }
        ClauseContainer nodeSample = NodeFromStatuteSampleText(sample);
        OpenNodeStage.addOpenNodeChildren(nodeSample);
        }
    };
    

        //---- MORE EVENT HANDLERS ----

        EventHandler<ActionEvent> OpenInputFile = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Import button was pressed!");
            }
        };

        //update word counts
        EventHandler<ActionEvent> updateWordCounts = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Word Count Button was pressed!");

            //Update the import stage common words count text area
            String gotcha = Main.this.textArea1.getText();
            String newTA = Main.this.getCommonWordsNow(gotcha);
        }
    };
    
        /* Process the text in the input area of the current Node viewer 
        (whether saved or not)
        */

        EventHandler<ActionEvent> extractDefinitions = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            System.out.println("Get Defs Text Button was pressed!");
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            String gotcha = OpenNodeStage.getInputText();
            //System.out.println("Current input text:"+gotcha);
            String newDefs = Main.this.getMatched(gotcha);
            OpenNodeStage.setOutputText(newDefs); //output to current node viewer
            }
        };
        
        /*
        Method to take selected text and create a child node in open Node viewer with it
        Encapsulation:
        Since text selection works on the Open Stage, it is possible to call a public function on that object
        and make all functions private 
        */

        EventHandler<ActionEvent> makeSelectedChildNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            System.out.println("Make Node (From Selected Text) Button was pressed!");
            if (Stage_WS.getCurrentFocus()==OpenNodeStage) {
             System.out.println("Change of Viewer Focus OK in Main!");
             System.out.println("SCN Viewer :"+OpenNodeStage.toString());
             }
             else {
                System.out.println("Problem with change Viewer Focus");
                System.out.println("Current OpenNode :"+ OpenNodeStage);
                System.out.println("Stage WS Focus :"+ Stage_WS.getCurrentFocus());
             }
            OpenNodeStage.selectedAsChildNode();
            }
        };

       
        //to call function to make dictionary template as needed
        EventHandler<ActionEvent> makeDictNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer dictNode = myFS.getDictionaryTemplate();
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to call function to make dictionary template with counts as needed
        EventHandler<ActionEvent> makeDictCountsNode = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            String myfile = "1.html";
            ClauseContainer dictNode = myFS.getDictionaryWithCounts(myfile,"dictionary.txt");
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to call function to make dictionary template with counts as needed
        EventHandler<ActionEvent> getFirmCounts = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            String myfile = "1.html";
            ClauseContainer dictNode = myFS.getDictionaryWithCounts(myfile,"firms.txt");
            OpenNodeStage.OpenNewNodeNow(dictNode,Stage_WS);
            }
        };

        //to load a new template to workspace
        EventHandler<ActionEvent> openTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myLS = new LoadSave();
            myLS.makeLoad(Main.this.Stage_WS);

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
            ClauseContainer thisNode;
                    if (Main.this.masterNode!=null) {
                        myLS.makeSave(Main.this.Stage_WS,Main.this.masterNode);
                    }
                    else {
                       myLS.Close();
                    }
                }
            };

        //save  template
        EventHandler<ActionEvent> saveDocName = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            if (Main.this.getCurrentSprite()!=null) {
                ClauseContainer thisNode = Main.this.getCurrentSprite().getBoxNode();
                Main.this.saveDocTree(thisNode);
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

        //to call function to make an austlii folder (.html) node with word counts inside
        EventHandler<ActionEvent> countAustliiDictionary = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer austliiNode = myFS.getAustliiWithCounts("dictionary.txt");
            OpenNodeStage.OpenNewNodeNow(austliiNode,Stage_WS);
            }
        };

        EventHandler<ActionEvent> countAustliiFirms = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            OpenNodeStage = Stage_WS.getCurrentFocus();
            FileSearch myFS = new FileSearch();
            ClauseContainer austliiNode = myFS.getAustliiWithCounts("firms.txt");
            OpenNodeStage.OpenNewNodeNow(austliiNode,Stage_WS);
            }
        };
}