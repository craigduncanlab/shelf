//class to hold a new reconfigured DOM for an OOXML document
//Takes into account regions with tables
//(c) Craig Duncan 29 June 2021

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

/*
The 'xmlRegionDOM' is a class to hold xmlRegion objects.
An xmlRegion can be of type 'paragraph' or 'table' (TO DO: code, notes, list)
An xmlRegion can hold multiple consecutive xmlParas, but only one ooxmlTable.

At this stage, xmlRegion can be subdivided into Blocks/Books externally, and then Boocks acts as a GUI class for displaying contents
*/

public class xmlRegionDOM{
	ArrayList<xmlRegion> docRegions = new ArrayList<xmlRegion>();
	ArrayList<ooxmlTable> tableSet = new ArrayList<ooxmlTable>();
	ArrayList<xmlRegion> paraRegions = new ArrayList<xmlRegion>();
	ArrayList<ooxmlTable> tablelist = new ArrayList<ooxmlTable>();
	xmlStyles myStylesObject = new xmlStyles();
	String docString = "";

public xmlRegionDOM(){
	
}

public void setStylesObject(xmlStyles input){
	this.myStylesObject=input;
}

public xmlStyles getStylesObject(){
	return this.myStylesObject;
}

public void setTableList(ArrayList <ooxmlTable> input) {
    this.tablelist=input;
}

public ArrayList<ooxmlTable>getTableList() {
    return this.tablelist;
}

public void setDocString(String input){
	this.docString=input;
}

public String getDocString(){
	return this.docString;
}

public void setParaRegions(ArrayList<xmlRegion> input){
	this.paraRegions=input;
}

public ArrayList<xmlRegion> getParaRegions(){
	return this.paraRegions;
}

public void setTables(ArrayList<ooxmlTable> input){
	this.tableSet=input;
}

public void setAllRegions(ArrayList<xmlRegion> input){
	this.docRegions=input;
}

public ArrayList<xmlRegion> getAllRegions(){
	return this.docRegions;
}

/*

(1)
Input: 

Uses the current document.xml contents as basis for extraction.

Function: Find tables in this document, and since they are higher level XML element to
paragraphs, store both the string and the index positions in file to help
categorise paragraph entries that are also in tables

Output: stores tables in ooxmlTable objects, adds to:
tablelist - the global array of such objects in this class

(2)
Extract series of regions of text around tables.
Works with DocString (document.xml) in this object.

*/


public void initialise(){
    ArrayList<ooxmlTable> myTableSet = getTableIndexes(getDocString());
    if (myTableSet.size()>0) {
        setTableList(myTableSet);
    }
    if (myTableSet.size()>0){
        	getRegionsWithTables(myTableSet); //or have function get this table
    }
    else {
        	getRegionsWithNoTables();
    }
    //this newDoc object holds the new regions etc.
}

/*
Extract tables from the whole of the saved document.xml
*/

public ArrayList<xmlPara> extractTables(){
    String contents = getDocString(); 
    String starttag="<w:tbl>";
    String endtag="</w:tbl>";
    //ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    ArrayList<xmlPara> result=getXMLparasWithinLimits(contents,starttag,endtag,0,contents.length());
    return result;
}

/*
Extract paragraphs from a portion (indexes: start, end) of the saved document.xml
*/

public ArrayList<xmlPara> extractParasWithinLimits(int start, int end){
    String contents = getDocString(); 
    String starttag="<w:p>";
    String endtag="</w:p>";
    //ArrayList<String> result=getTagAttribInclusive(contents,starttag,endtag);
    ArrayList<xmlPara> result=getXMLparasWithinLimits(contents,starttag,endtag,start,end);
    return result;
}

/* 
Function to find Word's ooxml 'w:p' paragraphs, within the index limits of the input string.
This relies on indexing based on the original string contents and length, 
so to store indexes consistently you need to keep passing the same original string

*/

public ArrayList<xmlPara> getXMLparasWithinLimits(String input,String starttag, String endtag, int startindex,int endindex) {
    ArrayList<xmlPara> output= new ArrayList<xmlPara>();
    xmlPara currentPara = new xmlPara();
    int newstart=0;
    int stop = input.length();
    int startlimit=stop;
    //replace with the constraints if valid
    if (startindex>=0 && startindex<=endindex && endindex<=stop) {
        newstart=startindex;
    }
    if (endindex<stop) {
        stop=endindex;
    }
    int findex=0; //first index of found end tag
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop && findex+endtag.length()<endindex) {
        currentPara = new xmlPara(); //to reset the reference.
        int sindex=input.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            return output; // nothing found to end = None?
        }
        findex=input.indexOf(endtag,sindex); //find first index of end tag
        String test=input.substring(sindex+starttag.length(),sindex+starttag.length()+1);
        if (test.equals(starttagend) || test.equals(" ")) {
            if (findex!=-1){
                String thistext=input.substring(sindex,findex+endtag.length());
                // omit if this is a picture
                //testpict="<w:pict>"
                //if testpict not in thistext:
                currentPara.setParaString(thistext); //initialise the paragraph with text
                currentPara=setmyParaOutlineCode(currentPara); //update para with style information
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

//setmyParaOutlineCode is DUPLICATE OF function in DOCXML.JAVA

/*  

Input: a <w:p> tagged paragraph from OOXML document.xml
Also, linecount to see where we are in file.
 
Function: checks to see if the StyleId in this paragraph is same as the list of StyleIds that
have Outline lvl 0.  

Output: If  a match, returns 0 (as if H1 or # in markdown). Otherwise returns 1.
*/

public xmlPara setmyParaOutlineCode(xmlPara thisPara) {
        int code=99;
        //System.out.println("Row:"+thisRow);
        String paraStyle = thisPara.getpStyle(); //the styleId in para
        //no style found to check against
        if (paraStyle.length()==0) {
            //return code;
            thisPara.setLineCode(code);
        }
        //make reference to all the styles
        xmlStyles stylesObject = getStylesObject(); //(xmlStyles).  
        ArrayList<xstyle> stylesList=stylesObject.getOutlineStyles(); //all the Outline styles
        //
        for (xstyle item : stylesList ) {
            thisPara.setLineCode(code); //for custom block definition
            String styleId=item.getId(); //this is styleid in the xstyle
            //match on the style with this paragraph
            if (paraStyle.equals(styleId)) {
                //thisPara.setLineCode(0);
                thisPara.setOutlineLevel(item.getOutlineLevel()); //para stores same outline level as its style
                thisPara.setLineCode(item.getOutlineLevel()); //TO DO: can be an indepenent category
                thisPara.setStyleXML(item.getStyleXML()); //this is full xml
        }
    }
    return thisPara; //need to do this to update the object 
 }

/* 
Function to process the regions, once setup, into a master array of regions plus tables
*/

public void processRegionsWithTables() {
		Integer numRegions=getParaRegions().size();
		if (numRegions<1){
			System.out.println("Detected no regions:"+numRegions);
			System.exit(0);
		}

		//process the first 'pairs' of regions (para) + table
        ArrayList<xmlRegion> masterSet = new ArrayList<xmlRegion>();
        for (int y=0;y<numRegions-1;y++){
            xmlRegion myRegion = getParaRegions().get(y);
            myRegion.setType("paragraph");
            xmlRegion myTable = new xmlRegion();
            myTable.setTable(tableSet.get(y));
            myTable.setType("table");
            masterSet.add(myRegion);
            masterSet.add(myTable);
        }
        //add last region
        xmlRegion myRegion2 = getParaRegions().get(numRegions-1);
        myRegion2.setType("paragraph");
        masterSet.add(myRegion2);
        setAllRegions(masterSet);
        //logging();
    }

/* 
Function to process the regions, once setup, into a master array of regions plus tables
*/

public void processRegionsWithoutTables() {
		Integer numRegions=getParaRegions().size();
		if (numRegions<1){
			System.out.println("Detected no regions:"+numRegions);
			System.exit(0);
		}

		//process the first 'pairs' of regions (para) + table
        ArrayList<xmlRegion> masterSet = new ArrayList<xmlRegion>();
        for (int y=0;y<numRegions;y++){
            xmlRegion myRegion = getParaRegions().get(y);
            myRegion.setType("paragraph");
            masterSet.add(myRegion);
        }
        setAllRegions(masterSet);
        //logging();
    }

/*
Look at what has been produced
*/

public void logging(){
	int region=0;
	 for (xmlRegion item: getAllRegions()) {
	 		System.out.println("Region:"+region);
            System.out.println("-------------");
            System.out.println(item.getType());
            if (item.getType().equals("table")){
                System.out.println(item.getTable().getXMLString());
            }
            if (item.getType().equals("paragraph")){
                ArrayList<xmlPara> myParas = item.getParaGroup();
                for (xmlPara item2 : myParas){
                     System.out.println(item2.getParaString());
                }
            }
            region++;
        }

        System.exit(0);
}

/*
Function to group each set of paragraphs that lies before or after tables in OOXML
Each set of <w:p> paragraphs is collected into an 'xmlRegion'
These are collected into an array, then returned.

The extraction function relies on having the document.xml string as a variable
in this object.  It also has a different approach if no tables (creates one region).

At this stage, unlike docXML, it does not store the individual paragraphs as an array outside these 
objects.  They are found as a group of paras insid each xmlRegion of 'paragraph' type.

*/

public void getRegionsWithNoTables(){
        ArrayList<xmlRegion> output = new ArrayList<xmlRegion>();
        ArrayList<xmlPara> myParas = extractParasWithinLimits(0,getDocString().length());
        xmlRegion myGroup = new xmlRegion();
        myGroup.setParaGroup(myParas);
        output.add(myGroup);
        //now put the paragraph regions into newDoc to be interleaved
        setParaRegions(output);
        processRegionsWithoutTables();
}

private void getRegionsWithTables(ArrayList<ooxmlTable> tableSet) {    
        ArrayList<Integer> myMarkers = new ArrayList<Integer>();
        myMarkers.add(0);
        //get markers
        for (ooxmlTable item: tableSet){
            myMarkers.add(item.getStartIndex());
            //
            myMarkers.add(item.getEndIndex());
        }
        Integer last = getDocString().length();
        myMarkers.add(last);
        Integer numRegions=myMarkers.size()/2;
        //System.out.println("Regions:"+numRegions);

        // Now encapsulate the paragraphs in each regions, as per markers
        ArrayList<xmlRegion> myParaRegions = new ArrayList<xmlRegion>();
        for (int y=0;y<numRegions;y++){
            //System.out.println((2*y)+","+(2*y+1));
            int sp=myMarkers.get(2*y); //get start index at (2y)
            int ep=myMarkers.get((2*y)+1); //get end index at (2y+1)
            //System.out.println(sp+","+ep);
            ArrayList<xmlPara> myParas = extractParasWithinLimits(sp,ep);
            xmlRegion myGroup = new xmlRegion();
            myGroup.setParaGroup(myParas);
            myParaRegions.add(myGroup);
        }
        //ArrayList<xmlRegion> myParaRegions = getRegionsNoTables();
        setParaRegions(myParaRegions);
        setTables(tableSet);
        processRegionsWithTables();   
        //return myParaRegions;
    }
  
public void logTables(){
    ArrayList<ooxmlTable> myTL = getTableList();
    for (ooxmlTable currentTable : myTL){
        System.out.println("Found table with index positions: "+currentTable.getStartIndex()+" to: "+currentTable.getEndIndex());
        System.out.println("----");
        System.out.println(currentTable.getXMLString());
        System.out.println("----");
    }
    System.out.println("Current number of tables:"+myTL.size());
    // System.exit(0);
}       

/*
This will be a function to find tables, store index positions for secondary parsing
It will then integrate the table objects into main document model 'array' of content

Input : The whole document.xml string

Output: Array of ooxmlTable objects, for number of tables detected.

*/

public ArrayList<ooxmlTable> getTableIndexes(String input) {
    String starttag="<w:tbl>";
    String endtag="</w:tbl>";
    ArrayList<ooxmlTable> output= new ArrayList<ooxmlTable>();
    ooxmlTable currentTable = new ooxmlTable();
    int stop = input.length();
    int newstart=0;
    String starttagend=starttag.substring(starttag.length()-1,starttag.length()); // this will be > in all cases
    starttag=starttag.substring(0,starttag.length()-1);// strip off closing >
    while (newstart<=stop) {
        currentTable = new ooxmlTable(); //to reset the pointer to object / reference.
        int sindex=input.indexOf(starttag,newstart); //unlike python 'find', stop value not needed
        if (sindex==-1) {
            //System.out.println("Found nothing");
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
                currentTable.setXMLString(thistext); //initialise the table with text
                currentTable.setStartIndex(sindex);
                currentTable.setEndIndex(findex+endtag.length());
                //TO DO: currentTable.setTableFeatures();
                //currentPara=setmyParaOutlineCode(currentPara); //update para with style information
                output.add(currentTable); // add to the array
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
    //System.exit(0);
    return output;
}


}