
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 21.11.2017
This will store Definition objects
A Definition object as defined here is not just a 'word', it is a combination of data fields 
(e.g. 1. The defined term   2.The text of that defined term
So this document will be a container for definitions, which can then be read off as a list.
Each of those objects will have methods for accessing the defined terms or definitions

nb a set of iteration methods could be written here, but OOP school of thought is that you are better writing an "iterator" object, pass it the [list/object] on creation, then call the iterator methods you need and have it insert/return the object i the list at the relevant place in list.
*/

public class DefContainer {
//setup declare instance variables. shared in class if preceded by static.	
ArrayList<Definition> myDefinitions = new ArrayList<Definition>();  
int numDefs=0; //this will hold number of definitions

//empty constructor no arguments
public DefContainer() {

}

public void addDef(Definition newDef) {
	this.myDefinitions.add(newDef);
}

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/

public void doPrintIteration() {
	//make the iterator <Definition> type otherwise it defaults to Object
	Iterator<Definition> myiterator = this.myDefinitions.iterator();
	while (myiterator.hasNext()) {
		Definition mydefinition = myiterator.next();
		String mylabel = mydefinition.getLabel();
		String mytext = mydefinition.getDef();
		System.out.println(mylabel+" : "+mytext);
	}
}

/* 
This method returns both labels and text.  It uses the instance container. 
*/

public String getDefAndText() {
	String output="";
	//make the iterator <Definition> type otherwise it defaults to Object
	Iterator<Definition> myiterator = this.myDefinitions.iterator();
	while (myiterator.hasNext()) {
		Definition mydefinition = myiterator.next();
		String mylabel = mydefinition.getLabel();
		String mytext = mydefinition.getDef();
		System.out.println(mylabel+" : "+mytext);
		output=output+mylabel+":\n----------\n"+mytext+"\n\n";
	}
	return output;
}

public ArrayList<Definition> getDefArray() {
	return this.myDefinitions;
}


}