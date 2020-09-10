import java.util.zip.*;
import java.util.zip.ZipInputStream;
import java.io.*;  //Buffered Reader, File Reader, IOException, FileNoteFoundException etc
import java.util.*; //scanner, HashMap, ArrayList etc, Zip...
import java.nio.charset.StandardCharsets; 

/* python3 ref 

def writeNewFileZip(nbpath,newname,docxml):

    maindoc='word/document.xml'
    excludednames = [maindoc] 
    nbpackages=_getZipInfo(nbpath) 
    unzipsrc=zipfile.ZipFile(nbpath,'r',zipfile.ZIP_DEFLATED)
    with zipfile.ZipFile(newname, 'w',zipfile.ZIP_DEFLATED) as zipwrite:
        
        # --- WRITE ALL EXISTING NOTEBOOK CONTENTS EXCEPT document.xml
        for item in nbpackages:
            if item.filename not in excludednames:  # no duplication
                print("saving notebook package:"+item.filename)
                insertdata = unzipsrc.read(item.filename)
                # check xml and rels strings are utf decoded/encoded
                newdata=utftest(item.filename,insertdata)
                zipwrite.writestr(item, newdata)  
            else: 
                print("notebook package not re-saved:"+item.filename)
                zipwrite.writestr(item,docxml)       
        zipwrite.close()

 */

public class ZipUtil {

public ZipUtil() {

}

//input is a File or String?
public ArrayList<ZipEntry> readZip(String fileZip){
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	try  {
		FileInputStream myFileStream = new FileInputStream(fileZip);
		ZipInputStream zis = new ZipInputStream(myFileStream);
	    ZipEntry zipItem = zis.getNextEntry();
	    while (zipItem != null) {
	    		zipItem = zis.getNextEntry();
	    		if (zipItem!=null) {
	    			String name =zipItem.getName();
	    			System.out.println(name);
	    		}
	    }
	}
	catch (FileNotFoundException fnf) {
		System.out.println("Exception");
	}
	catch (IOException e) {
		System.out.println("Exception");
	}
	return myItemList;
}

/*
public void openStyleTest(String filepath) {
	System.out.println("OST");
	File file = new File(filepath);
	System.out.println(file.getName());
	System.out.println(file.getPath());
    String contents = getFileText(file);
    String myFile="content/StylesTemplate.docx"; //TO DO:put in sother folder
    readAndReplaceZip(myFile,contents);
}
*/

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
      System.out.println(nl);
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
  return myText.toString();
}

//need to check compression format...
public ArrayList<ZipEntry> readAndReplaceZip(String fileZip, String docxml, String outFile) {
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	File f = new File(outFile); //outputfile
	String target="word/document.xml";
	try  {
		FileInputStream myFileStream = new FileInputStream(fileZip);
		FileOutputStream myOutputStream = new FileOutputStream(f);
		//Setup 2 streams
		ZipInputStream zis = new ZipInputStream(myFileStream);
		ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		out.setLevel(6); //setcompression level for deflated entries.  Should be =6 for docx
		out.setMethod(ZipEntry.DEFLATED);
	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		 //a ZipEntry is used in the Zip file system.  automatically closes last entry
	    	count++;
	    		
	    	String name =zipItem.getName();

	    	if (name.equals(target)) {
				System.out.println(name);
				//read in as uncompressed...

				//we're going to replace the target document.xml with the input string
				//out.putNextEntry(zipItem); //make use of the ZipEntry we read in from input.  Name.  NO.  need to make new one to rezie
				ZipEntry newItem = new ZipEntry(target);
				out.putNextEntry(newItem);
				byte[] data = docxml.getBytes();  //turn String into bytes since that what ZipFileOutputStream wants
   				newItem.setSize(data.length); //set size
				System.out.println(docxml);
				System.out.println(data.length);
				System.out.println("About to write");
				out.write(data);
				//out.write(data, 0, data.length);  //writes this entry as compressed data bytes to the output file
				out.closeEntry();
				//System.exit(0);
			}
			//otherwise, we're going to write the ZipEntry file data back into the new file
			else {
	    			//we need to buffer the byte reads because we don't know byte size
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    				int rdlength;
    				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
    				//check that the actual read bytes isn't empty and make sure UTF-8 is used.
    				//while ((rdlength = zis.read(data,0,data.length)) != -1) {
    				//rdlength = zis.read(data,0,data.length);
    				//System.out.println(rdlength);
    				
    				while ((rdlength = zis.read(data,0,data.length)) != -1) {
    						//message = new String(result, 0, resultLength, "UTF-8");
    						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
    				}
    				buffer.flush(); //finish buffering
    				//byte[] tester = buffer.toByteArray();
					byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
					String mystring = new String(byteArray, StandardCharsets.UTF_8);
    				System.out.println(mystring);
					//System.out.println(tester.toString());
						//decode with utf8 if string files
					if (name.indexOf("xml") != -1 || name.indexOf("rels") != -1) {
        				System.out.println("This is a string not binary file: "+name);
        				
   					}
   					//images etc
   					if (name.indexOf("xml") == -1 && name.indexOf("rels") == -1) {
        				System.out.println("This is a binary file: "+name);
        				//System.exit(0);
   					}
   					//we have our new bytearray (for contents) rather than a file per se.
   					//new ZipEntry for output
   					ZipEntry zipOutItem = new ZipEntry(name);
   					zipOutItem.setSize(byteArray.length); //set size
    				out.putNextEntry(zipOutItem); //
					out.write(byteArray);
					//out.write(byteArray,0,byteArray.length); //now write out the contents of byte array
    				out.closeEntry();  //optional if you use 'get next entry'
	    		}	
	    		zipItem = zis.getNextEntry();
	    }
	    out.close(); //close the output stream and file.  zis?
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	return myItemList;
}

//need to check compression format...
public ArrayList<ZipEntry> readAndWriteZip(String fileZip){
	ArrayList<ZipEntry> myItemList = new ArrayList<ZipEntry>();
	File f = new File("content/test.docx"); //outputfile
	//String target="word/document.xml";
	try  {
		FileInputStream myFileStream = new FileInputStream(fileZip);
		FileOutputStream myOutputStream = new FileOutputStream(f);
		//Setup 2 streams
		ZipInputStream zis = new ZipInputStream(myFileStream);
		ZipOutputStream out = new ZipOutputStream(myOutputStream,java.nio.charset.StandardCharsets.UTF_8); //utf 8 for docx;
		out.setLevel(6); //setcompression level for deflated entries.  Should be =6 for docx
		out.setMethod(ZipEntry.DEFLATED);
	    ZipEntry zipItem = zis.getNextEntry();
	    int count=0;
	    while (zipItem!= null) {
	    		 //a ZipEntry is used in the Zip file system.  automatically closes last entry
	    		count++;
	    		
	    		String name =zipItem.getName();


			

	    			//we need to buffer the byte reads because we don't know byte size
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    				int rdlength;
    				byte[] data = new byte[1024]; //size for the maximum bytes read at any time
    				//check that the actual read bytes isn't empty and make sure UTF-8 is used.
    				//while ((rdlength = zis.read(data,0,data.length)) != -1) {
    				//rdlength = zis.read(data,0,data.length);
    				//System.out.println(rdlength);
    				
    				while ((rdlength = zis.read(data,0,data.length)) != -1) {
    						//message = new String(result, 0, resultLength, "UTF-8");
    						buffer.write(data,0, rdlength); //add our latest bytes to the buffer
    				}
    				buffer.flush(); //finish buffering
    				//byte[] tester = buffer.toByteArray();
					byte[] byteArray = buffer.toByteArray(); //convert buffer back to byte array of actual length
					String mystring = new String(byteArray, StandardCharsets.UTF_8);
    				System.out.println(mystring);
					//System.out.println(tester.toString());
						//decode with utf8 if string files
					if (name.indexOf("xml") != -1 || name.indexOf("rels") != -1) {
        				System.out.println("This is a string not binary file: "+name);
        				
   					}
   					//images etc
   					if (name.indexOf("xml") == -1 && name.indexOf("rels") == -1) {
        				System.out.println("This is a binary file: "+name);
        				//System.exit(0);
   					}
   					//we have our new bytearray (for contents) rather than a file per se.
   					//new ZipEntry for output
   					ZipEntry zipOutItem = new ZipEntry(name);
   					zipOutItem.setSize(byteArray.length); //set size
    				out.putNextEntry(zipOutItem); //
					out.write(byteArray);
					//out.write(byteArray,0,byteArray.length); //now write out the contents of byte array
    				out.closeEntry();  //optional if you use 'get next entry'
    				zipItem = zis.getNextEntry();

	    			
	    }
	    out.close(); //close the output stream and file.  zis?
	}
	catch (FileNotFoundException fnf) {
		System.out.println("FNF Exception");
		fnf.printStackTrace();
	}
	catch (IOException e) {
		System.out.println("IO Exception");
		e.printStackTrace();
	}
	return myItemList;
}

/*
public class UnzipFile {
    public static void main(String[] args) throws IOException {
        String fileZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File("src/main/resources/unzipTest");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
    
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        
        return destFile;
    }
}
*/
/*
//zip multiple files
public void ZipMultiple (ArrayList myInput) throws IOException {
        List<String> srcFiles = myInput; //Arrays.asList("test1.txt", "test2.txt");
        FileOutputStream fos = new FileOutputStream("multiCompressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
 
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }

//read

//String fileZip = "src/main/resources/unzipTest/compressed.zip";
//File destDir = new File("src/main/resources/unzipTest");
public void writeZip(String fileZip,File destDir) {     
        
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

//class to make a new File object
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName()); //prevent ZipSlip attacks with relative file paths
        }
        
        return destFile;
    }
    */
}

