/* Class to handle global active sprite tracking 

Operates as a controller class; listens for mouse clicks on Sprites and updates current sprite

*/

public class SpriteTracker {

SpriteBox activeSprite;
StageManager currentFocus = new StageManager(); //to hold StageManager that tracks focus.  TO DO: Put focus data in this object.

//constructor
public SpriteTracker() {

}

//constructor - not used?
public SpriteTracker(StageManager myStageMan) {
	this.currentFocus=myStageMan;
}


public void setCurrentSprite(SpriteBox myBox) {
	this.activeSprite=myBox;
	this.activeSprite.doAlert();
}

public SpriteBox getCurrentSprite() {
	return this.activeSprite;
}

//change the focus of the stage and show it
public void setCurrentFocus(StageManager withFocus) {
	this.currentFocus = withFocus;
	//this.currentFocus.getStage().show();  //do not show until last minute 
}

public StageManager getCurrentFocus() {
	return this.currentFocus;
}

//set current sprite to active
public void setActiveSprite(SpriteBox newSprite) {
	SpriteBox cs=getCurrentSprite();
	if (cs!=null) {  //might be no current sprite if not dbl clicked
            cs.endAlert();
    }
    setCurrentSprite(newSprite);
    newSprite.doAlert(); 
    setCurrentFocus(newSprite.getStageLocation());
    }
}