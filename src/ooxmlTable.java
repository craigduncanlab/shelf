//class to hold an OOXML table

public class ooxmlTable {
	String tableString="";
	int startindex=0; //hold original file position indexes
	int endindex=0;
	int numRows=0;; //replace in favour of array of rows and getter
	int numCols=0;; //replace in favour of array of cols and getter


public ooxmlTable(){
	
}

public void setString(String input){
	this.tableString=input;
}

public String getString(){
	return this.tableString;
}

public void setStartIndex(int input){
	this.startindex=input;
}

public int getStartIndex(){
	return this.startindex;
}

public void setEndIndex(int input){
	this.endindex=input;
}

public int getEndIndex(){
	return this.endindex;
}


}