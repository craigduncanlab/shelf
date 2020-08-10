
//import utilities needed for Arrays lists etc
import java.util.*;

/* By Craig Duncan 29.3.2018

A class to hold projects (high level container)

*/

public class ProjectContainer implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -3871234567208496L;
//setup declare instance variables. shared in class if preceded by static.	
//TO DO: Make this generic i.e. will hold different objects, even if not subclasses?
ArrayList<Project> projectList = new ArrayList<Project>(); 
String ProjectType=""; 
int numClauses=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String date="";

//Stage ContainerStage = new Stage(); //to hold Stage associated with this container?

//empty constructor no arguments
public ProjectContainer() {

}

//CONTAINER TYPE
public void setType(String myString) {
	this.ProjectType=myString;
}

public String getType() {
	return this.ProjectType;
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
public void addProject(Project newProject) {
	this.projectList.add(newProject);
}

/* TO DO: remove clause 
Java ArrayList remove
"Removes the first occurrence of the specified element from this list, if it is present."
*/

public void removeProject(Clause oldProject) {
	this.projectList.remove(oldProject);
}


/*Method to set ProjectContainer's array by copying each entry
Should it be Object or clause ? */

public void setProjectArray(ArrayList<Project> myArray) {
	this.projectList = new ArrayList<Project>(); 
	Iterator<Project> myIterator = myArray.iterator(); 
	while (myIterator.hasNext()) {
		Project tempProject = myIterator.next();
		this.projectList.add(tempProject);
	}
}

public ArrayList<Project> getProjectList() {
	return this.projectList;
}

/* method to return number of clauses in this Container */

public int getNumProjects() {
	return this.projectList.size();
}

public ProjectContainer cloneContainer() {
	ProjectContainer clone = new ProjectContainer();
	clone.setProjectArray(this.projectList); 
	clone.setDocName(this.ProjectType); 
	//clone.setNumClauses(this.numProjects); //this will hold number of clauses
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setAuthorName(this.docauthor); //to hold author name
	clone.setNotes(this.docnotes);
	clone.setDate(this.date);
	clone.setType(this.ProjectType);
	return clone;
}

}