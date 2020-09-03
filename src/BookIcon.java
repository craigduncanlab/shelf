import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//for displaying images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
//ArrayList etc
import java.util.*;




public class BookIcon extends Rectangle{   
    //set default or current colour here?
    String boxcolour; //to hold String with colour description.
    Integer colourIndex;
    Integer stdWith=80;
    Integer stdHeight=80;
    /* Some sample colours:
    "chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown");
    */

//default constructor
public BookIcon() {
    	this.setColour(Color.WHITE); //default
        this.setWidth(this.stdWith); //was (40,150) (width,height)
        this.setHeight(this.stdHeight);
        //setArcWidth(60);  //do this enough you get a circle.  option
        //setArcHeight(60);                
        setStroke(Color.BLACK); //stroke is border colour

     }  

    //constructor with colour
    public BookIcon(String mycol) {
        //default
        
        //this.setColour(mycol);
        this.setWidth(this.stdWith); //was (40,150) (width,height)
        this.setHeight(this.stdHeight);
        /* image too.  
        //This works but proportions must be correct for rectangle if image used as a fill
        Image img = new Image("paper.png");
        this.setFill(new ImagePattern(img));
        */
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        setStroke(Color.BLACK);
        setColour(Color.WHITE); //default just in case
     } 

     //TO DO: Use key, value pairs
     //See https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html

      public void setColour (Color myColour) {
        //Color myColour = colourPicker(mycol);
        //this.boxcolour=myColour;//not updated yet?
        //update the Rectangle Colour for display; allows for transparency
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));
     } 

     public void rotateBookColour() {
        ArrayList <Color>newArray = new ArrayList<Color>( 
            Arrays.asList(Color.BLUE,Color.BLACK, Color.DARKBLUE,Color.LEMONCHIFFON,Color.LIGHTBLUE,Color.GREEN,Color.YELLOW,Color.RED));
        Integer test = newArray.size();
        if (this.colourIndex<test) {
            this.colourIndex=this.colourIndex+1;
        }
        else {
            this.colourIndex=0;
        }
        Color myColour=newArray.get(this.colourIndex);
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6)); //transparency
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
