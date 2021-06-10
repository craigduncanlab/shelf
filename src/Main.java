//(c) Craig Duncan 2017-2020 
//www.craigduncan.com.au

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
import javafx.stage.FileChooser.ExtensionFilter;
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
    //Main Stage (Workspace window) that owns all other Stages
    MainStage Stage_WS;
    //Text Output windows (no edits)
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
    //Container editor
    TextArea docnameEdit;
    TextArea authorEdit;
    TextArea notesEdit;
    TextArea CCdateEdit;
    
    //Group editGroup_root;
    Stage editorStage;
    Pane editGroup_root;
    //document loaded sequence
    int loaddocnum=0;
    int libdocnum=0;
    
    //Menus that need to be individually referenced/updated
    Menu theRecentMenu;
  
    //To hold another Stage with open node that is current
    BookMetaStage bookshelfInspectorStage;  
    BookMetaStage bulkFillStage;  
    //File input/output
    File currentOpenFile;
    //Word
    HashMap wordStyles = new HashMap();
    //Display
    Integer displayMode=1;


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
        MenuItem OpenTempl = new MenuItem("Open (CMD-O)");
        MenuItem SaveShelf = new MenuItem("Save (CMD-S)");
        MenuItem SaveAsMenuItem = new MenuItem("Save As (CMD-SHIFT-S)");
        Menu UpdateDocxMenuItem = new Menu("UpdateDocx");
        Menu ExportMenuItem = new Menu("Export");
        MenuItem SaveDocxStylesMenuItem = new MenuItem("Save Styles");
        UpdateDocxMenuItem.getItems().addAll(SaveDocxStylesMenuItem);
        MenuItem SaveAsDocxMenuItem = new MenuItem("Save As Docx");
        MenuItem saveRowAsMenuItem = new MenuItem("Save Row As");
        MenuItem ClearBookshelfMenuItem = new MenuItem("Close (CMD-W)");
        MenuItem importAsRowBelow = new MenuItem("Import as Row Below");
        //MenuItem exportTextMenuItem = new MenuItem("Output as Text");
        MenuItem PrintTree = new MenuItem("Print as HTML");
        //PrintTree.setOnAction(writeHTML);
        
        //there is no Stage_WS defined at this point
        //this.theRecentMenu = new Menu("Open Recent $");
        //refreshRecentMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            System.exit(0);
            }
        });
        menuFile.getItems().addAll(OpenTempl,SaveShelf,SaveAsMenuItem,SaveAsDocxMenuItem,UpdateDocxMenuItem,saveRowAsMenuItem,ClearBookshelfMenuItem,importAsRowBelow,exit);
        SaveShelf.setOnAction(SaveHandler);
        SaveAsMenuItem.setOnAction(SaveAsHandler); //docname
        SaveAsDocxMenuItem.setOnAction(SaveAsDocxEvent);
        SaveDocxStylesMenuItem.setOnAction(SaveDocxStylesEvent);
        saveRowAsMenuItem.setOnAction(saveRowAsHandler);
        OpenTempl.setOnAction(openTemplate);
        ClearBookshelfMenuItem.setOnAction(clearBookShelf);
        importAsRowBelow.setOnAction(importAsRowHandler);

        //GENERAL LAYOUT/WRAP OPTIONS
        Menu layoutMenu = new Menu("Layout");
        MenuItem layoutWrap = new MenuItem("Wrap");
        MenuItem rowLayout = new MenuItem("By Row");
        MenuItem colLayout = new MenuItem("By Col");
        MenuItem checkersLayout = new MenuItem("Checkers");
        layoutWrap.setOnAction(performBoxWrap);
        rowLayout.setOnAction(performRowWrap);
        colLayout.setOnAction(performColWrap);
        checkersLayout.setOnAction(performCheckersWrap);
        //layoutMenu.getItems().addAll(layoutWrap);
        //MENU GRID : BOX MOVEMENTS
        Menu moveMenu = new Menu("Move");
        MenuItem insertRowAfterItem = new MenuItem("Insert Row (After)");
        MenuItem insertRowBeforeItem = new MenuItem("Insert Row (Before)");
        MenuItem insertCellShiftRightItem = new MenuItem("Nudge (Shift Right)");
        MenuItem nudgeCellShiftLeftItem = new MenuItem("Nudge (Shift Left)");
        insertRowBeforeItem.setOnAction(insertRowBeforeHandler);
        insertRowAfterItem.setOnAction(insertRowAfterHandler);
        insertCellShiftRightItem.setOnAction(insertCellShiftRightHandler);
        nudgeCellShiftLeftItem.setOnAction(nudgeCellShiftLeftHandler);
        moveMenu.getItems().addAll(insertRowBeforeItem,insertRowAfterItem,nudgeCellShiftLeftItem,insertCellShiftRightItem);
        layoutMenu.getItems().addAll(layoutWrap,rowLayout,colLayout,checkersLayout);
        
        //SPLITTING OPTIONS
        Menu splitMenu = new Menu ("Split");
        MenuItem splitOutline = new MenuItem("OutlineLvl1");
        MenuItem splitBookmarks = new MenuItem("Bookmarks");
        splitMenu.getItems().addAll(splitOutline,splitBookmarks);
        splitOutline.setOnAction(splitOnOutline);
        splitBookmarks.setOnAction(splitOnBookmarks);

        //DISPLAY OPTIONS
        Menu displayMenu = new Menu("Display");
        Menu labelsMenu = new Menu("Labels");
        MenuItem displayTitles = new MenuItem("Titles");
        MenuItem displayWithDate = new MenuItem("With Date");
        MenuItem displayFieldStyle = new MenuItem("Field/Style");
        MenuItem displayBookmarks = new MenuItem("Bookmarks");
        labelsMenu.getItems().addAll(displayTitles,displayWithDate,displayFieldStyle,displayBookmarks);
        displayTitles.setOnAction(setDisplayTitles);
        displayWithDate.setOnAction(setDisplayTitleWithDate);
        displayFieldStyle.setOnAction(setDisplayFieldStyle);
        displayBookmarks.setOnAction(setDisplayBookmarks);
        displayMenu.getItems().addAll(labelsMenu,layoutMenu);

        //--- MENU CONCEPTS
        Menu menuEdit = new Menu("Edit");
        MenuItem addNewBook = new MenuItem("New Book");
        addNewBook.setOnAction(addNewBookMaker);
        MenuItem bookDeleteMenuItem = new MenuItem("Delete Selected");
        bookDeleteMenuItem.setOnAction(deleteSelectedBook);
        menuEdit.getItems().addAll(addNewBook,bookDeleteMenuItem);

        // --- STYLEDB MENU ---
        Menu menuFields = new Menu("Fields");
        Menu menuOffice = new Menu("Office");
        //setFileMenu(menuFile);
        MenuItem addMDStyle = new MenuItem("Add MD styles");
        MenuItem addEvidenceStyle = new MenuItem("Add Evid styles");
        MenuItem addLetterStyle = new MenuItem("Add Letter styles");
        Menu legalFields = new Menu("Legal");
        addMDStyle.setOnAction(addMDStyleXML);
        addLetterStyle.setOnAction(addLetterStyleXML);
        addEvidenceStyle.setOnAction(addEvidenceStyleXML);
        menuFields.getItems().addAll(menuOffice,legalFields);
        menuOffice.getItems().addAll(addMDStyle,addLetterStyle);
        //Legal Sub Menu
        Menu contractMenu = new Menu("Contracts");
        MenuItem leaseMenuItem = new MenuItem("Add Lease Fields");
        contractMenu.getItems().addAll(leaseMenuItem);
        legalFields.getItems().addAll(contractMenu,addEvidenceStyle);
        
         // --- TEXT MENU ---
        //MenuItem FileOpen = new MenuItem("FileOpen");
        
       
        /* --- MENU BAR --- */
        menuBar.getMenus().addAll(menuFile,menuEdit,moveMenu,splitMenu,displayMenu,menuFields);     

        //create an event filter so we can process mouse clicks on menubar (and ignore them!)
        menuBar.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
            System.out.println("MenuBar click detected! " + mouseEvent.getSource());
            
            //refreshRecentMenu();
             }
        });

        return menuBar;
}


//StylesXML

public void setMDStyleXML(){
    Stage_WS.addNewStyleTheme("MD");
}

public void setEvidenceStyleXML(){
    Stage_WS.addNewStyleTheme("Evidence");
}

public void setLetterStyleXML(){
    Stage_WS.addNewStyleTheme("Letter");
}

//
public void setDisplayModeTitles(int input) {
    if (input>0 && input<6) {
        this.displayMode=input;
        Stage_WS.setDisplayModeTitles(this.displayMode);
    }
}

//clearBooksFromShelf
public void clearBooksFromShelf() {
    //clear filepath too, to prevent saving over?
    Stage_WS.clearAllBooks(); //do not call - circular reference
    //Stage_WS.resetBookOrigin();
}

public Integer getRowofActiveBook() {
    Integer row=0;
    Book thisBook = getActiveBook();
    if (thisBook!=null) {
        row=thisBook.getRow();
    }
    return row;
}

public Integer getColofActiveBook() {
    Integer col=0;
    Book thisBook = getActiveBook();
    if (thisBook!=null) {
        col=thisBook.getCol();
    }
    return col;
}

//clearBooksFromShelf
public void importAsRowBelowMethod() {
    //clear filepath too, to prevent saving over?
    Integer row = getRowofActiveBook();
    Integer newRow=row+1;
    insertRowAfterMethod(row); //clear out the row.
    //to do - see if 'length' of array with Books from Row x+1 is zero.  If so, just import.  If not, 'insert' row first.
    Stage_WS.openFileAsRow(newRow);
}

//inserts a row before input row
public void insertRowBeforeMethod(Integer firstrow) {
    if (firstrow<0) {
        firstrow=0;
    }
    Stage_WS.insertRow(firstrow);
    //update appearance?
}

//inserts a row after input row
public void insertRowAfterMethod(Integer firstrow) {
    if (firstrow<0) {
        firstrow=0;
    }
    firstrow=firstrow+1;
    Stage_WS.insertRow(firstrow);
    //update appearance?
}

//insertCellShiftRightMethod
public void insertCellShiftRightMethod(Integer firstrow, Integer firstcol) {
    if (firstrow<0) {
        firstrow=0;
    }
    if (firstcol<0) {
        firstcol=0;
    }
    //firstcol=firstcol+1;
    Stage_WS.nudgeCellRightInRow(firstrow,firstcol);
    //update appearance?
}

//nudgeCellShiftLeftMethod
public void nudgeCellShiftLeftMethod(Integer firstrow, Integer firstcol) {
    if (firstrow<0) {
        firstrow=0;
    }
    if (firstcol<0) {
        firstcol=0;
    }
    //firstcol=firstcol+1;
    Stage_WS.nudgeCellLeftInRow(firstrow,firstcol);
    //update appearance?
}

public void resetFileNames(String title) {
    this.currentOpenFile=new File (title);
    //this.currentOpenFile = new File ("untitled.md");
    //Stage_WS.setFilename(this.currentOpenFile.getPath());
}

/*
Method to end alert status for current sprite and reassign
Currently this looks at all Sprite Boxes globally (regardless of viewer/location)
*/
private void moveAlertFromBooktoBook(Book hadFocus, Book myBook) {

    if (Stage_WS.getActiveBook()==null) {
            System.out.println("activeBook is null move alert");
            System.exit(0);
        }
    Stage_WS.setActiveBook(myBook);
    }
 

//general method to store currentSprite

private void setActiveBook(Book myBook) {
    if (Stage_WS.getActiveBook()==null) {
            System.out.println("activeBook is null set current sprite");
            System.exit(0);
        }
    Stage_WS.setActiveBook(myBook);
}

private Book getActiveBook() {
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
       
        /* This only affects the primary stage set by the application (os level) */
        //primaryStage.setTitle("Fact Processor");
        //primaryStage.hide();
        
        MenuBar myMenu = makeMenuBar();
       
        //The main Stage for Workspace.  
        Stage_WS = new MainStage("Literate Database (c) Craig Duncan 2021", myMenu,Main.this,primaryStage);  //sets up GUI for view
        
        if (this.Stage_WS==null) {
            System.out.println("Stage_WS is null start application");
            System.exit(0);
        }
        else {
            System.out.println("Stage_WS object created.");
        }
        //setWordStyles();
        //testZip();
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
    //This is called from the GUI and Event Handler (OpenTempl on Main menu).
    public void mainFileLoader() {
        this.currentOpenFile=Main.this.Stage_WS.openNewFile();
    }

    //TO DO: Link this to 'Project' class, which will hold filetypes, and kind of file we loaded.
    //if we have a list of the Book objects inside the Books, and they have the relevant metadata (including x,y),
    //Then we do not need to actually query the Book class - we can just save as per clause container

/* Save the current file.
The myProject settings should be used by default.
*/

    public void mainFileSaver() {
        //this.Stage_WS.setFile(this.currentOpenFile);
        this.Stage_WS.writeFileOut();
    }

    public void saveAsFileSaver() {
        //this.Stage_WS.setFile(this.currentOpenFile);
        this.Stage_WS.saveAs();
    }

     public void saveAsDocx() {
        //this.Stage_WS.setFile(this.currentOpenFile);
        this.Stage_WS.saveAsDocx();
    }

    //saveAsDocxStyles
     public void saveAsDocxStyles() {
        //this.Stage_WS.setFile(this.currentOpenFile);
        this.Stage_WS.saveAsDocxStyles();
    }

    public void saveRowAsFileSaver() {
        //this.Stage_WS.setFile(this.currentOpenFile); //not needed?
        Book myBook = Main.this.getActiveBook();
        Integer myRow = myBook.getRow();
        this.Stage_WS.saveRowAs(myRow);
    }

    // --- EVENT HANDLERS

    //SPLITTING
    EventHandler<ActionEvent> splitOnOutline = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Stage_WS.setSplitOption(1);
        }
    };

    EventHandler<ActionEvent> splitOnBookmarks = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Stage_WS.setSplitOption(2);
        }
    };

    //LAYOUT
    EventHandler<ActionEvent> performBoxWrap = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //Main.this.performBoxWrapFunction();
            Stage_WS.wrapBooks();
            }
        };

    EventHandler<ActionEvent> performCheckersWrap = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //Main.this.performBoxWrapFunction();
            Stage_WS.wrapProjectBooksCheckers();
            }
        };

    EventHandler<ActionEvent> performRowWrap = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //Main.this.performBoxWrapFunction();
            Stage_WS.unpackBooksAsRow();
            }
        };

    EventHandler<ActionEvent> performColWrap = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //Main.this.performBoxWrapFunction();
            Stage_WS.unpackBooksAsCol();
            }
        };

   

    EventHandler<ActionEvent> addMDStyleXML = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setMDStyleXML();
        }
    };

    EventHandler<ActionEvent> addEvidenceStyleXML = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setEvidenceStyleXML();
        }
    };

    EventHandler<ActionEvent> addLetterStyleXML = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setLetterStyleXML();
        }
    };


    //display mode handlers
    EventHandler<ActionEvent> setDisplayTitles = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setDisplayModeTitles(1);
        }
    };

    EventHandler<ActionEvent> setDisplayTitleWithDate = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setDisplayModeTitles(4);
        }
    };


    EventHandler<ActionEvent> setDisplayFieldStyle = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setDisplayModeTitles(3);
        }
    };

    EventHandler<ActionEvent> setDisplayBookmarks = 
    new EventHandler<ActionEvent>() {
    @Override 
    public void handle(ActionEvent event) {
        Main.this.setDisplayModeTitles(5);
        }
    };

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

        //to 
        EventHandler<ActionEvent> importAsRowHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.importAsRowBelowMethod();
           
            }
        };

        //insertRowAfterHandler
        EventHandler<ActionEvent> insertRowAfterHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Integer row=Main.this.getRowofActiveBook();
            Main.this.insertRowAfterMethod(row);
           
            }
        };
        EventHandler<ActionEvent> insertRowBeforeHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Integer row=Main.this.getRowofActiveBook();
            Main.this.insertRowBeforeMethod(row);
           
            }
        };

        //insertCellShiftRightHandler
        EventHandler<ActionEvent> insertCellShiftRightHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Integer row=Main.this.getRowofActiveBook();
            Integer col=Main.this.getColofActiveBook();
            Main.this.insertCellShiftRightMethod(row,col);
           
            }
        };

        //nudgeCellShiftLeftHandler
        EventHandler<ActionEvent> nudgeCellShiftLeftHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Integer row=Main.this.getRowofActiveBook();
            Integer col=Main.this.getColofActiveBook();
            Main.this.nudgeCellShiftLeftMethod(row,col);
           
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

        //save 
        EventHandler<ActionEvent> SaveHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.mainFileSaver();
                }
            }; 

        //save As Markdown template
        EventHandler<ActionEvent> SaveAsHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.saveAsFileSaver();
                }
            }; 

        //save As Docx template
        EventHandler<ActionEvent> SaveAsDocxEvent = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.saveAsDocx();
                }
            }; 

        //SaveDocxStylesEvent
        EventHandler<ActionEvent> SaveDocxStylesEvent = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.saveAsDocxStyles();
                }
            }; 

        //save Row As template
        EventHandler<ActionEvent> saveRowAsHandler = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            Main.this.saveRowAsFileSaver();
                }
            };

}