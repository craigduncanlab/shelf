//class to hold an OOXML table
import java.io.*; //includes java.io.File.  cf the new nio classes
import java.io.IOException;
import java.util.*; //scanner, HashMap etc

public class ooxmlTable {
	String tableOOXMLString="";
	int startindex=0; //hold original file position indexes
	int endindex=0;
	int numRows=0; //replace in favour of array of rows and getter
	int numCols=0; //replace in favour of array of cols and getter
	ArrayList<tableRow> tableRows  = new ArrayList<tableRow>();
	//Once we have a table in OOP we can navigate contents
	int currentRow=0;
	int currentCol=0;
	//each cell will contain an xmlPara object (itself holding the OOXML tagged string), not just String.

public ooxmlTable(){
	
}
/* 
subclass to hold the row of cells information
TO DO: rename as vector so it can be row or column vector
*/
public class tableRow {
	ArrayList<tableCell> rowCells  = new ArrayList<tableCell>();
	int currentCell = 0;
	int cellCount=0;

	public tableRow(){

	}
	public void addCell(tableCell input){
		rowCells.add(input);
		cellCount=rowCells.size();
	}

	public ArrayList<tableCell> getAllCells(){
		return this.rowCells;
	}

	public tableCell getCurrentCell(){
		return rowCells.get(this.currentCell);
	}

	public void setCurrentCell(Integer input){
		this.currentCell=input;
	}

	public Integer getCurrentCellIndex(){
		return this.currentCell;
	}

	public Integer getCellCount(){
		return this.cellCount;
	}

}  

/* 
subclass to hold the cell information
*/
public class tableCell{
	xmlPara myPara = new xmlPara();
	ArrayList<xmlPara> cellParas = new ArrayList<xmlPara>();
	ArrayList<String> cellList = new ArrayList<String>();

	String cellString = "";
	
	public tableCell(String taginput){
		setString(taginput);
		xmlTool myTool = new xmlTool();
		ArrayList<xmlPara> newParas = myTool.extractParas(taginput);
		if (newParas.size()>0){
			//we have a list inside the cell
			makeList(newParas);
			//logList();
		}
		setXMLparas(newParas);
	}

	public void setString(String input){
		this.cellString=input;
	}

	public ArrayList<xmlPara> getAllXMLparas(){
		return this.cellParas;
	}

	public void setXMLparas(ArrayList<xmlPara> input){
		this.cellParas=input;
	}

	public String getPlainText(){
		String output="";
		for (xmlPara item : getAllXMLparas()) {
			String para = item.getplainText();
			output=output+para+System.getProperty("line.separator");
		}
		return output;
	}

	/* When there are several paragraphs in a single cell this is effectively a list item */

	public String getHTMLcell(){
		String output="";
		for (xmlPara item : getAllXMLparas()) {
			String para = item.getplainText();
			output=output+"<p>"+para+"</p>";
		}
		return output;
	}

	//make discrete data item from paragraphs in cell
	
	public void makeList(ArrayList<xmlPara> inputParas){
		ArrayList<String> newList = new ArrayList<String>();
		for (xmlPara item : inputParas){
			newList.add(item.getplainText());
		}
		setCellList(newList);
	}

	private void logList(){
		System.out.println("Logging list in file");
		System.out.println("--------------------");
		for (String item : getCellList()){
			System.out.println(item);
		}
	}

	public void setCellList(ArrayList<String> input){
		this.cellList = input;
	}

	public ArrayList<String> getCellList(){
		return this.cellList;
	}

	//external objects can query cell to see if has list.
	public Boolean hasList(){
		if (cellList.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}

}

public void setCurrentRowIndex(Integer input){
	this.currentRow=input;
}

public void setCurrentColIndex(Integer input){
	this.currentRow=input;
}

public Integer getCurrentRowIndex(){
	return this.currentRow;
}

public Integer getCurrentColIndex(){
	return this.currentRow;
}

public void addRow(tableRow input){
	tableRows.add(input);
}

public ArrayList<tableRow> getAllRows(){
	return tableRows;
}

public void setXMLString(String input){
	this.tableOOXMLString=input;
	initialise(input);
}

public void setTableRows(ArrayList<tableRow> inputRows){
	this.tableRows=inputRows;
}

/*
Function to return table as string
This currently returns OOXML
TO DO: convert table to row, col objects and string/plaintext, then this can return string
*/

public String getXMLString(){
	return this.tableOOXMLString;
}

public void setStartIndex(int input){
	this.startindex=input;
}

public int getStartIndex(){
	return this.startindex;
}

public void setEndIndex(int input){
	this.endindex=input;
}

public int getEndIndex(){
	return this.endindex;
}

public void initialise(String inputXML){
	extractTableRowObjects(inputXML);
}

/*
Input
Table as tagged String (OOXML)
Output: Array of Strings for further processing
*/

private void extractTableRowObjects(String input){
	ArrayList<tableRow> tableRows = new ArrayList<tableRow>();
    xmlTool myTool = new xmlTool();
    ArrayList<String> myRows = myTool.getTableRowText(input);
    //log_rows(myRows);
    for (String rowText : myRows){
    	tableRow newRow = new tableRow();
        ArrayList<String> myCells = myTool.getTableCellText(rowText);
        //System.out.println(myCells.size());
        //System.exit(0);
        for (String cellTagged : myCells) {
        	tableCell newCell = new tableCell(cellTagged);
        	//log_cells(newCell);
        	newRow.addCell(newCell);
        }
        tableRows.add(newRow);
    }

    setTableRows(tableRows);   
}

private void log_rows(ArrayList<String> myRows){
    System.out.println(myRows.size());
    int rowcount=0;
    for (String rowText : myRows){
    	System.out.println(rowcount);
    	System.out.println(rowText);
    	rowcount++;
    }
    System.exit(0);
}

private void log_cells(tableCell newCell){
	ArrayList<xmlPara> myps =newCell.getAllXMLparas();
	System.out.println(myps.size());
	for (xmlPara pp : myps) {
		System.out.println("Plain text:"+pp.getplainText());
		System.out.println("Para string:"+pp.getParaString()); //OOXML
	}
	System.exit(0);
}

public String getTableAsText(){
	String output="";
	ArrayList<tableRow> myRows = getAllRows();
	for (tableRow myRow : myRows){
		ArrayList<tableCell> myCells = myRow.getAllCells();
		String row = "";
		for (tableCell myCell : myCells){
			row=row+myCell.getPlainText()+"|";
		}
		output=output+row+System.getProperty("line.separator");
	}

	return output;
}



public String getTableAsHTML(){
	String scripted="<style>table td { border: 2px solid black ;} table {border-collapse:collapse;}</style>";
	String output="<html><head></head>"+scripted+"<body><table style=\"width:80%\">";
	ArrayList<tableRow> myRows = getAllRows();
	for (tableRow myRow : myRows){
		ArrayList<tableCell> myCells = myRow.getAllCells();
		String row = "<tr>";
		for (tableCell myCell : myCells){
			row=row+"<td>"+myCell.getHTMLcell()+"</td>";
		}
		output=output+row+"</tr>";//System.getProperty("line.separator");
	}
	output=output+"</table></body></html>";
	return output;
}

}