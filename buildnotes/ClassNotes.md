# Class Notes

# Main.java

This application creates a GUI as a legal doc staging, editing & visualisation environment
JavaFX implementation of GUI started 17.11.2017 by Craig Duncan

/*The main method uses the launch method of the Application class.
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html
*/


/*
This 'extends Application' will be the standard extension to collect classes for JavaFX applications.
JavaFX applications have no general constructor and must override the 'start' method.
Note that JavaFX applications have a completely new command line interface:
https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.Parameters.html

usage:
From powerdock folder:
javac -d classes ./src/*.java
java -cp :classes Main

or java -cp classes Main

From classes folder (this is now the default for config folder etc):
javac -d ../classes ../src/*.java
java Main

or java -cp ./ Main
nb: https://askubuntu.com/questions/64222/how-can-i-create-launchers-on-my-desktop


*/

# StageManager.java

/* 
Class to manage each Stage
30.3.2018
Until now, Stage Manager class was used as a singleton.
However, by creating a 'StageManager' object for each stage, it can keep Stage-specific information
and enormously reduce the complexity of stage position, current sprite location etc.

This is required because the Stage object in JavaFX defined for the GUI.
This class is a conceptual object that will hold not only the javaFX Stage object, but associated data

Requires stageID to be set at start of app.
The Group that is part of the JavaFX node tree to which SpriteBoxes are to be added can be stored here.
(i.e. this saves having to navigate through the GUI node instances to find it each time)

26.4.18
Most of the functions are intended to be used with a stage that displays a 'node'.
In effect, this class helps make a GUI: to create a Stage that will display a node, its text and its child nodes, and allow editing
It also performs tracking of the stage (open node window) with current focus.
The Workspace is an instance of this class but uses far fewer helper functions.

The stages are iterative: in creating new child node boxes, each box can open a new node editing window
Therefore, the StageManager is like a visual tree navigator for the node data.
A node or Stage does not require opening up a separate 'edit' window because each node viewer's design is informative and functional.
(To do: Consider if "NodeViewer" is a better class name.  Nodes represent abstract 'frames'
A display option is to have background colour of node editor change for different types/levels)

The stage manager will provide its own GUI functions for updating the node's text.
28.4.18
This is also possible with images and video:
Each node can hold 1 image see https://www.tutorialspoint.com/javafx/javafx_images.htm
https://docs.oracle.com/javase/8/javafx/media-tutorial/overview.htm

30.4.18
Provided user choice of views e.g. (a) node text/input, child nodes, output area (b) node text only (c) child nodes only 
Keys to cycle through that for any chosen node.  [Every node is an app, the app is flexible]
Easy to achieve through a MVC model.
*/

# ClauseContainer.java
/* By Craig Duncan 1.12.2017 (based on Definitions Container made 21.11.2017)
This will store Clause objects, not the 'SpriteBox' that may enclose specific Clauses.
[10.6.18 - this is now a data node, and a 'super node' in the sense it has lots of functions and data fields]

You can use a ClauseContainer to quickly read of Clauses and then create SpriteBoxes for them in GUI.
Or create empty SpriteBoxes and populate with Clauses from a list?

3.4.18
Design notes:
GUI effectively allows copying of nodes at any level = branch cut/copy
i.e. node inspection in GUI or copy to WS is like temporary node detachment.
although GUI Stages may closely match Node levels, they are independent of one another
i.e. Stages can be made for ease of collecting Nodes at same level.
GUI also allows setting of Node level in background. ("New" = a  node level setter).

GUINodes so far have been the 'nodes' that hold stage contents.
These can either hold Boxes (if serializable) or a copy of the data Nodes that are in the boxes on a stage.
It comes down to data separation.
So far I've enforced 'levels' by allowing nodes to move acoss stages and thereby acquire a 'level'

My first design had: data levels were associated with what the GUI displayed:
i.e. level 2 was whatever the 'CollectionNode' for CollectionStage displayed.
However the Workspace was always intended to hold Nodes at any level
(in the GUI, it became a separate Node repository).
The solution is to have Collections of the Data held in the Stages,
and the GUI will display that Node on a particular stage when needed.
However, we can still enforce Stages only showing certain levels of objects if Nodes record level
(but relax this in the case of the Workspace) 

However, if each Node stores a 'level' then it will allow some search and save for particular kinds of nodes later, regadless of where they are located in the GUI.

27.4.18
Testing the idea that every node is [has data elements to make] a functional workspace.
i.e. it has input (text data) and output (text) areas, both can be displayed in GUI.
The GUI can then apply any of the general operations on text to any node (do not need a specific importer window - just use the current node text area).
This repurposes any space, makes the environment flexible and nodes are functional
--> everything is local (a kind of OO design?).  
The fact that a node can be added as a child makes this scaleable.

28.4.18
Each node should be able to hold 1 image see https://www.tutorialspoint.com/javafx/javafx_images.htm
(enables scalability by add child nodes if needed, in a single node)
What about sound/video?

3.5.18
Separation of ideas about associated nodes:
(a)child nodes can be for navigation
(b)there can be associations between data nodes relevant to content of THIS NODE, but not navigation.
In this respect, separate ideas of node's static data (what it holds) and other data it might want to link to (i.e. follow data in another node)
In order to retain flexibility, a node can keep its own data for a time, then switch to a 'follower' for a while or permanently, switch back.
It can also copy of the data from the node it has been following.
(TO DO)

Data pipeline modes:
Data link parent enables another node's data to be 'followed' i.e. the priority data accessed in the GUI. etc
If data mode is set to follower this node will then "show" that other data to other objects (including the GUI)
However, it always has its own data.
So if the data mode is set to "own", it will its own data.
This will also allow the 'followed' node to be copied into own data periodically.
Storing the parent data node for following will maintain that information for the node's benefit.
This is useful for obtaining GUI-access to data in other contexts, and for precedent creation etc.

10.6.18
Include counter field for dictionary work

# ClauseContainer (2)


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

# Parser.java

//returns a new ClauseContainer based on contents of file
//That is, the ClauseContainer is the primary data structure object
//Qn: Should it return an SQL table with all of the contents of the file?
//Where each row of the SQL is, in effect, a 'ClauseContainer' observation?
//Alternatively, store as in-memory array that is used to pull out 'ClauseContainer' as needed.