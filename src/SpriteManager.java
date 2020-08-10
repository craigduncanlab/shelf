/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

public class SpriteManager {

//Stackbox boxWithFocus = null;
SpriteBox currentSprite = null;
SpriteBox targetSprite = null;
String StageFocus = "";

//locations (open stage/window)
int WS = 1;
int DOC = 2;
int LIB = 3;
int COLL = 4;
int PROJ = 5;
int PROJLIB = 6;
int OTHER = 99;


//constructor
public SpriteManager() {

}

/* Set target sprite.  The Target Sprite differs from 'current sprite' in that it is set 
here, in the Sprite Manager and isn't necessarily the 'current sprite' triggered by a mouse event.
Target sprite is the last tagged sprite e.g. the last sprite that had its red alert triggered?
e.g. current sprite could be a box that is merely dragged, but not 'selected'.
No need for this distinction?  Just change focus when touched?
*/

public void setTargetSprite(SpriteBox mySprite) {
    this.targetSprite=mySprite;
}

public SpriteBox getCurrentSprite(){

	return this.currentSprite;
}

public double[] getXY() {
        double[] output = this.currentSprite.getXY();
        return output;
    }

public void setXY(double x, double y) {
        //sync
         this.currentSprite.setXY(x,y);
}

public void setCurrentSprite(SpriteBox mySprite, String thisStage) {
        
         if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        this.currentSprite=mySprite;
        mySprite.doAlert();  
    }
public void setStageFocus(String myFocus) {
    this.StageFocus=myFocus;
}

public String getStageFocus() {
    return this.StageFocus;
}
 
}