import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


public class TemplateUtil {

//File IO locations.  If running from classes folder with java -cp ./ Main use ../templates
Config myConfig = new Config();
String templatefolder = myConfig.getTemplatesFolder();
       

public TemplateUtil() {
	
}

//Simple utility to return contents of file as String
public String getFileText(File myFile) {
	StringBuffer myText = new StringBuffer(); //mutable String
	String endOfLine="\n";
	try {
		Scanner scanner1 = new Scanner(myFile);
		if (scanner1==null) {
			System.out.println("No text/html content");
			return null;
		}
		int nl=0;
		while (scanner1.hasNextLine()) {
			nl++;
			String thisRow=scanner1.nextLine();
			System.out.println(thisRow);
			myText.append(thisRow);
			myText.append(endOfLine);
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return null;
	}
	//System.out.println(myText);
	//System.exit(0);
	return myText.toString();
}

//return a graph node holding the current dictionary as a graph of subnodes
public ClauseContainer getTemplate(String filename) {
	return readSimpleTemplate(filename);
}

/*
Method to allow easy setup of templates just using headings in a text file.
the first entry in each row is read in as a container (node)
the rest of the entries in each row are read in as sub-nodes of that node

The node returned is the root node which is to be placed onto the workspace.
*/

private ClauseContainer readSimpleTemplate(String filename) {
	int nodeindex=0;
	String fileref=templatefolder+filename+".pdt";
	String boxlabel = "Template";
	NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
	ClauseContainer templateNode = new ClauseContainer(NC_templ);
	templateNode.setDocName(filename);
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No text/html content");
			return null;
		}
		int nl=0;
		while (scanner1.hasNextLine()) {
			nl++;
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first word in row
			String hdword = scanner2.next();
			ClauseContainer wordNode = new ClauseContainer(NC_templ,templateNode,hdword,hdword);
			templateNode.addChildNode(wordNode);
			//create child nodes for rest of words in row
			while (scanner2.hasNext()) {
				String rowword = scanner2.next();
				wordNode.addChildNode(new ClauseContainer(NC_templ,wordNode,rowword,rowword));
			}
		scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return null;
	}
	return templateNode;
}


//return a graph node holding the current file as a graph of subnodes, but with content included
//Node 0 will be the root node, to be returned.

public ClauseContainer getStructuredData(String filename) {
	ClauseContainer[] myNodeBase = readNodeDataSetup(filename);
	return myNodeBase[0]; //returns root node
}


/*
Fill the graph structure with the stored data, then return first entry (root data node)
This is currently fixed on the assumption that nodes have defined sections 
e.g. name, heading, notes, HTML
*/
private ClauseContainer[] readNodeData(String filename) {
	String fileref=this.templatefolder+filename+".pdd";
	ClauseContainer[] newNodeBase=new ClauseContainer[300];
	try {
		Scanner scanner1 = new Scanner(new File(fileref)).useDelimiter("@EOR"); //instead of \n
		if (scanner1==null) {
			System.out.println("No text/html content");
			return newNodeBase;
		}
		while (scanner1.hasNext()) {
			String thisRow=scanner1.next();
			Scanner scanner2= new Scanner(thisRow).useDelimiter("@@P"); //change to benign delimiter
			String hdword = scanner2.next();
			System.out.println(hdword);
			int noderef = Integer.valueOf(hdword);
			String name = scanner2.next();
			String heading = scanner2.next();
			String notes = scanner2.next();
			String htmltext = scanner2.next();
			NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
			newNodeBase[noderef] = new ClauseContainer(NC_templ);
			newNodeBase[noderef].setDocName(name);
			newNodeBase[noderef].setHeading(heading);
			newNodeBase[noderef].setNotes(notes);
			newNodeBase[noderef].setHTML(htmltext);
			scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return newNodeBase;
	}

	//
	return newNodeBase; //return root node, with its references to other nodes
}

/*This splits the powerdock file into the first part (structure) and second part (node data)
 It is intended to create an array of Nodes, with a defined structure of child nodes.
 It returns that array.

(better to call these child nodes 'internal' nodes, if they are hidden until the node is opened.)

nb: each of these is stored with its coordinates.   The data treats each as unique (i.e. each node has many sub-nodes....which are individual contents... they are not modelled as unique ID nodes as in a true graph.  i.e. if you drop the same information as a child of different nodes it is not an 'update'...it is a copy.)
Can retain this concept for now, and build up the idea of 'links' for nodes to other nodes.
filepath
TO DO:
Except for root node, each node should record:
(a) its coordinates if it is in the root node (currently the coordinates for the nodes in the root node are specified there, so OK)
(b) if it is linked to any other parent node in the same view (this works ok if every node is unique).  This could be stored in more than 1 way e.g. along with the info in parent nodes about its embedded child nodes, or with each individual node.  The link info is really data (connections info).

In effect, this is an UP/DOWN and ACROSS system for locating nodes.  Information can be conveyed in a sequence, and also embedded within a sequence.  This can be done at any level?

So the info we should split up and write separately is:
1. Node number and coordinates (in a table form, 3 columns).  If each node appears once, this is ok.
2. Node number and parent node (in a table, 1 column)
3. Node number and embedded nodes (this is just a list, really deserves to be separate, but since each node is currently a unique reference, it could just be a single table with a reference to the parent node of any specific node - this way we get back to a set number of columns for the data, and all data types together.)
4. Node number and contents of each of its categories (heading, text, html, etc)
2. 

OKAY, Here's the proposed write out structure (new format .pdt)
Node H_parentID  V-parentID  X-coord	Y-coord  Name   Heading Input  HTML  
#		#				#			#		#			str   str     #		#

 */
public ClauseContainer[] readNodeDataSetup(String filename) {
	ClauseContainer[] myNodeBase = new ClauseContainer[300];
	String fileref=this.templatefolder+filename+".pdn";
	//whole file
	try {
		String entireFileText = new Scanner(new File(fileref)).useDelimiter("\\A").next();
		String[] pdocfile = entireFileText.split("@@EndGraph@@"); //regex to keep delimeter in second part(look behind), not first. (@00P)|(?<=0@@P)
		String pds = pdocfile[0]; // 004
		String pdd = pdocfile[1];
	//process the nodes and their daa
	myNodeBase=readNodeDataString(pdd); //read in the second part of file contents: node data
	//System.out.println(".pdd done");
	/*System.out.println(pdd);
	System.exit(0);*/
	readStructureXYString(myNodeBase,pds); //read in the node positions, and structure
	//System.out.println(".pds done");
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return myNodeBase;
	}
	return myNodeBase;
	//readStructureXY(myNodeBase,filename);
	//System.out.println(".pds done");

}

/*read node data pdd
The function uses a new global array of 300 Container items (newNodeBase)
It takes as its input the string that forms the second part of the template file
It parses that by looking for the node reference number and the contents that follow
(i.e. this part of file handles sparce data)
Once the string values have been read in, the newNodeBase entries are updated.
This newNodeBase Array is then returned.

*/
private ClauseContainer[] readNodeDataString(String datastring) {
	//String fileref=this.templatefolder+filename+".pdd";
	ClauseContainer[] newNodeBase=new ClauseContainer[300];
	try {
		Scanner scanner1 = new Scanner(datastring).useDelimiter("@EOR"); //instead of \n
		if (scanner1==null) {
			System.out.println("No text/html content");
			return newNodeBase;
		}
		while (scanner1.hasNext()) {
			String thisRow=scanner1.next();
			Scanner scanner2= new Scanner(thisRow).useDelimiter("@@P"); //change to benign delimiter
			String hdword = scanner2.next();
			//System.out.println(hdword);
			int noderef = Integer.valueOf(hdword);
			String docname = scanner2.next();
			String heading = scanner2.next();
			String inputtext = scanner2.next();
			String htmltext = scanner2.next();
			NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
			newNodeBase[noderef] = new ClauseContainer(NC_templ);
			String outputtext="";
			newNodeBase[noderef].updateText(htmltext,docname,heading,inputtext,outputtext);
			newNodeBase[noderef].setUltimateParent(newNodeBase[0]);
			/*
			newNodeBase[noderef].setDocName(name);
			newNodeBase[noderef].setHeading(heading);
			newNodeBase[noderef].setNotes(notes);
			newNodeBase[noderef].setHTML(htmltext);
			*/
			/*if (noderef>0) {
				newNodeBase[noderef].setUltimateParent(newNodeBase[0]);//set all to root node
			}
			*/
			scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return newNodeBase;
	}

	//
	return newNodeBase; //return root node, with its references to other nodes
}

/*
This method takes the nodebase (an array of Container objects) as an object.
This has already been created and populated (because nodes are a finite list).
Now, this functions retrieves index numbers for those nodes, and gives them information about child nodes and coordinates. 

The string passed to this function is taken from the template file, and consists of line items with each nodes' child nodes (as numbered) and coordinates.
Each line item has a parent ID ('nodeID' in this function) and a list of node IDs (for the child nodes), with coordinates for the child nodes.

TO DO: Classify nodes information as :

(1) Internal nodes or captured/embedded nodes (currently the default).  e.g. each data line refers to a node which will not be seen until the parent node is opened to display a child window.
i.e. vertical navigation.

(2) Peer nodes with sequence/flow joins left to right.  e.g. each data line specifies the predecessor node within this layer.  This is restricted to 1.  That means that only 1 path leads to any particular node (except for root), but there can be more than one path leading out from a node. 

The predecessor nodes is the parent (FROM) and the node in the file is the child (TO).   A line/arrow will be drawn by the GUI to reflect this.

*/

private void readStructureXYString(ClauseContainer[] nodebase, String struct) {
	//ClauseContainer[] nodebase = new ClauseContainer[300];
	int nodeindex=0;
	//String fileref=this.templatefolder+filename+".pdn";
	//
	try {
		Scanner scanner1 = new Scanner(struct);
		if (scanner1==null) {
			System.out.println("No text/html content");
			//return nodebase;
		}
		//obtain max number of nodes from first line
		String firstRow=scanner1.nextLine();
		int nodeCount = Integer.valueOf(firstRow);
		int readCount=0;
		//
		while (scanner1.hasNextLine() && readCount<nodeCount) {
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first node in row
			String hdword = scanner2.next();
			int nodeID = Integer.valueOf(hdword);
			while (scanner2.hasNext()) {
				//3 entries per child node
				String rowword = scanner2.next();
				String xpos = scanner2.next();
				String ypos = scanner2.next();
				//
				double x = Double.valueOf(xpos);
				double y = Double.valueOf(ypos);
				int childref = Integer.valueOf(rowword);
				if (nodebase==null) {
					System.out.println("Nodebase is null");
				}
				if (childref==0) {
					System.out.println("Error in graph structure at nodeID:"+nodeID+" in row :"+hdword);
				}
				nodebase[nodeID].addChildNode(nodebase[childref]);
				nodebase[childref].setParentNode(nodebase[nodeID]);
				nodebase[childref].setChildNodeXY(x,y); //position
			}
		scanner2.close();
		readCount++;
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		//return nodebase;
	}
}

public void saveTemplateSingle(ClauseContainer myNode, String filename) {
	//
	cleantemplate(filename);
	//
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getBFS(myNode);
	int nodeCount = myGraphUtil.getBFSnum();
	System.out.println("container length: "+myGraphSeq.length);
	System.out.println("unique nodes: "+nodeCount);
	//structure
	int u=0;
	writeFirstLineHeader(nodeCount, filename);
	while (myGraphSeq[u]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[u];
		writeNewStructSingle(thisNode,filename); //this is just the structure
		u++;
		System.out.println("writing loop u: "+u);
	}
	writeSplitString(filename);
	int p=0;
	while (myGraphSeq[p]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[p];
		writeDataOutputNew(thisNode,filename); //this is the string data
		p++;
	}
}

/* This is a new method that saves all the data for every node in a rectangular data format
By insisting that every node can only have one parent, and that each node is unique, 
all of the data required to navigate horizontally and vertically can be stored with each node.
Also, the node's contents are able to be stored in a 'row' with each node, though this is harder to see in a text file that spans multiple lines

*/

public void saveTidyTemplate(ClauseContainer myNode, String filename) {
	//
	String extension = "pdt";
	cleanTidyTemplate(filename, extension);
	//
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getBFS(myNode); //get the tree breadth-first
	int nodeCount = myGraphUtil.getBFSnum();
	System.out.println("(Tidy) container length: "+myGraphSeq.length);
	System.out.println("unique nodes: "+nodeCount);

	//structure
	int u=0;
	writeTidyFirstLine(nodeCount, filename, extension);
	while (myGraphSeq[u]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[u];
		//TO DO: obtain the parentID from each node during this iteration.
		//use that parent ID when writing this out, it will enable cross-ref later.
		writeTidyOutput(thisNode,filename,extension); //this should write a whole row now
		u++;
	}
	/*
	writeSplitString(filename);
	int p=0;
	while (myGraphSeq[p]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[p];
		writeDataOutputNew(thisNode,filename); //this is the string data
		p++;
	}
	*/
}

/*  Method to write out row with node and child nodes listed as node index numbers
*/
private void cleanTidyTemplate(String filename, String extension) {
	String reportfile=templatefolder+filename+"."+extension;
	cleanfile(reportfile);
}

/*  Method to write out row with node and child nodes listed as node index numbers
*/
private void cleantemplate(String filename) {
	String reportfile=this.templatefolder+filename+".pdn";
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
A new method to write all the nodes in the tree to a single file, in a rectangular table
TO DO (19 Dec 2018):
write in, in this order:
(a) node ID
(b) parent node for vertical structure
(c) parent node for horizontal structure
(d) coordinates (vertical structure)
(e) strings...

*/

private void writeTidyOutput(ClauseContainer myNode, String filename, String extension) {
	String reportfile=this.templatefolder+filename+"."+extension;
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String coords = Double.toString(myNode.getChildNodeX())+","+Double.toString(myNode.getChildNodeY())+",";
	int vparentID = myNode.getVParentIndex();
	int hparentID = myNode.getHParentIndex();
	String logString = Integer.toString(myNode.getNodeRef())+","+coords+","+vparentID+","+hparentID+",";
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//
private void writeNewStructSingle(ClauseContainer myNode, String filename) {
	String reportfile=this.templatefolder+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+","+getChildrenData(myNode);
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

/*
write tidy first line with count of number of node entries to follow
*/
private void writeTidyFirstLine(int entryCount, String filename, String extension) {
	String reportfile=this.templatefolder+filename+"."+extension;
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(entryCount);
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//write first line header with number of objects

private void writeFirstLineHeader(int entryCount, String filename) {
	String reportfile=this.templatefolder+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(entryCount);
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

/*
write out structure and child node positions
//TO DO: In future, these could be binary data files
nb: the container method 'getChildNodeX' is returning a coordinate for that node, on the assumption it is embedded in a parent node
*/
private String getChildrenData(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return "";
	}
	String output="";
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	String childstring = "";
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		childstring=Integer.toString(nextNode.getNodeRef())+",";
		childstring = childstring+ Double.toString(nextNode.getChildNodeX())+","+Double.toString(nextNode.getChildNodeY())+",";
		output=output+childstring;
		}
	
	return output;
	}


//write out structure only
private String getChildrenList(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return "";
	}
	String output="";
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		output=output+Integer.toString(nextNode.getNodeRef())+",";
		}
	return output;
	}

//Method to write out row: node index plus data

private void writeSplitString(String filename) {
	String reportfile=this.templatefolder+filename+".pdn";
	String splitter="@@EndGraph@@";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	System.out.print(splitter); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//Method to write out the data for the node passed as argument

private void writeDataOutputNew(ClauseContainer myNode, String filename) {
	String reportfile=this.templatefolder+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+"@@P"+myNode.getDocName()+"@@P"+myNode.getHeading()+"@@P"+myNode.getNotes()+"@@P"+myNode.getHTML()+"@@P@EOR";
	System.out.print(logString); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

}