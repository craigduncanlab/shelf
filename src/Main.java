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
import java.io.*;
import java.io.File;

//File chooser
import javafx.stage.FileChooser;
//key events
import javafx.scene.input.KeyEvent;
//Desktop etc and file chooser
import java.awt.Desktop;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    //SpriteManager myBookManager;
    ControlsManager myControlsManager = new ControlsManager();
    //
    //BookMetaStage bookshelfInstance;//= new BookMetaStage();
    Stage ParentStage;
    //Main Stage (Workspace window) that owns all other Stages
    MainStage Stage_WS;
    //Text Output windows (no edits)
    //BookMetaStage Stage_Output;
    
    //Extracted Definitions window (text)
    Stage defsTextStage;
    ScrollPane defsTextStage_root;
    //Toolbar
    //BookMetaStage Stage_Toolbar;
    Stage toolbarStage = null;
    Group toolbarGroup = null;
    Scene toolbarScene = null;   
    //Clause editor
    //BookMetaStage Stage_EDITNODEPROP;
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
    Book myEditCC;
    //Group editGroup_root;
    Stage editorStage;
    Pane editGroup_root;
    //document loaded sequence
    int loaddocnum=0;
    int libdocnum=0;
    //move active sprite tracking to here from spritemanager class (redundant)
    Book activeSprite;
    SpriteTracker myTracker; // = new SpriteTracker();
    //STAGE IDS
    int location = 0;
    //Menus that need to be individually referenced/updated
    Menu theRecentMenu;
    Recents theRecent;

    ArrayList<NodeCategory> nodeCatList;

    //To hold Stage with open node that is current
    BookMetaStage bookshelfInspectorStage;  
    Book NodeTarget;
    //to hold Master Node for project i.e. data
    Book masterNode = new Book();
    //
    WhiteBoard mainWhiteBoard = new WhiteBoard();
    //File input/output
    File currentOpenFile;


//main launches from Application class
public static void main(String[] args) {
        launch(args);
  }


//---EVENT HANDLER FUNCTIONS

//TO DO: close old Inspector stage and open a new one.
private void toggleView(BookMetaStage mySM) {
             
    mySM.toggleStage();
    bookshelfInspectorStage=mySM;
}

//General function for box clicks
/*
private void processBoxClick(MouseEvent t) {

Book hadFocus=null;
Book currentSprite = (Book)t.getSource();  //selects a class for click source

int clickcount = t.getClickCount();

orgSceneX = t.getSceneX();
orgSceneY = t.getSceneY();

orgTranslateX = currentSprite.getTranslateX();
orgTranslateY = currentSprite.getTranslateY();
System.out.println("getx: "+ orgSceneX+ " gety: "+orgSceneY);

switch(clickcount) {
    //single click
    case 1:
        moveAlertFromBooktoBook(getActiveBook(),currentSprite);
        System.out.println("One click");
        //change stage focus with just one click on Book (but node still closed)
        //refreshNodeViewScene();
        break;
    case 2:
        System.out.println("Two clicks");
        System.exit(0);
        moveAlertFromBooktoBook(getActiveBook(),currentSprite);
        
        //Dbl Click action options depending on box type
       
        //only open if not already open (TO DO: reset when all children closed)
        //prevent closing until all children closed
        //close all children when node closed.
        OpenRedNodeNow(currentSprite);
        System.exit(0);
        
        break;
    case 3:
        System.out.println("Three clicks");
        break;
}
}     
*/
private MenuBar makeMenuBar() {
        
        //MENUBAR SETUP
        MenuBar menuBar = new MenuBar();
        // --- FILE MENU ---
        Menu menuFile = new Menu("File");
        //setFileMenu(menuFile);
        MenuItem OpenTempl = new MenuItem("Open MD document");
        MenuItem SaveName = new MenuItem("Save (current bookshelf)");
        MenuItem SaveTempl = new MenuItem("Save As (selected)");
        MenuItem ClearBookshelfMenuItem = new MenuItem("Clear Bookshelf");
        MenuItem OutputWork = new MenuItem("Output as Text");
        MenuItem PrintTree = new MenuItem("Print as HTML");
        //PrintTree.setOnAction(writeHTML);
        
        //there is no Stage_WS defined at this point
        this.theRecentMenu = new Menu("Open Recent $");
        //refreshRecentMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            System.exit(0);
            }
        });
         menuFile.getItems().addAll(OpenTempl,this.theRecentMenu,SaveName,SaveTempl,ClearBookshelfMenuItem,
            OutputWork,
            PrintTree,exit);
        
        //--- MENU CONCEPTS
        Menu menuBooks = new Menu("Books");
        MenuItem addNewBook = new MenuItem("New Book");
        addNewBook.setOnAction(addNewBookMaker);
        MenuItem bookDeleteMenuItem = new MenuItem("Delete Selected");
        bookDeleteMenuItem.setOnAction(deleteSelectedBook);
        menuBooks.getItems().addAll(addNewBook,bookDeleteMenuItem);
        
         // --- TEXT MENU ---
        MenuItem FileOpen = new MenuItem("FileOpen");
        SaveName.setOnAction(saveTemplate);
        SaveTempl.setOnAction(saveTemplate); //docname
        OpenTempl.setOnAction(openTemplate);
        ClearBookshelfMenuItem.setOnAction(clearBookShelf);
       
        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuFile, menuBooks);     

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

//clearBooksFromShelf
private void clearBooksFromShelf() {
    //clear filepath too, to prevent saving over?
    this.currentOpenFile = new File ("default.md");
    Stage_WS.clearAllBooks();
    Stage_WS.resetBookOrigin();
}

/*
Method to end alert status for current sprite and reassign
Currently this looks at all Sprite Boxes globally (regardless of viewer/location)
*/
private void moveAlertFromBooktoBook(Book hadFocus, Book myBook) {

    if (Stage_WS.getActiveBook()==null) {
            System.out.println("ActiveSprite is null move alert");
            System.exit(0);
        }
    Stage_WS.setActiveBook(myBook);
    }
 

//general method to store currentSprite

private void setActiveBook(Book myBook) {
    if (Stage_WS.getActiveBook()==null) {
            System.out.println("ActiveSprite is null set current sprite");
            System.exit(0);
        }
    Stage_WS.setActiveBook(myBook);
}

private Book getActiveBook() {
    //return this.activeSprite;
    return Stage_WS.getActiveBook();  
}

/* Method to remove current Book and contents 
*/

public void deleteSpriteGUI(Book myBook) {
    
    if (myBook!=null) {
        Stage_WS.removeBookFromStage(myBook);
    }
    else
    {
        System.out.println("Error : no sprite selected to delete");
    }
}

//STAGE METHODS

/* ---- JAVAFX APPLICATION STARTS HERE --- */
  
    @Override
    public void start(Stage primaryStage) {
       
        /* This only affects the primary stage set by the application */
        primaryStage.setTitle("Powerdock App");
        primaryStage.hide();
        
        ParentStage = new Stage();

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
    }

    //BUTTON EVENT HANDLERS

    EventHandler<ActionEvent> deleteSelectedBook = 
        new EventHandler<ActionEvent>() {
 
        @Override
        public void handle(ActionEvent t) {
        
            deleteSpriteGUI(getActiveBook());
            }
        };

    //FILE LOADERS AND SAVERS
    public void mainFileLoader() {
         final FileChooser fileChooser = new FileChooser();
        Stage myStage = new Stage();
        myStage.setTitle("Open File");
        File file = fileChooser.showOpenDialog(myStage);
        if (file != null) {
          this.currentOpenFile = file; //store for later
          Main.this.Stage_WS.processMarkdown(file);
        } 
    }

    //if we have a list of the Book objects inside the Bookes, and they have the relevant metadata (including x,y),
    //Then we do not need to actually query the Book class - we can just save as per clause container
    public void mainFileSaver() {
        Main.this.Stage_WS.writeFileOut(this.currentOpenFile.getPath());
    }

    // --- EVENT HANDLERS

    // new Book on stage

    EventHandler<ActionEvent> addNewBookMaker = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Stage_WS.addNewBookToView(); // check they both update
            }
    };
        
        //to load a new template to workspace (e.g. from markdown)
        EventHandler<ActionEvent> openTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.mainFileLoader();
           
            }
        };

        //to load a new template to workspace (e.g. from markdown)
        EventHandler<ActionEvent> clearBookShelf = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.clearBooksFromShelf();
           
            }
        };

        //to load a new filepath
        EventHandler<ActionEvent> getFilepath = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //use the persistent Stage_WS instance to get the current stage (class variable)
            LoadSave myUtil = new LoadSave(Main.this.Stage_WS);
            //myLS.makeLoad(Main.this.Stage_WS);
            myUtil.makeChooser(); //not attached to stage?

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
                }
            };

        //save  template
        EventHandler<ActionEvent> saveDocName = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            if (Main.this.getActiveBook()!=null) {
                Book thisNode = Main.this.getActiveBook();
                //Main.this.saveDocTree(thisNode);
            }
                }
            }; 

         //save As template
        EventHandler<ActionEvent> saveTemplate = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.mainFileSaver();
                }
            }; 

}