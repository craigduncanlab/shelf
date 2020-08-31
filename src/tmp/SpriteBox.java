//FX and events
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//text
//Scene - Text as text, with font option
import javafx.scene.text.Text; 
import javafx.scene.text.Font;
import javafx.scene.text.*;
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;
//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
//Serializable
import java.io.Serializable;
//For storing current Stage location
import javafx.stage.Stage;

/* 

JavaFX node (StackPane) that holds GUI objects (Rectangle), as well as abstract data objects like the 'Book' that holds metadata.

Mouse clicks are detectable as a result of the underlying 'Rectangle' ('BookIcon') being detected.

Data can be passed to this object when it is created for display.

This object can hold other complex data types.

*/

public class SpriteBox extends StackPane implements java.io.Serializable {         
    //instance variables are contained Nodes/Objects.
    //Not class variables as they are not 'static'
    //
    //Try the bookshelf view for the 'library'.  Make both available?
    //ColBox myBox;
    BookIcon myBookIcon; //appearance (the extended Rectangle class)
    Book BoxNode; //generic holder of content/metadata
    Clause myClause;  //To do: remove this data type
    Book myDocument; //UNUSED
    String Category=""; //will be Clause, Definition etc
    Text boxlabel = new Text ("new box");//Default label text for every SpriteBox
    String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
    double Xpos = 100; //default for shelves
    double Ypos = 50;
    Boolean isAlert=false;
    //To do : review need for location variables
    Boolean OnStage=false;
    Boolean InProject=false;
    Boolean InProjectLib=false;
    Boolean InLibrary=false;
    Boolean InCollection=false;
    Boolean InDocumentStage=false;
    Boolean OtherStage=false;
    //
    String defaultColour="white";
    //String alertColour="red";
    Color alertColour=Color.RED;
    String followerColour="pink";
    MainStage StageLocation;
    StageManager childStage; //i.e. the nodeviewer stage
    //using alternate states representation for open window
    int location = 0;
    //position data stored to allow mouse drags in UI
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //
    String boxcategory=""; 
    //Track if this box has been opened to edit node or not.
    Boolean viewingNode=false;
    Integer colourIndex;



    //basic default constructor
    public SpriteBox() {
        this.setup();
    }

    /*
    Box constructor with Box for Existing Node (not ChildNode)?  
    Not used by Main or StageManager.  Redundant?
    */

    public SpriteBox(Book node, MainStage mySM) {
    
    this.setup();
    setStageLocation(mySM);
    setBoxNode(node); //sets and updates appearance
    //inherited methods - need to reference from the object type to call them
    setOnMousePressed(PressBoxEventHandler);  // JavaFX - inherited from Rectangle 
    setOnMouseDragged(DragBoxEventHandler);
    setOnMouseReleased(DragBoxEventHandler);    //ditto
}

/*
Box constructor that puts a (Book)Node inside as the Box's node.
Called from StageManager objects [BookMetaStage]
*/

    public SpriteBox(EventHandler PressBox, EventHandler DragBox, Book node) {
    
    this.setup();
    setBoxNode(node); //sets and updates appearance//works with Book method
    //SpriteBox.this.setStageLocation(mySM);
    setOnMousePressed(PressBox);  // JavaFX - inherited from Rectangle 
    setOnMouseDragged(DragBox);   //ditto
    //add filters
    //SpriteBox.this.addEventFilter(MouseEvent.MOUSE_PRESSED, PressBox);
    SpriteBox.this.setOnMousePressed(PressBox);
    SpriteBox.this.setOnMouseDragged(DragBox);
    SpriteBox.this.setOnMouseReleased(DragBox); 
}

//box constructor for image viewer or snapshot - no event handlers needed

 public SpriteBox(Book node) {
    
    this.setup();
    setBoxNode(node); //sets and updates appearance//works with Book method

}
//default constructor with label
public SpriteBox(String startLabel) {
	this.setup();
    this.boxlabel = new Text (startLabel);//myBookIcon.getLabel();
    
 }  
// constructor with colour
public SpriteBox(String startLabel, String mycolour) {
    this.setup();
    this.myBookIcon = new BookIcon(mycolour);
    this.boxlabel = new Text (startLabel);//myBookIcon.getLabel();
 }

 //SPRITEBOX STATUS: NODE OPEN OR NOT
 public Boolean isOpen() {
    return this.viewingNode;
 }

 public void setOpen() {
    this.viewingNode=true;
 }

 public StageManager getChildStage() {
    return this.childStage;
 }

 public void setChildStage(StageManager myCSM) {
    this.childStage = myCSM;
 }

 //EVENT HANDLERS THAT PROVIDE CONTEXT [NOT USED]

 EventHandler<MouseEvent> PressBoxEventHandler = 
    new EventHandler<MouseEvent>() {

    @Override
    public void handle(MouseEvent t) {
     //current position of mouse
    orgSceneX = t.getSceneX(); //Mouse event X, Y coords relative to scene that triggered
    orgSceneY = t.getSceneY();

    //update the origin point to this click/press
    orgTranslateX = SpriteBox.this.getTranslateX(); //references this instance at Runtime
    orgTranslateY = SpriteBox.this.getTranslateY();
    t.consume();

    }
};

    //Not invoked?  Uses the handler passed in from main method.
    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //TO DO: tell stage manager etc this box is active
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            System.out.println("The local (#) Box handler for drag box is acting");
            //update stored box location
            SpriteBox.this.setXY(offsetX,offsetY);
            System.out.println("Offsets (X,Y): "+offsetX+","+offsetY);
            //updates to sprite that triggered event
            SpriteBox.this.setTranslateX(newTranslateX);
            SpriteBox.this.setTranslateY(newTranslateY);
            SpriteBox.this.doAlert(); //in case single click event doesn't detect
            t.consume();//check
        }
    };

    /*cycle colour of box (bookIcon) on which this Sprite is based */
    public void cycleBookColour () {
        myBookIcon.rotateBookColour();
    }

    /* GETTERS AND SETTERS FOR BOX ITSELF */

    public void setBoxCategory(String category) {
        this.boxcategory = category;
    }

    private String getBoxCategory() {
        return this.boxcategory;
    }

     /*Place an Book inside (e.g. handles subclasses Clause or Book) */

    public void setBoxNode (Book myNode) {
        this.BoxNode = myNode;
        this.getXY();
        this.updateAppearance();
    }

     /*Return the Book inside (e.g. handles subclasses Clause or Book) */

    public Book getBoxNode() {
        return this.BoxNode;
    }

    /*Return the Book inside (if Book) REDUNDANT 
    Now private to test external dependencies */

    private Book getCC() {
        if(this.BoxNode instanceof Book) {
            return (Book)this.BoxNode;
        }
        else {
            return new Book(); //error?
        }
    }

    /* General setup with Clause inside */

    public void setup() {
        this.myBookIcon = new BookIcon();   //Uses defaults.
        myClause = new Clause(); //TO DO: remove this data item.  Book set in constructor
        Font boxfont=Font.font ("Verdana", 12); //check this size on monitor/screen
        boxlabel.setFont(boxfont);
        boxlabel.setFill(myBookIcon.colourPicker("black"));
        boxlabel.setWrappingWidth(130);
        this.setCursor(Cursor.HAND);
        this.getChildren().addAll(myBookIcon,boxlabel); 
    }

     /* SUPERFICIAL SPRITE APPEARANCE AND STORE LOCATION */

    public double[] getXY() {
        this.Xpos=this.getBoxNode().getX();
        this.Ypos=this.getBoxNode().getY();
        return new double[]{this.Xpos,this.Ypos};
    }

    public double getX() {
        return this.Xpos;
    }

    public double getY() {
        return this.Ypos;
    }

    //set child node offset for this box.  Must be positive.
    public void setXY(double x, double y) {
        if (x>0) {
            this.Xpos=x;
        }
        else {
            this.Xpos=0; //default
        }
        if (y>0) {
            this.Ypos=y;
        }
        else {
            this.Ypos=0;//default
        }
        this.getBoxNode().setXY(x,y); //set data node to store this position for save
        System.out.println ("Updated child node offset:"+x+","+y);
    }
     
    public String getLabel() {
        return this.boxlabel.getText();
    }

    public void setLabel(String myString) {
        if (!myString.equals("")) {
            this.boxlabel.setText(myString);
        }
    }

    public void setContent(String myString) {
        this.contents=myString;
    }

    public String getContent() {
        return this.contents;
    }

    public void SetColour(Color myColour) {
        myBookIcon.setColour(myColour);
    }

    public void SetDefaultColour (String mycol) {
        this.defaultColour=mycol;
    }

    public String getColour() {
        return myBookIcon.getColour();
    }

    //--- LOCATION SETTING/TESTING ---//

    public void setStageLocation(MainStage currentSM) {
        this.StageLocation = currentSM;
    }

    public MainStage getStageLocation() {
        return this.StageLocation;
    }

     /* Set index for location of spritebox (i.e. an open window or workspace) 
     TO DO: just use this for the GUI object (StageManager class) that is parent*/
    public void resetLocation() {
        this.StageLocation = null;
        //
        this.location=0;
        //delete these when no longer needed:
        this.OnStage=false;
        this.InProject=false;
        this.InLibrary=false;
        this.InCollection=false;
        this.InDocumentStage=false;
        this.OtherStage=false;
    }

    //clone function is based on box just holding a data node

    public SpriteBox clone() {
        SpriteBox clone = new SpriteBox();
        clone.setup();
        clone.setStageID(getStageID());
        //do not use setClause first - will update BoxNode
        clone.setBoxNode(this.getBoxNode());
        return clone;
    } 

    //LOCATION SETTERS
     /* Simple setter to store stageID */
    public void setStageID(int myLoc) {
        this.location = myLoc;
    }

     /* Get currentStageID for this box */
    public int getStageID() {
        return this.location;
    }

    // ----------- COLOURS FOR STATES

    public Boolean isAlert() {
        return this.isAlert;
    }

    public void doAlert() {
        this.isAlert=true;
        //myBookIcon.setColour(alertColour);
        updateAppearance();
    }
     public void endAlert() {
        this.isAlert=false;
        //myBookIcon.setColour(defaultColour);
        updateAppearance();
    }

    // --------------------------------

    public String getBoxDocName() {
        Book thisNode = this.getBoxNode();
        return thisNode.getDocName();
    }

    
    /*
    Method to refresh appearance and default colour based on associated node.
    3.5.18:
    Use the data that the node 'shows' to the GUI.
    i.e. if node can swap out its data or 'show' to the outside, then the node's public methods will choose.
    This SpriteBox will not know the difference.
    */

    // There is a Book object associated with this box.
    // The Book stores the metadata, including the 'colour'
    // The colour is, in turn, based on the 'category' of the Node.
    // TO DO: simplify this so that each 'Book' object has a type?
    //i.e. we will just treat the SpriteBox as the "Book" with its metadata and it tells the BookIcon what colour to be?
    //

    private void updateAppearance() {
        
        Book thisNode = this.getBoxNode();  
        this.setLabel(thisNode.getDocName());
        this.boxlabel.setRotate(270); 
        //this.SetColour(thisNode.getNodeColour());
        //this.SetDefaultColour(thisNode.getNodeColour());
        if (this.isAlert==true) {
           myBookIcon.setColour(alertColour);
        }
        else {
            myBookIcon.setColour(Color.BLUE);
        }
        }

    /* ----  INTERNAL OBJECT DATA ---  ALL OF THE METHODS BELOW NOW REDUNDANT? */

    public Clause getClause() {
        return this.myClause;
    }

    /* Sync or obtain text from internal clause container */    

    public String getClauseText() {
        return this.myClause.getClauseText();
    }

    /* Sync internal clause container text with external data */  

    public void setClauseText(String myString) {
        this.myClause.setClauseText(myString);
        //sync the content of this spritebox too i.e. displayed in inspector
        //TO DO: inspector should look straight to clause text?  mirror for definitions? defs are stripped down clauses?
        this.setContent(myString);
    }

    /* Set label and sync internal clause container label with spritebox label */  

    public void setClauseLabel(String myString) {
        this.myClause.setClauselabel(myString);
        //this.setLabel(myString);  //in case you want box to have freq count on face of it etc leave off
    }

    public void setBoxLabel(String myString) {
        this.setLabel(myString); 
        //this.myClause.setClauselabel(myString);
        //this.setLabel(myString);  //in case you want box to have freq count on face of it etc leave off
    }

    /* Set / sync internal clause container heading */  

    public void setClauseHeading(String myString) {
        this.myClause.setHeading(myString);
    }

    public String getClauseHeading() {
        return this.myClause.getHeading();
    }

    /* Set / sync internal clause category */  

    public void setCategory(String myString) {
        this.myClause.setCategory(myString);  //sets internal clause category
        this.setCategory(myString); //sets sprite category to same.  needed?
    }


}
