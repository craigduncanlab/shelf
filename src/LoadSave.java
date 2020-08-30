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
MainStage targetSM = new MainStage();
WhiteBoard defaultWhiteBoard = new WhiteBoard();
//current dialogue
Stage myStage;
Book targetNode = new Book();
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
public LoadSave (MainStage myMS) {
  this.targetSM=myMS;
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

public void makeSave(MainStage targetSM, Book myNode) {
	this.targetSM = targetSM; //store for later
	this.targetNode = myNode; //store for later
	//make this dialogue
	makeDialogue("Save Template As",0);
}

public void makeChooser() {
 
  //Makes a custom button for the stage
   final Button openButton = new Button("Select Filepath");
         final FileChooser fileChooser = new FileChooser();
         openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(LoadSave.this.myStage);
                    if (file != null) {
                      //LoadSave.this.processFilepath(file);
                    } //end if
                } //end new defn of event handler
            });
  Stage myChooserStage = makeLoaderStage(openButton);  // this is not attached to Stage_WS at all?
  myChooserStage.show();
}

public void simpleOpen(Book myNode) {
  System.out.println(myNode.toString());
  this.targetSM.OpenNewNodeNow(myNode); //TO DO: make this open up in whiteboard.  Should be triggered as if double click on a red box.  i.e. changes focus.
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
//Accepts button as input: currently used for loading markdown files
//It is opened up as a prompt on the Stage_WS, and then adds nodes/boxes to the Stage_WS.
//TO DO: add a close button
private Stage makeLoaderStage(Button openButton) {
        this.myStage= new Stage();
        this.myStage.setTitle("Open File");
        final GridPane inputGridPane = new GridPane();
 
        GridPane.setConstraints(openButton, 0, 0);
        //GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        //inputGridPane.getChildren().addAll(openButton, openMultipleButton);
        inputGridPane.getChildren().addAll(openButton);
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
        }
      };

EventHandler<ActionEvent> clickSave = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
	        
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