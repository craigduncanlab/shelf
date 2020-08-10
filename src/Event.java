
//import utilities needed for Arrays lists etc
import java.util.*;

/*This is a class to extend (i.e. subclass of) the Clause class.
Allows for some specific and additional information in editor if Clause is also an instanceOf Event
Inherits serializable property from Clause
Requires specific constructors consistent with superclass as these are not inherited.

Order of encapsulation is BoxContainer-->SpriteBox-->Clause
*/

public class Event extends Clause {

private static final long serialVersionUID = -4726827960765930891L;
//new categories
String eventDesc = "";
String eventDate = "";
//Event related data TO DO: include a SimpleDateFormat data type
ArrayList<String> Participants = new ArrayList<String>();
ArrayList<String> Witnesses = new ArrayList<String>();
ArrayList<String> Items = new ArrayList<String>();

//empty constructor no arguments
public Event() {

}

//Event with basic properties set at construction
public Event (String myLabel, String myText) {
	this.label = myLabel;
	this.heading=myLabel;
	this.clausetext="Event may have clause text"; //or event text
	this.eventDesc=myText;
	this.Category="event";
}

//Clause with properties set at construction
//nb: to utilise existing Clause constructors code, Event class needs to mirror the argument syntax
//Otherwise write Event class specific code where needed.
public Event(String myLabel, String myHeading, String myText, String myCategory) {
	this.label = myLabel;
	this.heading=myHeading;
	this.clausetext=myText;
	this.Category=myCategory;
	//override
	this.Category="event";
	this.eventDesc=myText;
}

public void setDate(String mytext) {
	this.eventDate=mytext;
}

public String getDate() {
	return this.eventDate;
}

//Main clause text for this clause object

public void setEventDesc(String mytext) {
	this.eventDesc=mytext;
}

public String getEventDesc() {
	return this.eventDesc;
}

public Event clone(Event myClause) {
	Event anotherClause = new Event();
	anotherClause.setEventDesc(myClause.getEventDesc());
    anotherClause.setDate(myClause.getDate());
    anotherClause.setClauselabel(myClause.getLabel());
    anotherClause.setClauseText(myClause.getClauseText());
    anotherClause.setHeading(myClause.getHeading());
    anotherClause.setCategory(myClause.getCategory());
    anotherClause.setFreq(myClause.getFreq());
    return anotherClause;
}

}