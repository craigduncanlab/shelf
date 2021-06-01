//Class to hold a docx style

public class xstyle {
	String styleString="";
	String name="";
	String Id="";
	String type="";//this could be paragraph, character or table
	String customStyle=""; // this is '1' for user defined?
	String basedOn="";
	int outlineLevel=99;
	
//constructor
public xstyle(){

}

public String getStyle(){
	return this.styleString;
}

public void setStyle(String input){
	this.styleString=input;
	extractParams(); //initialise
}

public void setType(String input) {
	this.type=input;
}

public String getType() {
	return this.type;
}

public void setBasedOn(String input) {
	this.basedOn=input;
}

public String getBasedOn() {
	return this.basedOn;
}

//This is (unlike xml paras) a name, not a HEX number
public void setId(String input){
	this.Id=input;
}

public String getId(){
	return this.Id;
}

public void setOutlineLevel(int input){
	this.outlineLevel=input;
}

public int getOutlineLevel(){
	return this.outlineLevel;
}

public String getName(){
	return this.name;
}

public void setName(String input){
	this.name=input;
}

/* 
get the styleId of the current style in styles.xml 
input: item is string with style
*/


public void extractParams(){
	extractId();
	extractName();
	extractBasedOn();
	extractOutlineLevel();
}

public void extractId(){
	String parameter = "w:styleId";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.styleString,parameter);
	setId(output);
}

public void extractName(){
	String parameter="w:name w:val";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.styleString,parameter);
	setName(output);
}

public void extractBasedOn(){
	String parameter="w:basedOn w:val";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(this.styleString,parameter);
	setBasedOn(output);
}

public void extractOutlineLevel(){
	int output=99;
	String parameter="w:outlineLvl w:val";
	xmlTool myP = new xmlTool();
	String result=myP.getParameterValue(this.styleString,parameter);
	//allow for no outline level being present
	if (result.length()>0) {
		output=Integer.parseInt(result);
	}
	setOutlineLevel(output);
}
}