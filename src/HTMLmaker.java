//Class to hold HTML page maker from text
//(c) Craig Duncan 2021
//file i/o
import java.io.*;
import java.io.File;
import java.io.IOException;
//net function for browser links
import java.net.URI;
import java.net.URISyntaxException;
// for image file copying
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//Scanner
import java.util.*;

public class HTMLmaker{
	Book sourceBook;

public HTMLmaker(Book myBook){
	this.sourceBook=myBook;
}

public String addHTMLEndTags(String logString){
	String output=logString+"</body></html>";
	return output;	
	
}


/*
<!DOCTYPE html>
<html lang="en"> 
<head>
<meta charset="utf-8"/>
*/
public String updateBodyHTML(String nav, String newdir) {
	String input = sourceBook.getMD();
	String label = sourceBook.getLabel();
	String notes = sourceBook.getNotes();
	String logString="";
	//take out any existing headers?
	//String replaceString = input.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
	int index =0; //
	//top row or heading
	if(index==0) {
	 	logString = "<!DOCTYPE html><html lang=\"en\"><head>";
	 	logString=logString+"<meta charset=\"utf-8\"/>";
	 	logString=logString+"<title>"+label+"</title>";
	 	//logString=logString+"<script> border {border-style:dotted;}</script>"; //css
	 	//inherits main page style sheet?
	 	logString=logString+"<link rel=\"stylesheet\" href=\"../shelf.css\">";
	 	logString=logString+"</head>"+"<body>";// use the label for the html page (if needed)
	 	//logString=logString+"<p><b>"+label+"</b></p>";
	 	logString=logString+"<H1 class=\"a\">"+label+"</H1>"; //class a is arial
	 	logString=logString+nav; //navigation line (if any)
	 }
	 //iterate and create rest of file
	Scanner scanner1 = new Scanner(input);
 	String prefix = "<p>";
 	String suffix="</p>";

	 while (scanner1.hasNextLine()) {
	 	//just make paragraphs for now
	 	String thisLine=scanner1.nextLine();
	 	logString=logString+checkMD2(thisLine);
	 }

	 //include notes
	 if (notes.length()>0) {
		 Scanner scanner2 = new Scanner(notes);
		 //use class in .css external sheets where you have a .period in front of it
		 String prefixdiv="<div class=\"border\" id=\"border\">";
		 String suffixdiv="</div>";
		 logString=logString+prefixdiv;
		 while (scanner2.hasNextLine()) {
		 	String notesLine=scanner2.nextLine();
		 	logString=logString+p_Default(notesLine); //always default
		 	//System.out.println(notesLine);
		 }
		 logString=logString+suffixdiv;
	}
	//includes link
	 String linkout=includeLink();
	 System.out.println("link for html:"+linkout);
	 if (linkout.length()>0){
	 	logString=logString+linkout;
	 }
	 //include image (local)
	 //String imlink=includeImage();
	 String imlink=includeImageRel(newdir);
	 System.out.println("link for image:"+imlink);
	 if (imlink.length()>0){
	 	logString=logString+imlink;
	 }

	 return logString;
}

public String includeLink(){
	String output="";
	String linkpath=sourceBook.geturlpath();
     if (linkpath.length()>0) {
         String linkprefix="<p class=\"a\"><a href=\"";
         String linksuffix="\">Web link</a></p>";
         String linkfile = linkprefix+linkpath+linksuffix;
         output=output+linkfile;
    }
    return output;
}


//This uses local file system link to image.  
//TO DO: copy to /images/ folder inside this HTML export folder
public String includeImageRel(String newdir){
	String notes = sourceBook.getNotes(); //not sure why we need this for title
	//link.  Can we show the image?
	String output="";
    String imagepath=sourceBook.getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it (local files only)
    //From Java 8 use Files and Paths
     if (imagepath.length()>0) {
			File myFile = new File (imagepath);
			URI imageURI = myFile.toURI(); //this is an absolute local link.  Change to relative.
			System.out.println(imageURI);
			//DO A STD COPY
			int count=1;
			String linkfile = "image"+count+"."+imagepath.substring(imagepath.length()-3,imagepath.length());
			standardCopy(count,imagepath,newdir,linkfile); //TO DO: get image filenames or use sequence
			String linkpath="images/"+linkfile;
			String imgprefix="<p class=\"a\"><img src=\"";
			String imgsuffix="\" alt=\"user image\" width=\"600\" class=\"feature\" title=\""+notes+"\"></p>";
			//use linkfile not image URI for HTML folders
			String imgfile = imgprefix+linkpath+imgsuffix;
			output=output+imgfile;
    }
    return output;
}

public void standardCopy(int count,String imagepath, String newdir, String filename){
		
		String fullpath=newdir+"/images/"+filename;
		// Path of file where data is to copied
        Path pathIn = (Path)Paths.get(imagepath);
        // Path of file whose data is to be copied. To do: pass name or file ext in?
        Path pathOut = (Path)Paths.get(newdir,"images",filename);// or just String
  
        System.out.println("Path of target file: "
                           + pathOut.toString());
  
        System.out.println("Path of source file: "
                           + pathIn.toString());
  
        // Try block to check for exceptions
        try {
  			Files.copy(pathIn, pathOut,StandardCopyOption.REPLACE_EXISTING);
            // Printing number of bytes copied
            //System.out.println("Number of bytes copied: "+bts);
        }
  
        // Catch block to handle the exceptions
        catch (IOException e) {
  
            // Print the line number where exception occured
            e.printStackTrace();
        }
    }

//This uses local file system link to image.  
//TO DO: copy to /images/ folder inside this HTML export folder
public String includeImage(){
	String notes = sourceBook.getNotes(); //not sure why we need this for title
	//link.  Can we show the image?
	String output="";
    String imagepath=sourceBook.getimagefilepath();
    //the image path must be converted to URI or URL so webView can read it (local files only)
    //From Java 8 use Files and Paths
     if (imagepath.length()>0) {
			File myFile = new File (imagepath);
			URI imageURI = myFile.toURI(); //this is an absolute local link.  Change to relative.
			System.out.println(imageURI);
			//<img src="img_girl.jpg" alt="Girl in a jacket" width="500" height="600">
			String imgprefix="<p class=\"a\"><img src=\"";
			String imgsuffix="\" alt=\"user image\" width=\"600\" title=\""+notes+"\"></p>";

			String imgfile = imgprefix+imageURI+imgsuffix;
			output=output+imgfile;
    }
    return output;
}

public String p_Default(String thisLine){
	String prefix = "<p class=\"a\">"; 
	//String prefix = "<p>";
 	String suffix="</p>";
 	String output=prefix+thisLine+suffix;
	return output;
}

public String checkMD2(String thisLine){
	String h2code="## ";
	String h3code="### ";
    String h2prefix="<H2 class=\"a\">"; //<span style=\"font-family: Arial;\">
    String h2suffix="</H2>";
    String h3prefix="<H3 class=\"a\">"; //<span style=\"font-family: Arial;\">
    String h3suffix="</H3>";
    int mdFormatCode=0;
    String balanceString="";
    String output="";
    //fix escape characters
    thisLine=fixASCII(thisLine);
    //
    int testlength=thisLine.length();
        if (testlength>3) {
            String testString=thisLine.substring(0,3);
            if (testString.equals(h2code)) {
                mdFormatCode=2;
                balanceString=thisLine.substring(3,testlength);
            }
        }  //end length if
        if (testlength>4) {
            String testString=thisLine.substring(0,4);
            if (testString.equals(h3code)) {
                mdFormatCode=3;
                balanceString=thisLine.substring(4,testlength);
            }
        }  //end length if

        switch (mdFormatCode) {
			case(2) : 	output=output+h2prefix+balanceString+h2suffix;
	            		break;
			case(3) : 	output=output+h3prefix+balanceString+h3suffix;
						break;
			default :	output=output+p_Default(thisLine);
        }
        return output;
}

//TO DO: replace characters out of range e.g. ä or specify foreign character set for HTML?
public String fixASCII(String thisLine) {
	String rep1=thisLine.replace("“","\"");
 	String rep2=rep1.replace("”","\"");
 	String rep3=rep2.replace("–","-");
 	String output=rep3.replace("ä","a");
 	return output;
}



}