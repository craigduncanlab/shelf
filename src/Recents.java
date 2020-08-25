import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap, ArrayList etc
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//build a menu from these entries
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;


//Class to load in recents list to populate Open menu subitems
//nb Java supplies a Preferences class which stores data in registry etc and is an alternative to local text file: https://docs.oracle.com/javase/6/docs/api/java/util/prefs/Preferences.html

public class Recents {

//File IO locations - relative to classes.  TO DO: put folder data in general config file.
//String searchfolder = "../config/"; //relative
Config myConfig = new Config();
String recentfolder = myConfig.getRecentsFolder();
ArrayList<String> recentfiles = new ArrayList<String>();  //10 recent items  
String outputfile = "recents";     
MainStage targetSM = new MainStage();
EventHandler<ActionEvent> RecentFilesHandler;
Menu myRecent = new Menu("Open Recent");
String menufilename="";
ClauseContainer loadedNode=new ClauseContainer();
LoadSave myLS;
WhiteBoard defaultWhiteBoard = new WhiteBoard();

public Recents() {
	
}

//constructor with stage
public Recents(MainStage target, LoadSave loader) {
	//this.RecentFilesHandler = myEventHandler;
	this.myLS=loader;
	this.targetSM = target;
	if (this.targetSM==null) {
		System.out.println("Problem with Stage Manager used for constructor in Recents");
	}
}

//constructor with WhiteBoard
public Recents(WhiteBoard myWB, LoadSave loader) {
	//this.RecentFilesHandler = myEventHandler;
	this.myLS=loader;
	//this.targetSM = target;
	this.defaultWhiteBoard=myWB;
	if (this.defaultWhiteBoard==null) {
		System.out.println("Problem with WhiteBoard used for constructor in Recents");
	}
}

public Menu makeRecentMenu () {
	Menu myMenu = new Menu("Open Recent ->"); //make with no entries
	//this.myRecent.clear();
	ArrayList<String> latest = getList();
	Iterator<String> myIterator = latest.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
    	String filename = myIterator.next();
    	MenuItem myMI = makeMenuItem(filename);
    	//System.out.println("menu item added:"+filename);
		myMenu.getItems().add(myMI);
    }
    return myMenu;
}

public Menu getRecentMenu() {
	//System.out.println("Trying to make menu");
	return makeRecentMenu();
}

private MainStage getTarget() {
	return this.targetSM;
}

private MainStage getFilename() {
	return this.targetSM;
}

private void openFile(String filename) {
	//System.out.println("Opening file:"+filename);
	TemplateUtil myUtil = new TemplateUtil();
	ClauseContainer newNode = myUtil.getStructuredData(filename); 
	if (newNode!=null) {
		updateRecents(filename);
		//System.out.println("Created:"+newNode.toString());
		//System.out.println(newNode.toString());
		//this.myLS.simpleOpen(newNode);
		this.targetSM.OpenNewNodeNow(newNode); //TO DO: WhiteBoard
	}
}


//Make a menu item that will open the filename passed to this function
//For variable scope, ensure the set on Action and new Event are all in this function

public MenuItem makeMenuItem(String filename) {
	MenuItem menuItemA = new MenuItem(filename);
	this.menufilename=filename;
    //menuItemA.setOnAction(this.RecentFilesHandler);
    menuItemA.setOnAction(new EventHandler<ActionEvent>() {
	@Override 
        public void handle(ActionEvent event) {
        	//System.out.println("Event handled for Recents");
        	MenuItem source = (MenuItem)event.getSource();
        	//System.out.println("filename:"+source.getText());
        	Recents.this.openFile(filename);
        }
    });
    return menuItemA;
}

//return ArrayList with Menu Items
public ArrayList<String> getList() {
	this.recentfiles = readRecents(this.outputfile);
	return this.recentfiles;
}

//update recents and rewrite file with recent entries
//add to recents up to 10 entries, then drop last
//query: hold in memory?

public void updateRecents(String newEntry) {
	this.recentfiles = readRecents(this.outputfile);
	//if this entry already exists, remove it now
	if (this.recentfiles.contains(newEntry)) {
		this.recentfiles.remove(newEntry); //removes it, wherever it is
	}
	if (this.recentfiles.size()<20) {
		this.recentfiles.add(0,newEntry); //add to start
		writeRecents(this.recentfiles,this.outputfile);
	}
	else {
		int listend = this.recentfiles.size()-1; //starts at 0 index
		this.recentfiles.remove(listend);
		this.recentfiles.add(0,newEntry); //add to start
		writeRecents(this.recentfiles,this.outputfile);
	}
}

/*
Method to allow easy setup of templates just using headings in a text file.
the first entry in each row read in as a a container (node)
the rest of the entries in each row are read in as sub-nodes of that node
The node returned is the root node which is to be placed onto the workspace.
*/

private ArrayList<String> readRecents(String filename) {
	ArrayList<String> newList = new ArrayList<String>();
	String fileref=this.recentfolder+filename+".pdu"; //power dock utility file
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No content");
			return null;
		}
		while (scanner1.hasNextLine()) {
			String thisRow=scanner1.nextLine();
			newList.add(thisRow);
		}
		scanner1.close();
	}

	catch (FileNotFoundException e) {
		System.out.println("Cannot find the Recents.pdu file");
		String stringDefault = " ";
       //initialize an immutable list from array using asList method
        newList.add(stringDefault);
		return newList;
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		String stringDefault = " ";
       //initialize an immutable list from array using asList method
        newList.add(stringDefault);
		return newList;
	}
	return newList;
}

public void writeRecents(ArrayList<String> recents, String filename) {
	clean(filename);
	Iterator<String> myIterator = this.recentfiles.iterator(); //alternatively use Java method to see if in Array?
    while (myIterator.hasNext()) {
    	String thisEntry=myIterator.next();
        writeOut(thisEntry,filename);
    }
}

//method to write one recent entry out to config file

private void writeOut(String listEntry, String filename) {
	String recentfile =this.recentfolder+filename+".pdu";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(recentfile,true));
	System.setOut(outstream);
	System.out.println(listEntry); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}

	catch (FileNotFoundException e) {
		System.out.println("Cannot find the Recents.pdu file");
		return;
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		return;
	}
}

private void clean(String filename) {
	String reportfile=this.recentfolder+filename+".pdu";
	try {

	PrintWriter pw = new PrintWriter(reportfile);
	pw.close();
	}
	catch (FileNotFoundException e) {
		System.out.println("Clean.  Cannot find the Recents.pdu file");
		return;
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		return;
	}

}
}