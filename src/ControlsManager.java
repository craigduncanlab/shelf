//Controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

public class ControlsManager {

//Stackbox boxWithFocus = null;
double width = 120;
double minwidth = 150;


//constructor
public ControlsManager() {

}

public Button newStdButton() {
    Button myButton = new Button();
    myButton.setMinWidth(this.minwidth); 
    myButton.setPrefWidth(this.width);  
    return myButton;
}

}