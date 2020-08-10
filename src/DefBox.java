import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//for displaying images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;


public class DefBox extends Rectangle{   
    //set default or current colour here?
    Color myColour; //colour object for this instance
    String boxcolour; //to hold String with colour description.

//default constructor
    public DefBox() {
    	this.setColour("blue");
        setWidth(150);
        setHeight(40);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        //-->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);

     }  

    //constructor with colour
    public DefBox(String mycol) {
        this.setColour("blue");
        this.setColour(mycol);
        setWidth(150);
        setHeight(40);
        /* image too.  
        //This works but proportions must be correct for rectangle if a fill
        Image img = new Image("paper.png");
        this.setFill(new ImagePattern(img));
        */
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        //--->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);
     } 

     //TO DO: Use key, value pairs

     public void setColour (String mycol) {
        if (mycol.equals("blue")) {
            myColour = Color.BLUE;
            this.boxcolour = mycol;
        }
        if (mycol.equals("green")) {
            myColour = Color.GREEN;
            this.boxcolour = mycol;
        }
         if (mycol.equals("yellow")) {
            myColour = Color.YELLOW;
            this.boxcolour = mycol;
        }
        if (mycol.equals("red")) {
            myColour = Color.RED;
            this.boxcolour = mycol;
        }
        if (mycol.equals("orange")) {
            myColour = Color.ORANGE;
            this.boxcolour = mycol;
        }
        //update the Colour for display; allows for transparency
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));
     }
     public String getColour() {
        return this.boxcolour;
    }
}
