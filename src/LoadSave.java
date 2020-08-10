import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;

//import utilities needed for Arrays lists etc
import java.util.*; //scanner etc
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
//
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
//Desktop etc and file chooser
import java.awt.Desktop;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Class to create a load/save dialogue box (Stage) for use in application

public class LoadSave {

//JavaFX instance variables
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup;
ScrollPane spriteScrollPane;
Pane spritePane;
Scene localScene;
TextArea inputTextArea = new TextArea();
//target Stage information
StageManager targetSM = new StageManager();
WhiteBoard defaultWhiteBoard = new WhiteBoard();
//current dialogue
Stage myStage;
ClauseContainer targetNode = new ClauseContainer();
Desktop desktop; 
/*
The FileChooser class is located in the javafx.stage package 
along with the other basic root graphical elements, such as Stage, Window, and Popup. 
*/

//contructor
public LoadSave () {
  this.desktop = Desktop.getDesktop();
}

//constructor with Stage
public LoadSave (StageManager mySM) {
  this.targetSM=mySM;
  this.desktop = Desktop.getDesktop();
}

//constructor with WhiteBoard
public LoadSave (WhiteBoard myWB) {
  this.defaultWhiteBoard=myWB;
  this.desktop = Desktop.getDesktop();
}

private HBox SaveButtonSetup() {
	Button btnSave = new Button();
	Button btnCancel = new Button();
	Button btnOpen = new Button();
	//
    btnSave.setText("Save");
    btnSave.setTooltip(new Tooltip ("Save highlighted as template file"));
    btnSave.setOnAction(clickSave);
    //
    btnCancel.setText("Cancel");
    btnCancel.setTooltip(new Tooltip ("Cancel file"));
    btnCancel.setOnAction(clickCancel);
    //
	HBox hboxButton = new HBox(0,btnSave,btnCancel);
	return hboxButton;
}

private HBox LoadButtonSetup() {
	Button btnSave = new Button();
	Button btnCancel = new Button();
	Button btnOpen = new Button();
	//
	btnOpen.setText("Open");
    btnOpen.setTooltip(new Tooltip ("Open file"));
    btnOpen.setOnAction(clickOpen);
    //
    btnCancel.setText("Cancel");
    btnCancel.setTooltip(new Tooltip ("Cancel file"));
    btnCancel.setOnAction(clickCancel);
    //
	HBox hboxButton = new HBox(0,btnOpen,btnCancel);
	return hboxButton;
}

private VBox vertSetup(HBox myhbox) {
	inputTextArea.setPrefRowCount(1);
  inputTextArea.setText("Hello There"); //default text in loadbox
	VBox myvbox = new VBox(0,inputTextArea,myhbox);
	return myvbox;
}

public void makeSave(StageManager targetSM, ClauseContainer myNode) {
	this.targetSM = targetSM; //store for later
	this.targetNode = myNode; //store for later
	//make this dialogue
	makeDialogue("Save Template As",0);
}

public void saveName(ClauseContainer myNode) {
  TemplateUtil myUtil = new TemplateUtil();
  String filename = myNode.getDocName();
  //***** myUtil.saveTemplate(myNode,filename);
  myUtil.saveTemplateSingle(myNode,filename);
  myUtil.saveTidyTemplate(myNode,filename);
  System.out.println("Save template completed");
}

public void makeLoad(StageManager targetSM) {
	this.targetSM = targetSM; //store for later
	//this.targetNode = null; //store for later
	//make this dialogue
	//makeDialogue("Load Template",1);
  //FileChooser newFC = new FileChooser();
  Stage myChooser = makeStage();
  myChooser.show();
}

public void simpleOpen(ClauseContainer myNode) {
  System.out.println(myNode.toString());
  System.out.println(this.targetSM.toString());
  this.targetSM.OpenNewNodeNow(myNode, this.targetSM); //TO DO: make this open up in whiteboard.  Should be triggered as if double click on a red box.  i.e. changes focus.
}

// args is redundant input argument to List: String args[]
// TO  throws IOException 
public void ListOfFiles(){
      //Creating a File object for directory
      //File directoryPath = new File("D:\\ExampleDirectory");
      //List of all files and directories
      try {
        File directoryPath = new File("");
        String contents[] = directoryPath.list();
        System.out.println("List of files and directories in the specified directory:");
        for(int i=0; i<contents.length; i++) {
         System.out.println(contents[i]);
        }
        }
      catch (Exception e) {
        System.out.println ("Problem with listing files and directories");
        }
      }

//create dialogue box and display

private void makeDialogue(String title, int option) {
	int winWidth=200;
	int winHeight=100;
	double x = 600;
	double y = 50;
	this.myStage = new Stage();
	HBox myHBox = new HBox();
	if (option==0) {
		myHBox=SaveButtonSetup();
	}
	if (option==1) {
		myHBox=LoadButtonSetup();
	}
	VBox vertFrame=vertSetup(myHBox);  //The text field to display...
  //Test the ability to list files (TO DO: insert into selectable list)
  String testoutput=inputTextArea.getText();
  System.out.println(testoutput);
  this.inputTextArea.setText("Blah");
  this.ListOfFiles();
  //
	Pane largePane = new Pane();
    largePane.setPrefSize(winWidth, winHeight);
    largePane.getChildren().add(vertFrame); 
    Scene tempScene = new Scene (largePane,winWidth,winHeight+100); //default width x height (px)
    this.myStage.setScene(tempScene);
    this.myStage.setX(x);
   	this.myStage.setY(y);
   	this.myStage.setTitle(title);
   	this.myStage.initOwner(this.targetSM.getStage());//set parent to workstage Stage
   	this.myStage.show();
   	//return myStage;
}

public void Close() {
	this.myStage.close();
}

//This is a separate Loader stage.  Can run it off menu selector or keystrokes.
// Currently used for loading markdown files
private Stage makeStage() {
        this.myStage= new Stage();
        this.myStage.setTitle("Open File");
 
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Open a Markdown File");
        final Button openMultipleButton = new Button("Open Multiple MD");
 
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(LoadSave.this.myStage);
                    if (file != null) {
                      //String filename=System.out.print(file.toString()); // this is full path
                      String last=file.getName();
                      last=last.substring(last.length() - 3);
                      if (last.equals(".md")==true) {
                        TemplateUtil myUtil = new TemplateUtil();
                        String contents = myUtil.getFileText(file);
                        //Recents myR = new Recents();
                        //myR.updateRecents(file.getName());
                        Parser myParser=new Parser();
                        // Split the MD file
                        ArrayList<String> blocklist= myParser.splitMDfile(contents);
                        int length = blocklist.size();
                        //System.out.println(length);
                        //System.exit(0);
                        if (length>0) {
                          Iterator<String> iter = blocklist.iterator(); 
                            while (iter.hasNext()) {
                                ClauseContainer newNode=myParser.parseMDfile(iter.next());
                                if (newNode!=null) {
                                  LoadSave.this.targetSM.OpenNewNodeNow(newNode,LoadSave.this.targetSM);
                                }
                           } //end while
                        }
                        
                        System.out.println("Finished parse in 'open button' makeStage");
                        LoadSave.this.ListOfFiles();// print out current directory
                      }
                    }
                }
            });
 
        openMultipleButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    List<File> list =
                        fileChooser.showOpenMultipleDialog(LoadSave.this.myStage);
                    /*if (list != null) {
                        for (File file : list) {
                            openFile(file);
                        }
                    }
                    */
                }
            });
 
 
        final GridPane inputGridPane = new GridPane();
 
        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, openMultipleButton);
 
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        this.myStage.setScene(new Scene(rootGroup));
        return this.myStage;
    }

//This opens a file, but it defaults to the local system application?
private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                FileChooserSample.class.getName()).log(
                    Level.SEVERE, null, ex
                );
        }
    }

EventHandler<ActionEvent> clickOpen = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            String testoutput=inputTextArea.getText();
            System.out.println(testoutput);
            LoadSave.this.inputTextArea.setText("Blah");
            LoadSave.this.ListOfFiles();
            /*TemplateUtil myUtil = new TemplateUtil();
            String filename=inputTextArea.getText();
            //ClauseContainer newNode = myUtil.getTemplate(filename); 
            ClauseContainer newNode = myUtil.getStructuredData(filename); 
            if (newNode!=null) {
                LoadSave.this.targetSM.OpenNewNodeNow(newNode,LoadSave.this.targetSM); //make this new whitebaord
                 //TO DO: change whiteboard display node.  Add child node.
                 //update recents list
                Recents myR = new Recents();
                myR.updateRecents(filename);
                LoadSave.this.Close();
            }
          */
        }
      };

EventHandler<ActionEvent> clickSave = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
	        //
	        StageManager myStage=LoadSave.this.targetSM.getCurrentFocus();
	        ClauseContainer thisNode = LoadSave.this.targetNode;
	        String filename=inputTextArea.getText();
	        TemplateUtil myUtil = new TemplateUtil();
	        myUtil.saveTemplateSingle(thisNode,filename);
          myUtil.saveTidyTemplate(thisNode,filename);
	        System.out.println("Save template completed");
	        LoadSave.this.Close();
          }
      };

EventHandler<ActionEvent> clickCancel = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            LoadSave.this.myStage.close(); //closes this object 
          }
      };
}