
//import utilities needed for Arrays lists etc
//import

//This is a class for a single definition object, not a collection.

public class Definition {
//setup declare instance variables. shared in class if preceded by static.	
String label=""; //will hold the GUI box term (initially same as def'n)
String deftext=""; //will hold the definition text
int frequency = 0;  //frequency of use in text

//empty constructor no arguments
public Definition() {

}

//Add the text to this definition object

public void setDeftext(String mytext) {
	this.deftext=mytext;
}

//Add the label to this definition object

public void setDeflabel(String mytext) {
	this.label=mytext;
}

public String getDef() {
	return this.deftext;
}

public String getLabel() {
	return this.label;

}

public void setFreq(int myFreq) {
	this.frequency = myFreq;
}

public int getFreq() {
	return this.frequency;
}

public void incFreq() {
	this.frequency++;
}

}