import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//for displaying images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;


public class ColBox extends Rectangle{   
    //set default or current colour here?
    String boxcolour; //to hold String with colour description.

    /* Some sample colours:
    "chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown");
    */

//default constructor
    public ColBox() {
    	this.setColour("salmon"); //default
        this.setWidth(150);
        this.setHeight(40);
        //setArcWidth(60);  //do this enough you get a circle.  option
        //setArcHeight(60);                
        setStroke(Color.BLACK); //stroke is border colour

     }  

    //constructor with colour
    public ColBox(String mycol) {
        this.setColour("salmon"); //default just in case
        this.setColour(mycol);
        setWidth(150);
        setHeight(40);
        /* image too.  
        //This works but proportions must be correct for rectangle if image used as a fill
        Image img = new Image("paper.png");
        this.setFill(new ImagePattern(img));
        */
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        setStroke(Color.BLACK);
     } 

     //TO DO: Use key, value pairs
     //See https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html

      public void setColour (String mycol) {
        Color myColour = colourPicker(mycol);
        this.boxcolour=mycol;//not updated yet?
        //update the Rectangle Colour for display; allows for transparency
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));
     } 

     //this is public so can be generally useful to other objects
     public Color colourPicker (String mycol) {
        if (mycol.equals("blue")) {
            return Color.BLUE;
        }
        if (mycol.equals("black")) {
            return Color.BLACK;
        }
        if (mycol.equals("darkblue")) {
            return Color.DARKBLUE;
        }
        if (mycol.equals("lemon")) {
            return Color.LEMONCHIFFON;
        }
        if (mycol.equals("lightblue")) {
            return Color.LIGHTBLUE;
        }
        if (mycol.equals("green")) {
            return Color.GREEN;
        }
         if (mycol.equals("yellow")) {
            return Color.YELLOW;
        }
        if (mycol.equals("red")) {
           return Color.RED;
        }
        if (mycol.equals("pink")) {
           return Color.DEEPPINK;
        }
        //DARKSLATEGREY
        if (mycol.equals("darkslate")) {
            return Color.DARKSLATEGREY;
        }
        if (mycol.equals("maroon")) {
            return Color.MAROON;
        }
        if (mycol.equals("darkgold")) {
            return Color.DARKGOLDENROD;
        }
        if (mycol.equals("khaki")) {
            return Color.DARKKHAKI;
        }
        if (mycol.equals("orange")) {
            return Color.ORANGE;
        }
        if (mycol.equals("salmon")) {
           return Color.SALMON;
        }
        if (mycol.equals("gold")) {
          return Color.GOLD;
        }
        if (mycol.equals("white")) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
     }
     public String getColour() {
        return this.boxcolour;
    }
}
