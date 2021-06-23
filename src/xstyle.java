//Class to hold a docx style

public class xstyle {
	String styleString="";
	
	String type="paragraph";//this could be paragraph, character or table
	String Id="NewStyle"; //default
	String name="NewStyle";
	String basedOn="Normal"; //default
	String nextPara=""; //default
	int uiPriority=0; //priority for display of styles in Word
	String customStyle="1"; // this is '1' for user defined?
	//para props
	int outlineLevel=99;
	Shade shadeObject= new Shade();
	String shade="";
	int afterSpace=0; //default for 6pt is 120
	//run props
	String Fonts=""; //No default. To do.  Times New Roman.  create Font object and XML parser inside it.
	String FontColorXML="";
	String formatXML="";
	int size=1;//default for 11 is '22';
	String lang="en-GB";

//subclass shade
public class Shade{
	String val="clear";
	String color="auto";
	String fill="F8F8F8";

public Shade(){

}

//E2EFD9" w:themeFill="accent6" w:themeFillTint="33"/>
public void setNoteFill(){
	this.fill="E2EFD9";
}

public String getXML(){
	String output="<w:shd w:val=\""+this.val+"\" w:color=\""+this.color+"\" w:fill=\""+this.fill+"\"/>";
	return output;
}

}

public class Tint{
	String blue="2F5496";

//<w:color w:val="2F5496" />
public Tint(){

}

public String getBlue(){
	return getXML(this.blue);
}

public String getXML(String input){
	String output="<w:color w:val=\""+input+"\"/>";
	return output;
}

}


/*
	"<w:style w:type=\"paragraph\" w:styleId=\"NormalReally\"><w:name w:val=\"NormalReally\"/><w:basedOn w:val=\"Normal\"/><w:uiPriority w:val=\"0\"/><w:qFormat/><w:pPr><w:jc w:val=\"both\"/><w:outlineLvl w:val=\"4\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Times New Roman\" w:eastAsia=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\"/><w:sz w:val=\"22\"/><w:lang w:bidi=\"ar-SA\"/></w:rPr></w:style>";
	*/


/*
 w:style w:type="paragraph" w:customStyle="1" w:styleId="Code"><w:name w:val="Code"/><w:aliases w:val="RCode,PythonCode"/><w:basedOn w:val="Normal"/><w:autoRedefine/><w:qFormat/><w:pPr><w:shd w:val="clear" w:color="auto" w:fill="F8F8F8"/></w:pPr><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/></w:rPr></w:style></w:styles>
 */

//<w:shd w:val="clear" w:color="auto" w:fill="F8F8F8"/>

private String updateXMLString(){
	//general properties
	String output = "<w:style w:type=\""+this.type+"\" w:customStyle=\""+this.customStyle+"\" w:styleId=\""+this.Id+"\">";

	output=output+"<w:name w:val=\""+this.name+"\"/>";
	output=output+"<w:basedOn w:val=\""+this.basedOn+"\"/>";
	if (this.nextPara.length()>0){
		output=output+"<w:next w:val=\""+this.nextPara+"\"/>";
	}
	output=output+"<w:autoRedefine/><w:qFormat/>"; //Word UI: automatically update; add to QuickStyles.
	//output=output+"<w:uiPriority w:val=\""+this.uiPriority+"\"/>";
	//start paragraph properties
	output=output+"<w:pPr>";
	if (this.outlineLevel!=99) {
		output=output+"<w:outlineLvl w:val=\""+this.outlineLevel+"\"/>";
	}
	if (this.afterSpace>0){
		output=output+"<w:spacing w:after=\""+this.afterSpace+"\"/>"; //120 equals 6 pt after?
	}
	output=output+shade; //default is no shading  
	output=output+"</w:pPr>"; //end para parameters
	output=output+"<w:rPr>"; //start run parameters
	if (getFont().length()>0) {
		output=output+"<w:rFonts w:ascii=\""+this.Fonts+"\" w:hAnsi=\""+this.Fonts+"\"/>"; 
	}
	output=output+this.FontColorXML;
	output=output+this.formatXML;
	if (getSize()>1){
		output=output+"<w:sz w:val=\""+this.size+"\"/>";
	}
	//output=output+"<w:lang w:eastAsia=\""+this.lang+"\"/>"; //w:bidi=
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
//If too complicated, split up and set according to paragraphs and spacing, fill and other criteria
public void setStyleAttrib(String id, String basedOn, String nextp, int outline, String font, int size, String shade, int after, String color, String format) { 
  setId(id); //must match the test otherwise circular
  setOutlineLevel(outline);
  if (font.length()>0) {
	  setFont(font); 
  }
  setBasedOn(basedOn);
  if (size>1) {
	  setSize(size);   	
  }
  setNextPara(nextp);

  if (shade.equals("shade")){
  	Shade aShader = new Shade();
  	setShade(aShader.getXML());
  }
  if (shade.equals("note")){
  	Shade noteShade = new Shade();
  	noteShade.setNoteFill();
  	setShade(noteShade.getXML());
  }
  if (after>0) {
  	this.afterSpace=after; //alternatively, ask user in 6pt, 12pt and adjust here.
  }
  if (color.equals("blue")) {
  	Tint colTint=new Tint();
  	setFontColor(colTint.getBlue());
  }
  if (format.length()>0){
  	setFormat(format);
  }
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

public void setFontColor(String input) {
	this.FontColorXML=input;
}

public void setFormat(String input){
	if (input.equals("bold")) {
		this.formatXML="<w:b/>";
	}
	if (input.equals("italic")) {
		this.formatXML="<w:i/>";
	}
}

public String getFontColor() {
	return this.FontColorXML;
}

public void setBasedOn(String input) {
	this.basedOn=input;
}

public void setNextPara(String input) {
	this.nextPara=input;
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

public String setShade(){
	return this.shade;
}

public void setShade(String input){
	this.shade=input;
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

/* Complicating factors in Word:
If a style is 'based on' another style this will not immediately find outline level
in the style definition.  So an additional step is needed to query other styles.
see function convertStylesXMLtoObjects() in xmlStyles
e.g. Heading 1, then Word doesn't independently store the outline level with this style.
*/
public void extractOutlineLevel(String ss){
	System.out.println("ss string:"+ss);
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
