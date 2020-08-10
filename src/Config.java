import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


public class Config {

//File IO locations.  If running from classes folder with java -cp ./ Main use ../templates
String projectfolder = "";  //This is the current top level folder with the src and fxlib folders in it
String templatesfolder = projectfolder+"/templates/";
String recentsfolder = projectfolder+"/config/";

public Config() {
	
}

//return a graph node holding the current dictionary as a graph of subnodes
public String getProjectFolder () {
	return projectfolder;
}

public String getRecentsFolder () {
	return recentsfolder;
}

public String getTemplatesFolder () {
	return templatesfolder;
}

}
