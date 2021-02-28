//(C) Craig Duncan 2017-2021
//www.craigduncan.com.au
//import utilities needed for Arrays lists etc not in Base Java
import java.util.*;

public class Layer{

//instance parameters
Integer layerNumber;
ArrayList<Book> booksInLayer = new ArrayList<Book>(); //should have a Book collection as a layer?	
Boolean visibility = true;

//empty constructor no arguments
public Layer() {

}

public ArrayList<Book> getBooksInLayer() {
	return booksInLayer;
}

public void setBooksInLayer(ArrayList<Book> myBookSet) {
	this.booksInLayer=myBookSet;
}

public void addBookToLayer(Book myBook){
	this.booksInLayer.add(myBook); //add to array list
}

public void removeBookFromLayer(Book myBook){
	this.booksInLayer.remove(myBook); //add to array list
}


}


