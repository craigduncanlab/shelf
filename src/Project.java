//class to hold a collection or set of Books (boxes/data modules)

//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File; //different to nio.Files

public class Project {
	
ArrayList<Book> booksOnShelf = new ArrayList<Book>();

File file;
File rowfile; //to hold separate file details
String filepath = "";
String rowfilepath="";
String parentpath = "";
String rowparentpath="";
String filename = "";
String shortfilename=""; //current filename for saving this project's contents
String extension="";
String Rmd_header="";
ArrayList<String> docxStyles;
docXML openDocx = new docXML();
mdFile openMD = new mdFile();
mdFile openRMD = new mdFile();

//constructor
public Project() {
    //docxStyles = new ArrayList<String>();
}

public void setOpenDocx(docXML input) {
    this.openDocx=input;
    addBooksToProject(input.getBooklist());
}

public docXML getOpenDocx() {
    return this.openDocx;
}

public void setOpenMD(mdFile input){
    this.openMD = input;
    addBooksToProject(input.getBooklist());
}

public void setOpenRMD(mdFile input){
    this.openRMD = input;
    addBooksToProject(input.getBooklist());
}

public mdFile getOpenMD() {
    return this.openMD;
}

// --- FILE OPERATIONS (BASED ON IO.FILE FOR NOW)

public void setFile(File myFile) {
    this.file = myFile;
    setFilepath(this.file.getPath());
    //This will return full path to folder where myFile is located)
    setParentpath(this.file.getParent());
    setFilename(myFile);
}

public void setRowFile(File myFile) {
    this.rowfile = myFile;
    setRowFilepath(this.rowfile.getPath());
    //This will return full path to folder where myFile is located)
    setRowParentpath(this.rowfile.getParent());
    //setFilename(myFile);
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

public void setRowFilepath(String myPath) {
    this.rowfilepath = myPath;
}

public void setParentpath(String myParent) {
    this.parentpath = myParent;
}

public void setRowParentpath(String myParent) {
    this.rowparentpath = myParent;
}

//This will return full path (to where project file was loaded from)
public String getParentPath() {
    return this.parentpath;
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

public String getFilepathNoExt() {
    String name = this.filepath;
    String output = name.substring(0,name.lastIndexOf("."));
    return output;
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

public void addBooksToProject(ArrayList<Book> input) {
    for (Book myItem: input){
        addBookToProject(myItem);
    }
}

//add a new Book but not before checking it is not duplicate
//Even though this is for data only purposes, because a 'Book' is also (for now) a JavaFX object
//We need to ensure that it's basic FX setup is complete, including adding event handlers
//by the time it is added to stage.
public Book addBookToProject(Book myBook){
    if (myBook==null) {
        System.out.println("Project. addBookToStage.  No Book to add");
        System.exit(0);
    }
    Book newBook = myBook;
    if (this.booksOnShelf.contains(myBook)) {
      newBook = myBook.cloneBook(); //make a new object to add to avoid object duplication.
    } 
    int numbooks = this.booksOnShelf.size();
    //if more than one book adopt a default position before layouts
    if (numbooks>0) {
        Book lastBook = this.booksOnShelf.get(numbooks-1);
        int row = lastBook.getRow();
        int col = lastBook.getCol();
        newBook.setCol(col+1);
        newBook.setRow(row);
     }
    //add to the collection of books in app 
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

//
public void writeOutMDBooksToWord() {    //
    ArrayList<Book> mySaveBooks = listBooksShelfOrder();//getBooksOnShelf();
    String filepath=getFilepath(); //use getFilepath not filename
    docXMLmaker myDocSave = new docXMLmaker(); //we may be able to use the existing docXML in future.
    myDocSave.writeOutWordFromBooks(filepath,mySaveBooks);
}

public void writeDocxNewStyles(){
    docXML myDoc = getOpenDocx();
    myDoc.saveDocxWithNewStylesOnly(getFilepath()); //uses current project name and path after chooser
}


////for direct save - of 'Books' - main MD file saver.
public void writeMDFileOut() {
    writeOutBooks();  
}

public void writeOutBooks() {    
    ArrayList<Book> mySaveBooks = listBooksShelfOrder();
    System.out.println("SaveKeyEventHandler..");
    System.out.println("BACK TO WRITE FILE OUT.");
     System.out.println("BACK TO PROJECT FOR WRITEMDFILE OUT+WRITEOUTBOOKS.");
 
    writeOutCommon(mySaveBooks);
}

//Should this be an MD writeout only
private void writeOutCommon(ArrayList<Book> mySaveBooks) {
    
    //CONTENT AND FILEPATHS
    String filepath="";
    String fn=getFilepathNoExt(); 
    System.out.println("Filepath:"+fn);
    System.out.println(getExt()); //This has no dot in it
    if (getExt().equals("md")){
         System.out.println("Filepath:"+fn);
         System.out.println("1");
   
        filepath=fn+".md";
        markdownMaker myMD = new markdownMaker(filepath);
        String myOutput=myMD.makeMarkdown(mySaveBooks);
    }
    if (getExt().equals("rmd")){  //opened markdown
         System.out.println("Filepath:"+fn);
         System.out.println("2");
        filepath=fn+".rmd";
        markdownMaker myMD = new markdownMaker(filepath);
        String myOutput=myMD.makeMarkdown(mySaveBooks);
        writeOutMDBooksToWord(); //divert.   But if there was no markdown in blocks, only works with ooxml.
    }
    if (getExt().equals("docx")){ //original file was docx
         System.out.println("Filepath:"+fn);
         // System.out.println("3");
        // System.exit(0);
        fn=fn+"v2.docx";
        System.out.println("Saving: "+filepath); 
        File f = new File(fn);
        setFile(f); //TO DO: set a different filename for saves from the stored original name/File
      //  writeOutMDBooksToWord(); //divert.  If original file was docx, we shouldn't do this.
        System.out.println("Finished original docx save");
        
    }
}

public void writeRowOut(Integer row) {
    //using existing filename
    ArrayList<Book> myBookSet = listBooksShelfOrder();//getBooksOnShelf();
    ArrayList<Book> myBookRow = new ArrayList<Book>(); 
    //filter these to just one row
    Iterator<Book> myIterator = myBookSet.iterator();
    while (myIterator.hasNext()) {
      Book thisBook = myIterator.next();
      Integer checkRow = thisBook.getRow();
      //integer comparison.
      if (checkRow.intValue()==row.intValue()) {
        myBookRow.add(thisBook);
      }
    }
    writeOutCommon(myBookRow);
}

}