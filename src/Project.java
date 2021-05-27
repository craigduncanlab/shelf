//class to hold a collection or set of Books (boxes/data modules)

//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File; //different to nio.Files

public class Project {
	
ArrayList<Book> booksOnShelf = new ArrayList<Book>();

File file;
String filepath = "";
String parentpath = "";
String filename = "";
String shortfilename=""; //current filename for saving this project's contents
String extension="";
String Rmd_header="";
ArrayList<String> docxStyles;


//constructor
public Project() {
    docxStyles = new ArrayList<String>();
}

// --- FILE OPERATIONS (BASED ON IO.FILE FOR NOW)

public void setFile(File myFile) {
    this.file = myFile;
    setFilepath(this.file.getPath());
    setParentpath(this.file.getParent());
    setFilename(myFile);
}

//this is the entire file path as String with name included
public void setFilenameFromString(String myFile) {
    this.filename = myFile;
}

//takes File object as input
public void setFilename(File myFile) {
String myName = myFile.getName();
	if (myName.length()>0){
		setFilenameFromString(myName);
	}
	else {
		setFilenameFromString("default.md");
	}
}

public void setFilepath(String myPath) {
    this.filepath = myPath;
}

public void setParentpath(String myParent) {
    this.parentpath = myParent;
}

public void setExtension(String myExt) {
    this.extension = myExt;
}

public void setShortFilename(String myFile) {
    this.shortfilename = myFile;
    //this.shelfFileName.setText(this.filename);
}

public String getExt() {
    return this.extension;
}

public File getFile() {
    return this.file;
}

public String getFilepath() {
    return this.filepath;
}

//this returns filename with extension included
public String getFilename() {
	return this.filename;
}

public String getFilenameNoExt(){
	return (getNameNoExt(getFilename()));
}

public String getNameNoExt(String name){
	String output = name.substring(0,name.lastIndexOf("."));
    return output;
}

// -- METADATA FOR DOCX PROJECTS

public void setdocxStyles(ArrayList<String> input){
    this.docxStyles=input;
}


public ArrayList<String> getdocxStyles(){
    return this.docxStyles;
}

// --- PRESERVE METADATA FOR R MARKDOWN and/or Markdown
// TO DO - process further (title, author, date, output)

public void setHeader(String myInput) {
    this.Rmd_header = myInput;
}

public String getHeader() {
    return this.Rmd_header;
}


// --- BOOK OPERATIONS

public void setBooksOnShelf(ArrayList<Book> inputObject) {
    this.booksOnShelf = inputObject;
}

//add a new Book but not before checking it is not duplicate
public Book addBookToProject(Book myBook){
    if (myBook==null) {
        System.out.println("Project. addBookToStage.  No Book to add");
        System.exit(0);
    }
    Book newBook = myBook;
    //if trying to paste into scene twice...FX error  OK but need to deal with it.
    //use intermediate array 'booksOnShelf' to reason about books as a whole.
    if (this.booksOnShelf.contains(myBook)) {
      newBook = myBook.cloneBook(); //make a new object to add to avoid object duplication.
    } 
    //add to the collection of books in app (to DO: layer specific)
    this.booksOnShelf.add(newBook);  //add to metadata collection TO DO: cater for deletions.
    return newBook;
    }

public void removeBook(Book thisBook){
	this.booksOnShelf.remove(thisBook);
}

//sort books by shelf order
public ArrayList<Book> listBooksShelfOrder() {
    ArrayList<Book> myBooksonShelves = getBooksOnShelf();
    System.out.println("First access to sort");
    //System.exit(0);
    ArrayList<Integer> scoreIndexes = new ArrayList();
    Integer booknum=myBooksonShelves.size();
    for (int x=0;x<booknum;x++) {
        Book item = myBooksonShelves.get(x);
        Integer score=item.getRowScore();
        scoreIndexes.add(score);
    }
    Collections.sort(scoreIndexes); //performs a sort
    System.out.println("Sorting collection in Project.java");
    System.out.println(scoreIndexes);
    //System.exit(0);
    System.out.println("Books num: "+booknum);
    ArrayList<Book> sortedBooks = new ArrayList<Book>();
    Iterator<Integer> myIterator = scoreIndexes.iterator();
    System.out.println("Book score printout:\n");
         while (myIterator.hasNext()) {
            Integer targetscore = myIterator.next();
            System.out.println("target:"+targetscore);
            Iterator<Book> bookIterator = myBooksonShelves.iterator();
            while (bookIterator.hasNext()) {
                Book item = bookIterator.next();
                Integer test = item.getRowScore(); //calculated based on row, col
                System.out.println("test score:"+test);
                if (test.equals(targetscore)) {
                    System.out.println("matched");
                    sortedBooks.add(item);
                }
            }

        }
        
        //feedback and exit
        Iterator<Book> bookIterator=sortedBooks.iterator();
        while (bookIterator.hasNext()) {
            Book item = bookIterator.next();
            String label = item.getLabel();
            System.out.println(label);
        }
        //System.exit(0);
        return sortedBooks;
}

public ArrayList<Book> getBooksOnShelf() {
    return this.booksOnShelf;
}

public void clearAllBooks() {
    this.booksOnShelf.clear(); 
}


}