// A class to hold HTMLFile, translate between Book class
// To do: call this 'HTMLBlock' and integrate into a broader HTMLFile class?

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
//net function for browser links
import java.net.URI;
import java.net.URISyntaxException;
//import utilities needed for ArraysLists, lists etc
import java.util.*; //collections too
//File i/o
import java.io.*;
import java.io.File;
//for file handling
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class HTMLFile {
	String docString; //main text
	String fileExt;
	ArrayList<mdBlock> blocklist = new ArrayList<mdBlock>();//legacy
	ArrayList<Book>booklist = new ArrayList<Book>();//legacy

//constructor
public HTMLFile(){
	String docString;
	//ArrayList
}

public void openHTML(File file){
}
    

public Boolean checkExtensions(File file){
    String thefilename=file.getName();
    String ext1=thefilename.substring(thefilename.length() - 3);
    String ext2=thefilename.substring(thefilename.length() - 4);
    String ext3=thefilename.substring(thefilename.length() - 5);
    
    if (ext1.equals(".md")==true) {
      setExtension("md");
      return true;
    }

    else if (ext2.equals(".rmd")==true || ext2.equals(".Rmd")==true){
      setExtension("rmd");
      return true;
    }

     else if (ext3.equals(".docx")==true){
      setExtension("docx");
      return true;
    }
    else {
      return false;
    }
}

public void setExtension(String input){
	this.fileExt=input;
}

public String getExtension(){
	return this.fileExt;
}

//Simple utility to return contents of file as String
public String getFileText(File myFile) {
  StringBuffer myText = new StringBuffer(); //mutable String
  String endOfLine="\n"; //to do - operating system independent
  try {
    Scanner scanner1 = new Scanner(myFile);
    if (scanner1==null) {
      System.out.println("No text/html content");
      return null;
    }
    int nl=0;
    while (scanner1.hasNextLine()) {
      nl++;
      String thisRow=scanner1.nextLine();
      System.out.println(thisRow);
      myText.append(thisRow);
      myText.append(endOfLine);
    }
    scanner1.close();
  }
  catch (Throwable t)
  {
    t.printStackTrace();
    //System.exit(0);
    return null;
  }
  //System.out.println(myText);
  //System.exit(0);
  return myText.toString();
}

// -- Unzipped Strings

public void setDocString(String input) {
	this.docString = input;
}

public String getDocString() {
	return this.docString;
}

// - BLOCKS/BOOKS API

public void setBlocklist(ArrayList<mdBlock> input) {
    this.blocklist = input;
}

//TO DO: Change to HTML
public ArrayList<mdBlock> getBlocklist() {
    return this.blocklist;
}

public void setBooklist(ArrayList<Book> input) {
    this.booklist = input;
}

public ArrayList<Book> getBooklist() {
    return this.booklist;
}

// --- line processing for main API
//The setMDlinecode() is '0' or '1' - it doesn't code for comments.

public void codeMDLines(ArrayList<mdLineObject>myLines) {
	for (mdLineObject lineItem : myLines) {
		lineItem.setMDLineCode();
	} 
	//return fileindex;
}

/*
public void makeBlocklist() {
	ArrayList<mdLineObject>myLines = getFileLines(getDocString());
	myLines=codeMDLinesForNotes(myLines); //updates contents of line objects (codes)?
	Parser myParser=new Parser();
	ArrayList<mdBlock> newblocks = packageBlocksFromLineObjects(myLines);
	setBlocklist(newblocks);
}
*/

//Convert the MD section of current Book to some HTML and update the HTML parameter  
//TO DO: Can this function be enclosed in a Book object?   
public String getHTMLfromContents(Book myBook) {
  if (myBook.getBlockType().equals("table")) {
      ooxmlTable myTable = myBook.getOOXMLtable();
      String output = myTable.getTableAsHTML();
      //String output = "<html><body><p>"+text+"</p></body></html>";
      return output;
  }
  else {
    return (getHTMLfromMDContents(myBook));
  }
}

//Convert the MD section of current Book to some HTML and update the HTML parameter  
//TO DO: Can this function be enclosed in a Book object?   
public String getHTMLfromMDContents(Book myBook) {
    String input = myBook.getMD();
    String label = myBook.getLabel();
    String notes = myBook.getNotes();
    Boolean showNotes=false;
    if (notes.length()>0) {
        showNotes=true;
    }
    String fontfamily = "Garamond"; //Arial
    String logString="";
    //take out any existing headers?
    //String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
    int index =0; //
    String cssString="<style> .bordered { font-family: Arial; font-size: small; width: 900px; padding: 20px; background-color: #ddd; border: 1px dashed darkorange; border-radius: 8px; } .a {font-family: Arial;} </style>";
    //top row or heading
    if(index==0) {
        logString = "<html><head>";
        logString=logString+"<title>"+label+"</title>";
        logString=logString+cssString; //css
        logString=logString+"</head>"+"<body>";// use the label for the html page (if needed)
        //logString=logString+"<p><b>"+label+"</b></p>";
        //logString=logString+"<H1>"+label+"</H1>";
        //logString = "<html><head><title>"+label+"</title></head>"+"<body>";// use the label for the html page (if needed)
        //logString=logString+"<p><b>"+label+"</b></p>";
        logString=logString+"<H1><span style=\"font-family: Arial;\">"+label+"<span style=\"font-family: Arial;\"></H1>";
     }

    //date and time
    String dtstring=myBook.getdate();
    String styleIdString=myBook.getStyleId(); //unused
    if(dtstring.length()>0) { //timestring.length()>0 && 
        dtstring=dtstring;// +","+timestring;
    }
    else if (dtstring.length()>1) {
         /*String dtprefix="<p><span style=\"font-family: "+fontfamily+";\">";
         String dtsuffix="</span></p>";
         String dtfile = dtprefix+dtstring+dtsuffix;
         */
         String h2prefix="<H2><span style=\"font-family: Arial;\">";
         String h2suffix="</H2>";
         String dtfile=h2prefix+dtstring+h2suffix;
         logString=logString+dtfile;
    }
     //iterate and create rest of file
    Scanner scanner1 = new Scanner(input);
    String prefix = "<p class=\"a\">";
    String suffix="</span></p>";
    // filter md content for h2 or p
    String h2code="## ";
    String h2prefix="<H2><span style=\"font-family: Arial;\">";
    String h2suffix="</H2>";
    int mdFormatCode=0;
    String balanceString="";
     while (scanner1.hasNextLine()) {
        //just make paragraphs for now, unless h2
        String thisLine=scanner1.nextLine();
        int testlength=thisLine.length();
        if (testlength>3) {
            String testString=thisLine.substring(0,3);
            balanceString=thisLine.substring(3,testlength);
        //System.out.println(testString);
            if (testString.equals(h2code)) {
                mdFormatCode=2;
            }
        }  //end length if
        if (mdFormatCode==2) {
            logString=logString+h2prefix+balanceString+h2suffix;
            mdFormatCode=0; //reset
        }
        else {
             logString=logString+prefix+thisLine+suffix; //normal paragraph
        }
       
     } //end while
     //notes
     if (showNotes==true) {
         Scanner scanner2 = new Scanner(notes);
         String prefixdiv="<div class=\"bordered\">";
         String suffixdiv="</div>";
         String prefix2="<p>";
         logString=logString+prefixdiv;
         while (scanner2.hasNextLine()) {
            String notesLine=scanner2.nextLine();
            logString=logString+prefix2+notesLine+suffix;
            System.out.println(notesLine);
         }
         logString=logString+suffixdiv;
     }
     //
     String linkpath=myBook.getdocfilepath();
     if (linkpath.length()>0) {
         String linkprefix="<p><span style=\"font-family: Arial;\"><a href=\"";
         String linksuffix="\">Filelink</a></span></p>";
         String linkfile = linkprefix+linkpath+linksuffix;
         logString=logString+linkfile;
    }
    //embedded links will probably only open www addresses in the inbuilt JavaFX WebView.  You can open these from the Button in edit view.
    String urlpath=myBook.geturlpath();
     if (urlpath.length()>0) {
         String urlprefix="<p><span style=\"font-family: "+fontfamily+";\"><a href=\"";
         String urlsuffix="\">weblink</a></span></p>";
         String urlfile = urlprefix+urlpath+urlsuffix;
         logString=logString+urlfile;
    }
    //link.  Can we show the image?
    String imagepath=myBook.getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it
     if (imagepath.length()>0) {
        File myFile = new File (imagepath);
        URI imageURI = myFile.toURI();
        System.out.println(imageURI);
        //<img src="img_girl.jpg" alt="Girl in a jacket" width="500" height="600">
         String imgprefix="<p><img src=\"";
         String imgsuffix="\" alt=\"user image\" width=\"600\" title=\""+notes+"\"></p>";

         String imgfile = imgprefix+imageURI+imgsuffix;
         logString=logString+imgfile;
    }
    /*
    String imagepath=myBook.getimagefilepath();
     if (imagepath.length()>0) {
         String imgprefix="<p><span style=\"font-family: Arial;\"><a href=\"";
         String imgsuffix="\">imagelink</a></span></p>";
         String imgfile = imgprefix+imagepath+imgsuffix;
         logString=logString+imgfile;
    }
    */
    logString=logString+"</body></html>";
    System.out.println(logString);
    return logString;
    }

//input: the shelf books/objects
//output: an html page in similar layout to the grid items in the editor
/*
public void setBooksOnShelf(ArrayList<Book> inputObject) {
    this.booksOnShelf = inputObject;
}
*/

//Inputs are Book list, filename, filename without extension

public void writeOutHTML(ArrayList<Book> mySaveBooks, String nameExt, String filename, String htmlpath) {
  //header section
  ZipUtil util = new ZipUtil();
  String author="Craig Duncan";
  String year="2021";
  String byline1="(c)"+year+" "+author;
  String byline="";
  String logString = "<html><head>"; //use StringBuffer?
  logString=logString+"<title>"+filename+" "+byline+"</title>";
  //logString=logString+"<script> border {border-style:dotted;}</script>"; //css - put in shared css file?
  logString=logString+"<link rel=\"stylesheet\" href=\"shelf.css\">";
  logString=logString+"</head>"+"<body>";// use the label for the html page (if needed)
  //logString=logString+"<p><b>"+name+"</b></p>";
  logString=logString+"<H1>"+filename+"</H1>";
  logString=logString+"<p class=\"feature\">"+byline+"</p>";
  logString=logString+"<div class=\"grid\">";
  //
  ArrayList<Book> bookList = mySaveBooks;
  int pagecount=1;
  //path to main .md file
  //if this is a 'save as' then...?
  //Possibly use Files to do this in future
  //String filenamelink="../"+filename+".html"; //relative link if main page was in folder above
  String filenamelink=filename+".html";
  String newdir="/"+filename;
  
  createNewHTMLDir(htmlpath); //for html and images. Does this need to be fullpath? If so, use htmlpath
  int pagemax=bookList.size();
  String navlink="";
  String mainlink="<a href=\""+filenamelink+"\">index</a>";
  Iterator<Book> myIterator=bookList.iterator();
  while(myIterator.hasNext()) {
        Book item = myIterator.next();
        Integer checkRow=item.getRow();
        Integer checkCol=item.getCol();
        String label2 = item.getLabel();
        String pagename=pagecount+"_"+label2;
        //+maintitle in link?
        String linkname=pagecount+".html";
        //page links
        if (pagecount==1 && pagemax==1){
           navlink=mainlink;
        }
        if (pagecount==1 && pagemax>1){
          String nextlinkname=pagecount+1+".html";
          String nextlink="<a href=\""+nextlinkname+"\">"+"next:"+(pagecount+1)+"</a>";
          navlink=mainlink+" | "+nextlink+" > ";
        }
        if (pagecount>1 && pagemax>pagecount){
          String prevlinkname=pagecount-1+".html";
          String nextlinkname=pagecount+1+".html";
          //absolute links - redundant
          //String prevlink="<a href=\""+fileNoExt+"/"+prevlinkname+"\">"+"prev:"+(pagecount-1)+"</a>";
          //String nextlink="<a href=\""+fileNoExt+"/"+nextlinkname+"\">"+"next:"+(pagecount+1)+"</a>";
          //relative links
          String prevlink="<a href=\""+prevlinkname+"\">"+"prev:"+(pagecount-1)+"</a>";
          String nextlink="<a href=\""+nextlinkname+"\">"+"next:"+(pagecount+1)+"</a>";
          navlink="< "+prevlink+" | "+mainlink+" | "+nextlink+" >";
        }
        if (pagecount>1 && pagemax==pagecount){
          String prevlinkname=pagecount-1+".html";
          String prevlink="<a href=\""+prevlinkname+"\">"+"prev:"+(pagecount-1)+"</a>";
          navlink=prevlink+" | "+mainlink;
        }
        //absolute Links in the main grid contents page
        //String link="<a href=\""+fileNoExt+"/"+linkname+"\">"+pagename+"</a>";
        //relative links
        //String sublink=filename+"/"+pagecount+".html"; //relative link
        String sublink=pagecount+".html"; //relative link to same folder as the main file
        String link="<a href=\""+sublink+"\">"+pagename+"</a>";
        logString=logString+"<div class=\"cell\">"+link+"</div>";
        //
        String notes = item.getNotes();
        //String bookpage = item.getHTML();
        //Get new HTML pages, each with nav links, save into new directory
        String bookpage = item.getHTMLlink(navlink,newdir);
        //filewriter needs local path to save each of the individual files correctly
        String linkpath=htmlpath+"/"+linkname; //was newdir
        util.basicFileWriter(bookpage,linkpath);
        //Advance page counter
        pagecount++;
    }
    logString=logString+"</div></body></html";
    //write out the main index page to htmlpath and html filename
    String htmlfilename=htmlpath+"/"+filename+".html"; //local file sysetm
    util.basicFileWriter(logString,htmlfilename);
    System.out.println("Write out HTML completed");
    writeOutCSS(htmlpath);
}

//create new HTML directories in local file system, where source .md file is found
public void createNewHTMLDir(String fileNoExt) {
  // Create directory to hold individual HTML pages
  String newdir="/"+fileNoExt; //Works to create dir on local file system.  
  String newimagedir=newdir+"/images";
  System.out.println("New dir name/path:"+newdir);
  try {

    Path dpath = Paths.get(newdir);
    Path ipath = Paths.get(newimagedir);

    //java.nio.file.Files;
    Files.createDirectories(dpath);
    Files.createDirectories(ipath);

    System.out.println("Directory(s) is created!");

  } catch (IOException e) {

    System.err.println("Failed to create directory!" + e.getMessage());

  }
}

public void writeOutCSS(String foldername) {
    ZipUtil util = new ZipUtil();
    String mycss=".grid { display: grid; grid-template-columns: auto auto;} \ndiv.cell {background: LightBlue; border: 1px solid Blue;  padding: 10px;}";
    //\ndiv.border {border-style:dotted;}
    //Query if width should be here?  width: 900px; 
    String h1="h1 {text-align:center;}";
    String f1="\n.feature {display: block; margin-left: auto; margin-right: auto; width: 50%};";
    String body="\nbody { background-color:#00A8C5; font-family:Lucida,Helvetica,sans-serif; font-weight:500; text-decoration: none;  position: relative;  width: 100%; margin-left: 0px;}";
    String borders="\n.border { font-family: Arial; font-size: small; padding: 20px; background-color: #ddd; border: 1px dashed darkorange; border-radius: 8px; }";
    String arial="\n.a {font-family: Arial;}";
    mycss=mycss+h1+body+f1+borders+arial;
    String cssname=foldername+"/shelf.css";
    System.out.println(cssname);
    util.basicFileWriter(mycss,cssname);
    System.out.println("Write out CSS completed");
}



} 

