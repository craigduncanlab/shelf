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
    //This will return full path to folder where myFile is located)
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

public void addBooksToProject(ArrayList<Book> input) {
    for (Book myItem: input){
        addBookToProject(myItem);
    }
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

//
public void writeOutMDBooksToWord() {    //
    ArrayList<Book> mySaveBooks = listBooksShelfOrder();//getBooksOnShelf();
    String filepath=getFilepath(); //use getFilepath not filename
    docXMLmaker myDocSave = new docXMLmaker(); //we may be able to use the existing docXML in future.
    myDocSave.writeOutWordFromBooks(filepath,mySaveBooks);
}


////for direct save - of 'Books'
public void writeMDFileOut() {
    writeOutBooks();  
}

public void writeOutBooks() {    
    ArrayList<Book> mySaveBooks = listBooksShelfOrder();
    writeOutCommon(mySaveBooks);
}

private void writeOutCommon(ArrayList<Book> mySaveBooks) {
    Iterator<Book> myIterator = mySaveBooks.iterator();
    String myOutput="";
    //String filepath=getFilename(); //just name, no path
    String fn=getFilepathNoExt();
    String filepath="";
    if (getExt().equals(".md")){
        filepath=fn+".md";
    }
    if (getExt().equals(".rmd")){
        filepath=fn+".rmd";
    }
    if (getExt().equals(".docx")){
        fn=fn+"v2.docx";
        File f = new File(fn);
        setFile(f); //TO DO: set a different filename for saves from the stored original name/File
        writeOutMDBooksToWord(); //divert
    }
    System.out.println("Saving: "+filepath); 
         while (myIterator.hasNext()) {
            Book myNode=myIterator.next();
            //System.out.println(myNode.toString());
            String myString=convertBookMetaToString(myNode);
            myOutput=myOutput+myString;
            myString="";
             //option: prepare string here, then write once.
        }
        ZipUtil util = new ZipUtil();
        util.basicFileWriter(myOutput,filepath);
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

//Convert this book meta into a String of markdown.  Only write links if data is there.
public String convertBookMetaToString(Book myBook) {
    String myOutput="# "+trim(myBook.getLabel()); //check on EOL
    String markdown=myBook.getMD();
    String filteredMD=trim(markdown); //trims but inserts EOL
    myOutput=myOutput+filteredMD; //check on EOL
    if (myBook.getdocfilepath().length()>5) {
        String tmp = myBook.getdocfilepath();
        Integer len = tmp.length();
        myOutput=myOutput+"[filepath]("+tmp+")"+System.getProperty("line.separator");
    }
    if (myBook.getdate().length()>6) {
        myOutput=myOutput+"[date]("+myBook.getdate()+")"+System.getProperty("line.separator");
    }
    if (myBook.gettime().length()>4) {
        myOutput=myOutput+"[time]("+myBook.gettime()+")"+System.getProperty("line.separator");
    }
    /*
    if (myBook.getRow()>=0 && myBook.getCol()>=0) {
        myOutput=myOutput+"[r,c]("+myBook.getRow()+","+myBook.getCol()+")"+System.getProperty("line.separator");
    }
    */
    if (myBook.getRow()>=0 && myBook.getCol()>=0) {
        myOutput=myOutput+"[r,c,l]("+myBook.getRow()+","+myBook.getCol()+","+myBook.getLayer()+")"+System.getProperty("line.separator");
    }
    if (myBook.getimagefilepath().length()>6) {
        myOutput=myOutput+"[img]("+myBook.getimagefilepath()+")"+System.getProperty("line.separator");
    }
     if (myBook.geturlpath().length()>6) {
        myOutput=myOutput+"[url]("+myBook.geturlpath()+")"+System.getProperty("line.separator");
    }
    /*
    if (myBook.getX()>0 || myBook.getY()>0) {
        myOutput=myOutput+"[x,y]("+myBook.getX()+","+myBook.getY()+")"+System.getProperty("line.separator");
    }
    */
    if (myBook.getX()>0 || myBook.getY()>0) {
        myOutput=myOutput+"[x,y,z]("+myBook.getX()+","+myBook.getY()+","+myBook.getZ()+")"+System.getProperty("line.separator");
    }
    if (myBook.getthisNotes().length()>0) {
        String notes = myBook.getthisNotes();
        String filteredNote=trim(notes);
        myOutput=myOutput+"```"+System.getProperty("line.separator")+filteredNote+"```"+System.getProperty("line.separator");
    }
    return myOutput;
}

private String trim(String input){
    Scanner scanner1 = new Scanner(input).useDelimiter(System.getProperty("line.separator"));
    ArrayList<String> myList = new ArrayList<String>();
    while (scanner1.hasNext()) {
        String item=scanner1.next();
        System.out.println(item+","+item.length());
        myList.add(item);
    }
    Integer stop=0;
    Integer trimcount=0;
    Integer listlength=myList.size();
    for (int i=listlength-1;i>0;i=i-1) {
        int size = myList.get(i).length();
        if (stop==0 && size==0) {
            trimcount++;
        }
        else {
            stop=1;
        }
    }
    //System.out.println(trimcount);
    //System.out.println(listlength-trimcount-1+","+myList.get(listlength-trimcount-1));
    //System.out.println(listlength-trimcount+","+myList.get(listlength-trimcount));
    StringBuffer newString = new StringBuffer();
    int end = listlength-(trimcount-1);
    if (end<0 || end>listlength-1) {
        end=0; 
        System.out.println("listlength: "+listlength+" trim: "+trimcount);
        //System.exit(0);
        end=listlength;
    }
    for (int i =0;i<end;i++) {
        newString=newString.append(myList.get(i));
        newString=newString.append(System.getProperty("line.separator"));
    }
    //System.out.println(newString);
    //System.exit(0);
    String output = newString.toString();
    return output;
}


}