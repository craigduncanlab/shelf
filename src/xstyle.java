//Class to hold a docx style

public class xstyle {
	String styleString="";
	
	String type="paragraph";//this could be paragraph, character or table
	String Id="NewStyle"; //default
	String name="NewStyle";
	String basedOn="Normal"; //default
	int uiPriority=0; //priority for display of styles in Word
	String customStyle="1"; // this is '1' for user defined?
	//para props
	int outlineLevel=99;
	Shade shadeObject= new Shade();
	String shade="";
	//run props
	String Fonts="Times New Roman"; //default. To do.  create Font object and XML parser inside it.
	int size=22;//default is 11;
	String lang="en-GB";

	/*
	"<w:style w:type=\"paragraph\" w:styleId=\"NormalReally\"><w:name w:val=\"NormalReally\"/><w:basedOn w:val=\"Normal\"/><w:uiPriority w:val=\"0\"/><w:qFormat/><w:pPr><w:jc w:val=\"both\"/><w:outlineLvl w:val=\"4\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Times New Roman\" w:eastAsia=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\"/><w:sz w:val=\"22\"/><w:lang w:bidi=\"ar-SA\"/></w:rPr></w:style>";
	*/

//subclass shade
public class Shade{
	String val="clear";
	String color="auto";
	String fill="F8F8F8";

public Shade(){

}

public String getXML(){
	String output="<w:shd w:val=\""+this.val+"\" w:color=\""+this.color+"\" w:fill=\""+this.fill+"\"/>";
	return output;
}

}

private String updateXMLString(){
	//general properties
	String output = "<w:style w:type=\""+this.Id+"\">";
	output=output+"<w:name w:val=\""+this.name+"\">";
	output=output+"<w:basedOn w:val=\""+this.basedOn+"\">";
	output=output+"<w:uiPriority w:val=\""+this.uiPriority+"\"/>";
	//start paragraph properties
	output=output+"<w:pPr>";
	output=output+"<w:outlineLvl w:val=\""+this.outlineLevel+"\">";
	output=output+shade; //default is no shading
	output=output+"</w:pPr>"; //end para parameters
	output=output+"<w:rPr>"; //start run parameters
	output=output+"<w:rFonts w:ascii=\""+this.Fonts+"\" w:eastAsia w:ascii=\""+this.Fonts+"\" w:hAnsi=\""+this.Fonts+"/>"; 
	output=output+"<w:sz w:val=\""+this.size+"\"/>";
	output=output+"<w:lang w:eastAsia=\""+this.lang+"/>"; //w:bidi=
	output=output+"</w:rPr>"; //end para parameters
	output=output+"</w:style>"; //end XML style 
	setStyleXMLNoExtract(output);
	/*
	System.out.println(output);
	System.out.println(shadeObject.getXML());
	System.exit(0);
	*/
	return output;
}
	
//constructor
public xstyle(){

}

//external calls only
public String getStyleXML(){
	return this.styleString;
}
//if called externally, extract parameters
public void setStyleXML(String input){
	this.styleString=input;
	extractParamsFromString(); 
}

//For external setting of style parameters id, outline,font,size
//Require them to be done as a set
public void setStyleAttrib(String id, int outline, String font, int size, String shade) { 
  setId(id); //must match the test otherwise circular
  setOutlineLevel(outline);
  setFont(font);
  setSize(size); //tiny
  //setShade(shade);
  updateXMLString(); //do this for external updates
}

//if called here, sets based on internal XML generation
private void setStyleXMLNoExtract(String input){
	this.styleString=input;
}

public void setType(String input) {
	this.type=input;
}

public String getType() {
	return this.type;
}

public void setFont(String input) {
	this.Fonts=input;
}

public String getFont() {
	return this.Fonts;
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
	this.name=input; //default is to set both at same time
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

public void setSize(int input){
	this.size=input;
}

public int getSize(){
	return this.size;
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


public void extractParamsFromString(){
	String ss = this.styleString;
	extractId(ss);
	extractName(ss);
	extractBasedOn(ss);
	extractOutlineLevel(ss);
	extractFont(ss);
	//extractShade(ss)
}

public void extractId(String ss){
	String parameter = "w:styleId";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(ss,parameter);
	setId(output);
}

//w:rFonts w:ascii=
public void extractFont(String ss){
	String parameter = "w:rFonts w:ascii";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(ss,parameter);
	setFont(output);
}

public void extractName(String ss){
	String parameter="w:name w:val";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(ss,parameter);
	setName(output);
}

public void extractBasedOn(String ss){
	String parameter="w:basedOn w:val";
	xmlTool myP = new xmlTool();
	String output=myP.getParameterValue(ss,parameter);
	setBasedOn(output);
}

public void extractOutlineLevel(String ss){
	int output=99; //default if not specified in document
	String parameter="w:outlineLvl w:val";
	xmlTool myP = new xmlTool();
	String result=myP.getParameterValue(ss,parameter);
	//allow for no outline level being present
	if (result.length()>0) {
		output=Integer.parseInt(result);
	}
	setOutlineLevel(output);
}
}
