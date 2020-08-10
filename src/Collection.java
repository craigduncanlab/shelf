
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 27.3.18
29.3.18 - Modify so that a Collection can hold one or more ArrayLists?
*/

public class Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -503004441412532L;
//setup declare instance variables. shared in class if preceded by static.	
//TO DO: Make this generic i.e. will hold different objects, even if not subclasses?
ArrayList<Collection> myCollections = new ArrayList<Collection>();  
String ContainerType=""; 
int numClauses=0; 
int numItems=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String date="";
//Stage ContainerStage = new Stage(); //to hold Stage associated with this container?

//empty constructor no arguments
public Collection() {

}

//CONTAINER TYPE
public void setType(String myString) {
	this.ContainerType=myString;
}

public String getType() {
	return this.ContainerType;
}

//CLONING
public void setNumClauses(int myNum) {
	this.numClauses=myNum;
}

public void setAuthorName(String myString) {
	this.docauthor=myString;
}

public void setNotes(String myString) {
	this.docnotes=myString;
}

public void setDate(String myString) {
	this.date=myString;
}

//FILE FUNCTIONS
public void setDocName(String myString) {
	this.docname=myString;
}

//GETTERS
public String getDocName() {
	return this.docname;
}

public String getAuthorName() {
	return this.docauthor;
}

public String getNotes() {
	return this.docnotes;
}

public String getDate() {
	return this.date;
}

/* STAGE SYNC
public void setStage(Stage myStage) {
	this.ContainerStage=myStage;
}

public String getStage() {
	return this.ContainerStage;
}
*/

//CLAUSE OPS
public void addCC(Collection newCC) {
	this.myCollections.add(newCC);
}

public void addCount() {
	this.numItems++;
}

public int getCount() {
	return this.numItems;
}

/* TO DO: remove clause 
Java ArrayList remove
"Removes the first occurrence of the specified element from this list, if it is present."
*/

public void removeCC(Collection oldCC) {
	this.myCollections.remove(oldCC);
}

public ArrayList<Collection> getCollectionItems() {
	return this.myCollections;
}

public void setCollectionItems(ArrayList<Collection> myItems) {
	this.myCollections = myItems;
	//TO DO: iteration? 
}

public Collection clone() {
	Collection clone = new Collection();
	//clone.setClauseArray(this.myClauses);
	clone.setNumClauses(this.numClauses); //this will hold number of clauses
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setAuthorName(this.docauthor); //to hold author name
	clone.setNotes(this.docnotes);
	clone.setDate(this.date);
	//clone.setType(this.ContainerType);
	return clone;
}

}