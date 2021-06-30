//class to handle xml parameter extraction for xml classes

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class xmlTool {
	
//constructor
public xmlTool(){
	
}

/*
# removes end of start tag so that included attributes section can be found
# e.g. <w:p> becomes <w:p and looks for > ahead.
# end tag is unaltered
# To do: use this to replace getTagListInclusive function
Also: <w:table> will appear in midst of <w:p> tags so need to allow for this in Block creation
*/
public ArrayList<String> getTagAttribInclusive(String thispara,String starttag, String endtag) {
    ArrayList<String> output= new ArrayList<String>();
    int stop = thispara.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        int sindex=thispara.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        int findex=thispara.indexOf(endtag,sindex); //find first index of end tag
        String test=thispara.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=thispara.substring(sindex,findex+endtag.length());
                // omit if this is a picture
                //testpict="<w:pict>"
                //if testpict not in thistext:
                output.add(thistext); // add to the array
                newstart=findex+endtag.length(); //len(endtag);
            }
            else {
                newstart=newstart+1;
            }
        }
        else {
            newstart=newstart+1;
        }
    }
    return output;
}

public String getParameterValue(String thispara,String parameter) {
    String output="";
    int stop = thispara.length();
    int newstart=0;
    //String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    String starttag=parameter+"=\""; //starttag.substring(0,starttag.length()-1);// strip off closing >
    String endtag="\"";
    while (newstart<=stop) {
        int sindex=thispara.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        //find first index of end tag, but start past end of starttag
        int findex=thispara.indexOf(endtag,sindex+starttag.length()); 
        //String test=thispara.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (findex!=-1){
                String thistext=thispara.substring(sindex+starttag.length(),findex);
                return thistext;
            }
            else {
                newstart=newstart+1;
            }
    }
    return output;
}

//copy of function in docXML.java. Put in here.
//Use start and end tags

public ArrayList<xmlPara> getXMLparas(String input,String starttag, String endtag) {
    ArrayList<xmlPara> output= new ArrayList<xmlPara>();
    xmlPara currentPara = new xmlPara();
    int stop = input.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        currentPara = new xmlPara(); //to reset the reference.
        int sindex=input.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        int findex=input.indexOf(endtag,sindex); //find first index of end tag
        String test=input.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=input.substring(sindex,findex+endtag.length());
                // omit if this is a picture
                //testpict="<w:pict>"
                //if testpict not in thistext:
                currentPara.setParaString(thistext); //initialise the paragraph with text
                
                //TO DO: Update this so that tables and other tool users get outline code
                //******------------*********
                //currentPara=setmyParaOutlineCode(currentPara); //update para with style information
                
                //store position for relative reference to tables etc
                currentPara.setStartIndex(sindex);
                currentPara.setEndIndex(findex+endtag.length());

                output.add(currentPara); // add to the array
                newstart=findex+endtag.length(); //len(endtag);
            }
            else {
                newstart=newstart+1;
            }
        }
        else {
            newstart=newstart+1;
        }
    }
   
    return output;
}

//get OOXML <w:p> paras (modelled on my xmlutil.py)
/* input:
The original input was the entire document.xml (could be a smaller string, if needed)

Note: Due to the nature of OOXML tagging, which can wrap some paras inside table properties,
this specific tag matching will extract paras from inside tables without recognising they are inside tables.

*/
public ArrayList<xmlPara> extractParas(String contents){
    String starttag="<w:p>";
    String endtag="</w:p>";
    //ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    ArrayList<xmlPara> result=getXMLparas(contents,starttag,endtag);
    return result;
}

public ArrayList<String> getTableCellText(String input) {
    String starttag="<w:tc>";
    String endtag="</w:tc>";
    ArrayList<String> output = getTagArrayInclusive(input,starttag,endtag);
    return output;
}

public ArrayList<String> getTableRowText(String input) {
    String starttag="<w:tr>";
    String endtag="</w:tr>";
    ArrayList<String> output = getTagArrayInclusive(input,starttag,endtag);
    return output;
}

public ArrayList<String> getTagArrayInclusive (String input, String starttag, String endtag){
    ArrayList<String> output= new ArrayList<String>();
    String currentPara = "";
    int stop = input.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        currentPara = ""; //to reset the reference.
        int sindex=input.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        int findex=input.indexOf(endtag,sindex); //find first index of end tag
        String test=input.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=input.substring(sindex,findex+endtag.length());
                
                currentPara = thistext; //initialise the paragraph with text
                
                output.add(currentPara); // add to the array
                newstart=findex+endtag.length(); //len(endtag);
            }
            else {
                newstart=newstart+1;
            }
        }
        else {
            newstart=newstart+1;
        }
    }
   
    return output;
}

/*
input: a paragraph string from document.xml

output: just the <w:t> tags section

(this varies from xmlPara where this function uses the 'state' of the xmlPara)
*/

public String extractTextTags(String input){
    String starttag="<w:t>";
    String endtag="</w:t>";
    xmlTool myTool = new xmlTool();
    ArrayList<String> result = myTool.getTagAttribInclusive(input,starttag,endtag); 
    ArrayList<String> result2 =  new ArrayList<String>();
    //remove ad hoc internal <w:t> tag
    for (String item: result) {
        item = item.replace("<w:t xml:space=\"preserve\">","");  //<---replace with no space (sometimes space is needd)
        result2.add(item);
    }
    String output = removeTags(result2,starttag,endtag);
    return output;
}

/*
Remove input tags from string
Input: a <w:t> set from <x:p> sourced from document.xml

*/

public String removeTags(ArrayList<String> inputList, String tag1, String tag2) {
    String empty="";
    String output="";
    for (String input: inputList) {
        //TO DO: move this to <w:t> text specific area
        String output1 = input.replace(tag1,empty);
        String output2 = output1.replace(tag2,empty);
        output=output+output2;
    }
    return output;
}

}