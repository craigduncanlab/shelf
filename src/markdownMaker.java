//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File; //different to nio.Files

public class markdownMaker{
	String fname="";

public markdownMaker(String filename){
	this.fname=filename;
}

public void saveMarkdownFile(String myOutput){
	System.out.println("Saving: "+this.fname); 
    ZipUtil util = new ZipUtil();
    util.basicFileWriter(myOutput,this.fname);
}

//Takes an array of Book objects and return string of markdown text
public String makeMarkdown (ArrayList<Book> mySaveBooks){

	Iterator<Book> myIterator = mySaveBooks.iterator();
    String myOutput="";
    /*
    String fn=getFilepathNoExt();
    System.out.println(getFilename());
    System.out.println(getFilepath());
    System.out.println("Exiting writeOutCommon in Project...");
    System.exit(0);
    */
         while (myIterator.hasNext()) {
            Book myNode=myIterator.next();
            //System.out.println(myNode.toString());
            String myString=convertBookMetaToString(myNode);
            myOutput=myOutput+myString;
            myString="";
             //option: prepare string here, then write once.
        }
    saveMarkdownFile(myOutput);
    System.out.println("Finished markdown save");
    return myOutput;
}
//Convert this book meta into a String of markdown.  Only write links if data is there.
public String convertBookMetaToString(Book myBook) {
    String myOutput="# ";
    if (myBook.getStyleId().length()>4) {
        myOutput=myOutput+"{"+myBook.getStyleId()+"} ";
    }
    myOutput=myOutput+trim(myBook.getLabel())+System.getProperty("line.separator"); //check on EOL
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