
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 29.3.18
*/

public class Project extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -243113331412532L;
//setup declare instance variables. shared in class if preceded by static.	
//TO DO: Make this generic i.e. will hold different objects, even if not subclasses?
ArrayList<Collection> myCollections = new ArrayList<Collection>();  
int numItems=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
int numClauses=0; //this will hold number of clauses
String date="";
String ProjectType=""; 
//empty constructor no arguments
public Project() {

}

//FILE FUNCTIONS
public void setDocName(String myString) {
	this.docname=myString;
}

public String getDocName() {
	return this.docname;
}

//CLAUSE OPS
public void addCollection(Collection newCo) {
	this.myCollections.add(newCo);
}

public void addCount() {
	this.numItems++;
}

public int getCount() {
	return this.numItems;
}

public void removeCollection(Collection oldCo) {
	this.myCollections.remove(oldCo);
}

public ArrayList<Collection> getProjectItems() {
	return this.myCollections;
}

public void setProjectItems(ArrayList<Collection> myItems) {
	this.myCollections = myItems;
	//TO DO: iteration? 
}

}