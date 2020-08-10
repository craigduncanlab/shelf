import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap, Array,ArrayList etc


public class GraphUtil {

//for queue
ArrayList<ClauseContainer> myQueue = new ArrayList<ClauseContainer>();
//for node indexing
int nodeSeq=0;
int nodeIndex=0;
int nodeAdded=0;
ClauseContainer[] myGraphSeq = new ClauseContainer[300];
//
LinkedList<ClauseContainer> myStack = new LinkedList<ClauseContainer>();
//
String fileOutput=""; //use stringbuffer


public GraphUtil() {
	
}

/*
method to obtain list of graph nodes in sequence, starting with the selected node/box
This is equivalent to a breadth first search (BFS)

*/

public ClauseContainer[] getBFS(ClauseContainer myNode) {
	updateBFS(myNode);
	System.out.println("BFS count:"+this.nodeAdded);
	return this.myGraphSeq;
}

public int getBFSnum() {
	return this.nodeAdded;
}

/* obtain list of nodes in DFS sequence */

public ClauseContainer[] getDFS(ClauseContainer myNode) {
	updateDFS(myNode);
	return this.myGraphSeq;
}

private int updateBFS(ClauseContainer myRootNode) {
	nodeSeq=0;
	myRootNode.setNodeRef(0);
	this.myQueue.add(myRootNode);
	this.myGraphSeq[0]=myRootNode;
	this.nodeSeq++;
	//
	while(this.myQueue.isEmpty()==false) {
		ClauseContainer firstNode = this.myQueue.get(0);
		addChildrenBFS(firstNode); //add children to Queue
		this.nodeAdded++;
		this.myQueue.remove(firstNode); //remove from Queue
	}
	return this.nodeSeq++;
}

//put child nodes on queue and give them a node index
//the noderef is a parameter in the ClauseContainer object
private void addChildrenBFS(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return;
	}
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		this.myQueue.add(nextNode);
		nextNode.setVParentIndex(thisNode.getNodeRef());
		nextNode.setNodeRef(this.nodeSeq);
		this.myGraphSeq[this.nodeSeq]=nextNode; //could use .add but this ensures index is aligned?
		this.nodeSeq++;
		}
		//return this.nodeSeq++;
	}

/*method uses a DFS.  Uses a stack (the LinkedList is overkill but has nice methods)
Only take a node off the stack (or mark as done) when all its children have been visited.
When the stack is empty (or root node is done) the job is done.
*/

private void updateDFS(ClauseContainer myRootNode) {
	this.nodeIndex=0;
	int rootlevel = 0;
	int count = 1;
	String setOutline="";
	System.out.println("Inside updateDFS.  Root node:"+myRootNode.toString());
	addChildrenDFS(myRootNode,rootlevel,count,setOutline);
}

private Boolean isVisited(ClauseContainer myNode) {
	if (myNode.getVisited()) {
		return true;
	}
	else {
		return false;
	}
}

private void updateVisitedDFS(ClauseContainer currentNode, int level, int count,String outline) {
	currentNode.setNodeRef(this.nodeIndex+1); 
	currentNode.setVisited(true);//visited
	currentNode.setDepth(level);
	
	if (level>0) {
		currentNode.setOutline(outline);
		currentNode.setLevelCount(count);
	}
	this.myGraphSeq[this.nodeIndex]=currentNode;
	this.nodeIndex++;
}

//put child nodes on queue and give them a node index
private void addChildrenDFS(ClauseContainer thisNode, int level, int count,String outline) {
	updateVisitedDFS(thisNode,level,count,outline);
	if (thisNode.NodeIsLeaf()==true) {
		return;
	}
	int hcount=1;
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		String newOutline="";
			//this.myStack.add(nextNode); //add to list of unvisited
			if (!isVisited(nextNode)) {
				if (level>0) {
					newOutline=thisNode.getOutline()+hcount+"."; //outline numbers
				}
				else {
					newOutline=Integer.toString(hcount)+".";
				}
				addChildrenDFS(nextNode,level+1,hcount,newOutline); 
				hcount++;//advance count at this level
			}	//recursive
			else {
				System.out.println("Throwing cycle(?).  Inside updateDFS/addChildren: visited = true: Count:"+count);
				System.out.println(" node:"+thisNode.toString()+"next childnode:"+ nextNode.toString());
			}
	}
}

}