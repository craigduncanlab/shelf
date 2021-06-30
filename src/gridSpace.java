/*
(c) Craig Duncan 28 June 2021

A JavaFX gridspace for visualisation in 2D
TO DO: the Pane should only display a 'Window' of content and then shift content, so that it is an infinite size and does not need to be defined.
We might need at least 30 rows for some outline views of Word docs etc.
*/

//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning and size/bounds
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;
//lines and shapes for joining
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
//Scene graph (nodes) and traversal
import javafx.scene.Group;
//Scene - Text controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.layout.Pane;

public class gridSpace {
	Group gridGroup = new Group(); //subgroup of Pane where boxes are stored
	ScrollPane gridSpaceScrollPane;
	Pane workspacePane = new Pane();
	Integer cellcols=50;
	Integer cellrows=50; //make this 50 for sure
	Integer cellgap_x=80; //cellwidth x dimension
	Integer cellgap_y=100; //cell width y dimension
	Integer firstcell_x=this.cellgap_x;
	Integer firstcell_y=0;
	Integer cellrowoffset_y=30;
	Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
	double myBigX = ScreenBounds.getWidth();
	double myBigY = ScreenBounds.getHeight();
	double wsPaneWidth=0.8*myBigX;
	double wsPaneHeight=0.8*myBigY;
	double scrollSceneWidth=0.8*myBigX;
	double scrollSceneHeight=0.8*myBigY;
	int spriteX = 0;
	int spriteY = 0;
	Integer boxtopmargin=10;
	ArrayList<Book> selectedBooks = new ArrayList<Book>(); //for GUI selections
	ArrayList<Book> booksInView = new ArrayList<Book>();
	Book focusBook; //for holding active sprite in this scene.  Pass to app.
	
public gridSpace(){
	makeScrollPane();
	makePane();
}

private void makeScrollPane(){
		ScrollPane myScrollPane = new ScrollPane(workspacePane); //content is the workspacePane
		myScrollPane.setPrefViewportWidth(this.scrollSceneWidth);
		myScrollPane.setPrefViewportHeight(this.scrollSceneHeight);
		myScrollPane.pannableProperty().set(false);  //to prevent panning by mouse
		myScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS")); //AS_NEEDED or ALWAYS
		myScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.valueOf("ALWAYS"));
        String scrollpaneStyle="-fx-background-color:blue; ";
        myScrollPane.setStyle(scrollpaneStyle);
        setScrollPane(myScrollPane);
}

private void makePane(){
	workspacePane.getChildren().addAll(gridGroup);
	//not white or LightGrey //main grid background colour
	String wpaneStyle="-fx-background-color: CADETBLUE; "; 
	workspacePane.setStyle(wpaneStyle);
	makeGridLines();
}

private void makeGridLines() {
	//Make horizontal lines for grid, and add to FX root node for this Stage
		ArrayList<Line> myRowLines=new ArrayList<Line>();
		double startX=0.0; //+cellrowoffset_y;
		double endX=(this.cellcols+2)*this.cellgap_x;
		for (int i=0;i<this.cellrows+2;i++) {
            Line line = new Line(startX,(i*cellgap_y)+cellrowoffset_y,endX,(i*cellgap_y)+cellrowoffset_y);
            myRowLines.add(line); //future use
            this.workspacePane.getChildren().add(line); //put them here so they are not 'erased' and remains visible
        }
        ArrayList<Line> myColLines=new ArrayList<Line>();
        double startY=0.0+this.cellrowoffset_y;
        double endY=((this.cellrows+2)*this.cellgap_y)+this.cellrowoffset_y;
        //we need 2 extra lines
        for (int i=0;i<this.cellcols+3;i++) {
            //System.out.println(cellrows+", i:"+i+" startY:"+startY+" endY:"+endY);
            Line line2 = new Line(i*this.cellgap_x,startY,i*this.cellgap_x,endY);
            myColLines.add(line2); //future use
            this.workspacePane.getChildren().add(line2);
        }
}

private void setScrollPane (ScrollPane input){
	this.gridSpaceScrollPane= input;
}

public ScrollPane getScrollPane(){
	return this.gridSpaceScrollPane;
}


private void setGridGroup(Group myGroup) {
    this.gridGroup = myGroup;
}

public Group getGridGroup() {
    return this.gridGroup;
}

public Boolean isBookInGrid(Book myBook){
	if (this.gridGroup.getChildren().contains(myBook)) {
		return true;
	}
	else {
		return false;
	}
}

/*
Function that will add a Book object (created externally)
Need clearer data/display separation
4 tasks: add to data array, add to FX node, set as active, update XY from row,col
*/

public void addBook(Book newBook){
	if (isBookInGrid(newBook)) {
            return; //<----do nothing: JAVAFX ADDS OBJECT. CAN'T DO TWICE (ERROR)
  }
  else {
    //data
    this.booksInView.add(newBook);
    //FX node
		this.gridGroup.getChildren().add(newBook);
		//selection
		singleSelection(newBook); //ok?
    //graphic position
    setXYfromRowCol(newBook);
	}
}

/*
pasteBook is like addBook but it shifts the location one column to right
The event handlers are already there because it uses clone function to create.
*/

public void pasteBook(Book newBook){
	Integer xcol=newBook.getCol()+1;
    newBook.setCol(xcol); //offset
    addBook(newBook);
}

//clears both data model and FX based object
public void clear(){
	this.booksInView = new ArrayList<Book>();
	this.gridGroup.getChildren().clear();
}

public void remove(Book thisBook){
	this.booksInView.remove(thisBook);
	this.gridGroup.getChildren().remove(thisBook); //view/GUI
}


public void setLabelsMode(String input) {
    ArrayList<Book> myBooksOnShelves=getBooksInView();
    Integer booknum=myBooksOnShelves.size();
    logger("Books number:"+booknum);
    for (Book item : myBooksOnShelves) {
        item.setDisplayMode(input); //only affects local pointer
        //logger(""+input);
    }
    setBooksInView(myBooksOnShelves); //update contents of pointer
}

/*
Function to handle how boxes are displayed, depending on user choice
*/
public void setLayoutMode(String input){
	if (input.equals("wrap")){
		wrapHorizontal(1);
	}
	else if (input.equals("checkers")){
		wrapHorizontal(2);
	}
	else if (input.equals("row")){
		unpackBooksAsRow();
	}
	else if (input.equals("col")){
		unpackBooksAsCol();
	}
}

private void unpackBooksAsRow() {
    if (getNumberBooks()>20){
        wrapHorizontal(1);
    } 
    setHorizontalLayout();
}

private void unpackBooksAsCol() {
    if (getNumberBooks()>20){
        wrapHorizontal(1); //not vertical?
    } 
    setVerticalLayout();
}

private void logger (String input){
	System.out.println(input);
}

//snap to grid - move to nearest cell based on <= 50% out of position
public double snapYtoShelf(Book myBook, double newTranslateY){
    Integer modY=(int)newTranslateY%(int)this.cellgap_y;
    Integer quotY=(int)((newTranslateY-modY)/this.cellgap_y);
    Integer half = (int)this.cellgap_y/2;
    System.out.println("Num: "+newTranslateY+"cellgapY: "+this.cellgap_y+" quot:"+quotY+" mod:"+modY);
    if (modY>half) {
        quotY=quotY+1;
    }
    //checkbounds
    if (quotY<0) {
        quotY=0;
    }
    if(quotY>(this.cellrows-1)) {
        quotY=(this.cellrows-1);
    }
    myBook.setRow(quotY);
    newTranslateY=(this.cellgap_y*quotY)+this.cellrowoffset_y+this.boxtopmargin;
    return newTranslateY;
}

//snap to grid - move to nearest cell based on <= 50% out of position
public double snapXtoShelf(Book myBook, double newTranslateX){
    Integer modX=(int)newTranslateX%(int)this.cellgap_x;
    Integer quotX=(int)((newTranslateX-modX)/this.cellgap_x);
    Integer half = (int)this.cellgap_x/2;
    System.out.println("Num: "+newTranslateX+"cellgapX: "+this.cellgap_x+" quot:"+quotX+" mod:"+modX);
    if (modX>half) {
        quotX=quotX+1;
    }
    //checkbounds
    if (quotX<0) {
        quotX=0;
    }
    if(quotX>this.cellcols+1) {
        quotX=this.cellcols+1;
    }
    myBook.setCol(quotX);
    newTranslateX=this.cellgap_x*quotX;
    return newTranslateX;
}

public void snapBook(Book currentBook, double newTranslateX, double newTranslateY){
	newTranslateY=snapYtoShelf(currentBook,newTranslateY);
    newTranslateX=snapXtoShelf(currentBook,newTranslateX);
    //
    currentBook.setTranslateX(newTranslateX);
    currentBook.setTranslateY(newTranslateY);
    currentBook.doAlert();
}

//this acts as a layout API - converts raw row, col to pixel coords
private double convertRowtoY(Integer myRow){
     double newY=(myRow*this.cellgap_y)+this.cellrowoffset_y+this.boxtopmargin;
     return newY;
}

private double convertColtoX(Integer myCol) {
    double newX=(myCol*this.cellgap_x);
    return newX;
}

/*
Function to update Book X,Y position based on current Row,Col coordinates

*/

private void setXYfromRowCol(Book myBook) {
    int minimumY=40;
    double newY=this.cellgap_y*myBook.getRow()+minimumY;
    double newX=this.cellgap_x*myBook.getCol();
    myBook.setXY(newX,newY);
}

/*
Function to update Book X,Y position based on some new Row,Col coordinates
*/

public void setXYfromNewRowCol(Book myBook, Integer row, Integer col) {
    int minimumY=40;
    double newY=this.cellgap_y*row+minimumY;
    double newX=this.cellgap_x*col;
    myBook.setXY(newX,newY);
}


//SELECTION FUNCTIONS

public Book getActiveBook() {
    if (this.focusBook==null) {
        System.out.println("No book in setActiveBook method");
        return null;//just creates one
    }
    return this.focusBook;
}

public ArrayList<Book> getSelectedBooks(){
	return this.selectedBooks;
}

//return the number of books loaded into this view
private Integer getNumberBooks(){
	return booksInView.size();
}

//this.selectedBooks.size()

public Integer getNumberSelectedBooks(){
	return this.selectedBooks.size();
}

public void setSelectedBooks(ArrayList<Book> input){
	this.selectedBooks=input;
}

public void singleSelection(Book thisBook){
  
  if (this.selectedBooks.size()>0) {
      for (Book item: this.selectedBooks) {
         item.endAlert();
      }
  }
  
  try {
      ArrayList<Book> newSelection= new ArrayList<Book>();
      newSelection.add(thisBook);
      this.focusBook=thisBook; 
      thisBook.doAlert(); //Change this so there is a general 'undo alert'
      this.selectedBooks=newSelection;
  }
  catch (Throwable t) //for greater detail for debugging
        {
            t.printStackTrace();
            return;
        }
}

/*
Helper function (currently not used) that will cycle selected cells
and ensure selected are in red
*/

private void refreshSelectedBooksColour(ArrayList<Book> sorted) {
  
  Iterator <Book> myIterator = sorted.iterator();
  while(myIterator.hasNext()){
    Book item = myIterator.next();
    if (this.selectedBooks.contains(item)) {
      item.doAlert();
    }
    else {
      item.endAlert();
    }
  }
}


public void toggleExtraSelection(Book thisBook){
    //ArrayList<Book> myBooksonShelves = getBooksOnShelf();

    if (selectedBooks.contains(thisBook)) {
        int num = selectedBooks.size();
        System.out.println("selectedBooks items:"+num);
        for (int x =0;x<num;x++){
          System.out.println(selectedBooks.get(x).getLabel());
        }
        selectedBooks.remove(thisBook);
        thisBook.endAlert();
        System.out.println("Toggle, but found book");
    }
    else if (!selectedBooks.contains(thisBook)) {
        selectedBooks.add(thisBook);
        thisBook.doAlert();
        this.focusBook=thisBook;
        System.out.println("Toggle, but did not find book");
    }
}

//If this was purely visual, we would retrieve books from the nodes, rather than
//The underlying data model
public ArrayList<Book> getBooksInView(){
	return this.booksInView;
}

public void setBooksInView(ArrayList<Book> input){
	this.booksInView=input;
}

public void shiftedSelection(Book thisBook) {
      Book firstBook = this.selectedBooks.get(0);
      ArrayList<Book> newList = new ArrayList<Book>();
      //newList.add(firstBook); //start selection again with only origin book
      this.selectedBooks = newList;
      ArrayList<Book> sorted=getBooksInView();//myProject.listBooksShelfOrder(); //can this be stored, only updated when needed?
      Iterator <Book> myIterator = sorted.iterator();
      Boolean selection=false;
      Boolean stop=false;
      while(myIterator.hasNext()) {
      Book item = myIterator.next();
      item.endAlert(); //reset

      //find start of selection
      if (item==firstBook && !this.selectedBooks.contains(thisBook)){
            selection=true;
      }
      if (item==thisBook && !this.selectedBooks.contains(firstBook)){
            selection=true;
      }
      //find end of selection
      if (item==firstBook && this.selectedBooks.contains(thisBook)){
            stop=true;
            selection=false;
            this.selectedBooks.add(item);
            item.doAlert();
      }
      if (item==thisBook && this.selectedBooks.contains(firstBook)){
            stop=true;
            selection=false;
            this.selectedBooks.add(item);
            item.doAlert();
      }
      //if still in mid range selection
      if (selection==true && !this.selectedBooks.contains(item)) {
            this.selectedBooks.add(item); 
            item.doAlert();     
      }
    } //end while
   }

/*
The default origin is (0,0)
*/

public void setVerticalLayout(){
  ArrayList<Book> myBookSet = getBooksInView();
  //not yet the project booklist? DO THAT.  get these books from project.
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int rowcount=0;
  //STEP 1: SET GUI PARAMETERS BEFORE ADDING TO STAGE
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
          Book thisBook =iter.next(); 
          if (thisBook!=null) {
           
            //set position as part of data model
            thisBook.setRow(rowcount); //default col is 0.
            thisBook.setCol(0);
            }
            else {
              System.out.println("No book to add");
            }           
            rowcount++;
          } //end while
     } //end if
     updateAllXYFromRowCol();
  } 

/* 
input:
All stored books in this view.

Output/update:

Arranges row, col and stores in each object.
Updates the x,y GUI positions at end, in bulk.
This will update in the scene to which these nodes are already child nodes.
*/

//TO DO: Allow function to take a row number for insert.

/*
The default origin is (0,0)
*/

public void setHorizontalLayout(){
  ArrayList<Book> myBookSet = getBooksInView();
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int colcount=0;
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
          Book thisBook =iter.next(); 
          //
          if (thisBook!=null) {
            thisBook.setCol(colcount); //default col is 0.
            thisBook.setRow(0);
            }
            else {
              System.out.println("No book to add");
            }
            colcount++;
          } //end while
     } //end if
     updateAllXYFromRowCol();
  }

/*
The default origin is (0,0)
*/

public void wrapHorizontal(int space){
  ArrayList<Book> myBookSet = getBooksInView();
  int length = myBookSet.size();  // number of blocks
  System.out.println(length); //each of these numbered blocks is a string.
  int colcount=0;
  int rowcount=0;
  int wrapcol=11;//column to wrap on
  if (length>0) {
    Iterator<Book> iter = myBookSet.iterator(); 
      while (iter.hasNext()) {
        //if books do not appear change the pointer to iter.next() directly
          Book thisBook =iter.next(); 
          //
          if (thisBook!=null) {
            //set position as part of data model
            thisBook.setCol(colcount); //default col is 0.
            thisBook.setRow(rowcount);
            }
            else {
              System.out.println("No book to add");
            }
            if (colcount>wrapcol) {
                //checkers
              if (space==2 && rowcount % 2 != 0) {
                colcount=1;
              }
              else {
                colcount=0;
              }
              rowcount=rowcount+1;
            }
            else {
              colcount=colcount+space;
            }
          } //end while        
     } //end if
     updateAllXYFromRowCol();
  }  

/*
General function to update the GUI x,y positions of all book objects
based on current row,col (integer) representation
*/

private void updateAllXYFromRowCol(){

	for (Book item : getBooksInView()) {
		setXYfromRowCol(item);
	}
} 


public void insertRowActive(String input){
	Book myBook = getActiveBook();
	Integer row=myBook.getRow();
	if (input.equals("before")){
		insertRow(row);
	}
	if (input.equals("after")){
		insertRow(row+1);
	}
}

private void insertRow(Integer firstrow){
    ArrayList<Book> bookList = getBooksInView();
    Iterator<Book> myIterator=bookList.iterator();
    while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        if (checkRow>=firstrow) {
            checkRow=checkRow+1;
            item.setRow(checkRow);
        }
    }
    updateAllXYFromRowCol();
}

public void cellShift(String input){
	 nudgeActiveCells(input);
}

//Nudge active book left or right

private void nudgeActiveCells(String input){
 	Book myBook= getActiveBook();
	Integer firstrow=myBook.getRow();
	Integer firstcol=myBook.getCol();
    ArrayList<Book> bookList = getBooksInView();
    Iterator<Book> myIterator=bookList.iterator();
    while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        Integer checkCol=item.getCol();
        if (checkRow==firstrow && checkCol>=firstcol) {
            if (input.equals("left") && firstcol>0){
		        checkCol=checkCol-1;
		    }
		    if (input.equals("right")){
		        checkCol=checkCol+1;
		    }
            item.setCol(checkCol);
        }
    }
    updateAllXYFromRowCol();
}

public void moveFocusCell(String input){
	changeFocusCell(input);
}

private void changeFocusCell(String input){
	Book myBook = getActiveBook();
    Integer row=myBook.getRow();
    Integer col=myBook.getCol();
    Boolean moved = false;
    //ArrayList<Book> sorted= myProject.listBooksShelfOrder();
    ArrayList<Book> sorted = getViewBooksInOrder();
    
    //Boolean start=false;
    if (input.equals("down")) {
    	Iterator <Book> myIterator = sorted.iterator();
	    while(myIterator.hasNext()) {
	      Book item = myIterator.next();
		      if (item.getRow()>row && item.getCol()==col && moved==false) {
		          singleSelection(item);
		          moved=true;
		        }  
	   		}
	}
	else if (input.equals("up")){
		Integer mySize=sorted.size();
	    ListIterator <Book> myIterator = sorted.listIterator(mySize); //must pass argument at time of creation to set at right end
	    Boolean start=false;
	    while(myIterator.hasPrevious()) {
	      Book item = myIterator.previous();
	      if (item.getRow()<row && item.getCol()==col && moved==false) {
	          singleSelection(item);
	          moved=true;
	        }  
	   }
	}
	else if (input.equals("left")){
		Integer mySize=sorted.size();
    	ListIterator <Book> myIterator = sorted.listIterator(mySize); //must pass argument at time of creation to set at right end
		while(myIterator.hasPrevious()) {
	      	Book item = myIterator.previous();
	      	if (item.getRow()==row && item.getCol()==col && moved==false) {
		        if (myIterator.hasPrevious()) {
		          singleSelection(myIterator.previous());
		          moved=true;
		        }  
	      	}
   		}
	}
	else if (input.equals("right")) {
	  Iterator <Book> myIterator = sorted.iterator();
	  while(myIterator.hasNext()) {
	      Book item = myIterator.next();
	      System.out.println(item+" "+item.getLabel());
	      if (item.getRow()==row && item.getCol()==col && moved==false) {
	          if (myIterator.hasNext()) {
	              singleSelection(myIterator.next());
	              moved=true;
	          }
	      }
   		}
	}
}

//sort books by shelf order
private ArrayList<Book> getViewBooksInOrder() {
    ArrayList<Book> myBooksonShelves = getBooksInView();
    ArrayList<Integer> scoreIndexes = new ArrayList();
    Integer booknum=myBooksonShelves.size();
    for (int x=0;x<booknum;x++) {
        Book item = myBooksonShelves.get(x);
        Integer score=item.getRowScore();
        scoreIndexes.add(score);
    }
    Collections.sort(scoreIndexes); //performs a sort
    ArrayList<Book> sortedBooks = new ArrayList<Book>();
    Iterator<Integer> myIterator = scoreIndexes.iterator();
   
         while (myIterator.hasNext()) {
            Integer targetscore = myIterator.next();
            
            Iterator<Book> bookIterator = myBooksonShelves.iterator();
            while (bookIterator.hasNext()) {
                Book item = bookIterator.next();
                Integer test = item.getRowScore(); //calculated based on row, col
                
                if (test.equals(targetscore)) {
                    
                    sortedBooks.add(item);
                }
            }

        }
        
        return sortedBooks;
}

}