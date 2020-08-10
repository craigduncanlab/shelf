//(c) Craig Duncan 2020
//A class to parse markdown files for this application (input and output)
import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;

public class Parser {

//default constructor
public Parser(){

}

//read in an .md file and then process it
//returns a new ClauseContainer based on contents of file
//That is, the ClauseContainer is the primary data structure object
//Qn: Should it return an SQL table with all of the contents of the file?
//Where each row of the SQL is, in effect, a 'ClauseContainer' observation?
//Alternatively, store as in-memory array that is used to pull out 'ClauseContainer' as needed.
public ClauseContainer parseMDfile(String contents) {
    System.out.println("Begin parsing MD file");
    // for now, no processing of contents
    ClauseContainer newNode = new ClauseContainer("Test",contents,"notes");
    System.out.println("Finished parsing MD file");
    return newNode;
}

}