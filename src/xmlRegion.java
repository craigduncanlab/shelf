//class to hold OOXML document classes for sub-regions

import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc


public class xmlRegion {
	ArrayList<xmlPara> myParaGroup = new ArrayList<xmlPara>();
	ooxmlTable myTable = new ooxmlTable();
	String regionType = "paragraph";
	
public xmlRegion(){
	
}

public void setType(String input){
	this.regionType=input;
}

public String getType(){
	return this.regionType;
}

public void setParaGroup(ArrayList<xmlPara> input){
	this.myParaGroup = input;
}

public ArrayList<xmlPara> getParaGroup(){
	return this.myParaGroup;
}

public void setTable(ooxmlTable input){
	this.myTable = input;
}

public ooxmlTable getTable(){
	return this.myTable;
}


}