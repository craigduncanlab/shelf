
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class
//(C) Craig Duncan 2017-2020

public class ClauseContainer extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -64702044414208496L;


//This node's metadata
String ContainerType=""; 
int numClauses=0; //this will hold number of clauses
//NODE INPUT FIELDS
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String shortname="";
String heading="";
String date="";
//this node's local reference for input/ouput
int noderef=0;
//NODE OUTPUT FIELDS
String output="";

//NODE CATEGORY
NodeCategory nodeCat = new NodeCategory();

//NODE ASSOCIATIONS: i.e. Graph nodes along edges:
ClauseContainer ultimateParent;
ClauseContainer myParentNode; //for structural associations
ClauseContainer dataLinkParent; //the node to 'follow' for follow mode.
ClauseContainer dataDisplayNode; //the data this Node will display 
ArrayList<ClauseContainer> myChildNodes = new ArrayList<ClauseContainer>();

//NODE CATEGORIES (FOR WORLD NODES) TO DO: Turn into a ClauseContainer array
ArrayList<NodeCategory> nodeCatList = new ArrayList<NodeCategory>();
//This node's data and level in tree:
Clause dataClause = new Clause(); 

String nodecategory = "";
int nodelevel = 0; //start at root 0 (project) allows future tree expansion
int nodeGUIloc = 0; //to store Stage or GUI element where the node is located
//(nodelocation can match Stage?)
// store the node's preference for what GUI view & data view to start in.
//(for current session only.  TO DO: store when saving.)
String userNodeView;
String followerMode;
//counters
int count=0; //general purpose counter for node
int branchcount=0; //general purpose counter for this branch of node
//html text
String htmlString="";
String mdString="";
//depth, level count for DFS
int graphdepth=0;
int graphcount=0;
String outlinenum="";
Boolean visited=false;
int VparentIndex=0; //to hold the parentindex value after BFS
int HparentIndex=0;
//TO DO: rename.  These mean the embedded node positions (within a layer of child nodes)
double childNodeX = 0.0;
double childNodeY = 0.0;

//As of 26.4.18 - Make this node hold its own text, title (for now use docnotes as node text)
//consequences: the concept of a 'clause' can be replaced by 
//a) nodeCat to hold node category b) docnotes here to hold the text itself. 

/* 3.5.18 - introduce possibility of data link (parent).  
i.e. separation of 
(a) structure of related concent: child nodes for navigation/association
(b) links for association (mirroring/styling) of data from another node.
should this be all content, or just text?
in effect, this node can carry a pointer to a parent node that will override specific contents
a 'persistent' override will always override (i.e. box is just a shell: points to master copy somewhere?).
i.e. the shell is subordinate/dependant: a 'linked box'.  How to show?
Linked boxes content (displayNode) not directly editable, but can they open the parent data node?
an 'apply once' will just refresh the static data (which elements of node?).
(this is no different to a 'copy data' option for a selected target?)
a 'refresh' option can reproduce nodes and refresh them from parent data links AT THAT TIME.
if there is a parent link, when can local (static) data change?
if data links at are node level, then opening a node will display the parent node content...
----
Maybe the 'displayNode', that is currently inside a StageManager (on Open) is not the only Node 'layer'
i.e. there can be a contentNode and a display node inside a StageManager...
(a parent data link, and static content/child data link?).
The StageManager (node viewer) can decide which of these has priority at any time?
i.e. you can switch on a data override, so that a node will display a parent OR a node can be edited as if it is independent.
i.e. your GUI can help decide what state a node is on - follower node, or independent node.
follower nodes are helpful for showing data, but when in follower mode, we say not editable.
i.e.'userView' state is GUI level for layout.
the 'dataMode' is follower or editable.  You can set to 'follower', then copy once, then go to editable.
or you can leave it in follower mode.
{This is really a user-level, flexible pointer system}.
nb - if we store 'userView' in the node itself (rather than the StageManager), then even when the GUI recreates the scene, it can find the last setting for "UserView" and reinstate that as well.
this way, we can pre-save the views for presentations.

*/
//empty constructor no arguments
public ClauseContainer() {

}

//constructor with category
public ClauseContainer(String category) {
	setNodeCategory(category);

}

//constructor with label, main text and notes
public ClauseContainer(String label, String text, String note) {
	setShortname(label); 
	setMD(text);
	setHTML(text); //TO DO: make this auto-update into HTML note MD using Stage editor
	setNotes(note);
	//other
	setAuthorName("Craig");
	setDocName(label); //to be phased out.  It's just a MD section.
    setHeading(label);
    setOutputText("");
}

//general update text function
public void updateMDText(String label, String text, String note) {
	//setDocName(name);
	setMD(text);
    setHeading(label);
    setNotes(note);
    //setOutputText(outputtext);
    //setHTML(htmltext);
}

/*constructor with category and node text
The label is used to set document name and heading 
In turn, document name will be used for the viewer title when Node is opened. */

public ClauseContainer(NodeCategory nodecat, ClauseContainer parentNode, String inputtext, String label) {
	setNC(nodecat);
    setShortname(label); // word tool only ???
    String htmltext=defaultHTML();
    updateText(htmltext,label,label,inputtext,"output");
    setType(nodecat.getCategory());
    setAuthorName("Craig");
    //heading/label for this node
    
    dataLinkParent= new ClauseContainer(); //detached from main tree?
    setParentNode(parentNode);
    setUltimateParent(parentNode.getUltimateParent());
}

//constructor 2 - a default container based on category 
//parent node is updated for this node.
//nb if a child of the master data node (visually represented as Stage_WS)
//checks current docnum for this stage and advances it.
//TO DO: advance docnumber based on category.
public ClauseContainer (NodeCategory nodecat, ClauseContainer parentNode) {
	int docNum = nodecat.advanceDocCount();
   
    setNC(nodecat);
    //
    String docname=nodecat.getCategory()+docNum;
    String htmltext=defaultHTML();
    updateText(htmltext,docname,"heading","","output");
    setType(nodecat.getCategory());
    setAuthorName("Craig");
    dataLinkParent = new ClauseContainer();
    setParentNode(parentNode);
    setUltimateParent(parentNode.getUltimateParent());
}

//constructor 3 - constructor for word tool node creation
//parent node not set here (it's unknown) - will be set when added to display

public ClauseContainer (NodeCategory nodecat) {
	int docNum = nodecat.advanceDocCount();
    setNC(nodecat);
    setType(nodecat.getCategory());
    String docname=nodecat.getCategory()+docNum;
    //
    setShortname(docname);
    String htmltext=defaultHTML();
    updateText(htmltext,docname,"default","","output");
    setAuthorName("Craig");
    dataLinkParent = new ClauseContainer();
    //setParentNode(parentNode);
}

//META
public void setType(String myString) {
	this.ContainerType=myString;
}

public String getType() {
	return this.ContainerType;
}

/*Method to specify the node utilised for data 
(another node or this node's data) depending on data mode node is in.
Defaults to returning this object if errors.
*/

private ClauseContainer getdataDisplayNode() {
	if (getDataMode()==null) {
		return ClauseContainer.this;
	}
	if (getDataMode().equals("follower")) {
		if (this.dataLinkParent==null) {
			return ClauseContainer.this;
		}
		else {
			return this.dataLinkParent;
		}
	}
	else {
		return ClauseContainer.this;
	}
}

//DEFAULT USER VIEWS
//GUI layout.  This is currently not affected by follower mode.
public String getUserView() {
	return this.userNodeView;
}

public void setUserView(String myView) {
	this.userNodeView=myView;
}

public void setChildNodeXY(double cnx, double cny) {
	this.childNodeX=cnx;
	this.childNodeY=cny;
}

public double getChildNodeX() {
	return this.childNodeX;
}

public double getChildNodeY() {
	return this.childNodeY;
}

// ---- FOLLOWER MODE AND DATA

public void setFollow(ClauseContainer myParentLink) {
	this.dataLinkParent = myParentLink;
	setDataMode("follower");
}

public void unsetFollow() {
	setDataMode("own");
}

public ClauseContainer getFollow() {
	return this.dataLinkParent;
}

public void setDataMode(String myDataMode) {
	this.followerMode=myDataMode;
}

//data level pipeline
private String getDataMode() {
	return this.followerMode;
}

//public access to state.  
//Boolean allows internal structures to change without having to write external code.
public Boolean isFollower() {
	if (getDataMode()=="follower") {
		System.out.println("Current parent link:"+getFollow().toString());
		return true;
	}
	else {
		return false;
	}
}

//---ULTIMATE PARENT
public void setUltimateParent(ClauseContainer myFileNode) {
	this.ultimateParent = myFileNode;
}

public ClauseContainer getUltimateParent() {
	return this.ultimateParent;
}

//---PARENT (NAVIGATION) NODE DATA ---

public void addParentNode(ClauseContainer parentNode) {
	if (parentNode==null) {
		System.out.println("Error: problem with parentNode");
	}
	else {
		this.myParentNode = parentNode;
		myParentNode.setChildNode(ClauseContainer.this);
	}
}

public void setChildNode(ClauseContainer childNode) {
	this.myChildNodes.add(childNode);
}

public void setParentNode(ClauseContainer node) {
	this.myParentNode = node;
}

public void unsetParentNode() {
	/*ClauseContainer parentNode=getParentNode();
        if (parentNode==null) {
            System.out.println("No parent node to unset");
            return;
        }
        */
    if (this.myParentNode!=null) {
    	this.myParentNode.removeChildNode(ClauseContainer.this);
    }
}

public ClauseContainer getParentNode() {
	return this.myParentNode;
}

//---CHILD NODES---

public void addChildNode(ClauseContainer node) {
	this.myChildNodes.add(node);
	node.setParentNode(ClauseContainer.this);
}

public void removeChildNode(ClauseContainer node) {
	this.myChildNodes.remove(node);
}

public Boolean NodeIsLeaf() {
	return this.myChildNodes.isEmpty();
}

//method to print clause category
public void addNodeChildren(ClauseContainer parentNode) {
	if (parentNode.getChildNodes().size()==0) {
		System.out.println("No child nodes to add");
		return;
	}
	ArrayList<ClauseContainer> childrenArray = parentNode.getChildNodes();
	Iterator<ClauseContainer> NodeCycle = childrenArray.iterator();
	while (NodeCycle.hasNext()) {
		ClauseContainer myNode = NodeCycle.next();
		addChildNode(myNode);
		}
	}



//TRANSITION METHODS

//replace function name with 'addChildClause' i.e. Child node with clause
public void addClause(Clause newClause) {
	//new Container with the clause as data
	ClauseContainer newContainer = new ClauseContainer();
	newContainer.addNodeClause(newClause);
	//add Clause Container as child node
	this.myChildNodes.add(newContainer);
}

//replace function name with 'addChildClause' i.e. Child node with clause
public void removeClause(Clause oldClause) {
	this.myChildNodes.remove(oldClause);
}

//rename this function - it returns all child nodes, but not data
//redundant: use getChildNodesimport java.net.*;

public ArrayList<ClauseContainer> getClauseArray() {
	return this.myChildNodes;
}

//METHODS FOR INTERNAL AND EXTERNAL UPDATES TO NODE

/*Helper ethod for cloning node (ClauseContainer)
Sets all set child nodes in this node using passed array of Nodes (ClauseContainer objects)
Rename this e.g. 'populateChildNodes'
*/

public void setAllChildNodes(ArrayList<ClauseContainer> myArray) {
	ArrayList<ClauseContainer> tempArray = new ArrayList<ClauseContainer>(); 
	Iterator<ClauseContainer> myIterator = myArray.iterator(); 
	while (myIterator.hasNext()) {
		ClauseContainer tempNode = myIterator.next();
		tempArray.add(tempNode);
	}
	this.myChildNodes=tempArray;
}

/*  --- THESE PUBLIC METHODS FORM A MINI API THAT EXPOSES A PUBLIC DATA LAYER (NODE) TO THE GUI ---
This allows the node to choose to follow another node and make that data
available to the GUI in preference to its own data.
(i.e. its own data 'wrapper')

Also enables the GUI to toggle the 'view' - i.e. to change the node's preference and save it.
- must work in conjunction with setting a parent node to link to.
*/

public String getDocName () {
	return publicText(getdataDisplayNode().getthisDocname());
}

public String getHeading() {
	return publicText(getdataDisplayNode().getthisHeading());
}

private String getShortname () {
	return publicText(getdataDisplayNode().getthisShortname());
}

public String getOutputText() {
	return publicText(getdataDisplayNode().getthisOutputText());
}

public String getNotes () {
	//return publicText(getdataDisplayNode().getthisNotes());
	return this.docnotes;
}

public ArrayList<ClauseContainer> getChildNodes() {
	//return getdataDisplayNode().getthisChildNodes();
	return this.myChildNodes;

}

// --- PUBLIC METHODS ACCESSING PRIVATE DATA SPECIFICALLY FOR THIS NODE

public void setCount(int mycount) {
	this.count = mycount;
}

public int getCount() {
	return this.count;
}


//GRAPH TRAVERSALS
public void setNodeRef(int myindex) {
	this.noderef = myindex;
}

public int getVParentIndex() {
	return this.VparentIndex;
}

public void setVParentIndex(int u) {
	this.VparentIndex = u;
}

public int getHParentIndex() {
	return this.HparentIndex;
}

public void setHParentIndex(int u) {
	this.HparentIndex = u;
}

public int getNodeRef() {
	return this.noderef;
}

public void setDepth(int level) {
	this.graphdepth = level;
}

public int getDepth() {
	return this.graphdepth;
}
public void setLevelCount(int count) {
	this.graphcount = count;
}

public int getLevelCount() {
	return this.graphcount;
}

public void setOutline(String num) {
	this.outlinenum = num;
}

public String getOutline() {
	return this.outlinenum;
}

public void setVisited(Boolean bool) {
	this.visited = bool;
}

public Boolean getVisited() {
	return this.visited;
}



public void setBranchCount(int mycount) {
	this.branchcount = mycount;
}

public int getBranchCount() {
	return this.branchcount;
}

//default HTML
private String defaultHTML() {
	//return "<html dir="ltr"><head></head><body contenteditable="true"></body></html>"
	return "<html><head></head><body></body></html>";
}

//general update text function
public void updateText(String htmltext, String name, String heading, String inputtext, String outputtext) {
	setDocName(name);
    setHeading(heading);
    setNotes(inputtext);
    setOutputText(outputtext);
    setHTML(htmltext);
}

//set the text that will be the main descriptive or clause text in this node
public void setNotes (String myString) {
	this.docnotes = myString;
}

public void setHTML (String myString) {
	this.htmlString = myString;
}

public void setMD (String myString) {
	this.mdString = myString;
}

public String getMD () {
	return this.mdString;
}

public String getHTML() {
	return this.htmlString;
}

private void setShortname (String myString) {
	this.shortname = myString;
}

//set the text that will be the main text for identifying this node
public void setHeading (String myString) {
	this.heading = myString;
}

//set the text that will be the main text for identifying this node (and using on SpriteBox label)
public void setDocName (String myString) {
	this.docname = myString;
}

public void setOutputText(String myString) {
	this.output = myString;
}

// --- PRIVATE METHODS ACCESSING PRIVATE DATA FOR THIS NODE

private String publicText(String myString) {
	return myString;
	/*
	if (isFollower()==true) {
		return myString+" [Followed]";
	}
	else {
		return myString;
	}
	*/
}

private String getthisNotes() {
	return this.docnotes;
}

private String getthisDocname() {
	return this.docname;
}

private String getthisHeading () {
	return this.heading;
}

private String getthisShortname () {
	return this.shortname;
}

private ArrayList<ClauseContainer> getthisChildNodes() {
	return this.myChildNodes;
}

//NODE'S OUTPUT TEXT FIELD
private String getthisOutputText() {
	return this.output;
}

//THIS NODE'S CLAUSE DATA (OBSOLETE)

public void addNodeClause(Clause thisClause) {
	this.dataClause = thisClause;
}

public Clause getNodeClause() {
	return this.dataClause;
}

//THIS NODE'S CATEGORY
//using a category object
public NodeCategory getNC() {
	return this.nodeCat;
}

public void setNC(NodeCategory myNC) {
	this.nodeCat=myNC;
}

//a query from the category object itself.
public String getNodeCategory() {
	return this.nodeCat.getCategory();
}

public void setNodeCategory(String myCat) {
	this.nodeCat.setCategory(myCat);
}

public int getNodeLevel() {
	return this.nodeCat.getLevel();
}

public void setNodeLevel(int myLevel) {
	this.nodeCat.setLevel(myLevel);
}

public int getNodeLocation() {
	return this.nodeGUIloc;
}

public void setNodeLocation(int myLoc) {
	this.nodeGUIloc=myLoc;
}

public String getNodeColour() {
	return this.nodeCat.getColour();
}

public void setNodeColour(String myCol) {
	this.nodeCat.setColour(myCol);
}

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/

public void doPrintIteration() {
	//Do first iteration to print out only Definitions in sequence
	if (NodeIsLeaf()==false) {
		return;
	}
	//TO DO
}

//method to print clause category
private String printClauseCategory(String testCat) {
	if (this.myChildNodes.size()==0) {
		System.out.println("No child nodes to print");
		return "";
	}
	Iterator<ClauseContainer> myNodeIt = this.myChildNodes.iterator();
	String output="";
	output=output+"\n "+testCat+" \n\n";
	//printClauseCategory(testCat);
	while (myNodeIt.hasNext()) {
		ClauseContainer myNode = myNodeIt.next();
		String category = myNode.getNodeCategory();
		//Clause myclause = myNode.getNodeClause();
		if (category.equals(testCat)) {
			String mylabel = myNode.getDocName();
			String myheading = myNode.getHeading();
			String mytext = myNode.getNotes();
			//output=output+myheading+" ("+category+")"+":\n----------\n"+mytext+"\n\n";
			output=output+"\""+myheading+"\""+" means "+mytext+"\n";
		}
	}
	return output;
}


/* Method to summarily output different categories of Clause in this Node.
*/

public String getClauseAndText() {
	
	String outText = printClauseCategory("legalrole");
	outText=outText+printClauseCategory("definition");
	outText=outText+printClauseCategory("clause");
	outText=outText+printClauseCategory("event");
	return outText;
}

/* method to return number of clauses in this Container */

public int getNumClauses() {
	return this.myChildNodes.size();
}

public ClauseContainer cloneContainer() {
	ClauseContainer clone = new ClauseContainer();
	clone.setAllChildNodes(this.myChildNodes);  
	clone.setHTML(this.htmlString); 
	clone.setMD(this.mdString);
	clone.setNumClauses(this.numClauses); //this will hold number of clauses
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setAuthorName(this.docauthor); //to hold author name
	clone.setNotes(this.docnotes);
	clone.setDate(this.date);
	clone.setType(this.ContainerType);
	return clone;
}

}