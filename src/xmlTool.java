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

}