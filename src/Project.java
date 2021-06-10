//class to hold a collection or set of Books (boxes/data modules)

//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File; //different to nio.Files

public class Project {
	
ArrayList<Book> booksOnShelf = new ArrayList<Book>();

File docFile;
File rowfile; //to hold separate file details
String filepath = "";
String rowfilepath="";
String parentpath = "";
String rowparentpath="";
String filename = "";
String shortfilename=""; //current filename for saving this project's contents
String extension="";
String Rmd_header="";
String splitOption="OutlineLvl0";
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
    ArrayList<Book> myBooks = makeBooksFromXMLBlocklist();
    clearAllBooks(); //in case there are books from previous project
    addBooksToProject(myBooks);
}

public docXML getOpenDocx() {
    return this.openDocx;
}

public void setOpenMD(mdFile input){
    this.openMD = input;
    ArrayList<Book> myBooks = makeBooksFromMDBlocklist(input);
    addBooksToProject(myBooks);
}

public void setOpenRMD(mdFile input){
    this.openRMD = input;
    addBooksToProject(input.getBooklist());
}

public mdFile getOpenMD() {
    return this.openMD;
}

private void setDocFile(File input){
    this.docFile=input;
}

public File getDocFile(){
    return this.docFile;
}

// NUMBER OF BOOKS ON SHELF

public int getNumberBooks(){
    return getBooksOnShelf().size();
}

// SPLITTING

//this doesn't perform a save - it just reworks content from xmlParas.
//it also doesn't place books on stage; it merely updates what project describes as books.

public void updateSplitOptionBooks() {
    
    if (getExt().equals("docx")) {
        ArrayList<Book> myBooks = makeBooksFromXMLBlocklist(); //differs depending on split options
        if (myBooks.size()>0) {
            clearAllBooks(); //clear all books from project
            addBooksToProject(myBooks);
        }
       
    }
    if (getExt().equals("md")) {
        clearAllBooks(); //clear all books from project
        ArrayList<Book> myBooks = makeBooksFromMDBlocklist(this.openMD); //differs depending on split options
        if (myBooks.size()>0) {
            clearAllBooks(); //clear all books from project
            addBooksToProject(myBooks);
        }
    }

    if (getExt().equals("rmd") || getExt().equals("Rmd")) {
        clearAllBooks(); //clear all books from project
        ArrayList<Book> myBooks = makeBooksFromMDBlocklist(this.openRMD); //differs depending on split options
        if (myBooks.size()>0) {
            clearAllBooks(); //clear all books from project
            addBooksToProject(myBooks);
        }
    }
}

public void setSplitOption(int input){
    if (input==1) {
        this.splitOption="OutlineLvl0";
    }
    if (input==2) {
        this.splitOption="Bookmarks";
    }
}

private String getSplitOption(){
        return this.splitOption;
}

// --- FILE OPERATIONS (BASED ON IO.FILE FOR NOW)

public void setFile(File myFile) {
    setDocFile(myFile);
    setFilepath(myFile.getPath());
    setParentpath(myFile.getParent());
    setFilename(myFile);
}

public void setRowFile(File myFile) {
    setFile(myFile);
}

//this is the entire file path as String with name included
public void setFilenameFromString(String myFile) {
    this.filename = myFile;
}

//takes File object as input
private void setFilename(File myFile) {
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

private void setRowFilepath(String myPath) {
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

//  HANDLE LOADING IN OF DIFFERENT FILE TYPES TO PROJECT

public ArrayList<Book> makeBooksFromXMLBlocklist(){
    ArrayList<xmlBlock> myBlockList = new ArrayList<xmlBlock>(); 
    if (getSplitOption().equals("OutlineLvl0")) {
        myBlockList = this.openDocx.getBlocklist();  //choose blocks depending on Split setting.
    }
    if (getSplitOption().equals("Bookmarks")) {
        myBlockList = this.openDocx.getBookmarkBlocklist(); //same for now
    }
    ArrayList<Book> myBookList = new ArrayList<Book>();
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int length = myBlockList.size();  // number of blocks
      System.out.println(length); //each of these numbered blocks is a string.
      int rowcount=0;
      if (length>0) {
        Iterator<xmlBlock> iter = myBlockList.iterator(); 
          while (iter.hasNext()) {
              xmlBlock myBlock = iter.next();
              Book newBook =new Book(myBlock);  //constructor handles xmlBlock or mdBlock differently (polymorphism!)
              System.out.println("Book heading:"+newBook.getLabel());
              //
              if (newBook!=null) {
                //set default position for GUI?
                newBook.setRow(rowcount); //default col is 0.
                newBook.setCol(0);
              }
              else {
                System.out.println("Nothing returned from parser");
              }
              myBookList.add(newBook);
              rowcount++;
         } //end while
      } //end if
    return myBookList;
    }

public ArrayList<Book> makeBooksFromMDBlocklist(mdFile myDoc){
      ArrayList<mdBlock> myBlockList = myDoc.getBlocklist();
      ArrayList<Book> myBookList = new ArrayList<Book>();
      int length = myBlockList.size();  // number of blocks
      Parser myParser=new Parser();
      //starting with the blocklist, get blocks and put each one inside a 'Book' object
      int rowcount=0;
      if (length>0) {
        Iterator<mdBlock> iter = myBlockList.iterator(); 
          while (iter.hasNext()) {
            mdBlock myBlock = iter.next();
            Book newBook =new Book(myBlock); //constructor handles xmlBlock or mdBlock differently (polymorphism!)
            //add book to list
            myBookList.add(newBook);
            rowcount++;
         } //end while
      } //end if        
      else {
        System.out.println("Nothing returned from parser");
      }
    return myBookList;
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
    myDoc.saveDocxWithNewStylesOnly(getDocFile()); //uses current file after chooser
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