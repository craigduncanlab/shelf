
//import utilities needed for Arrays lists etc
import java.util.*;

/*This is a (super)class for a single paragraph object, not a collection.
Implements serializable for save functions.
Order of encapsulation is BoxContainer-->SpriteBox-->ClauseContainer(Node)-->Clause/Child nodes

26.4.18 This originally held data structures for the clause, like heading, footnote etc.
However, it makes more sense to have objects for these (e.g. a notes object) which can be added as child nodes to the ClauseContainer node as required.
The ClauseContainer can provide a function to check whether its child nodes are a particular category.
The text and heading within a 'Clause' object should be able to be duplicated by having these properties added to 
the ClauseContainer.   Add an edit function button to each stage which simply saves any updates to the relevant objects (e.g. the text)
*/

//TO DO: Rename this class as 'Contents' or 'DataObject' etc.  Equivalent to a scene?
public class Clause implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -64701044414208496L;
//setup declare instance variables. shared in class if preceded by static.	
String label=""; //will hold the GUI box term (initially same as def'n)
String heading="";
String interpretation="";
String clausetext=""; //will hold the definition or clause text
String footnote=""; //to hold footnote text
//meta-data or property information
String author=""; //to hold author name
String notes=""; //to hold Document notes

ArrayList<String> Definitions = new ArrayList<String>(); //Currently: list of most freq definition words, not Def objects
ArrayList<String> Keywords = new ArrayList<String>();
String Category = "";
int IndexValue=0; //index for positioning in documents
int frequency = 0;  //frequency of use in text
//TO DO: financial terms.  These may need to be extensions of Clause?
//TO DO: terms, time periods
//new event-related categories
//String eventDesc = "";
//String eventDate = "";
//Event related data TO DO: include a SimpleDateFormat data type
ArrayList<String> Participants = new ArrayList<String>();
ArrayList<String> Witnesses = new ArrayList<String>();
ArrayList<String> Items = new ArrayList<String>();

//empty constructor no arguments
public Clause() {

}

//Clause with properties set at construction
public Clause (String myLabel, String myHeading, String myText, String myCategory) {
	this.label = myLabel;
	this.heading=myHeading;
	this.clausetext=myText;
	this.Category=myCategory;
}

//Category types include definition, clause, event

public void setCategory(String mytext) {
	this.Category=mytext;
}

public String getCategory() {
	return this.Category;
}

//Main clause text for this clause object

public void setClauseText(String mytext) {
	this.clausetext=mytext;
}

public String getClauseText() {
	return this.clausetext;
}

//Add the (external use) label to this clause object

public void setClauselabel(String mytext) {
	this.label=mytext;
}

public String getLabel() {
	return this.label;
}

//heading for this clause object

public void setHeading(String mytext) {
	this.heading=mytext;
}

public String getHeading() {
	return this.heading;
}

public void setClauseinterp(String mytext) {
	this.interpretation=mytext;
}

//freq count

public void setFreq(int myFreq) {
	this.frequency = myFreq;
}

public int getFreq() {
	return this.frequency;
}

public void incFreq() {
	this.frequency++;
}

public Clause clone(Clause myClause) {
    Clause anotherClause = new Clause();
    anotherClause.setClauselabel(myClause.getLabel());
    anotherClause.setClauseText(myClause.getClauseText());
    anotherClause.setHeading(myClause.getHeading());
    anotherClause.setCategory(myClause.getCategory());
    anotherClause.setFreq(myClause.getFreq());
    return anotherClause;
}

}