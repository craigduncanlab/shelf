import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc
//some other libraries needed for HTML output
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
//
//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
//scene
import javafx.scene.layout.Pane;
//for saving writable images as files
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class makeHTML {

String[] numberLevel = new String[100];

public makeHTML() {
	
}

public void HTMLoutput(ClauseContainer myNode, String filename) {
	//
	cleantemplate(filename);
	//
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getDFS(myNode);
	//System.out.println("Inside HTMLoutput.  The current nodes[1] is :"+myGraphSeq[1].toString());
	System.out.println("container length: "+myGraphSeq.length);
	//structure
	int u=0;
	ClauseContainer thisNode=new ClauseContainer();
	while (myGraphSeq[u]!=null)
	{
		thisNode = myGraphSeq[u];
		System.out.println(u+":"+thisNode.toString());
		thisNode.setNodeRef(0); //reset "Visited"
		writeHTMLFilesOut(u,thisNode,filename);
		//concepts image
		if (thisNode.NodeIsLeaf()==false) {  //has some children so will have a 'concept image'
			saveConceptImage(thisNode,filename);
		}
		System.out.println("Inside HTMLoutput.  The current index is:"+u);
		u++;
	}
	writeHTMLFilesEnd(filename);
	//thisNode.setNodeRef(0);
}

//builds concept image from the node data alone, then saves as image and creates HTML link.
private void saveConceptImage(ClauseContainer myNode,String mainfile) {
	Group myConceptGroup=makeConceptsImage();
	//iterate through node children and call addNodeToConceptsImage() for each
	ArrayList<ClauseContainer> childNodes = myNode.getChildNodes();
    Iterator<ClauseContainer> myiterator = childNodes.iterator();
	//only operates if there are Child Nodes to add
    while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            //System.out.println("Current child node to be added: "+thisNode.toString());
            myConceptGroup=addNodeToConceptsImage(thisNode,myConceptGroup);
        }
    String filename = myNode.getDocName();
	writeSnapshot(filename,myConceptGroup); //present state of Group.
	writeHTMLimage(myNode,mainfile);	
}

//general method to write out to
private void writer(String logstring, String reportfile) {
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	System.out.print(logstring); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//method to write all html output in this branch to same file
//needs to remove the HTMLeditor tags from each record and insert new ones around composite file

private void writeHTMLFilesOut(int index, ClauseContainer myNode, String filename) {
	String reportfile="../html/"+filename+".html";
	String logString = myNode.getHTML();
	//remove section tags in default HTML editor text
	String replaceString = logString.replaceAll("(<html[ =\\w\\\"]*>{1})|(<body[ =\\w\\\"]*>{1})|<html>|</html>|<body>|</body>|<head>|</head>",""); //regEx
	if(index==0) {
	 	logString = "<html><head><title>"+filename+"</title></head>"+"<body>";//+replaceString;
	 	logString=logString+"<p><b>"+myNode.getOutline()+" "+myNode.getHeading()+"</b></p>"+replaceString;
	 }
	 else {
	 	logString = "<p><b>";
	 	logString=logString+myNode.getOutline();
	 	logString=logString+" "+myNode.getHeading()+"</b></p>"+replaceString;
	 }
	writer(logString,reportfile); 
	myNode.setVisited(false); //reset state before next HTML write/graph traversal
}

private void writeHTMLFilesStart (String filename) {
	String reportfile="../html/"+filename+".html";
	String logString = "<html><body>";
	writer (logString,reportfile);
}

private void writeHTMLFilesEnd (String filename) {
	String reportfile="../html/"+filename+".html";
	String logString = "</body></html>";
	writer (logString,reportfile);
}

private void cleantemplate(String filename) {
	String reportfile="../html/"+filename+".html";
	cleanfile(reportfile);
}

private void cleanfile(String reportfile) {
	try {

	PrintWriter pw = new PrintWriter(reportfile);
	pw.close();
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}

}

/*
Method to make ConceptsImage in its own stage for image purposes (snapshot) only
TO DO:
(int) Math.round(bounds.getWidth() * scale),
(int) Math.round(bounds.getHeight() * scale));
*/
private Group makeConceptsImage() {
    int winWidth=650;
    int winHeight=400;
    Group myConceptGroup = new Group();
    Pane largePane = new Pane();
    largePane.setPrefSize(winWidth, winHeight);
    largePane.getChildren().add(myConceptGroup); //toggle option? 
    //nodes must be in scene to use with WriteableImage snapshot
    Scene myScene = new Scene (largePane,winWidth, winHeight); //default width x height (px)
    return myConceptGroup;
}

//simply make up a box for each 'node', then add to group and capture that as image
//WritableImage image = root.snapshot(new SnapshotParameters(), null);

private Group addNodeToConceptsImage (ClauseContainer myNode, Group myGroup) {
    SpriteBox b = new SpriteBox(myNode); //evenhandlers not needed
    if (b==null) {
        System.out.println("SpriteBox null in addNodeToConceptsImage");
        System.exit(0);
    }
    myGroup.getChildren().add(b); //GUI tree 
    //System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    //simple update to position or put at origin.  Position is held in data node.
    if (b.getX()!=0 || b.getY()!=0) {
        b.setTranslateX(myNode.getChildNodeX());
        b.setTranslateY(myNode.getChildNodeY());
    }
    else {
        b.setTranslateX(0);
        b.setTranslateY(0); 
    } 
    return myGroup; //return updated
    }

//method to write the concepts node as an image which will then be used in HTML statements
//get the image
private void writeSnapshot(String filename, Group myGroup) {
	int winWidth=650;
    int winHeight=400;
	try {
	WritableImage image = new WritableImage (winWidth,winHeight); //set this to same as myGroup etc
	SnapshotParameters spa = new SnapshotParameters();
	ImageView view = new ImageView(myGroup.snapshot(spa, image));

	//name the output file
	String pathname = "../html/images/"+filename+".png"; //still need extension?
	File file = new File(pathname);
	BufferedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
	ImageIO.write(renderedImage,"png",file);
	} catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//Method to write out link to HTML image for current concept image
private void writeHTMLimage(ClauseContainer myNode, String filename) {
	int winWidth=650;
    int winHeight=400;
    String localimage = myNode.getDocName();
	String reportfile="../html/"+filename+".html";
	String pathname = "../html/images/"+localimage+".png";
	String quot="\"";
	String logstring = "<p><img src="+quot+pathname+quot+" alt="+quot+filename+quot+" height="+quot+winHeight+quot+" width="+quot+winWidth+quot+"></p>";
	writer(logstring,reportfile);
}
}

