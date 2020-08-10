//ArrayList etc
import java.util.*;
//For serialization IO 
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class NodeConfig extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -647978698708496L;

//full list of config options 
NodeCategory NC_World = new NodeCategory("World",0,"darkgrey");
//
NodeCategory NC_notes = new NodeCategory("notes",0,"khaki");
NodeCategory NC_footnotes = new NodeCategory ("footnotes",0,"khaki");
NodeCategory NC_docprecis = new NodeCategory ("doc precis",0,"khaki");
//
NodeCategory NC_clause = new NodeCategory ("clause",0,"blue");
NodeCategory NC_def = new NodeCategory ("definition",0,"green");
NodeCategory NC_Memory = new NodeCategory ("memory",0,"lightblue");
NodeCategory NC_testimony = new NodeCategory ("testimony",0,"lightblue");
NodeCategory NC_witness = new NodeCategory ("witness",0,"lightblue");

//NodeCategory NC_library = new NodeCategory ("library",1,"lemon");
NodeCategory NC_document = new NodeCategory ("document",1,"darkblue");
NodeCategory NC_caselaw = new NodeCategory ("caselaw",0,"darkgold");
NodeCategory NC_statutelaw = new NodeCategory ("statutelaw",0,"darkgold");
//NodeCategory NC_collection = new NodeCategory ("collection",2,"orange");
//NodeCategory NC_project = new NodeCategory ("project",3,"white");
//
NodeCategory NC_Alien = new NodeCategory("Alien",0,"khaki");

NodeCategory NC_State = new NodeCategory("State",0,"darkblue");
//
NodeCategory NC_Trader = new NodeCategory("Trader",0,"white");
//
NodeCategory NC_fact = new NodeCategory ("fact",0,"lightblue");
NodeCategory NC_event = new NodeCategory ("event",0,"lightblue");
NodeCategory NC_matter = new NodeCategory ("matter",0,"lightblue");
NodeCategory NC_circumstance = new NodeCategory ("circumstance",0,"lightblue");
//
NodeCategory NC_dict = new NodeCategory ("dictionary",88,"white");

private void initialiseNodeCategories() {
        
    }

public ArrayList<NodeCategory> getDefaultNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_World));
}

/* The following code initialises the NodeCategories. 
These should be saved with world view (so doc count is maintained).
It may be possible to add these in a child node to Worldview at some point,
swapping the main node class (ClauseContainer) for this.
*/

/*

public ArrayList<NodeCategory> getLawNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_notes,NC_footnotes,NC_clause,NC_def,NC_law,NC_fact,NC_Memory,NC_event,NC_witness,NC_testimony));

}
*/

public ArrayList<NodeCategory> getLawNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_caselaw,NC_statutelaw));

}

public ArrayList<NodeCategory> getLitNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_fact,NC_Memory,NC_event,NC_witness,NC_testimony));

}

public ArrayList<NodeCategory> getNotesNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_notes,NC_docprecis,NC_footnotes));

}

public ArrayList<NodeCategory> getCommercialNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_document,NC_clause,NC_def));

}

public ArrayList<NodeCategory> getMerchantNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_Alien,NC_State,NC_Trader));

}

public ArrayList<NodeCategory> getDictionaryNodes() {

return new ArrayList<NodeCategory>(Arrays.asList(NC_dict));

}

public ArrayList<NodeCategory> getEvents() {

 
return new ArrayList<NodeCategory>(Arrays.asList(NC_event, NC_fact,NC_matter,NC_circumstance));

}

//notice constructor can be at end, but convention is at start before methods
public NodeConfig() {}


}