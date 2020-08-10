//import utilities needed for Arrays lists etc
import java.util.*;
//Serializable
import java.io.Serializable;

/* By Craig Duncan 13.3.2018 
Aim is to create a Container to store 'SpriteBox' objects on Stage
To then use for load/save functions for serialization.
Depends on SpriteBox class.
QN: is it easier to serialise the BoxContainer as a singleton than deal with ArrayList directly?

nb - all non-serializable fields should be mark transient...e.g.

To do: Use this at project level, so entire project can be dumped into text file for alternative archive form.

*/

public class BoxContainer implements java.io.Serializable {
//setup declare instance variables. shared in class if preceded by static.	
//preface with 'transient' if you don't want these to be serialized.
ArrayList<SpriteBox> myBoxes = new ArrayList<SpriteBox>();  
int numClauses=0; //this will hold number of clauses

//empty constructor no arguments
public BoxContainer() {

}

//uses Java collections property for the .add method
/*TO DO: Method to return if an object is already in ArrayList - call this before adding
Apparently, ArayList.contains() works but may iterate whole set, HashSet might be faster
*/ 

public void addBox(SpriteBox newBox) {
	this.myBoxes.add(newBox);
}

//To do: remove an object from list, if it is in the list.

public void removeBox(SpriteBox myBox) {
	this.myBoxes.remove(myBox);
}
/* Java 8 onwards has remove method.  Iterates internally to find the relevant object */

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/


public ArrayList<SpriteBox> getBoxArray() {
	System.out.println("outputting box array:");
	//System.out.println(this.myBoxes.toString());
	return this.myBoxes;
}

public void ContentsDump() {
	System.out.println("outputting box contents:");
	Iterator<SpriteBox> myIt = this.myBoxes.iterator();
	while (myIt.hasNext()) {
		SpriteBox mySB = myIt.next();
		String sometext = mySB.getClauseText();
		System.out.println("Found SpriteBox with this clause text: "+sometext+"\n");
		}
	}

/* method to return number of SpriteBoxes in the Container */

public int getNumBoxes() {
	return this.myBoxes.size();
}

//TO DO: Methods to delete or remove boxes


}