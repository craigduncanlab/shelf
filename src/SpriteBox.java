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

/* This class started Nov 2017 

Purpose is to create the principal object for GUI windows.
It holds data structures (Clauses) and those internal properties (e.g. category field of Clause) can determine appearance of SpriteBox.
Its instance 'objects' include a Rectangle (extended: ColBox), Text and Colour objects (for GUI appearance).  These are Shapes/Text objects that JavaFX recognises as Nodes.

Within JavaFX this object is also a "Node"/Layout Object called "StackPane".
The adjustment of the position of this object will adjust position of all visible Nodes contained within it.
to visualise concepts within a GUI (therefore extends StackPane) but also as a container for content/data that can be controller through the object itself.

TO DO: distinguish private/public methods and write javadocs info

13.3.18 Consider making Spritebox capable of holding some basic object types:
e.g. Clauses, Definitions, Facts

13.3.18 modifications to implement serializable class, to assist with saving IDE layout & content
The basic concept is that the boxes and their contents will be saved.  Other functions can be run on the contents of the boxes (e.g. export)
If the location of each Spritebox can be saved (e.g. the parent window), this may assist with retrieval.

14.3.18 Add Library status
Query if stage and library status should also be in Clause instances

27.3.18 Consider if SpriteBox can also hold a Document (ClauseContainer)

3.4.18 By passing in the node GUI location, the SpriteBox can retrieve the appropriate StageManager
If we make the location an object, then the GUILocation object 
can also have the StageManager for that Location already packaged by the Node?.

28.4.18
If this 'Box' only ever operates as a GUI representation of a Node
i.e. holds a GUI representation of a 'Node' as data object, then some of its functions unnecessary
i.e. operate on the data node directly through main app, and let this just create a GUI representation.
(separation of concerns)

20.6.18
The SpriteBox is always transitory so it doesn't store position data.
Do that in node (ClauseContainer)?

*/

public class SpriteBox extends StackPane implements java.io.Serializable {         
    //instance variables are contained Nodes/Objects.
    //Not class variables as they are not 'static'
    ColBox myBox;
    ClauseContainer BoxNode; //generic holder of content
    Clause myClause;  //To do: remove this data type
    ClauseContainer myDocument; //UNUSED
    String Category=""; //will be Clause, Definition etc
    Text boxlabel = new Text ("new box");//Default label text for every SpriteBox
    String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
    double Xpos = 0;
    double Ypos = 0;
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
    String defaultColour="";
    String alertColour="red";
    String followerColour="pink";
    StageManager StageLocation;
    StageManager childStage; //i.e. the nodeviewer stage
    //using alternate states representation for open window
    int location = 0;
    //mouse
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //
    String boxcategory=""; 
    //Track if this box has been opened to edit node or not.
    Boolean viewingNode=false;



    //basic default constructor
    public SpriteBox() {
        this.setup();
    }

    /*
    Box constructor with Box for Existing Node (not ChildNode)?  
    Not used by Main or StageManager.  Redundant?
    */

    public SpriteBox(ClauseContainer node, StageManager mySM) {
    
    this.setup();
    location = node.getNodeLocation();
    String myCat = node.getNodeCategory(); //works with clausecontainer method
    setStageLocation(mySM);
    setBoxCategory(myCat); 
    setBoxNode(node); //sets and updates appearance
    //inherited methods - need to reference from the object type to call them
    setOnMousePressed(PressBoxEventHandler);  // JavaFX - inherited from Rectangle 
    setOnMouseDragged(DragBoxEventHandler);   //ditto
}

/*
Box constructor that puts a (ClauseContainer)Node inside as the Box's node.
Called from StageManager objects 
*/

    public SpriteBox(EventHandler PressBox, EventHandler DragBox, ClauseContainer node) {
    
    this.setup();
    location = node.getNodeLocation(); //use parent Node instead??
    String myCat = node.getNodeCategory(); 
    setBoxNode(node); //sets and updates appearance//works with clausecontainer method
    //SpriteBox.this.setStageLocation(mySM);
    setOnMousePressed(PressBox);  // JavaFX - inherited from Rectangle 
    setOnMouseDragged(DragBox);   //ditto
    setBoxCategory(myCat); //TO DO: Abandon.  Just get directly when needed.
}

//box constructor for image viewer or snapshot - no event handlers needed

 public SpriteBox(ClauseContainer node) {
    
    this.setup();
    location = node.getNodeLocation(); //use parent Node instead??
    String myCat = node.getNodeCategory(); 
    setBoxNode(node); //sets and updates appearance//works with clausecontainer method
    //SpriteBox.this.setStageLocation(mySM);
    //setOnMousePressed(PressBox);  // JavaFX - inherited from Rectangle 
    //setOnMouseDragged(DragBox);   //ditto
    setBoxCategory(myCat); //TO DO: Abandon.  Just get directly when needed.
}

//Box constructor that takes puts a (ClauseContainer)Node inside as the Box's node.
/*
    private SpriteBox(ClauseContainer node) {
    
    this.setup();
    location = node.getNodeLocation(); //use parent Node instead??
    String myCat = node.getNodeCategory(); 
    SpriteBox.this.setBoxNode(node); //sets and updates appearance//works with clausecontainer method
    //SpriteBox.this.setStageLocation(mySM);
    SpriteBox.this.setOnMousePressed(PressBoxEventHandler);  // JavaFX - inherited from Rectangle 
    SpriteBox.this.setOnMouseDragged(DragBoxEventHandler);   //ditto
    SpriteBox.this.setBoxCategory(myCat); //TO DO: Abandon.  Just get directly when needed.
    }
*/

    //default constructor with label
    public SpriteBox(String startLabel) {
    	this.setup();
        this.boxlabel = new Text (startLabel);//myBox.getLabel();
        
     }  
    // constructor with colour
    public SpriteBox(String startLabel, String mycolour) {
        this.setup();
        this.myBox = new ColBox(mycolour);
        this.boxlabel = new Text (startLabel);//myBox.getLabel();
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

     //EVENT HANDLERS THAT PROVIDE CONTEXT

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


    /* GETTERS AND SETTERS FOR BOX ITSELF */

    public void setBoxCategory(String category) {
        this.boxcategory = category;
    }

    private String getBoxCategory() {
        return this.boxcategory;
    }

     /*Place an ClauseContainer inside (e.g. handles subclasses Clause or ClauseContainer) */

    public void setBoxNode (ClauseContainer myNode) {
        this.BoxNode = myNode;
        this.getXY();
        this.updateAppearance();
    }

     /*Return the ClauseContainer inside (e.g. handles subclasses Clause or ClauseContainer) */

    public ClauseContainer getBoxNode() {
        return this.BoxNode;
    }

    /*Set parent node/unset*/
    //method to remove data links between Child node in this Box (closed node) and its parent.

    public void unsetParentNode() {
        if (getBoxNode()!=null) {
            getBoxNode().unsetParentNode();
        }
        else {
            System.out.println("Error : No parent node to unset");
        }
}

    /*Return the ClauseContainer inside (if ClauseContainer) REDUNDANT 
    Now private to test external dependencies */

    private ClauseContainer getCC() {
        if(this.BoxNode instanceof ClauseContainer) {
            return (ClauseContainer)this.BoxNode;
        }
        else {
            return new ClauseContainer(); //error?
        }
    }

    /* General setup with Clause inside */

    public void setup() {
        myBox = new ColBox();   //Uses defaults.
        myClause = new Clause(); //TO DO: remove this data item.  ClauseContainer set in constructor
        Font boxfont=Font.font ("Verdana", 12); //check this size on monitor/screen
        boxlabel.setFont(boxfont);
        boxlabel.setFill(myBox.colourPicker("black"));
        boxlabel.setWrappingWidth(130);
        this.setCursor(Cursor.HAND);
        this.getChildren().addAll(myBox,boxlabel); 
    }

     /* SUPERFICIAL SPRITE APPEARANCE AND STORE LOCATION */

    public double[] getXY() {
        this.Xpos=this.getBoxNode().getChildNodeX();
        this.Ypos=this.getBoxNode().getChildNodeY();
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
            this.Xpos=0;
        }
        if (y>0) {
            this.Ypos=y;
        }
        else {
            this.Ypos=0;
        }
        this.getBoxNode().setChildNodeXY(x,y); //set data node to store this position for save
        System.out.println ("Updated child node offset:"+x+","+y);
    }
     
    public Text getLabel() {
        return boxlabel;
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

    public void SetColour(String mycol) {
        myBox.setColour(mycol);
    }

    public void SetDefaultColour (String mycol) {
        this.defaultColour=mycol;
    }

    public String getColour() {
        return myBox.getColour();
    }

    //--- LOCATION SETTING/TESTING ---//

    public void setStageLocation(StageManager currentSM) {
        this.StageLocation = currentSM;
    }

    public StageManager getStageLocation() {
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
        //myBox.setColour(alertColour);
        updateAppearance();
    }
     public void endAlert() {
        this.isAlert=false;
        //myBox.setColour(defaultColour);
        updateAppearance();
    }

    // --------------------------------

    public String getBoxDocName() {
        ClauseContainer thisNode = this.getBoxNode();
        return thisNode.getDocName();
    }

    //get category from enclosed node
    private String getNodeCategory() {
        ClauseContainer thisNode = this.getBoxNode();
        return thisNode.getNodeCategory();
    }

    /*
    Method to refresh appearance and default colour based on associated node.
    3.5.18:
    Use the data that the node 'shows' to the GUI.
    i.e. if node can swap out its data or 'show' to the outside, then the node's public methods will choose.
    This SpriteBox will not know the difference.
    */

    private void updateAppearance() {
        
        ClauseContainer thisNode = this.getBoxNode();
        this.setLabel(thisNode.getDocName()); 
        this.SetColour(thisNode.getNodeColour());
        this.SetDefaultColour(thisNode.getNodeColour());
        if (this.isAlert==true) {
           myBox.setColour(alertColour);
        }
        else if (thisNode.isFollower()==true) {
                myBox.setColour(followerColour);
                this.boxlabel.setFill(myBox.colourPicker("white"));
            }
        else {
                 myBox.setColour(defaultColour);
                 this.boxlabel.setFill(myBox.colourPicker("black"));
            }        
        //to do : set shape based on node category too
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
